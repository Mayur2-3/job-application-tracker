import { StatsResponse } from '../types';

interface Props {
  stats:          StatsResponse | null;
  onFilterStatus: (s: string) => void;
  activeStatus:   string;
}

export default function StatsBar({ stats, onFilterStatus, activeStatus }: Props) {
  const items = [
    { label: 'Total',      value: stats?.total        ?? '—', filter: '',         color: '#a394ff' },
    { label: 'Applied',    value: stats?.byStatus?.APPLIED ?? '—', filter: 'APPLIED',  color: '#5ba3f5' },
    { label: 'Active',     value: stats?.active       ?? '—', filter: '',         color: '#f5a623' },
    { label: 'Offers',     value: stats?.offers       ?? '—', filter: 'OFFER',    color: '#3fcf8e' },
    { label: 'Response %', value: stats?.responseRate != null ? stats.responseRate + '%' : '—', filter: '', color: '#38d9a9' },
  ];

  return (
    <div className="stats-row">
      {items.map(item => (
        <div
          key={item.label}
          className={`stat-card ${activeStatus === item.filter && item.filter ? 'stat-active' : ''}`}
          onClick={() => item.filter && onFilterStatus(activeStatus === item.filter ? '' : item.filter)}
          style={{ cursor: item.filter ? 'pointer' : 'default' }}
        >
          <div className="stat-num" style={{ color: item.color }}>{item.value}</div>
          <div className="stat-label">{item.label}</div>
        </div>
      ))}
    </div>
  );
}
