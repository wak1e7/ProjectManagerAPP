import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member, ProjectRoleName } from './models/member.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TeamService {
  private base = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  listMembers(projectId: number): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.base}/${projectId}/members`);
  }

  addOrInviteMember(
    projectId: number,
    email: string,
    role: ProjectRoleName
  ): Observable<void> {
    const params = new HttpParams()
      .set('email', email)
      .set('role', role);
    return this.http.post<void>(
      `${this.base}/${projectId}/members`,
      null,
      { params }
    );
  }

  changeRole(
    projectId: number,
    userId: number,
    role: ProjectRoleName
  ): Observable<void> {
    const params = new HttpParams().set('role', role);
    return this.http.put<void>(
      `${this.base}/${projectId}/members/${userId}/roles`,
      null,
      { params }
    );
  }

    removeMember(projectId: number, userId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.base}/${projectId}/members/${userId}`
    );
  }
}
