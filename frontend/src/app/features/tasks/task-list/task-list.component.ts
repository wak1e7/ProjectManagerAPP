import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ActivatedRoute, Router } from '@angular/router';
import {
  DragDropModule,
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { faPlus, faGripVertical, faCircleCheck } from '@fortawesome/free-solid-svg-icons';
import { TaskService } from '../task.service';
import { Task, TaskStatus } from '../models/task.model';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterLink, DragDropModule, FontAwesomeModule],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css'],
})
export class TaskListComponent implements OnInit {
  private svc = inject(TaskService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  projectId!: number;
  tasks: Task[] = [];

  states: { key: TaskStatus; label: string }[] = [
    { key: 'TODO', label: 'Sin empezar' },
    { key: 'IN_PROGRESS', label: 'En curso' },
    { key: 'REVIEW', label: 'En revisión' },
    { key: 'DONE', label: 'Listo' },
  ];

  stateKeys = this.states.map((s) => s.key);

  constructor(library: FaIconLibrary) {
    library.addIcons(faPlus, faGripVertical, faCircleCheck);
  }

  ngOnInit(): void {
    this.projectId = Number(this.route.parent?.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.svc.list(this.projectId).subscribe((tasks) => (this.tasks = tasks));
  }

  tasksFor(state: TaskStatus): Task[] {
    return this.tasks.filter((t) => t.status === state);
  }

  newTask(state: TaskStatus): void {
    this.router.navigate(['projects', this.projectId, 'tasks', 'new'], {
      queryParams: { status: state },
    });
  }

  editTask(t: Task): void {
    this.router.navigate(['projects', this.projectId, 'tasks', t.id, 'edit']);
  }

  drop(event: CdkDragDrop<Task[]>, newStatus: TaskStatus) {
    if (event.previousContainer === event.container) {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      const task = event.previousContainer.data[event.previousIndex];
      this.svc
        .update(this.projectId, task.id, { status: newStatus })
        .subscribe({
          next: (updated) => {
            task.status = updated.status as TaskStatus;
            const i = this.tasks.findIndex((t) => t.id === updated.id);
            if (i > -1) {
              this.tasks[i] = {
                ...this.tasks[i],
                status: updated.status as TaskStatus,
              };
            }
          },
          error: () => {
            this.load();
          },
        });

      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
  }
}
