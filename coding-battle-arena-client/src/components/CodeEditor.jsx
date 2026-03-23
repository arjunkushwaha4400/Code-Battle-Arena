// components/CodeEditor.jsx
import React, { useRef, useCallback } from 'react';

const LANGS = [
  { value: 'JAVA',       label: 'Java'       },
  { value: 'PYTHON',     label: 'Python'     },
  { value: 'JAVASCRIPT', label: 'JavaScript' },
];

// ── Syntax rules ──────────────────────────────────────────────────────────────
const RULES = {
  JAVA: [
    { re: /(\/\/[^\n]*)/g,   cls: 'sh-comment' },
    { re: /("(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')/g, cls: 'sh-string' },
    { re: /\b(public|private|protected|static|final|void|class|interface|extends|implements|new|return|if|else|for|while|do|switch|case|break|continue|try|catch|finally|throw|throws|import|package|this|super|null|true|false|int|long|double|float|boolean|char|byte|short|String|var|instanceof)\b/g, cls: 'sh-keyword' },
    { re: /\b([A-Z][a-zA-Z0-9_]*)\b/g, cls: 'sh-class' },
    { re: /\b([a-z_][a-zA-Z0-9_]*)\s*(?=\()/g, cls: 'sh-fn' },
    { re: /\b(\d+\.?\d*[fFdDlL]?)\b/g, cls: 'sh-number' },
  ],
  PYTHON: [
    { re: /(#[^\n]*)/g,   cls: 'sh-comment' },
    { re: /("""[\s\S]*?"""|'''[\s\S]*?'''|"(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')/g, cls: 'sh-string' },
    { re: /\b(def|class|import|from|as|return|if|elif|else|for|while|in|not|and|or|is|lambda|with|try|except|finally|raise|pass|break|continue|yield|None|True|False|self|print|range|len|type|int|str|float|list|dict|set|tuple|super|global|nonlocal)\b/g, cls: 'sh-keyword' },
    { re: /\b([A-Z][a-zA-Z0-9_]*)\b/g, cls: 'sh-class' },
    { re: /\b([a-z_][a-zA-Z0-9_]*)\s*(?=\()/g, cls: 'sh-fn' },
    { re: /\b(\d+\.?\d*)\b/g, cls: 'sh-number' },
  ],
  JAVASCRIPT: [
    { re: /(\/\/[^\n]*|\/\*[\s\S]*?\*\/)/g, cls: 'sh-comment' },
    { re: /(`(?:[^`\\]|\\.)*`|"(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')/g, cls: 'sh-string' },
    { re: /\b(const|let|var|function|class|extends|new|return|if|else|for|while|do|switch|case|break|continue|try|catch|finally|throw|import|export|from|default|async|await|of|in|typeof|instanceof|null|undefined|true|false|this|super|static|get|set|Promise|console|require)\b/g, cls: 'sh-keyword' },
    { re: /\b([A-Z][a-zA-Z0-9_]*)\b/g, cls: 'sh-class' },
    { re: /\b([a-z_][a-zA-Z0-9_]*)\s*(?=\()/g, cls: 'sh-fn' },
    { re: /\b(\d+\.?\d*)\b/g, cls: 'sh-number' },
  ],
};

function escHtml(s) {
  return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

function highlight(code, lang) {
  const rules = RULES[lang] || RULES.JAVA;
  const esc   = escHtml(code);
  const hits  = [];

  rules.forEach(({ re, cls }) => {
    const r = new RegExp(re.source, re.flags);
    let m;
    while ((m = r.exec(esc)) !== null) {
      hits.push({ start: m.index, end: m.index + m[0].length, text: m[0], cls });
    }
  });

  hits.sort((a, b) => a.start - b.start);

  let out = '', cur = 0;
  for (const { start, end, text, cls } of hits) {
    if (start < cur) continue;
    out += esc.slice(cur, start);
    out += `<span class="${cls}">${text}</span>`;
    cur  = end;
  }
  out += esc.slice(cur);
  return out + '\n';
}

// ── Inject CSS once ───────────────────────────────────────────────────────────
let _injected = false;
function injectCss() {
  if (_injected || typeof document === 'undefined') return;
  _injected = true;
  const s = document.createElement('style');
  s.textContent = `
.ce-root{position:relative;display:flex;flex-direction:column;height:100%;background:#0b0d12}
.ce-head{display:flex;align-items:center;justify-content:space-between;padding:8px 14px;background:#13161f;border-bottom:1px solid #1e2535;flex-shrink:0}
.ce-label{font-size:10px;font-weight:700;color:#64748b;letter-spacing:.1em;text-transform:uppercase}
.ce-select{background:#0b0d12;border:1px solid #1e2535;color:#94a3b8;border-radius:5px;padding:3px 10px;font-size:12px;font-weight:600;outline:none;font-family:inherit;cursor:pointer}
.ce-select:disabled{cursor:not-allowed;opacity:.5}
.ce-body{position:relative;flex:1;overflow:hidden}
.ce-hi,.ce-ta{
  position:absolute;inset:0;
  padding:14px 16px;
  font-family:'Cascadia Code','Fira Code','JetBrains Mono','Consolas',monospace;
  font-size:13px;line-height:1.7;
  white-space:pre;overflow:auto;
  word-wrap:normal;overflow-wrap:normal;
  tab-size:4;
  scrollbar-width:thin;scrollbar-color:#1e2535 transparent;
}
.ce-hi{color:#e2e8f0;pointer-events:none;margin:0;border:none;background:transparent;z-index:0}
.ce-ta{color:transparent;caret-color:#60a5fa;background:transparent;border:none;outline:none;resize:none;z-index:1}
.ce-ta:disabled{cursor:not-allowed;opacity:.6}
.ce-ta::selection{background:rgba(96,165,250,.2)}
/* syntax colours */
.sh-keyword{color:#a78bfa;font-weight:600}
.sh-string {color:#34d399}
.sh-comment{color:#3a4d6b;font-style:italic}
.sh-class  {color:#60a5fa}
.sh-fn     {color:#fbbf24}
.sh-number {color:#f87171}
  `;
  document.head.appendChild(s);
}

// ── Component ─────────────────────────────────────────────────────────────────
function CodeEditor({ code, language, onChange, onLanguageChange, disabled = false }) {
  injectCss();
  const taRef  = useRef(null);
  const hiRef  = useRef(null);

  const syncScroll = useCallback(() => {
    if (taRef.current && hiRef.current) {
      hiRef.current.scrollTop  = taRef.current.scrollTop;
      hiRef.current.scrollLeft = taRef.current.scrollLeft;
    }
  }, []);

  const handleKeyDown = useCallback((e) => {
    if (e.key !== 'Tab') return;
    e.preventDefault();
    const ta    = e.target;
    const start = ta.selectionStart;
    const end   = ta.selectionEnd;
    const next  = code.substring(0, start) + '    ' + code.substring(end);
    onChange(next);
    requestAnimationFrame(() => { ta.selectionStart = ta.selectionEnd = start + 4; });
  }, [code, onChange]);

  return (
    <div className="ce-root">
      {/* Header */}
      <div className="ce-head">
        <span className="ce-label">Code Editor</span>
        <select className="ce-select" value={language} onChange={e => onLanguageChange(e.target.value)} disabled={disabled}>
          {LANGS.map(l => (
            <option key={l.value} value={l.value} style={{ background: '#0f1219', color: '#e2e8f0' }}>
              {l.label}
            </option>
          ))}
        </select>
      </div>

      {/* Editor */}
      <div className="ce-body">
        {/* highlighted layer — sits behind, pointer-events none */}
        <pre
          ref={hiRef}
          className="ce-hi"
          aria-hidden="true"
          dangerouslySetInnerHTML={{ __html: highlight(code, language) }}
        />
        {/* transparent textarea on top — captures all input */}
        <textarea
          ref={taRef}
          className="ce-ta"
          value={code}
          onChange={e => onChange(e.target.value)}
          onKeyDown={handleKeyDown}
          onScroll={syncScroll}
          disabled={disabled}
          placeholder="Write your code here..."
          spellCheck={false}
          autoComplete="off"
          autoCorrect="off"
          autoCapitalize="off"
        />
      </div>
    </div>
  );
}

export default CodeEditor;