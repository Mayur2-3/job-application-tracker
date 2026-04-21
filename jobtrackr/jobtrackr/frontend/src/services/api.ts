import axios, { AxiosInstance } from 'axios';
import {
  JobApplication, CreateRequest, UpdateRequest,
  StatsResponse, PageResponse
} from '../types';

/**
 * api.ts – All REST API calls to the Spring Boot backend.
 *
 * Uses Axios with a base instance so the base URL is configured once.
 * Mirrors the REST endpoints in JobApplicationController.java.
 */

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

const http: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
});

// ─── Request interceptor (add auth token if needed later) ─────────────────
http.interceptors.request.use(config => {
  // const token = localStorage.getItem('token');
  // if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// ─── Response interceptor (normalise errors) ──────────────────────────────
http.interceptors.response.use(
  res => res,
  err => {
    const msg = err.response?.data?.message ?? err.message ?? 'Unknown error';
    return Promise.reject(new Error(msg));
  }
);

// ─────────────────────────────────────────────────────────────────────────────

export interface ListParams {
  q?:        string;
  status?:   string;
  priority?: string;
  page?:     number;
  size?:     number;
  sort?:     string;
}

const api = {

  // GET /applications  (paginated + filtered)
  list: (params: ListParams = {}) =>
    http.get<PageResponse<JobApplication>>('/applications', { params })
        .then(r => r.data),

  // GET /applications/:id
  get: (id: number) =>
    http.get<JobApplication>(`/applications/${id}`).then(r => r.data),

  // POST /applications
  create: (body: CreateRequest) =>
    http.post<JobApplication>('/applications', body).then(r => r.data),

  // PATCH /applications/:id
  update: (id: number, body: UpdateRequest) =>
    http.patch<JobApplication>(`/applications/${id}`, body).then(r => r.data),

  // DELETE /applications/:id
  remove: (id: number) =>
    http.delete(`/applications/${id}`).then(r => r.data),

  // GET /applications/search?q=
  search: (q: string) =>
    http.get<JobApplication[]>('/applications/search', { params: { q } }).then(r => r.data),

  // GET /applications/stats
  stats: () =>
    http.get<StatsResponse>('/applications/stats').then(r => r.data),

  // GET /applications/follow-ups
  followUps: () =>
    http.get<JobApplication[]>('/applications/follow-ups').then(r => r.data),

  // GET /applications/by-tech?tech=
  byTech: (tech: string) =>
    http.get<JobApplication[]>('/applications/by-tech', { params: { tech } }).then(r => r.data),

  // GET /applications/by-date-range?from=&to=
  byDateRange: (from: string, to: string) =>
    http.get<JobApplication[]>('/applications/by-date-range', { params: { from, to } }).then(r => r.data),
};

export default api;
