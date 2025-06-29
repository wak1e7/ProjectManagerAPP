export type ProjectRoleName = 'PROJECT_ADMIN' | 'PROJECT_MEMBER';

export interface Member {
  userId: number;
  email: string;
  role: ProjectRoleName;
}