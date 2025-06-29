import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskStatus } from './models/task.model';
import { environment } from '../../../environments/environment';

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  status?: TaskStatus;
  dueDate?: string;
  assigneeIds?: number[];
}

@Injectable({ providedIn: 'root' })
export class TaskService {
  private base = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  list(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.base}/${projectId}/tasks`);
  }

  getById(projectId: number, taskId: number): Observable<Task> {
    return this.http.get<Task>(`${this.base}/${projectId}/tasks/${taskId}`);
  }

  create(projectId: number, data: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(`${this.base}/${projectId}/tasks`, data);
  }

  update(
    projectId: number,
    taskId: number,
    req: UpdateTaskRequest
  ): Observable<Task> {
    return this.http.put<Task>(
      `${this.base}/${projectId}/tasks/${taskId}`,
      req
    );
  }

  delete(projectId: number, taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${projectId}/tasks/${taskId}`);
  }
}
