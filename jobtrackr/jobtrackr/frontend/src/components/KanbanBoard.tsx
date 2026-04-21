import { JobApplication, ApplicationStatus, STATUS_LABELS, STATUS_COLORS, PRIORITY_COLORS } from '../types';

interface Props {
  apps:    JobApplication[];
  loading: boolean;
  onEdit:  (app: JobApplication) => void;
}

const STAGES: ApplicationStatus[] = [
  'APPLIED','PHONE_SCREEN','TECHNICAL','ON_SITE','OFFER','REJECTED','GHOSTED'
];

function fmtDate(d: string) {
  return new Date(d).toLocaleDateString('en-IN', { day: '2-digit', month: 'short' });
}

export default function KanbanBoard({ apps, loading, onEdit }: Props) {
  if (loading) return <div className="loading-state">Loading...</div>;

  return (
    <div className="kanban-grid">
      {STAGES.map(stage => {
        const stageApps = apps.filter(a => a.status === stage);
        return (
          <div key={stage} className="kanban-col">
            <div className="kanban-col-header">
              <span className="kanban-col-title">{STATUS_LABELS[stage]}</span>
              <span className="kanban-count" style={{ color: STATUS_COLORS[stage] }}>{stageApps.length}</span>
            </div>
            {stageApps.length === 0
              ? <div className="kanban-empty">Empty</div>
              : stageApps.map(app => (
                  <div key={app.id} className="kanban-card" onClick={() => onEdit(app)}>
                    <div className="kcard-company">{app.company}</div>
                    <div className="kcard-role">{app.role}</div>
                    {app.tech.length > 0 && (
                      <div className="kcard-tech">
                        {app.tech.slice(0, 2).map(t => <span key={t} className="kcard-tag">{t}</span>)}
                      </div>
                    )}
                    <div className="kcard-footer">
                      <span className="kcard-date">{app.dateApplied ? fmtDate(app.dateApplied) : ''}</span>
                      <span className="priority-dot" style={{ background: PRIORITY_COLORS[app.priority] }} />
                    </div>
                  </div>
                ))
            }
          </div>
        );
      })}
    </div>
  );
}
