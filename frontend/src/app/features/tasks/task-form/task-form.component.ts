import { Component, OnInit, inject }        from '@angular/core';
import { CommonModule }                     from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';

import { TaskService }                      from '../task.service';
import { TeamService }                      from '../../team/team.service';
import { TaskStatus, Task }                 from '../models/task.model';
import { Member }                           from '../../team/models/member.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [ CommonModule, ReactiveFormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css',
})
export class TaskFormComponent implements OnInit {
  private fb      = inject(FormBuilder);
  private svc     = inject(TaskService);
  private teamSvc = inject(TeamService);
  private route   = inject(ActivatedRoute);
  private router  = inject(Router);

  form!: FormGroup;
  projectId!: number;
  taskId?: number;
  error: string | null = null;

  statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];
  members: Member[]      = [];

  ngOnInit(): void {
    this.projectId = Number(this.route.parent?.snapshot.paramMap.get('id'));
    this.taskId    = this.route.snapshot.params['taskId'];

    this.form = this.fb.group({
      title:       ['', Validators.required],
      description: [''],
      status:      ['TODO', Validators.required],
      dueDate:     [''],
      assigneeIds: [[]]
    });

    this.teamSvc.listMembers(this.projectId).subscribe({
      next: ms => this.members = ms,
      error: () => {}
    });

    this.route.queryParamMap.subscribe(qp => {
      const s = qp.get('status') as TaskStatus;
      if (s && this.statuses.includes(s)) {
        this.form.get('status')!.setValue(s);
      }
    });

    if (this.taskId) {
      this.svc.getById(this.projectId, this.taskId).subscribe({
        next: (t: Task) => {
          this.form.patchValue({
            title:       t.title,
            description: t.description,
            status:      t.status,
            dueDate:     t.dueDate || '',
            assigneeIds: t.assigneeIds || []
          });
        },
        error: () => this.error = 'No se pudo cargar la tarea'
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.error = null;

    const payload = this.form.value as Partial<Task>;
    const op = this.taskId
      ? this.svc.update(this.projectId, this.taskId, payload)
      : this.svc.create(this.projectId, payload);

    op.subscribe({
      next: () => this.router.navigate(['/projects', this.projectId, 'tasks']),
      error: err =>
        this.error = err.error?.message || 'Error guardando tarea'
    });
  }

  cancel(): void {
    this.router.navigate(['/projects', this.projectId, 'tasks']);
  }

  label(s: TaskStatus) {
    return {
      TODO:        'Sin empezar',
      IN_PROGRESS: 'En curso',
      REVIEW:      'En revisión',
      DONE:        'Listo',
    }[s];
  }

  onDelete(): void {
    if (!this.taskId || !confirm('¿Eliminar esta tarea?')) return;
    this.svc.delete(this.projectId, this.taskId).subscribe({
      next: () => this.router.navigate(['/projects', this.projectId, 'tasks']),
      error: e => this.error = e.error?.message || 'Error eliminando'
    });
  }
}