import { Component, OnInit } from '@angular/core';
import {
  ActivatedRoute,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { ProjectService } from '../project.service';
import { Project } from '../models/project.model';
import { CommonModule } from '@angular/common';
import {
  FaIconLibrary,
  FontAwesomeModule,
} from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faFileAlt,
  faTasks,
  faUsers,
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    FontAwesomeModule,
  ],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css',
})
export class ProjectDetailComponent implements OnInit {
  project?: Project;
  error: string | null = null;
  id!: number;

  constructor(
    private svc: ProjectService,
    private route: ActivatedRoute,
    private router: Router,
    library: FaIconLibrary
  ) {
    library.addIcons(faArrowLeft, faTasks, faFileAlt, faUsers);
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.svc.getById(this.id).subscribe({
      next: (p) => (this.project = p),
      error: (err) => (this.error = 'No se pudo cargar el proyecto'),
    });
  }

  goBack() {
    this.router.navigate(['/projects']);
  }
}
