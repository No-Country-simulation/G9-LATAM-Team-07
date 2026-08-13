import { useState } from 'react';

export default function AskDocsTab() {
  const [query, setQuery] = useState('');
  const [busquedaActiva, setBusquedaActiva] = useState(false);

  // Base de datos de documentación simulada
  const allDocs = [
    { title: "Staging environment setup runbook", category: "DevOps", match: "92%", keywords: ["deployment", "server", "docker"] },
    { title: "Payment service deployment checklist", category: "DevOps", match: "88%", keywords: ["deployment", "api", "cloud"] },
    { title: "Kubernetes cluster upgrade guide", category: "DevOps", match: "85%", keywords: ["deployment", "kubernetes"] },
    { title: "PostgreSQL index tuning & query optimization", category: "Databases", match: "91%", keywords: ["database", "postgres", "sql"] },
    { title: "Database backup & disaster recovery procedure", category: "Databases", match: "86%", keywords: ["database", "backup", "sql"] },
    { title: "OAuth2 & JWT authentication flow in Spring", category: "Security", match: "94%", keywords: ["authentication", "jwt", "security", "backend"] },
    { title: "React State Management with Redux Toolkit", category: "Frontend", match: "95%", keywords: ["react", "frontend", "state", "next"] },
    { title: "Next.js Server Side Rendering (SSR) patterns", category: "Frontend", match: "89%", keywords: ["react", "next", "frontend", "html"] }
  ];

  // Filtra dinámicamente según la palabra escrita o seleccionada
  const resultadosFiltrados = allDocs.filter(doc => {
    if (!query.trim()) return true;
    const q = query.toLowerCase();
    return (
      doc.title.toLowerCase().includes(q) ||
      doc.category.toLowerCase().includes(q) ||
      doc.keywords.some(k => k.toLowerCase().includes(q))
    );
  });

  const handleSearch = (e) => {
    e?.preventDefault();
    setBusquedaActiva(true);
  };

  const handleTagClick = (tag) => {
    setQuery(tag);
    setBusquedaActiva(true);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div className="text-center space-y-2">
        <h1 className="text-4xl font-extrabold text-white">
          Ask your <span className="bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">own documentation</span>
        </h1>
        <p className="text-gray-400 text-sm">
          Connect a folder or server. Then ask about a topic and get every related document.
        </p>
      </div>

      <div className="bg-[#111726] p-6 rounded-2xl border border-gray-800 space-y-6">
        <div className="flex gap-4">
          <div className="flex-1">
            <label className="text-xs font-semibold text-gray-400 uppercase">Location</label>
            <input 
              type="text" 
              value="/home/team/docs" 
              readOnly 
              className="w-full mt-1 bg-[#0a0e17] border border-gray-800 rounded-lg p-2.5 text-gray-300 text-sm focus:outline-none"
            />
          </div>
          <button className="self-end bg-blue-500/20 text-blue-400 border border-blue-500/30 font-semibold px-4 py-2.5 rounded-lg text-sm">
            Connected (8 docs)
          </button>
        </div>

        <form onSubmit={handleSearch} className="space-y-3">
          <label className="text-xs font-semibold text-gray-400 uppercase">What topic do you need information about?</label>
          <div className="flex gap-2">
            <input 
              type="text" 
              placeholder="Type a topic (e.g. deployment, postgres, react...)"
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                setBusquedaActiva(true);
              }}
              className="flex-1 bg-[#0a0e17] border border-gray-800 rounded-lg p-3 text-white placeholder-gray-600 focus:outline-none focus:border-blue-500"
            />
            <button 
              type="submit"
              className="bg-gradient-to-r from-blue-500 to-emerald-400 text-slate-950 font-bold px-6 py-3 rounded-lg hover:opacity-90 transition cursor-pointer"
            >
              Search
            </button>
          </div>

          {/* Sugerencias de búsqueda rápida */}
          <div className="flex flex-wrap gap-2 pt-1">
            {['deployment', 'database', 'authentication', 'react'].map((tag) => (
              <button 
                key={tag}
                type="button"
                onClick={() => handleTagClick(tag)}
                className={`text-xs px-3 py-1 rounded-full border transition cursor-pointer ${
                  query.toLowerCase() === tag 
                    ? 'bg-blue-500 text-slate-950 border-blue-400 font-bold'
                    : 'bg-[#1a2333] hover:bg-gray-800 text-gray-300 border-gray-700'
                }`}
              >
                {tag}
              </button>
            ))}
          </div>
        </form>

        {/* Muestra los resultados si se activó la búsqueda o se escribió algo */}
        {busquedaActiva && (
          <div className="mt-6 pt-6 border-t border-gray-800 space-y-3">
            <p className="text-sm text-gray-400 font-semibold">
              {resultadosFiltrados.length} document(s) found about "{query || 'all'}"
            </p>

            {resultadosFiltrados.length > 0 ? (
              resultadosFiltrados.map((doc, idx) => (
                <div key={idx} className="bg-[#0a0e17] p-4 rounded-xl border border-gray-800 flex justify-between items-center hover:border-gray-700 transition">
                  <div>
                    <h4 className="text-white font-medium text-sm">{doc.title}</h4>
                    <span className="text-xs text-gray-500">{doc.category}</span>
                  </div>
                  <span className="text-emerald-400 font-bold text-sm">{doc.match}</span>
                </div>
              ))
            ) : (
              <div className="bg-[#0a0e17] p-6 rounded-xl border border-gray-800 text-center text-gray-500 text-sm">
                No indexed documents found matching "{query}". Try searching for <span className="text-gray-300">deployment</span>, <span className="text-gray-300">react</span> or <span className="text-gray-300">database</span>.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}