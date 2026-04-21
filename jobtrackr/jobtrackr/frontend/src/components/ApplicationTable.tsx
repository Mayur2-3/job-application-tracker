import { useState } from 'react';
import { JobApplication, STATUS_LABELS, STATUS_COLORS, PRIORITY_COLORS } from '../types';

interface Props {
  apps:    JobApplication[];
  loading: boolean;
  onEdit:  (app: JobApplication) => void;
}

type SortKey = 'company' | 'status' | 'priority' | 'dateApplied' | 'salaryMin';
type SortDir = 'asc' | 'desc';

function initials(name: string) {
  return name.split(/\s+/).slice(0, 2).map(w => w[0]?.toUpperCase() ?? '').join('');
}

const AVATAR_COLORS = ['#7c6af7','#3fcf8e','#5ba3f5','#f5a623','#e879a0','#38d9a9','#f06060','#a394ff'];
function avatarColor(name: string) { return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]; }

const TECH_COLORS: Record<string, string> = {
  Java: '#7cc84e', SQL: '#5ba3f5', JavaScript: '#f5d76e',
  'REST API': '#38d9a9', OOP: '#a394ff', 'Spring Boot': '#6abf69',
  TypeScript: '#3178c6', React: '#61dafb',
};

export default function ApplicationTable({ apps, loading, onEdit }: Props) {
  const [sortKey, setSortKey] = useState<SortKey>('dateApplied');
  const [sortDir, setSortDir] = useState<SortDir>('desc');

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) setSortDir(d => d === 'asc' ? 'desc' : 'asc');
    else { setSortKey(key); setSortDir('desc'); }
  };

  const sorted = [...apps].sort((a, b) => {
    let va: any = a[sortKey], vb: any = b[sortKey];
    if (sortKey === 'dateApplied') { va = new Date(va); vb = new Date(vb); }
    const cmp = va > vb ? 1 : va < vb ? -1 : 0;
    return sortDir === 'asc' ? cmp : -cmp;
  });

  const arrow = (key: SortKey) => (
    <span className="sort-arrow">{sortKey === key ? (sortDir === 'asc' ? '▲' : '▼') : '⇅'}</span>
  );

  if (loading) return <div className="loading-state">Loading applications...</div>;
  if (!apps.length) return <div className="empty-state"><div className="empty-icon">📭</div><div>No applications found</div></div>;

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th onClick={() => toggleSort('company')} className={sortKey === 'company' ? 'sorted' : ''}>Company {arrow('company')}</th>
            <th onClick={() => toggleSort('status')}  className={sortKey === 'status'  ? 'sorted' : ''}>Status  {arrow('status')}</th>
            <th onClick={() => toggleSort('priority')} className={sortKey === 'priority' ? 'sorted' : ''}>Pri {arrow('priority')}</th>
            <th>Tech Stack</th>
            <th onClick={() => toggleSort('salaryMin')} className={sortKey === 'salaryMin' ? 'sorted' : ''}>Salary {arrow('salaryMin')}</th>
            <th onClick={() => toggleSort('dateApplied')} className={sortKey === 'dateApplied' ? 'sorted' : ''}>Applied {arrow('dateApplied')}</th>
            <th>Source</th>
            <th>Next Step</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {sorted.map(app => (
            <tr key={app.id} onClick={() => onEdit(app)} style={{ cursor: 'pointer' }}>
              <td>
                <div className="company-cell">
                  <div className="avatar" style={{ background: avatarColor(app.company) + '22', color: avatarColor(app.company) }}>
                    {initials(app.company)}
                  </div>
                  <div>
                    <div className="company-name">{app.company}</div>
                    <div className="company-role">{app.role}</div>
                  </div>
                </div>
              </td>
              <td>
                <span className="status-badge" style={{ background: STATUS_COLORS[app.status] + '20', color: STATUS_COLORS[app.status] }}>
                  <span className="badge-dot" style={{ background: STATUS_COLORS[app.status] }} />
                  {STATUS_LABELS[app.status]}
                </span>
              </td>
              <td>
                <span className="priority-dot" style={{ background: PRIORITY_COLORS[app.priority] }} title={app.priority} />
              </td>
              <td>
                {app.tech.slice(0, 3).map(t => (
                  <span key={t} className="tech-tag" style={{ color: TECH_COLORS[t] ?? '#7a7a90', background: (TECH_COLORS[t] ?? '#7a7a90') + '18' }}>
                    {t}
                  </span>
                ))}
                {app.tech.length > 3 && <span className="tech-tag">+{app.tech.length - 3}</span>}
              </td>
              <td className="salary-cell">{app.salaryRange ?? (app.salaryMin ? `₹${app.salaryMin}–${app.salaryMax} L` : '—')}</td>
              <td className="date-cell">{app.dateApplied ? new Date(app.dateApplied).toLocaleDateString('en-IN', { day: '2-digit', month: 'short' }) : '—'}</td>
              <td><span className="source-pill">{app.source ?? '—'}</span></td>
              <td className="next-cell">{app.nextStep ?? '—'}</td>
              <td onClick={e => e.stopPropagation()}>
                <button className="icon-btn" onClick={() => onEdit(app)}>✎</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
