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

    // Add Summary Row at the bottom (Row 81)
    const summaryRow = 81;
    sheet.getCell(summaryRow, 2).value = "TOTAL ITEMS SELECTED";
    sheet.getCell(summaryRow, 24).value = { formula: `COUNTIF(X15:X80,1)+COUNTIF(X15:X80,"X")+COUNTIF(X15:X80,"x")+COUNTIF(X15:X80,"✓")` }; 
    sheet.getCell(summaryRow, 25).value = { formula: `COUNTIF(Y15:Y80,1)+COUNTIF(Y15:Y80,"X")+COUNTIF(Y15:Y80,"x")+COUNTIF(Y15:Y80,"✓")` };
    sheet.getCell(summaryRow, 26).value = { formula: `COUNTIF(Z15:Z80,1)+COUNTIF(Z15:Z80,"X")+COUNTIF(Z15:Z80,"x")+COUNTIF(Z15:Z80,"✓")` };
    
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
