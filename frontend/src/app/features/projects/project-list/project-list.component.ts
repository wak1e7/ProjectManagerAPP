import { Component, OnInit, HostListener } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ProjectService } from '../project.service';
import { Project } from '../models/project.model';
import { CommonModule } from '@angular/common';
import {
  faPlus,
  faEllipsisV,
  faEdit,
  faTrash,
} from '@fortawesome/free-solid-svg-icons';
import {
  FaIconLibrary,
  FontAwesomeModule,
} from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css',
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  error: string | null = null;
  openMenuId: number | null = null;

  constructor(
    private svc: ProjectService,
    private router: Router,
    library: FaIconLibrary
  ) {
    library.addIcons(faPlus, faEllipsisV, faEdit, faTrash);
  }

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.svc.list().subscribe({
      next: (data) => (this.projects = data),
      error: (err) =>
        (this.error = err.error?.message || 'Error cargando proyectos'),
    });
  }

  goNew() {
    this.router.navigate(['/projects/new']);
  }

  viewDetails(project: Project) {
    this.router.navigate(['/projects', project.id]);
  }

  edit(project: Project) {
    this.openMenuId = null;
    this.router.navigate(['/projects', project.id, 'edit']);
  }

  remove(project: Project) {
    this.openMenuId = null;
    if (!confirm(`¿Eliminar proyecto "${project.title}"?`)) return;
    this.svc.delete(project.id).subscribe({
      next: () => this.load(),
      error: (err) =>
        (this.error = err.error?.message || 'No se pudo eliminar'),
    });
  }

  toggleMenu(id: number, event: MouseEvent) {
    event.stopPropagation();
    this.openMenuId = this.openMenuId === id ? null : id;
  }

  @HostListener('document:click', ['$event.target'])
  closeMenu(target: HTMLElement) {
    if (!target.closest('.project-card')) {
      this.openMenuId = null;
    }
  }
}
