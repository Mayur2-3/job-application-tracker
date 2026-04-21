// ─── Enums (mirror Java enums) ───────────────────────────────────────────────

export type ApplicationStatus =
  | 'APPLIED' | 'PHONE_SCREEN' | 'TECHNICAL'
  | 'ON_SITE'  | 'OFFER'        | 'REJECTED'
  | 'WITHDRAWN'| 'GHOSTED';

export type Priority   = 'HIGH' | 'MEDIUM' | 'LOW';
export type RemoteType = 'REMOTE' | 'HYBRID' | 'ON_SITE';

// ─── Response DTO ─────────────────────────────────────────────────────────────

export interface JobApplication {
  id:             number;
  company:        string;
  role:           string;
  status:         ApplicationStatus;
  priority:       Priority;
  dateApplied:    string;          // ISO date
  salaryMin:      number | null;
  salaryMax:      number | null;
  salaryCurrency: string;
  salaryRange:    string | null;
  location:       string | null;
  remoteType:     RemoteType;
  source:         string | null;
  jdUrl:          string | null;
  tech:           string[];
  notes:          string | null;
  nextStep:       string | null;
  followUpDate:   string | null;
  recruiterName:  string | null;
  recruiterEmail: string | null;
  active:         boolean;
  createdAt:      string;
  updatedAt:      string;
}

// ─── Request DTOs ─────────────────────────────────────────────────────────────

export interface CreateRequest {
  company:        string;
  role:           string;
  status?:        ApplicationStatus;
  priority?:      Priority;
  dateApplied?:   string;
  salaryMin?:     number;
  salaryMax?:     number;
  salaryCurrency?:string;
  location?:      string;
  remoteType?:    RemoteType;
  source?:        string;
  jdUrl?:         string;
  tech?:          string[];
  notes?:         string;
  nextStep?:      string;
  followUpDate?:  string;
  recruiterName?: string;
  recruiterEmail?:string;
}

export type UpdateRequest = Partial<CreateRequest>;

// ─── Stats ────────────────────────────────────────────────────────────────────

export interface StatsResponse {
  total:        number;
  active:       number;
  offers:       number;
  rejected:     number;
  responseRate: number | null;
  byStatus:     Record<string, number>;
  byPriority:   Record<string, number>;
}

// ─── Paginated response ───────────────────────────────────────────────────────

export interface PageResponse<T> {
  content:       T[];
  page:          number;
  size:          number;
  totalElements: number;
  totalPages:    number;
  last:          boolean;
}

// ─── UI helpers ───────────────────────────────────────────────────────────────

export const STATUS_LABELS: Record<ApplicationStatus, string> = {
  APPLIED:      'Applied',
  PHONE_SCREEN: 'Phone Screen',
  TECHNICAL:    'Technical',
  ON_SITE:      'On-site',
  OFFER:        'Offer',
  REJECTED:     'Rejected',
  WITHDRAWN:    'Withdrawn',
  GHOSTED:      'Ghosted',
};

export const STATUS_COLORS: Record<ApplicationStatus, string> = {
  APPLIED:      '#5ba3f5',
  PHONE_SCREEN: '#f5a623',
  TECHNICAL:    '#a394ff',
  ON_SITE:      '#e879a0',
  OFFER:        '#3fcf8e',
  REJECTED:     '#f06060',
  WITHDRAWN:    '#7a7a90',
  GHOSTED:      '#50505e',
};

export const PRIORITY_COLORS: Record<Priority, string> = {
  HIGH:   '#f06060',
  MEDIUM: '#f5a623',
  LOW:    '#50505e',
};

export const TECH_OPTIONS = [
  'Java', 'Spring Boot', 'SQL', 'JavaScript', 'TypeScript',
  'REST API', 'OOP', 'React', 'Node.js', 'PostgreSQL',
  'Hibernate', 'Maven', 'Git', 'Docker',
];

export const SOURCE_OPTIONS = [
  'LinkedIn', 'Naukri', 'Company Website',
  'Referral', 'Indeed', 'AngelList', 'Other',
];
