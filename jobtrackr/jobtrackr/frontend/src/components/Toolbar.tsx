interface Props {
  search:     string; onSearch:   (v: string) => void;
  status:     string; onStatus:   (v: string) => void;
  priority:   string; onPriority: (v: string) => void;
  view:       string; onView:     (v: any)    => void;
}

export default function Toolbar({ search, onSearch, status, onStatus, priority, onPriority, view, onView }: Props) {
  return (
    <div className="toolbar">
      <div className="search-wrap">
        <span className="search-icon">⌕</span>
        <input
          type="text"
          placeholder="Search company, role, tech..."
          value={search}
          onChange={e => onSearch(e.target.value)}
          className="search-input"
        />
      </div>

      <select value={status} onChange={e => onStatus(e.target.value)} className="filter-select">
        <option value="">All statuses</option>
        <option value="APPLIED">Applied</option>
        <option value="PHONE_SCREEN">Phone Screen</option>
        <option value="TECHNICAL">Technical</option>
        <option value="ON_SITE">On-site</option>
        <option value="OFFER">Offer</option>
        <option value="REJECTED">Rejected</option>
        <option value="WITHDRAWN">Withdrawn</option>
        <option value="GHOSTED">Ghosted</option>
      </select>

      <select value={priority} onChange={e => onPriority(e.target.value)} className="filter-select">
        <option value="">All priorities</option>
        <option value="HIGH">High</option>
        <option value="MEDIUM">Medium</option>
        <option value="LOW">Low</option>
      </select>

      <div className="view-tabs">
        {(['table', 'kanban'] as const).map(v => (
          <button key={v} className={`view-tab ${view === v ? 'active' : ''}`} onClick={() => onView(v)}>
            {v.charAt(0).toUpperCase() + v.slice(1)}
          </button>
        ))}
      </div>
    </div>
  );
}
