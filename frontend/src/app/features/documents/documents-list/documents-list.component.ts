import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../document.service';
import { Document } from '../models/document.model';
import {
  FontAwesomeModule,
  FaIconLibrary,
} from '@fortawesome/angular-fontawesome';
import { faTrash, faDownload, faPlus } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-documents-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, FontAwesomeModule],
  providers: [DatePipe],
  templateUrl: './documents-list.component.html',
  styleUrls: ['./documents-list.component.css'],
})
export class DocumentListComponent implements OnInit {
  projectId!: number;
  documents: Document[] = [];
  error: string | null = null;

  editingId: number | null = null;
  editingField: 'updatedAt' | 'version' | null = null;
  editValue = '';

  openMenuId: number | null = null;

  constructor(
    private svc: DocumentService,
    private route: ActivatedRoute,
    private dp: DatePipe,
    library: FaIconLibrary
  ) {
    library.addIcons(faTrash, faDownload, faPlus);
  }

  ngOnInit(): void {
    this.projectId = Number(this.route.parent?.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.svc.list(this.projectId).subscribe({
      next: (docs) => (this.documents = docs),
      error: (err) =>
        (this.error = err.error?.message || 'Error cargando documentos'),
    });
  }

  fmt(date: string): string {
    return this.dp.transform(date, "dd 'de' MMMM yyyy")!;
  }

  delete(d: Document) {
    this.openMenuId = null;
    if (!confirm(`Eliminar "${d.fileName}"?`)) return;
    this.svc.delete(this.projectId, d.id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err.error?.message || 'Error eliminando'),
    });
  }

  download(d: Document) {
    this.svc.download(this.projectId, d.id).subscribe((blob) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = d.fileName;
      a.click();
      URL.revokeObjectURL(url);
    });
  }

  startEdit(d: Document, field: 'updatedAt' | 'version') {
    this.openMenuId = null;
    this.editingId = d.id;
    this.editingField = field;
    this.editValue =
      field === 'version' ? d.version : d.updatedAt.substring(0, 10);
  }

  save(d: Document) {
    if (!this.editingField) return;
    const payload: any = {};
    if (this.editingField === 'version') {
      payload.version = this.editValue;
    } else {
      payload.updatedAt = new Date(this.editValue).toISOString();
    }
    this.svc.update(this.projectId, d.id, payload).subscribe({
      next: (updated) => {
        Object.assign(d, updated);
        this.cancel();
      },
      error: () => {
        this.error = 'Error actualizando';
        this.cancel();
      },
    });
  }

  cancel() {
    this.editingId = null;
    this.editingField = null;
    this.editValue = '';
  }

  toggleMenu(id: number, event: MouseEvent) {
    event.stopPropagation();
    this.openMenuId = this.openMenuId === id ? null : id;
    this.cancel();
  }

  @HostListener('document:click', ['$event.target'])
  closeMenu(target: HTMLElement) {
    if (!target.closest('td')) {
      this.openMenuId = null;
    }
  }
}
