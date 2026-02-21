import React, { useState } from 'react';
import { 
  FileSpreadsheet, 
  FileText, 
  CheckCircle2, 
  Download, 
  Upload, 
  AlertCircle,
  ChevronRight,
  Settings,
  Terminal,
  FileCode,
  ShieldAlert,
  RefreshCw
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

type Phase = 1 | 2 | 'python';

export default function App() {
  const [activePhase, setActivePhase] = useState<Phase>(1);
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any>(null);
  const [securityError, setSecurityError] = useState(false);

  const handlePhaseChange = (phase: Phase) => {
    setActivePhase(phase);
    setResults(null);
  };

  const handleFileUpload = async (phase: Phase, formData: FormData) => {
    console.log(`Starting Phase ${phase} upload...`);
    setLoading(true);
    setResults(null);
    setSecurityError(false);
    
    // Add a timeout to the fetch request
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 120000); // Increased to 120 seconds

    try {
      const response = await fetch(`/api/phase${phase}`, {
        method: 'POST',
        body: formData,
        signal: controller.signal,
      });
      
      clearTimeout(timeoutId);
      console.log(`Phase ${phase} response received:`, response.status);
      
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        const data = await response.json();
        console.log(`Phase ${phase} data:`, data);
        if (data.success) {
          setResults(data);
        } else {
          alert('Error: ' + data.error);
        }
      } else {
        const text = await response.text();
        if (text.includes("Cookie check") || text.includes("auth_flow") || text.includes("Action required")) {
          setSecurityError(true);
        } else {
          console.error('Server returned non-JSON response:', text);
          alert('Server error: Received HTML instead of JSON. This usually means a 404 or 500 error occurred on the server.');
        }
      }
    } catch (error: any) {
      clearTimeout(timeoutId);
      if (error.name === 'AbortError') {
        alert('Request timed out. The file might be too large or the server is busy. Please try again with fewer files.');
      } else {
        console.error(`Phase ${phase} fetch error:`, error);
        alert('Failed to connect to the server. Please check your internet connection.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#E4E3E0] text-[#141414] font-sans flex">
      {/* Sidebar */}
      <aside className="w-64 border-r border-[#141414] flex flex-col">
        <div className="p-6 border-bottom border-[#141414]">
          <h1 className="font-serif italic text-2xl font-bold tracking-tight">ProcureFlow AI</h1>
          <p className="text-[10px] uppercase tracking-widest opacity-50 mt-1">Supply Chain Automation</p>
        </div>

        <nav className="flex-1 py-6">
          <NavItem 
            icon={<FileSpreadsheet size={18} />} 
            label="Phase 1: Matrix" 
            active={activePhase === 1} 
            onClick={() => handlePhaseChange(1)} 
          />
          <NavItem 
            icon={<FileText size={18} />} 
            label="Phase 2: Extract" 
            active={activePhase === 2} 
            onClick={() => handlePhaseChange(2)} 
          />
          <div className="mt-8 pt-8 border-t border-[#141414]/10">
            <NavItem 
              icon={<Terminal size={18} />} 
              label="Python Tool (.exe)" 
              active={activePhase === 'python'} 
              onClick={() => handlePhaseChange('python')} 
            />
          </div>
        </nav>

        <div className="p-6 border-t border-[#141414]">
          <div className="flex items-center gap-3 opacity-50 text-[11px] uppercase tracking-wider">
            <Settings size={14} />
            <span>System Ready</span>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">
        <header className="h-16 border-b border-[#141414] flex items-center justify-between px-8 bg-white/50 backdrop-blur-sm">
          <div className="flex items-center gap-2 text-sm font-mono">
            <span className="opacity-50">Workflow /</span>
            <span>{activePhase === 'python' ? 'Local Executable' : `Phase ${activePhase}`}</span>
          </div>
          <div className="flex items-center gap-4">
            <div className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="text-[10px] font-mono uppercase tracking-widest">Live Server</span>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-8">
          {securityError ? (
            <motion.div 
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="max-w-md mx-auto bg-white border border-[#141414] p-12 text-center space-y-6"
            >
              <div className="w-20 h-20 bg-[#141414]/5 rounded-full flex items-center justify-center mx-auto">
                <ShieldAlert className="w-10 h-10 text-[#141414]" />
              </div>
              <div className="space-y-2">
                <h2 className="text-2xl font-serif italic">Security Check Required</h2>
                <p className="text-sm text-[#141414]/60">
                  Your browser session has expired or is blocking security cookies. This is required by the platform to process your files securely.
                </p>
              </div>
              <div className="flex flex-col gap-3">
                <button 
                  onClick={() => window.location.reload()}
                  className="w-full py-4 bg-[#141414] text-white font-mono text-xs uppercase tracking-widest hover:opacity-90 transition-opacity flex items-center justify-center gap-2"
                >
                  <RefreshCw className="w-4 h-4" />
                  Refresh Page
                </button>
                <button 
                  onClick={() => setSecurityError(false)}
                  className="w-full py-4 border border-[#141414] font-mono text-xs uppercase tracking-widest hover:bg-[#141414]/5 transition-colors"
                >
                  Dismiss
                </button>
              </div>
              <p className="text-[10px] text-[#141414]/40 italic">
                Tip: If you are in Incognito/Private mode, please try a normal window.
              </p>
            </motion.div>
          ) : (
            <AnimatePresence mode="wait">
            <motion.div
              key={activePhase}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.2 }}
              className="max-w-4xl mx-auto"
            >
              {activePhase === 1 && <Phase1 onUpload={(fd) => handleFileUpload(1, fd)} loading={loading} results={results} />}
              {activePhase === 2 && <Phase2 onUpload={(fd) => handleFileUpload(2, fd)} loading={loading} results={results} />}
              {activePhase === 'python' && <PythonSection />}
            </motion.div>
          </AnimatePresence>
          )}
        </div>
      </main>
    </div>
  );
}

function NavItem({ icon, label, active, onClick }: { icon: React.ReactNode, label: string, active: boolean, onClick: () => void }) {
  return (
    <button
      onClick={onClick}
      className={cn(
        "w-full flex items-center gap-3 px-6 py-3 text-sm transition-all duration-200 group",
        active ? "bg-[#141414] text-[#E4E3E0]" : "hover:bg-[#141414]/5"
      )}
    >
      <span className={cn("transition-transform duration-200", active ? "scale-110" : "group-hover:scale-110")}>
        {icon}
      </span>
      <span className="font-medium tracking-tight">{label}</span>
      {active && <ChevronRight size={14} className="ml-auto opacity-50" />}
    </button>
  );
}

function Phase1({ onUpload, loading, results }: any) {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    onUpload(formData);
  };

  return (
    <div className="space-y-8">
      <div className="space-y-2">
        <h2 className="text-4xl font-serif italic">Generate Comparison Matrix</h2>
        <p className="text-[#141414]/60 max-w-2xl">
          Merge your Material Requisition with the Bill of Materials (BOM) to create a structured comparison template.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FileUploadBox name="requisition" label="Requisition (XLSX)" icon={<FileSpreadsheet />} />
        <FileUploadBox name="bom" label="Bill of Materials (XLSX)" icon={<FileSpreadsheet />} />
        <FileUploadBox name="template" label="Matrix Template (XLSX)" icon={<FileSpreadsheet />} className="md:col-span-2" />
        
        <div className="md:col-span-2 pt-4">
          <button
            disabled={loading}
            className="w-full bg-[#141414] text-[#E4E3E0] py-4 rounded-none font-bold uppercase tracking-widest hover:bg-[#141414]/90 disabled:opacity-50 transition-all"
          >
            {loading ? (
              <div className="flex items-center justify-center gap-3">
                <RefreshCw className="w-5 h-5 animate-spin" />
                <span>Processing Matrix & Merging BOM...</span>
              </div>
            ) : (
              'Generate Draft Matrix'
            )}
          </button>
        </div>
      </form>

      {results && <ResultBox results={results} />}
    </div>
  );
}

function Phase2({ onUpload, loading, results }: any) {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    onUpload(formData);
  };

  return (
    <div className="space-y-8">
      <div className="space-y-2">
        <h2 className="text-4xl font-serif italic">Extract Supplier Quotes</h2>
        <p className="text-[#141414]/60 max-w-2xl">
          Upload your Draft Matrix, the BOM for matching, and up to 3 Supplier PDFs. 
          The system will automatically assign them to P1, P2, and P3.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <FileUploadBox name="matrix" label="Draft Matrix (XLSX)" icon={<FileSpreadsheet />} />
          <FileUploadBox name="bom" label="BOM (XLSX)" icon={<FileSpreadsheet />} />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FileUploadBox name="pdfs" label="Supplier PDF 1 (P1)" icon={<FileText />} />
          <FileUploadBox name="pdfs" label="Supplier PDF 2 (P2)" icon={<FileText />} />
          <FileUploadBox name="pdfs" label="Supplier PDF 3 (P3)" icon={<FileText />} />
        </div>
        
        <button
          disabled={loading}
          className="w-full bg-[#141414] text-[#E4E3E0] py-4 rounded-none font-bold uppercase tracking-widest hover:bg-[#141414]/90 disabled:opacity-50 transition-all"
        >
          {loading ? 'Extracting PDF Data...' : 'Extract & Populate Matrix'}
        </button>
      </form>

      {results && <ResultBox results={results} />}
    </div>
  );
}

function PythonSection() {
  return (
    <div className="space-y-8">
      <div className="space-y-2">
        <h2 className="text-4xl font-serif italic">Local Desktop Application</h2>
        <p className="text-[#141414]/60 max-w-2xl">
          Download the source code and instructions to build your own standalone Windows executable (.exe) using Python and CustomTkinter.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 bg-white border border-[#141414] space-y-4">
          <div className="flex items-center gap-3 text-[#141414]">
            <FileCode size={24} />
            <h3 className="font-bold">Source Code</h3>
          </div>
          <p className="text-sm opacity-70">Complete Python implementation with CustomTkinter GUI and robust extraction logic.</p>
          <div className="flex flex-col gap-2">
            <a href="/main.py" download className="flex items-center justify-between p-3 bg-[#141414]/5 hover:bg-[#141414] hover:text-white transition-all text-sm font-mono">
              <span>main.py</span>
              <Download size={14} />
            </a>
            <a href="/requirements.txt" download className="flex items-center justify-between p-3 bg-[#141414]/5 hover:bg-[#141414] hover:text-white transition-all text-sm font-mono">
              <span>requirements.txt</span>
              <Download size={14} />
            </a>
          </div>
        </div>

        <div className="p-6 bg-white border border-[#141414] space-y-4">
          <div className="flex items-center gap-3 text-[#141414]">
            <Terminal size={24} />
            <h3 className="font-bold">Build Command</h3>
          </div>
          <p className="text-sm opacity-70">Run this command in your terminal to generate the standalone .exe file.</p>
          <div className="p-3 bg-[#141414] text-[#E4E3E0] font-mono text-xs overflow-x-auto">
            pyinstaller --noconsole --onefile --name "ProcureFlow" main.py
          </div>
          <div className="flex items-center gap-2 text-[10px] uppercase tracking-widest opacity-50">
            <AlertCircle size={12} />
            <span>Requires Python 3.x and pip install -r requirements.txt</span>
          </div>
        </div>
      </div>
    </div>
  );
}

function FileUploadBox({ name, label, icon, className }: { name: string, label: string, icon: React.ReactNode, className?: string }) {
  const [fileName, setFileName] = useState<string | null>(null);

  return (
    <div className={cn("relative group", className)}>
      <label className="block text-[10px] uppercase tracking-widest opacity-50 mb-2 font-bold">{label}</label>
      <div className="relative h-32 border border-dashed border-[#141414]/30 group-hover:border-[#141414] transition-all bg-white/30 flex flex-col items-center justify-center cursor-pointer">
        <input 
          type="file" 
          name={name}
          className="absolute inset-0 opacity-0 cursor-pointer z-10" 
          onChange={(e) => setFileName(e.target.files?.[0]?.name || null)}
        />
        <div className="flex flex-col items-center gap-2 opacity-40 group-hover:opacity-100 transition-opacity">
          {icon}
          <span className="text-xs font-mono">{fileName || 'Click or drag to upload'}</span>
        </div>
      </div>
    </div>
  );
}

function ResultBox({ results }: { results: any }) {
  return (
    <motion.div 
      initial={{ opacity: 0, scale: 0.98 }}
      animate={{ opacity: 1, scale: 1 }}
      className="p-6 bg-emerald-50 border border-emerald-500/30 flex items-center justify-between"
    >
      <div className="flex items-center gap-4">
        <div className="h-10 w-10 rounded-full bg-emerald-500 flex items-center justify-center text-white">
          <CheckCircle2 size={24} />
        </div>
        <div>
          <h4 className="font-bold text-emerald-900">Process Complete</h4>
          <p className="text-sm text-emerald-700 font-mono">
            {results.addedCount !== undefined ? `${results.addedCount} items processed. ` : ''}
            {results.filename || 'Files generated successfully'}
          </p>
          {results.addedCount === 0 && (
            <p className="text-xs text-amber-600 font-bold mt-1">
              Warning: 0 items were found in the requisition. Check your file format.
            </p>
          )}
        </div>
      </div>
      {results.downloadUrl && (
        <a 
          href={results.downloadUrl} 
          className="flex items-center gap-2 bg-emerald-600 text-white px-6 py-3 font-bold uppercase tracking-widest text-xs hover:bg-emerald-700 transition-all"
        >
          <Download size={16} />
          Download Result
        </a>
      )}
    </motion.div>
  );
}
