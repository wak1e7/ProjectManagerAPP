import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule }                     from '@angular/common';
import { FormsModule }                      from '@angular/forms';
import { ActivatedRoute, RouterLink }                   from '@angular/router';
import { TeamService }                      from '../team.service';
import { Member, ProjectRoleName }          from '../models/member.model';
import { FontAwesomeModule, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faTrash, faEdit, faPlus }                  from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-member-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, FontAwesomeModule],
  templateUrl: './member-list.component.html',
  styleUrl: './member-list.component.css'
})
export class MemberListComponent implements OnInit {
  projectId!: number;
  members: Member[] = [];
  error: string | null = null;

  editingId: number | null = null;
  editRole: ProjectRoleName = 'PROJECT_MEMBER';

  openMenuId: number | null = null;

  roles: ProjectRoleName[] = ['PROJECT_ADMIN','PROJECT_MEMBER'];

  faTrash = faTrash;
  faEdit  = faEdit;

  constructor(
    private svc: TeamService,
    private route: ActivatedRoute,
    library: FaIconLibrary
  ) {
    library.addIcons(faTrash, faEdit, faPlus);
  }

  ngOnInit(): void {
    this.projectId = Number(this.route.parent?.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.svc.listMembers(this.projectId).subscribe({
      next: ms => this.members = ms,
      error: err => this.error = err.error?.message || 'Error cargando miembros'
    });
  }

  startEdit(m: Member) {
    this.editingId = m.userId;
    this.editRole   = m.role;
    this.openMenuId = null;
  }

  saveEdit(m: Member) {
    this.svc.changeRole(this.projectId, m.userId, this.editRole)
      .subscribe({
        next: () => {
          m.role = this.editRole;
          this.cancelEdit();
        },
        error: () => {
          this.error = 'Error asignando rol';
          this.cancelEdit();
        }
      });
  }

  cancelEdit() {
    this.editingId = null;
  }

  toggleMenu(id: number, e: MouseEvent) {
    e.stopPropagation();
    this.openMenuId = this.openMenuId === id ? null : id;
  }

  remove(m: Member) {
    if (!confirm(`¿Quitar a ${m.email} del proyecto?`)) return;
    this.svc.removeMember(this.projectId, m.userId).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Error quitando miembro'
    });
  }
}