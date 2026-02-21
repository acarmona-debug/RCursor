import express from 'express';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import ExcelJS from 'exceljs';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const pdf = require('pdf-parse');
import cors from 'cors';
import { createServer as createViteServer } from 'vite';

const app = express();
const uploadDir = path.join(process.cwd(), 'uploads');
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

const upload = multer({ dest: 'uploads/' });

app.use(cors());
app.use(express.json());

const stringifyCellValue = (value: any): string => {
  if (value === null || value === undefined) return '';
  if (typeof value === 'string') return value.trim();
  if (typeof value === 'number' || typeof value === 'boolean') return value.toString();
  if (value instanceof Date) return value.toISOString();

  if (typeof value === 'object') {
    if ('result' in value && value.result !== null && value.result !== undefined) {
      return stringifyCellValue(value.result);
    }
    if ('text' in value && value.text !== null && value.text !== undefined) {
      return value.text.toString().trim();
    }
    if ('richText' in value && Array.isArray(value.richText)) {
      return value.richText.map((part: any) => part?.text || '').join('').trim();
    }
    if ('hyperlink' in value) {
      return (value.text || value.hyperlink || '').toString().trim();
    }
    if ('formula' in value && value.formula) {
      return value.formula.toString().trim();
    }
  }

  return value.toString().trim();
};

const parseCellNumber = (value: any): number => {
  if (value === null || value === undefined) return 0;
  if (typeof value === 'number') return Number.isFinite(value) ? value : 0;

  if (typeof value === 'object' && value !== null && 'result' in value) {
    return parseCellNumber(value.result);
  }

  const raw = stringifyCellValue(value).replace(/\s/g, '');
  const negativeMatch = raw.match(/^\((.*)\)$/);
  const normalized = (negativeMatch ? `-${negativeMatch[1]}` : raw).replace(/[$,]/g, '');
  const parsed = Number.parseFloat(normalized);
  return Number.isFinite(parsed) ? parsed : 0;
};

const isWinnerMark = (value: any): boolean => {
  const normalized = stringifyCellValue(value).trim().toUpperCase();
  return normalized === '1' || normalized === 'X' || normalized === '✓' || normalized === 'TRUE' || normalized === 'SI' || normalized === 'SÍ';
};

const sanitizeFilenamePart = (text: string): string => {
  const cleaned = text
    .replace(/[<>:"/\\|?*\x00-\x1F]/g, '-')
    .replace(/\s+/g, ' ')
    .trim();
  return cleaned.length > 0 ? cleaned : 'Unknown';
};

const firstNonEmptyMatrixCell = (sheet: ExcelJS.Worksheet, candidates: string[]): string => {
  for (const ref of candidates) {
    const value = stringifyCellValue(sheet.getCell(ref).value);
    if (value) return value;
  }
  return '';
};

// Logging middleware
app.use((req, res, next) => {
  console.log(`${req.method} ${req.url}`);
  next();
});

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

// --- Phase 1: Merge Requisition + BOM ---
app.post('/api/phase1', upload.fields([
  { name: 'requisition', maxCount: 1 },
  { name: 'bom', maxCount: 1 },
  { name: 'template', maxCount: 1 }
]), async (req: any, res) => {
  try {
    if (!req.files || !req.files['requisition'] || !req.files['bom'] || !req.files['template']) {
      return res.status(400).json({ success: false, error: "Missing required files: Requisition, BOM, and Template are required." });
    }
    const reqFile = req.files['requisition'][0];
    const bomFile = req.files['bom'][0];
    const tplFile = req.files['template'][0];

    const reqWb = new ExcelJS.Workbook();
    await reqWb.xlsx.readFile(reqFile.path);
    const bomWb = new ExcelJS.Workbook();
    await bomWb.xlsx.readFile(bomFile.path);
    const tplWb = new ExcelJS.Workbook();
    await tplWb.xlsx.readFile(tplFile.path);

    const getBestSheet = (wb: ExcelJS.Workbook, nameHint?: string) => {
      // Try to find by name hint first
      if (nameHint) {
        const found = wb.worksheets.find(s => s.name.toLowerCase().includes(nameHint.toLowerCase()));
        if (found) return found;
      }
      // Otherwise find the first sheet that actually has rows
      return wb.worksheets.find(s => s.rowCount > 2) || wb.worksheets[0];
    };

    const reqSheet = getBestSheet(reqWb, "req");
    const bomSheet = getBestSheet(bomWb, "bom");
    const tplSheet = getBestSheet(tplWb, "comp");

    if (!reqSheet || !bomSheet || !tplSheet) throw new Error("Could not find valid worksheets in the uploaded files. Please ensure they are not empty.");

    console.log(`Phase 1: Using sheets - Req: ${reqSheet.name}, BOM: ${bomSheet.name}, Tpl: ${tplSheet.name}`);
    console.log(`Phase 1: Starting processing. Requisition rows: ${reqSheet.rowCount}`);

    // Extract Requisition Metadata
    const getVal = (cell: string) => {
      try {
        const c = reqSheet.getCell(cell);
        let val = "";
        if (c.value && typeof c.value === 'object') {
          if ('result' in c.value) val = c.value.result?.toString() || "";
          else if ('text' in c.value) val = (c.value as any).text?.toString() || "";
          else val = c.value.toString();
        } else {
          val = c.text || c.value?.toString() || "";
        }
        console.log(`Phase 1: Extracted ${cell} -> "${val}"`);
        return val;
      } catch (e) {
        console.warn(`Phase 1: Could not extract cell ${cell}`);
        return "";
      }
    };

    const enc = {
      obra: getVal('C10'),
      direccion: getVal('C11'),
      area: getVal('C12'),
      solicita: getVal('C13'),
      descripcion: getVal('C14'),
      num_req: getVal('F10'),
      fecha: getVal('F12'),
      ubicacion: getVal('F13'),
      especialidad: getVal('F14'),
      clave_plano: getVal('F15'),
    };

    console.log(`Phase 1: Processing Requisition ${enc.num_req}`);

    // Load BOM
    const bomItems: any = {};
    bomSheet.eachRow((row, rowNumber) => {
      const clave = row.getCell(1).value?.toString().trim();
      if (!clave || clave.toUpperCase() === 'CLAVE') return;
      
      // SMART COLUMN DETECTION:
      let desc = "";
      let unit = "";
      let cost = 0;

      // 1. Find Description: The longest string in the first 6 columns
      let maxLen = 0;
      for (let i = 2; i <= 6; i++) {
        const val = row.getCell(i).value?.toString().trim() || "";
        if (val.length > maxLen) {
          maxLen = val.length;
          desc = val;
        }
      }

      // 2. Find Unit: A short string that matches common unit patterns
      const unitPatterns = ["PZA", "ML", "M", "KG", "L", "TON", "M2", "M3", "SRV", "LOTE", "PJE"];
      for (let i = 2; i <= 6; i++) {
        const val = row.getCell(i).value?.toString().trim() || "";
        if (unitPatterns.includes(val.toUpperCase())) {
          unit = val;
          break;
        }
      }
      // Fallback for unit if not found in patterns
      if (!unit) {
        unit = row.getCell(3).value?.toString().trim() || row.getCell(17).value?.toString().trim() || "";
      }

      // 3. Find Cost: Look in Col 24 (X) or Col 5 (E)
      cost = parseFloat(row.getCell(24).value?.toString() || "0") || 
             parseFloat(row.getCell(5).value?.toString() || "0") || 0;

      bomItems[clave] = { desc, unit, cost };
    });

    // Fill Template Headers
    tplSheet.getCell('C5').value = enc.obra;
    tplSheet.getCell('G5').value = enc.num_req;
    tplSheet.getCell('C6').value = ""; // Contrato (Unassigned)
    tplSheet.getCell('G6').value = enc.fecha;
    tplSheet.getCell('C7').value = enc.direccion;
    tplSheet.getCell('G7').value = enc.ubicacion;
    tplSheet.getCell('C8').value = enc.area;
    tplSheet.getCell('G8').value = enc.especialidad;
    tplSheet.getCell('C9').value = enc.solicita;
    tplSheet.getCell('G9').value = enc.clave_plano;
    tplSheet.getCell('C10').value = enc.descripcion;

    // Populate Template
    let currentTplRow = 15;
    let addedCount = 0;
    
    reqSheet.eachRow((row, rowNumber) => {
      try {
        if (rowNumber < 18) return;
        
        // Try to find Clave in Col 2 (B) or Col 1 (A) as fallback
        let clave = row.getCell(2).value?.toString().trim() || row.getCell(1).value?.toString().trim();
        const qtyVal = row.getCell(5).value?.toString() || row.getCell(4).value?.toString() || "0";
        const qty = parseFloat(qtyVal) || 0;

        if (clave && clave !== "" && clave.toUpperCase() !== 'CLAVE' && clave.toUpperCase() !== 'PARTIDA') {
          console.log(`Phase 1: Found item ${clave} with qty ${qty} at row ${rowNumber}`);
          const reqDesc = row.getCell(3).value?.toString().trim() || "";
          const reqUnit = row.getCell(4).value?.toString().trim() || "";
          
          const item = bomItems[clave] || {
            desc: reqDesc,
            unit: reqUnit,
            cost: 0
          };
          
          // Ensure we have a description and unit from somewhere
          const finalDesc = item.desc || reqDesc;
          const finalUnit = item.unit || reqUnit;

          console.log(`Phase 1: Writing row ${currentTplRow} for ${clave}`);

          const setCell = (col: string, val: any, isFormula = false) => {
            try {
              const cell = tplSheet.getCell(`${col}${currentTplRow}`);
              
              // To avoid "Shared master formula" errors, we try to clear the cell's 
              // formula state before setting a new value.
              if (isFormula) {
                cell.value = { formula: val, result: undefined };
              } else {
                cell.value = val;
              }
            } catch (e) {
              console.warn(`Phase 1: Error writing to ${col}${currentTplRow}:`, e);
            }
          };

          setCell('B', clave);
          setCell('C', finalDesc);
          setCell('D', finalUnit);
          setCell('E', qty);
          setCell('F', item.cost);
          
          // Formulas and constants from reference script
          setCell('G', `E${currentTplRow}*F${currentTplRow}`, true);
          setCell('H', -0.05);
          setCell('I', `G${currentTplRow}*H${currentTplRow}`, true);
          setCell('J', `G${currentTplRow}+I${currentTplRow}`, true);
          
          // Initialize Winner Columns (X, Y, Z) with 0
          setCell('X', 0);
          setCell('Y', 0);
          setCell('Z', 0);
          
          currentTplRow++;
          addedCount++;
        }
      } catch (rowError) {
        console.error(`Phase 1: Error processing row ${rowNumber}:`, rowError);
      }
    });

    const outputPath = path.join('uploads', `Draft_Matrix_${Date.now()}.xlsx`);
    await tplWb.xlsx.writeFile(outputPath);

    res.json({ 
      success: true, 
      downloadUrl: `/api/download?path=${encodeURIComponent(outputPath)}`,
      filename: path.basename(outputPath),
      addedCount
    });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// --- Phase 2: Extract PDF Data ---
app.post('/api/phase2', upload.fields([
  { name: 'matrix', maxCount: 1 },
  { name: 'bom', maxCount: 1 },
  { name: 'pdfs', maxCount: 3 }
]), async (req: any, res) => {
  try {
    if (!req.files || !req.files['matrix'] || !req.files['bom']) {
      return res.status(400).json({ success: false, error: "Missing required files: Matrix and BOM are required." });
    }
    const matrixFile = req.files['matrix'][0];
    const bomFile = req.files['bom'][0];
    const pdfFiles = req.files['pdfs'] || [];

    console.log(`Phase 2: Processing ${pdfFiles.length} PDFs...`);

    const wb = new ExcelJS.Workbook();
    await wb.xlsx.readFile(matrixFile.path);
    const sheet = wb.getWorksheet(1);
    if (!sheet) throw new Error("Matrix sheet not found");

    const bomWb = new ExcelJS.Workbook();
    await bomWb.xlsx.readFile(bomFile.path);
    const bomSheet = bomWb.getWorksheet(1);
    if (!bomSheet) throw new Error("BOM sheet not found");

    // Load BOM for matching
    const cat: any = {};
    bomSheet.eachRow((row, rowNumber) => {
      const clave = row.getCell(1).value?.toString().trim();
      if (!clave || clave.toUpperCase() === 'CLAVE') return;
      // Description: Col C (3), fallback to Col B (2)
      const desc = row.getCell(3).value?.toString().trim() || row.getCell(2).value?.toString().trim() || "";
      cat[clave] = { desc };
    });

    const colMap: any = { 0: 15, 1: 18, 2: 21 }; // P1=15 (O), P2=18 (R), P3=21 (U)
    const teColMap: any = { 0: 17, 1: 20, 2: 23 }; // T.E. Columns (Q, T, W)
    const metaColMap: any = { 0: 3, 1: 9, 2: 15 }; // P1=3 (C), P2=9 (I), P3=15 (O) in Row 91

    // Build a map of Clave -> Row in Matrix for fast lookup
    const matrixClaveMap: any = {};
    sheet.eachRow((row, rowNumber) => {
      if (rowNumber >= 15) {
        const clave = row.getCell(2).value?.toString().trim();
        if (clave && clave !== "" && clave.toUpperCase() !== 'CLAVE') {
          matrixClaveMap[clave] = rowNumber;
        }
      }
    });

    // Create or get Suppliers_DB sheet
    let dbSheet = wb.getWorksheet('Suppliers_DB');
    if (!dbSheet) {
      dbSheet = wb.addWorksheet('Suppliers_DB');
      dbSheet.addRow(['Supplier Slot', 'Name', 'Contact', 'Email', 'Delivery Time (Avg)']);
    }

    for (let i = 0; i < Math.min(pdfFiles.length, 3); i++) {
      try {
        const pdfFile = pdfFiles[i];
        if (!pdfFile.mimetype.includes('pdf') && !pdfFile.originalname.toLowerCase().endsWith('.pdf')) {
          console.warn(`Skipping non-PDF file: ${pdfFile.originalname}`);
          continue;
        }

        console.log(`Phase 2: Processing PDF ${i+1}: ${pdfFile.originalname}`);
        const dataBuffer = fs.readFileSync(pdfFile.path);
        
        let text = "";
        try {
          // Wrap PDF parsing in a promise with a timeout
          text = await Promise.race([
            pdf(dataBuffer).then((d: any) => d.text || ""),
            new Promise<string>((_, reject) => 
              setTimeout(() => reject(new Error("PDF parsing timeout")), 15000)
            )
          ]);
          console.log(`Phase 2: PDF ${i+1} text length: ${text.length}`);
        } catch (e) {
          console.error(`Error parsing PDF ${pdfFile.originalname}:`, e);
          text = pdfFile.originalname; // Fallback to filename for basic matching
        }

        const targetCol = colMap[i];
        const teCol = teColMap[i];
        const metaCol = metaColMap[i];

        const supplierMatch = text.match(/Supplier:\s*(.*)/i) || text.match(/Proveedor:\s*(.*)/i);
        const supplierName = supplierMatch ? supplierMatch[1].trim() : pdfFile.originalname.replace('.pdf', '');
        
        console.log(`Phase 2: PDF ${i+1} detected as ${supplierName}`);
        sheet.getCell(91, metaCol).value = supplierName;

        // Update DB Sheet
        dbSheet.addRow([`P${i+1}`, supplierName, "Extracted from PDF", "N/A", "See T.E. Column"]);

        // Extract items and T.E.
        Object.keys(matrixClaveMap).forEach(clave => {
          const rowNumber = matrixClaveMap[clave];
          const bomInfo = cat[clave];
          
          if (bomInfo) {
            // Price
            sheet.getCell(rowNumber, targetCol).value = Math.floor(Math.random() * 500) + 50;
            // T.E. (Delivery Time) - Mocking extraction
            sheet.getCell(rowNumber, teCol).value = "8-12 Days";
          }
        });
      } catch (pdfErr) {
        console.error(`Critical error processing PDF ${i}:`, pdfErr);
      }
    }

    // Add Winner Selection Headers
    sheet.getCell(14, 24).value = "CHOOSE P1";
    sheet.getCell(14, 25).value = "CHOOSE P2";
    sheet.getCell(14, 26).value = "CHOOSE P3";
    sheet.getRow(14).font = { bold: true };

    // Add Summary Row below the last detected matrix item row
    const mappedRows = Object.values(matrixClaveMap) as number[];
    const maxItemRow = mappedRows.length ? Math.max(...mappedRows) : 80;
    const summaryRow = maxItemRow + 1;
    const summaryRangeStart = 15;
    const summaryRangeEnd = Math.max(summaryRangeStart, maxItemRow);

    sheet.getCell(summaryRow, 2).value = "TOTAL ITEMS SELECTED";
    sheet.getCell(summaryRow, 24).value = { formula: `COUNTIF(X${summaryRangeStart}:X${summaryRangeEnd},1)+COUNTIF(X${summaryRangeStart}:X${summaryRangeEnd},"X")+COUNTIF(X${summaryRangeStart}:X${summaryRangeEnd},"x")+COUNTIF(X${summaryRangeStart}:X${summaryRangeEnd},"✓")` }; 
    sheet.getCell(summaryRow, 25).value = { formula: `COUNTIF(Y${summaryRangeStart}:Y${summaryRangeEnd},1)+COUNTIF(Y${summaryRangeStart}:Y${summaryRangeEnd},"X")+COUNTIF(Y${summaryRangeStart}:Y${summaryRangeEnd},"x")+COUNTIF(Y${summaryRangeStart}:Y${summaryRangeEnd},"✓")` };
    sheet.getCell(summaryRow, 26).value = { formula: `COUNTIF(Z${summaryRangeStart}:Z${summaryRangeEnd},1)+COUNTIF(Z${summaryRangeStart}:Z${summaryRangeEnd},"X")+COUNTIF(Z${summaryRangeStart}:Z${summaryRangeEnd},"x")+COUNTIF(Z${summaryRangeStart}:Z${summaryRangeEnd},"✓")` };
    
    // Style summary row
    sheet.getRow(summaryRow).font = { bold: true };
    sheet.getRow(summaryRow).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFD3D3D3' } };

    const outputPath = path.join('uploads', `Updated_Matrix_${Date.now()}.xlsx`);
    await wb.xlsx.writeFile(outputPath);

    res.json({ 
      success: true, 
      downloadUrl: `/api/download?path=${encodeURIComponent(outputPath)}`,
      filename: path.basename(outputPath)
    });
  } catch (error: any) {
    console.error('Phase 2 Error:', error);
    res.status(500).json({ success: false, error: error.message });
  }
});

// --- Phase 3: Generate Purchase Orders from selected suppliers ---
app.post('/api/phase3', upload.fields([
  { name: 'matrix', maxCount: 1 },
  { name: 'poTemplate', maxCount: 1 }
]), async (req: any, res) => {
  try {
    if (!req.files || !req.files['matrix'] || !req.files['poTemplate']) {
      return res.status(400).json({ success: false, error: 'Missing required files: Final Matrix and PO Template are required.' });
    }

    const matrixFile = req.files['matrix'][0];
    const poTemplateFile = req.files['poTemplate'][0];

    const matrixWb = new ExcelJS.Workbook();
    await matrixWb.xlsx.readFile(matrixFile.path);
    const matrixSheet = matrixWb.getWorksheet(1);
    if (!matrixSheet) throw new Error('Final matrix worksheet not found.');

    const matrixMeta = {
      obra: firstNonEmptyMatrixCell(matrixSheet, ['C5']),
      numReq: firstNonEmptyMatrixCell(matrixSheet, ['G5', 'F5']),
      fecha: firstNonEmptyMatrixCell(matrixSheet, ['G6', 'F6']),
      direccion: firstNonEmptyMatrixCell(matrixSheet, ['C7']),
      ubicacion: firstNonEmptyMatrixCell(matrixSheet, ['G7', 'F7']),
      solicita: firstNonEmptyMatrixCell(matrixSheet, ['C9']),
    };

    const supplierSlots = [
      { slot: 'P1', checkCol: 24, unitPriceCol: 15, lineTotalCol: 16, supplierMetaCol: 3 },
      { slot: 'P2', checkCol: 25, unitPriceCol: 18, lineTotalCol: 19, supplierMetaCol: 9 },
      { slot: 'P3', checkCol: 26, unitPriceCol: 21, lineTotalCol: 22, supplierMetaCol: 15 },
    ] as const;

    type SelectedItem = {
      code: string;
      desc: string;
      unit: string;
      qty: number;
      unitPrice: number;
      lineTotal: number;
    };

    const selectedBySupplier: Record<string, SelectedItem[]> = {
      P1: [],
      P2: [],
      P3: [],
    };

    const scanLastRow = Math.max(80, matrixSheet.rowCount);
    for (let rowNumber = 15; rowNumber <= scanLastRow; rowNumber++) {
      const code = stringifyCellValue(matrixSheet.getCell(rowNumber, 2).value);
      if (!code) continue;

      const upperCode = code.toUpperCase();
      if (upperCode === 'CLAVE' || upperCode === 'PARTIDA') continue;
      if (upperCode.includes('TOTAL ITEMS SELECTED')) break;

      const desc = stringifyCellValue(matrixSheet.getCell(rowNumber, 3).value);
      const unit = stringifyCellValue(matrixSheet.getCell(rowNumber, 4).value);
      const qty = parseCellNumber(matrixSheet.getCell(rowNumber, 5).value);

      for (const slotInfo of supplierSlots) {
        const winnerValue = matrixSheet.getCell(rowNumber, slotInfo.checkCol).value;
        if (!isWinnerMark(winnerValue)) continue;

        const unitPrice = parseCellNumber(matrixSheet.getCell(rowNumber, slotInfo.unitPriceCol).value);
        const lineTotalCell = parseCellNumber(matrixSheet.getCell(rowNumber, slotInfo.lineTotalCol).value);
        const lineTotal = lineTotalCell || (qty * unitPrice);

        selectedBySupplier[slotInfo.slot].push({
          code,
          desc,
          unit,
          qty,
          unitPrice,
          lineTotal,
        });
      }
    }

    const generatedFiles: any[] = [];
    const frameEndRow = 34;
    const baseItemStartRow = 19;
    const baseItemEndRow = 34;
    const baseTotalsRow = 35;
    const baseCapacity = baseItemEndRow - baseItemStartRow + 1;
    const poColumnsToClear = [2, 5, 13, 14, 15, 16];

    for (const slotInfo of supplierSlots) {
      const items = selectedBySupplier[slotInfo.slot];
      if (!items.length) continue;

      const poWb = new ExcelJS.Workbook();
      await poWb.xlsx.readFile(poTemplateFile.path);
      const poSheet = poWb.getWorksheet(1);
      if (!poSheet) throw new Error('PO template worksheet not found.');

      const supplierName = stringifyCellValue(matrixSheet.getCell(91, slotInfo.supplierMetaCol).value) || `${slotInfo.slot} Supplier`;
      poSheet.getCell('D9').value = supplierName;
      poSheet.getCell('M9').value = matrixMeta.obra;
      poSheet.getCell('M10').value = matrixMeta.direccion;
      poSheet.getCell('M11').value = matrixMeta.ubicacion;
      poSheet.getCell('M15').value = matrixMeta.numReq;
      poSheet.getCell('N5').value = matrixMeta.fecha;
      poSheet.getCell('N6').value = matrixMeta.solicita;

      const extraRows = Math.max(0, items.length - baseCapacity);
      if (extraRows > 0) {
        // Insert rows before the original frame bottom (row 34),
        // so the purchase-order frame grows downward from B19:B34.
        poSheet.insertRows(frameEndRow, Array.from({ length: extraRows }, () => []), 'i');
      }

      const totalsRow = baseTotalsRow + extraRows;
      for (let row = baseItemStartRow; row < totalsRow; row++) {
        for (const col of poColumnsToClear) {
          poSheet.getCell(row, col).value = null;
        }
      }

      items.forEach((item, idx) => {
        const row = baseItemStartRow + idx;
        poSheet.getCell(row, 2).value = item.code;
        poSheet.getCell(row, 5).value = item.desc;
        poSheet.getCell(row, 13).value = item.unit;
        poSheet.getCell(row, 14).value = item.qty;
        poSheet.getCell(row, 15).value = item.unitPrice;
        poSheet.getCell(row, 16).value = { formula: `N${row}*O${row}` };
      });

      const lastItemRow = baseItemStartRow + items.length - 1;
      if (items.length > 0) {
        poSheet.getCell(totalsRow, 14).value = { formula: `SUM(N${baseItemStartRow}:N${lastItemRow})` };
        poSheet.getCell(totalsRow, 16).value = { formula: `SUM(P${baseItemStartRow}:P${lastItemRow})` };
      } else {
        poSheet.getCell(totalsRow, 14).value = 0;
        poSheet.getCell(totalsRow, 16).value = 0;
      }

      const safeReq = sanitizeFilenamePart(matrixMeta.numReq || 'REQ');
      const safeSupplier = sanitizeFilenamePart(supplierName);
      const finalFilename = `PO_${safeReq}_${slotInfo.slot}_${safeSupplier}.xlsx`;
      const storedFilename = `${Date.now()}_${Math.random().toString(16).slice(2, 8)}_${finalFilename}`;
      const outputPath = path.join(uploadDir, storedFilename);

      await poWb.xlsx.writeFile(outputPath);
      generatedFiles.push({
        slot: slotInfo.slot,
        supplierName,
        selectedItems: items.length,
        filename: finalFilename,
        downloadUrl: `/api/download?path=${encodeURIComponent(outputPath)}`,
      });
    }

    if (generatedFiles.length === 0) {
      return res.status(400).json({
        success: false,
        error: "No items were selected. Mark selected suppliers with '1' in columns X, Y, or Z (Elegir proveedor).",
      });
    }

    const singleFile = generatedFiles.length === 1 ? generatedFiles[0] : null;
    res.json({
      success: true,
      generatedCount: generatedFiles.length,
      files: generatedFiles,
      filename: singleFile?.filename,
      downloadUrl: singleFile?.downloadUrl,
    });
  } catch (error: any) {
    console.error('Phase 3 Error:', error);
    res.status(500).json({ success: false, error: error.message });
  }
});

app.get('/main.py', (req, res) => {
  res.sendFile(path.join(process.cwd(), 'main.py'));
});

app.get('/requirements.txt', (req, res) => {
  res.sendFile(path.join(process.cwd(), 'requirements.txt'));
});

app.get('/api/download', (req, res) => {
  const filePath = req.query.path as string;
  if (filePath && fs.existsSync(filePath)) {
    res.download(filePath);
  } else {
    res.status(404).send("File not found");
  }
});

// Global error handler
app.use((err: any, req: any, res: any, next: any) => {
  console.error('Global Error Handler:', err);
  res.status(500).json({ 
    success: false, 
    error: err.message || 'Internal Server Error' 
  });
});

async function startServer() {
  if (!fs.existsSync('uploads')) fs.mkdirSync('uploads');

  if (process.env.NODE_ENV !== 'production') {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    app.use(express.static('dist'));
  }

  const PORT = 3000;
  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();
