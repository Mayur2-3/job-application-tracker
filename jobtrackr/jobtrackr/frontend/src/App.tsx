import { useState } from 'react';
import { Toaster } from 'react-hot-toast';
import StatsBar       from './components/StatsBar';
import Toolbar        from './components/Toolbar';
import ApplicationTable from './components/ApplicationTable';
import KanbanBoard    from './components/KanbanBoard';
import ApplicationModal from './components/ApplicationModal';
import { useApplications, useStats } from './hooks/useApplications';
import { JobApplication } from './types';
import './App.css';

type View = 'table' | 'kanban';

export default function App() {
  const [view,        setView]        = useState<View>('table');
  const [search,      setSearch]      = useState('');
  const [statusFilter,setStatusFilter] = useState('');
  const [priorityFilter,setPriorityFilter] = useState('');
  const [modalOpen,   setModalOpen]   = useState(false);
  const [editing,     setEditing]     = useState<JobApplication | null>(null);

  const params = {
    q:        search        || undefined,
    status:   statusFilter  || undefined,
    priority: priorityFilter || undefined,
    size:     50,
  };

  const { data, loading, error, create, update, remove } = useApplications(params);
  const { stats } = useStats();

  const openAdd  = () => { setEditing(null); setModalOpen(true); };
  const openEdit = (app: JobApplication) => { setEditing(app); setModalOpen(true); };
  const closeModal = () => { setModalOpen(false); setEditing(null); };

  const handleSave = async (body: any) => {
    if (editing) {
      await update(editing.id, body);
    } else {
      await create(body);
    }
    closeModal();
  };

  const handleDelete = async (id: number) => {
    await remove(id);
    closeModal();
  };

  const apps = data?.content ?? [];

  return (
    <div className="app">
      <Toaster position="top-right" toastOptions={{ style: { background: '#1e1e24', color: '#e8e8f0', border: '1px solid #2e2e38' } }} />

      <header className="topbar">
        <div className="topbar-left">
          <div className="logo-dot" />
          <h1 className="logo-title">JobTrackr</h1>
          <span className="badge-tag">Oct 2025</span>
        </div>
        <div className="topbar-right">
          <span className="tech-hint">Java · Spring Boot · SQL · REST API · React</span>
          <button className="btn-primary" onClick={openAdd}>+ Add Application</button>
        </div>
      </header>

      <StatsBar stats={stats} onFilterStatus={setStatusFilter} activeStatus={statusFilter} />

      <Toolbar
        search={search}        onSearch={setSearch}
        status={statusFilter}  onStatus={setStatusFilter}
        priority={priorityFilter} onPriority={setPriorityFilter}
        view={view}            onView={setView}
      />

      <main className="content">
        {error && <div className="error-banner">⚠ {error} — is the Spring Boot server running on :8080?</div>}

        {view === 'table' ? (
          <ApplicationTable apps={apps} loading={loading} onEdit={openEdit} />
        ) : (
          <KanbanBoard apps={apps} loading={loading} onEdit={openEdit} />
        )}
      </main>

      {modalOpen && (
        <ApplicationModal
          app={editing}
          onSave={handleSave}
          onDelete={handleDelete}
          onClose={closeModal}
        />
      )}
    </div>
  );
}
