import { useState, useEffect, useCallback } from 'react';
import api, { ListParams } from '../services/api';
import { JobApplication, StatsResponse, PageResponse } from '../types';
import toast from 'react-hot-toast';

/**
 * useApplications – Custom hook encapsulating all data-fetching logic.
 *
 * OOP / React principle: separates "data concerns" from "UI concerns",
 * mirroring the Service layer pattern from the Java backend.
 */
export function useApplications(params: ListParams = {}) {
  const [data,    setData]    = useState<PageResponse<JobApplication> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState<string | null>(null);

  const fetch = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await api.list(params);
      setData(result);
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(params)]);

  useEffect(() => { fetch(); }, [fetch]);

  const create = async (body: Parameters<typeof api.create>[0]) => {
    const saved = await api.create(body);
    toast.success(`Added: ${saved.company}`);
    await fetch();
    return saved;
  };

  const update = async (id: number, body: Parameters<typeof api.update>[1]) => {
    const saved = await api.update(id, body);
    toast.success('Updated successfully');
    await fetch();
    return saved;
  };

  const remove = async (id: number) => {
    await api.remove(id);
    toast.success('Application deleted');
    await fetch();
  };

  return { data, loading, error, refresh: fetch, create, update, remove };
}

/** Hook for dashboard statistics */
export function useStats() {
  const [stats,   setStats]   = useState<StatsResponse | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    api.stats()
       .then(setStats)
       .catch(() => {})
       .finally(() => setLoading(false));
  }, []);

  return { stats, loading };
}
