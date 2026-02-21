# ProcureFlow AI

ProcureFlow AI automates procurement in 3 phases:

1. **Phase 1: Matrix**
   - Upload Requisition + BOM + Matrix template.
   - Generates a Draft Matrix.
2. **Phase 2: Extract**
   - Upload Draft Matrix + BOM + up to 3 supplier PDFs.
   - Populates supplier pricing columns (P1, P2, P3).
3. **Phase 3: POs**
   - Upload Final Matrix + `Formato de Orden de Compra` template.
   - Reads supplier selection marks (`1`) in **Elegir proveedor** (X/Y/Z).
   - Generates **one PO per supplier**, including all selected items.
   - If selected items exceed template rows, rows are added automatically.

## Run locally (web app)

Prerequisite: Node.js 20+ recommended.

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the app:
   ```bash
   npm run dev
   ```
3. Open:
   - `http://localhost:3000`

## Desktop `.exe` option (for non-technical staff)

If your team prefers a standalone Windows app, use the Python desktop tool:

1. Install Python 3.x
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Build executable:
   ```bash
   pyinstaller --noconsole --onefile --name "ProcureFlow" main.py
   ```
4. Share:
   - `dist/ProcureFlow.exe`

This path is useful for offline usage and simpler distribution inside your company.
