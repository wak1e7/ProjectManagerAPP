import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../project.service';
import { Project } from '../models/project.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.css',
})
export class ProjectFormComponent implements OnInit {
  form: FormGroup;
  id?: number;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private svc: ProjectService,
    private route: ActivatedRoute,
    public router: Router
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: [''],
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.svc.getById(this.id).subscribe({
        next: (p: Project) => this.form.patchValue(p),
        error: (err) => (this.error = 'No se encontró el proyecto'),
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const data = this.form.value;
    const obs = this.id
      ? this.svc.update(this.id, data)
      : this.svc.create(data);
    obs.subscribe({
      next: () => this.router.navigate(['/projects']),
      error: (err) => (this.error = err.error?.message || 'Error guardando'),
    });
  }
}
