import { useState } from 'react';
import ClassifyTab from './components/ClassifyTab';
import AskDocsTab from './components/AskDocsTab';

export default function App() {
  const [activeTab, setActiveTab] = useState('classify');

  return (
    <div className="min-h-screen bg-[#0a0e17] text-white p-6">
      {/* HEADER PRINCIPAL */}
      <header className="max-w-5xl mx-auto flex justify-between items-center mb-12 border-b border-gray-800 pb-4">
        <div className="flex items-center gap-3">
          <div className="bg-gradient-to-tr from-blue-500 to-emerald-400 text-slate-950 font-black p-2 rounded-xl text-lg">
            TM
          </div>
          <span className="font-bold text-xl tracking-tight">
            TechMind <span className="text-gray-400 font-normal text-sm">Classifier</span>
          </span>
        </div>

        {/* NAVEGACIÓN ENTRE PESTAÑAS */}
        <div className="bg-[#111726] p-1 rounded-full border border-gray-800 flex items-center gap-1">
          <button
            onClick={() => setActiveTab('classify')}
            className={`px-5 py-1.5 rounded-full text-xs font-semibold transition ${
              activeTab === 'classify'
                ? 'bg-gradient-to-r from-blue-500 to-emerald-400 text-slate-950 shadow-md'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            Classify
          </button>
          
          <button
            onClick={() => setActiveTab('ask')}
            className={`px-4 py-1.5 rounded-full text-xs font-semibold flex items-center gap-2 transition ${
              activeTab === 'ask'
                ? 'bg-gradient-to-r from-blue-500 to-emerald-400 text-slate-950 shadow-md'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            Ask your docs
            <span className="bg-gray-800 text-emerald-400 text-[10px] px-1.5 py-0.5 rounded border border-gray-700">
              NEXT
            </span>
          </button>
        </div>
      </header>

      {/* CONTENIDO SEGÚN PESTAÑA */}
      <main className="max-w-5xl mx-auto">
        {activeTab === 'classify' ? <ClassifyTab /> : <AskDocsTab />}
      </main>
    </div>
  );
}