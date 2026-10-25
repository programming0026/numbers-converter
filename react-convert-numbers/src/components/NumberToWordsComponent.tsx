import React, { useState, useEffect } from 'react';
import { Calculator, Copy, CheckCircle } from 'lucide-react';

type LanguageCode = 'es' | 'en';
type ModeCode = 'toWords' | 'toNumber';

const LANGUAGE_LIMITS: Record<LanguageCode, { min: bigint; max: bigint }> = {
  es: { min: -999999999999999n, max: 999999999999999n },
  en: { min: -9223372036854775807n, max: 9223372036854775807n },
};

const WORDS_PATTERNS: Record<LanguageCode, RegExp> = {
  es: /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+(?:[ ][A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)*$/,
  en: /^[A-Za-z]+(?:[ -][A-Za-z]+)*$/,
};

const WORDS_MAX_LENGTH = 300;

const CONTENT = {
  es: {
    heroTitle: 'Convertidor de',
    heroHighlight: 'Números y Letras',
    heroSubtitle: 'Convierte números a letras y letras a números de forma instantánea',
    languageLabel: 'Idioma',
    modeLabel: 'Modo',
    modeToWords: 'Número → Letras',
    modeToNumber: 'Letras → Número',
    inputLabel: 'Ingresa tu número',
    wordsInputLabel: 'Ingresa el número en letras',
    placeholder: 'Ej: 12345',
    wordsPlaceholder: 'Ej: dos mil veinticuatro',
    rangeText: (min: string, max: string) => `Rango: ${min} a ${max}`,
    wordsHint: `Solo letras y espacios (máx. ${WORDS_MAX_LENGTH} caracteres)`,
    outOfRange: 'Número fuera del rango permitido',
    invalidNumber: 'Ingresa un número entero válido',
    invalidWords: 'El texto solo puede contener letras en español y espacios simples',
    errorPrefix: 'Error',
    genericError: 'Ocurrió un error al contactar el servicio',
    resultLabel: 'Resultado',
    copyLabel: 'Copiar',
    copiedLabel: '¡Copiado!',
    clearLabel: 'Limpiar',
    examples: [
      { num: '123', texto: 'ciento veintitrés' },
      { num: '1000', texto: 'mil' },
      { num: '2024', texto: 'dos mil veinticuatro' },
    ],
    howTitle: '¿Cómo funciona?',
    bulletsLeft: [
      '✨ Convierte números enteros de gran magnitud',
      '🔢 Admite números positivos y negativos',
      '📱 Interfaz completamente responsiva',
    ],
    bulletsRight: [
      '⚡ Conversión en tiempo real',
      '🔁 Funciona en ambos sentidos: número ↔ letras',
      '🎯 Siguiendo las reglas del español',
    ],
    footer: 'Convertidor de números a letras',
  },
  en: {
    heroTitle: 'Numbers &',
    heroHighlight: 'Words Converter',
    heroSubtitle: 'Convert numbers to words and words to numbers instantly',
    languageLabel: 'Language',
    modeLabel: 'Mode',
    modeToWords: 'Number → Words',
    modeToNumber: 'Words → Number',
    inputLabel: 'Enter your number',
    wordsInputLabel: 'Enter the number in words',
    placeholder: 'E.g.: 12345',
    wordsPlaceholder: 'E.g.: two thousand twenty-four',
    rangeText: (min: string, max: string) => `Range: ${min} to ${max}`,
    wordsHint: `Only letters, spaces and hyphens (max. ${WORDS_MAX_LENGTH} characters)`,
    outOfRange: 'Number out of the allowed range',
    invalidNumber: 'Enter a valid integer number',
    invalidWords: 'The text may only contain English letters, single spaces and hyphens',
    errorPrefix: 'Error',
    genericError: 'An error occurred while contacting the service',
    resultLabel: 'Result',
    copyLabel: 'Copy',
    copiedLabel: 'Copied!',
    clearLabel: 'Clear',
    examples: [
      { num: '123', texto: 'one hundred twenty-three' },
      { num: '1000', texto: 'one thousand' },
      { num: '2024', texto: 'two thousand twenty-four' },
    ],
    howTitle: 'How does it work?',
    bulletsLeft: [
      '✨ Converts very large integer numbers',
      '🔢 Supports positive and negative numbers',
      '📱 Fully responsive interface',
    ],
    bulletsRight: [
      '⚡ Real-time conversion',
      '🔁 Works both ways: number ↔ words',
      '🎯 Following English grammar rules',
    ],
    footer: 'Number to words converter',
  },
} as const;

const parseValidInteger = (value: string): bigint | null => {
  if (!/^-?\d+$/.test(value)) {
    return null;
  }
  try {
    return BigInt(value);
  } catch {
    return null;
  }
};

const normalizeIntegerLiteral = (value: string): string => {
  const negative = value.startsWith('-');
  const digits = (negative ? value.slice(1) : value).replace(/^0+(?=\d)/, '');
  return negative && digits !== '0' ? `-${digits}` : digits;
};

const extractNumberLiteral = (rawBody: string, fallback: unknown): string => {
  const match = rawBody.match(/"number"\s*:\s*(-?\d+)/);
  return match?.[1] ?? String(fallback ?? '');
};

const ConvertNumberToWords = () => {
  const [idioma, setIdioma] = useState<LanguageCode>('es');
  const [modo, setModo] = useState<ModeCode>('toWords');
  const [entrada, setEntrada] = useState('');
  const [resultado, setResultado] = useState('');
  const [copiado, setCopiado] = useState(false);

  const texto = CONTENT[idioma];
  const { min, max } = LANGUAGE_LIMITS[idioma];
  const maxDigits = max.toString().length;

  const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:9090/api';

  const sendNumberToWords = async (numberLiteral: string) => {
    const apiUrl = `${baseUrl}/v1/numbers/${idioma}/convert`;
    const normalized = normalizeIntegerLiteral(numberLiteral);

    try {
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: `{"number":${normalized}}`,
      });

      const data = await response.json();
      if (response.ok) {
        setResultado(data.words);
      } else {
        setResultado(`${texto.errorPrefix}: ${data.message}`);
      }
    } catch (err) {
      console.error('Error al contactar el servicio:', err);
      setResultado(`${texto.errorPrefix}: ${texto.genericError}`);
    }
  };

  const sendWordsToNumber = async (text: string) => {
    const apiUrl = `${baseUrl}/v1/numbers/${idioma}/to-number`;

    try {
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ text }),
      });

      const rawBody = await response.text();
      let parsedBody: { message?: string; number?: number } | null = null;
      try {
        parsedBody = JSON.parse(rawBody);
      } catch {
        parsedBody = null;
      }

      if (response.ok) {
        setResultado(extractNumberLiteral(rawBody, parsedBody?.number));
      } else {
        setResultado(`${texto.errorPrefix}: ${parsedBody?.message ?? texto.genericError}`);
      }
    } catch (err) {
      console.error('Error al contactar el servicio:', err);
      setResultado(`${texto.errorPrefix}: ${texto.genericError}`);
    }
  };

  useEffect(() => {
    const valor = entrada.trim();
    if (valor === '' || valor === '-') {
      setResultado('');
      return;
    }

    if (modo === 'toWords') {
      const parsed = parseValidInteger(valor);
      if (parsed === null) {
        setResultado(texto.invalidNumber);
        return;
      }
      if (parsed < min || parsed > max) {
        setResultado(texto.outOfRange);
        return;
      }
      sendNumberToWords(valor);
    } else {
      if (!WORDS_PATTERNS[idioma].test(valor)) {
        setResultado(texto.invalidWords);
        return;
      }
      sendWordsToNumber(valor);
    }

  }, [entrada, idioma, modo]);

  const copiarTexto = async () => {
    try {
      await navigator.clipboard.writeText(resultado);
      setCopiado(true);
      setTimeout(() => setCopiado(false), 2000);
    } catch (err) {
      console.error('Error al copiar:', err);
    }
  };

  const limpiar = () => {
    setEntrada('');
    setResultado('');
    setCopiado(false);
  };

  const cambiarIdioma = (nuevoIdioma: LanguageCode) => {
    if (nuevoIdioma === idioma) {
      return;
    }
    setIdioma(nuevoIdioma);
    setEntrada('');
    setResultado('');
    setCopiado(false);
  };

  const cambiarModo = (nuevoModo: ModeCode) => {
    if (nuevoModo === modo) {
      return;
    }
    setModo(nuevoModo);
    setEntrada('');
    setResultado('');
    setCopiado(false);
  };

  const handleEntradaChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const valor = e.target.value;
    if (modo === 'toWords') {
      const digitos = valor.replace('-', '');
      if (digitos.length > maxDigits) {
        return;
      }
    } else if (valor.length > WORDS_MAX_LENGTH) {
      return;
    }
    setEntrada(valor);
  };

  const handleEjemploClick = (ejemplo: { num: string; texto: string }) => {
    setEntrada(modo === 'toWords' ? ejemplo.num : ejemplo.texto);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50">
      {/* Header con efectos visuales */}
      <div className="relative overflow-hidden bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600">
        <div className="absolute inset-0 bg-black opacity-10"></div>
        <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent transform -skew-y-1"></div>
        <div className="relative max-w-4xl mx-auto px-6 py-16 text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full mb-6">
            <Calculator className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-4xl md:text-6xl font-bold text-white mb-4 tracking-tight">
            {texto.heroTitle}
            <span className="block bg-gradient-to-r from-yellow-200 to-orange-200 bg-clip-text text-transparent">
              {texto.heroHighlight}
            </span>
          </h1>
          <p className="text-xl text-blue-100 max-w-2xl mx-auto leading-relaxed">
            {texto.heroSubtitle}
          </p>
        </div>
      </div>

      {/* Contenido principal */}
      <div className="max-w-4xl mx-auto px-6 py-12">
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 overflow-hidden">
          <div className="p-8 md:p-12">

            {/* Selectores de idioma y modo */}
            <div className="mb-8 flex flex-col md:flex-row gap-8">
              <div>
                <label className="block text-lg font-semibold text-gray-800 mb-4">
                  {texto.languageLabel}
                </label>
                <div className="inline-flex rounded-xl border-2 border-gray-200 overflow-hidden">
                  <button
                    type="button"
                    onClick={() => cambiarIdioma('es')}
                    className={`px-6 py-2 font-medium transition-colors duration-200 ${
                      idioma === 'es'
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    Español
                  </button>
                  <button
                    type="button"
                    onClick={() => cambiarIdioma('en')}
                    className={`px-6 py-2 font-medium transition-colors duration-200 ${
                      idioma === 'en'
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    English
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-lg font-semibold text-gray-800 mb-4">
                  {texto.modeLabel}
                </label>
                <div className="inline-flex rounded-xl border-2 border-gray-200 overflow-hidden">
                  <button
                    type="button"
                    onClick={() => cambiarModo('toWords')}
                    className={`px-6 py-2 font-medium transition-colors duration-200 ${
                      modo === 'toWords'
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    {texto.modeToWords}
                  </button>
                  <button
                    type="button"
                    onClick={() => cambiarModo('toNumber')}
                    className={`px-6 py-2 font-medium transition-colors duration-200 ${
                      modo === 'toNumber'
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    {texto.modeToNumber}
                  </button>
                </div>
              </div>
            </div>

            {/* Input Section */}
            <div className="mb-8">
              <label htmlFor="entrada" className="block text-lg font-semibold text-gray-800 mb-4">
                {modo === 'toWords' ? texto.inputLabel : texto.wordsInputLabel}
              </label>
              <div className="relative">
                <input
                  id="entrada"
                  type="text"
                  inputMode={modo === 'toWords' ? 'numeric' : 'text'}
                  value={entrada}
                  onChange={handleEntradaChange}
                  placeholder={modo === 'toWords' ? texto.placeholder : texto.wordsPlaceholder}
                  className="w-full px-6 py-4 text-2xl font-medium bg-gray-50 border-2 border-gray-200 rounded-2xl focus:border-indigo-500 focus:ring-4 focus:ring-indigo-200 focus:bg-white transition-all duration-300 outline-none"
                  maxLength={modo === 'toWords' ? maxDigits + 1 : WORDS_MAX_LENGTH}
                />
                <div className="absolute inset-y-0 right-0 flex items-center pr-6">
                  <div className="w-2 h-2 bg-indigo-500 rounded-full animate-pulse"></div>
                </div>
              </div>
              <p className="text-sm text-gray-500 mt-2">
                {modo === 'toWords' ? texto.rangeText(min.toString(), max.toString()) : texto.wordsHint}
              </p>
            </div>

            {/* Resultado */}
            {resultado && (
              <div className="mb-8">
                <div className="flex items-center justify-between mb-4">
                  <label className="text-lg font-semibold text-gray-800">
                    {texto.resultLabel}
                  </label>
                  <button
                    onClick={copiarTexto}
                    className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl transition-all duration-200 transform hover:scale-105 focus:ring-4 focus:ring-indigo-200"
                  >
                    {copiado ? (
                      <>
                        <CheckCircle className="w-4 h-4" />
                        {texto.copiedLabel}
                      </>
                    ) : (
                      <>
                        <Copy className="w-4 h-4" />
                        {texto.copyLabel}
                      </>
                    )}
                  </button>
                </div>
                <div className="bg-gradient-to-r from-indigo-50 to-purple-50 border-2 border-indigo-200 rounded-2xl p-6">
                  <p
                    className={`text-xl md:text-2xl font-medium text-gray-800 leading-relaxed ${
                      modo === 'toWords' ? 'capitalize' : ''
                    }`}
                  >
                    {resultado}
                  </p>
                </div>
              </div>
            )}

            {/* Botón limpiar */}
            {entrada && (
              <div className="flex justify-center">
                <button
                  onClick={limpiar}
                  className="px-8 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium rounded-xl transition-all duration-200 transform hover:scale-105"
                >
                  {texto.clearLabel}
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Ejemplos */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
          {texto.examples.map((ejemplo, index) => (
            <div
              key={index}
              onClick={() => handleEjemploClick(ejemplo)}
              className="bg-white/60 backdrop-blur-sm rounded-2xl p-6 border border-white/30 hover:bg-white/80 hover:shadow-lg transition-all duration-300 cursor-pointer group"
            >
              <div className="text-center">
                <div className="text-2xl font-bold text-indigo-600 mb-2 group-hover:scale-110 transition-transform duration-200">
                  {ejemplo.num}
                </div>
                <div className="text-gray-600 capitalize font-medium">
                  {ejemplo.texto}
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Información adicional */}
        <div className="mt-12 bg-white/60 backdrop-blur-sm rounded-2xl p-8 border border-white/30">
          <h3 className="text-xl font-bold text-gray-800 mb-4 text-center">
            {texto.howTitle}
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-gray-600">
            <div className="space-y-3">
              {texto.bulletsLeft.map((bullet, index) => (
                <p key={index}>{bullet}</p>
              ))}
            </div>
            <div className="space-y-3">
              {texto.bulletsRight.map((bullet, index) => (
                <p key={index}>{bullet}</p>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Footer decorativo */}
      <div className="text-center py-8 text-gray-500">
        <p className="text-sm">
          {texto.footer} • Copyright &copy; {new Date().getFullYear()}
        </p>
      </div>
    </div>
  );
};

export default ConvertNumberToWords;
