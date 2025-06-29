import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpEvent, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Document } from './models/document.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private base = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  list(projectId: number): Observable<Document[]> {
    return this.http.get<Document[]>(`${this.base}/${projectId}/documents`);
  }

  upload(projectId: number, file: File): Observable<Document> { 
    const form = new FormData();
    form.append('file', file);
    return this.http.post<Document>(
      `${this.base}/${projectId}/documents`,
      form
    );
  }

  delete(projectId: number, documentId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.base}/${projectId}/documents/${documentId}`
    );
  }

  update(
    projectId: number,
    documentId: number,
    updates: Partial<Pick<Document, 'title' | 'category' | 'updatedAt' | 'version'>>
  ): Observable<Document> {
    return this.http.patch<Document>(
      `${this.base}/${projectId}/documents/${documentId}`,
      updates
    );
  }

  download(projectId: number, documentId: number): Observable<Blob> {
    return this.http.get(
      `${this.base}/${projectId}/documents/${documentId}`,
      { responseType: 'blob' }
    );
  }
}