import { useState, useEffect } from 'react';
import { clasificarContenido, obtenerHistorial } from '../services/api';

export default function ClassifyTab() {
  const [titulo, setTitulo] = useState('');
  const [texto, setTexto] = useState('');
  const [loading, setLoading] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState('');
  
  // Historial y Paginación UI
  const [historial, setHistorial] = useState([]);
  const [categoriaFiltro, setCategoriaFiltro] = useState('');
  const [visibles, setVisibles] = useState(5); // <-- Límite inicial de 5 ítems

  const cargarHistorial = async () => {
    try {
      const data = await obtenerHistorial(categoriaFiltro);
      setHistorial(data);
    } catch (err) {
      console.error("Error al cargar historial:", err);
    }
  };

  useEffect(() => {
    cargarHistorial();
  }, [categoriaFiltro]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const res = await clasificarContenido(titulo, texto);
      setResultado(res);
      setVisibles(5); // Resetea la vista a 5 al agregar uno nuevo
      cargarHistorial();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleVerMas = () => {
    setVisibles((prev) => prev + 5); // Carga de 5 en 5
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      {/* TÍTULO PRINCIPAL */}
      <div className="text-center space-y-2">
        <h1 className="text-4xl font-extrabold text-white">
          Organize your <span className="bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">technical knowledge</span>
        </h1>
        <p className="text-gray-400 text-sm">
          Paste any document and get its category, confidence score and keywords.
        </p>
      </div>

      {/* FORMULARIO */}
      <form onSubmit={handleSubmit} className="bg-[#111726] p-6 rounded-2xl border border-gray-800 space-y-4">
        <div>
          <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Title</label>
          <input 
            type="text" 
            placeholder="Paste the title"
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
            className="w-full bg-[#0a0e17] border border-gray-800 rounded-lg p-3 text-white placeholder-gray-600 focus:outline-none focus:border-blue-500"
            required
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Content</label>
          <textarea 
            rows="4"
            placeholder="Paste the text you want to classify (at least 10 chars)"
            value={texto}
            onChange={(e) => setTexto(e.target.value)}
            className="w-full bg-[#0a0e17] border border-gray-800 rounded-lg p-3 text-white placeholder-gray-600 focus:outline-none focus:border-blue-500"
            required
          />
        </div>

        {error && (
          <div className="text-red-400 text-sm bg-red-950/40 p-3 rounded-lg border border-red-800">
            {error}
          </div>
        )}

        <button 
          type="submit" 
          disabled={loading}
          className="bg-gradient-to-r from-blue-500 to-emerald-400 text-slate-950 font-bold px-6 py-2.5 rounded-full hover:opacity-90 transition disabled:opacity-50 cursor-pointer"
        >
          {loading ? 'Classifying...' : 'Classify'}
        </button>

        {/* TARJETA RESULTADO INFERENCIA */}
        {resultado && (
          <div className="mt-6 pt-6 border-t border-gray-800 space-y-4">
            <div className="flex justify-between items-center">
              <h2 className="text-3xl font-bold bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">
                {resultado.categoria}
              </h2>
              <span className="text-2xl font-bold text-emerald-400">
                {(resultado.probabilidad * 100).toFixed(0)}%
              </span>
            </div>

            <div className="w-full bg-gray-800 h-2 rounded-full overflow-hidden">
              <div 
                className="bg-gradient-to-r from-blue-500 to-emerald-400 h-full transition-all duration-500"
                style={{ width: `${resultado.probabilidad * 100}%` }}
              />
            </div>

            <div className="flex flex-wrap gap-2 pt-2">
              {resultado.informacion_adicional?.map((tag, idx) => (
                <span key={idx} className="bg-[#1a2333] text-gray-300 text-xs px-3 py-1.5 rounded-full border border-gray-700">
                  {tag}
                </span>
              ))}
            </div>
          </div>
        )}
      </form>

      {/* SECCIÓN HISTORIAL (HISTORIA HU-10) */}
      <div className="space-y-4">
        <div className="flex justify-between items-center">
          <h3 className="text-xl font-bold text-white">Classified</h3>
          <select 
            value={categoriaFiltro}
            onChange={(e) => setCategoriaFiltro(e.target.value)}
            className="bg-[#111726] border border-gray-800 text-gray-300 text-sm rounded-lg px-3 py-1.5 focus:outline-none"
          >
            <option value="">All categories</option>
            <option value="Backend">Backend</option>
            <option value="Frontend">Frontend</option>
            <option value="Data Science">Data Science</option>
          </select>
        </div>

        <div className="space-y-3">
          {historial.slice(0, visibles).map((item) => (
            <div key={item.id} className="bg-[#111726] border border-gray-800/80 rounded-xl p-4 flex justify-between items-center">
              <div>
                <h4 className="text-white font-semibold text-sm">{item.titulo}</h4>
                <p className="text-xs text-gray-500">{item.categoria}</p>
              </div>
              <span className="text-emerald-400 font-bold text-sm">
                {(item.probabilidad * 100).toFixed(0)}%
              </span>
            </div>
          ))}
        </div>

        {/* BOTÓN VER MÁS SI HAY MÁS DE 5 ELEMENTOS */}
        {historial.length > visibles && (
          <div className="text-center pt-2">
            <button
              onClick={handleVerMas}
              className="bg-[#111726] hover:bg-gray-800 text-gray-300 text-xs font-semibold px-5 py-2.5 rounded-full border border-gray-800 transition"
            >
              Show more ({historial.length - visibles} remaining)
            </button>
          </div>
        )}
      </div>
    </div>
  );
}