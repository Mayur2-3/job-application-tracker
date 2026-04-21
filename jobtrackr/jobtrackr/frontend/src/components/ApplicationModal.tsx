import { useState, useEffect } from 'react';
import { JobApplication, CreateRequest, SOURCE_OPTIONS, TECH_OPTIONS } from '../types';

interface Props {
  app:      JobApplication | null;
  onSave:   (body: CreateRequest) => Promise<void>;
  onDelete: (id: number) => Promise<void>;
  onClose:  () => void;
}

const EMPTY: CreateRequest = {
  company: '', role: '', status: 'APPLIED', priority: 'MEDIUM',
  dateApplied: new Date().toISOString().split('T')[0],
  salaryCurrency: 'INR', remoteType: 'HYBRID', source: 'LinkedIn', tech: [],
};

export default function ApplicationModal({ app, onSave, onDelete, onClose }: Props) {
  const [form, setForm] = useState<CreateRequest>(EMPTY);
  const [saving, setSaving] = useState(false);
  const [techInput, setTechInput] = useState('');

  useEffect(() => {
    if (app) {
      setForm({
        company: app.company, role: app.role, status: app.status, priority: app.priority,
        dateApplied: app.dateApplied, salaryMin: app.salaryMin ?? undefined,
        salaryMax: app.salaryMax ?? undefined, salaryCurrency: app.salaryCurrency,
        location: app.location ?? '', remoteType: app.remoteType, source: app.source ?? 'LinkedIn',
        jdUrl: app.jdUrl ?? '', tech: app.tech, notes: app.notes ?? '',
        nextStep: app.nextStep ?? '', followUpDate: app.followUpDate ?? undefined,
        recruiterName: app.recruiterName ?? '', recruiterEmail: app.recruiterEmail ?? '',
      });
    } else {
      setForm({ ...EMPTY, dateApplied: new Date().toISOString().split('T')[0] });
    }
  }, [app]);

  const set = (k: keyof CreateRequest, v: any) => setForm(f => ({ ...f, [k]: v }));

  const addTech = (t: string) => {
    const clean = t.trim();
    if (clean && !form.tech?.includes(clean)) set('tech', [...(form.tech ?? []), clean]);
    setTechInput('');
  };
  const removeTech = (t: string) => set('tech', (form.tech ?? []).filter(x => x !== t));

  const handleSubmit = async () => {
    if (!form.company?.trim() || !form.role?.trim()) { alert('Company and Role are required.'); return; }
    setSaving(true);
    try { await onSave(form); } finally { setSaving(false); }
  };

  const handleDelete = async () => {
    if (!app || !confirm('Delete this application?')) return;
    setSaving(true);
    try { await onDelete(app.id); } finally { setSaving(false); }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <span className="modal-title">{app ? 'Edit Application' : 'Add Application'}</span>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          <div className="form-grid">
            <div className="form-group">
              <label className="form-label">Company *</label>
              <input value={form.company} onChange={e => set('company', e.target.value)} placeholder="e.g. Google" />
            </div>
            <div className="form-group">
              <label className="form-label">Role *</label>
              <input value={form.role} onChange={e => set('role', e.target.value)} placeholder="e.g. Backend Engineer" />
            </div>

            <div className="form-group">
              <label className="form-label">Status</label>
              <select value={form.status} onChange={e => set('status', e.target.value as any)}>
                {['APPLIED','PHONE_SCREEN','TECHNICAL','ON_SITE','OFFER','REJECTED','WITHDRAWN','GHOSTED'].map(s => (
                  <option key={s} value={s}>{s.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Priority</label>
              <select value={form.priority} onChange={e => set('priority', e.target.value as any)}>
                <option value="HIGH">High</option>
                <option value="MEDIUM">Medium</option>
                <option value="LOW">Low</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Date Applied</label>
              <input type="date" value={form.dateApplied ?? ''} onChange={e => set('dateApplied', e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Source</label>
              <select value={form.source} onChange={e => set('source', e.target.value)}>
                {SOURCE_OPTIONS.map(s => <option key={s}>{s}</option>)}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Salary Min (LPA)</label>
              <input type="number" value={form.salaryMin ?? ''} onChange={e => set('salaryMin', e.target.value ? +e.target.value : undefined)} placeholder="e.g. 12" />
            </div>
            <div className="form-group">
              <label className="form-label">Salary Max (LPA)</label>
              <input type="number" value={form.salaryMax ?? ''} onChange={e => set('salaryMax', e.target.value ? +e.target.value : undefined)} placeholder="e.g. 18" />
            </div>

            <div className="form-group">
              <label className="form-label">Location</label>
              <input value={form.location ?? ''} onChange={e => set('location', e.target.value)} placeholder="e.g. Bangalore / Remote" />
            </div>
            <div className="form-group">
              <label className="form-label">Remote Type</label>
              <select value={form.remoteType} onChange={e => set('remoteType', e.target.value as any)}>
                <option value="ON_SITE">On-site</option>
                <option value="HYBRID">Hybrid</option>
                <option value="REMOTE">Remote</option>
              </select>
            </div>

            <div className="form-group form-full">
              <label className="form-label">Tech Stack</label>
              <div className="tech-input-row">
                <input
                  value={techInput}
                  onChange={e => setTechInput(e.target.value)}
                  onKeyDown={e => { if (e.key === 'Enter' || e.key === ',') { e.preventDefault(); addTech(techInput); }}}
                  placeholder="Type + Enter to add"
                  list="tech-options"
                />
                <datalist id="tech-options">
                  {TECH_OPTIONS.map(t => <option key={t} value={t} />)}
                </datalist>
                <button className="btn-ghost" onClick={() => addTech(techInput)}>Add</button>
              </div>
              <div className="tech-tags-row">
                {(form.tech ?? []).map(t => (
                  <span key={t} className="tech-removable" onClick={() => removeTech(t)}>{t} ×</span>
                ))}
              </div>
            </div>

            <div className="form-group form-full">
              <label className="form-label">JD URL</label>
              <input value={form.jdUrl ?? ''} onChange={e => set('jdUrl', e.target.value)} placeholder="https://..." />
            </div>

            <div className="form-group form-full">
              <label className="form-label">Notes</label>
              <textarea value={form.notes ?? ''} onChange={e => set('notes', e.target.value)} placeholder="Interview notes, recruiter info..." rows={3} />
            </div>

            <div className="form-group">
              <label className="form-label">Next Step</label>
              <input value={form.nextStep ?? ''} onChange={e => set('nextStep', e.target.value)} placeholder="e.g. Technical round Nov 3" />
            </div>
            <div className="form-group">
              <label className="form-label">Follow-up Date</label>
              <input type="date" value={form.followUpDate ?? ''} onChange={e => set('followUpDate', e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label">Recruiter Name</label>
              <input value={form.recruiterName ?? ''} onChange={e => set('recruiterName', e.target.value)} placeholder="Name" />
            </div>
            <div className="form-group">
              <label className="form-label">Recruiter Email</label>
              <input type="email" value={form.recruiterEmail ?? ''} onChange={e => set('recruiterEmail', e.target.value)} placeholder="recruiter@company.com" />
            </div>
          </div>
        </div>

        <div className="modal-footer">
          {app && <button className="btn-danger" onClick={handleDelete} disabled={saving}>Delete</button>}
          <button className="btn-ghost" onClick={onClose} disabled={saving}>Cancel</button>
          <button className="btn-primary" onClick={handleSubmit} disabled={saving}>
            {saving ? 'Saving…' : 'Save'}
          </button>
        </div>
      </div>
    </div>
  );
}
