import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DocumentService } from '../document.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload-document',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload-document.component.html',
  styleUrl: './upload-document.component.css',
})
export class UploadComponent implements OnInit {
  projectId!: number;
  selectedFile: File | null = null;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private svc: DocumentService
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.parent?.snapshot.paramMap.get('id'));
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  upload(): void {
    if (!this.selectedFile) {
      this.error = 'Selecciona un archivo antes de subir';
      return;
    }
    this.svc.upload(this.projectId, this.selectedFile).subscribe({
      next: () =>
        this.router.navigate(['/projects', this.projectId, 'documents']),
      error: (err) =>
        (this.error = err.error?.message || 'Error subiendo documento'),
    });
  }

  cancel(): void {
    this.router.navigate(['/projects', this.projectId, 'documents']);
  }
}
