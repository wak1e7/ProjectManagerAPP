import { Routes } from '@angular/router';

import { LoginComponent } from './core/auth/login/login.component';
import { RegisterComponent } from './core/auth/register/register.component';
import { AuthGuard } from './core/auth.guard';

import { ProjectListComponent } from './features/projects/project-list/project-list.component';
import { ProjectFormComponent } from './features/projects/project-form/project-form.component';
import { ProjectDetailComponent } from './features/projects/project-detail/project-detail.component';

import { TaskListComponent } from './features/tasks/task-list/task-list.component';
import { TaskFormComponent } from './features/tasks/task-form/task-form.component';
import { MemberListComponent } from './features/team/member-list/member-list.component';
import { DocumentListComponent } from './features/documents/documents-list/documents-list.component';
import { UploadComponent } from './features/documents/upload-document/upload-document.component';
import { ManageMemberComponent } from './features/team/manage-member/manage-member.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: '', redirectTo: 'projects', pathMatch: 'full' },
  {
    path: 'projects',
    canActivate: [AuthGuard],
    children: [
      { path: '', component: ProjectListComponent },
      { path: 'new', component: ProjectFormComponent },

      {
        path: ':id',
        component: ProjectDetailComponent,
        children: [
          { path: '', redirectTo: 'tasks', pathMatch: 'full' },

          { path: 'tasks', component: TaskListComponent },
          { path: 'tasks/new', component: TaskFormComponent },
          { path: 'tasks/:taskId/edit', component: TaskFormComponent },

          { path: 'team', component: MemberListComponent },
          { path: 'team/manage', component: ManageMemberComponent },

          { path: 'documents', component: DocumentListComponent },
          { path: 'documents/upload', component: UploadComponent },
        ],
      },
      { path: ':id/edit', component: ProjectFormComponent },
    ],
  },
  { path: '**', redirectTo: 'projects' },
];
