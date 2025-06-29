import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { TeamService } from '../team.service';
import { ProjectRoleName } from '../models/member.model';

@Component({
  selector: 'app-manage-member',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './manage-member.component.html',
  styleUrl: './manage-member.component.css',
})
export class ManageMemberComponent implements OnInit {
  form!: FormGroup;
  projectId!: number;
  roles: ProjectRoleName[] = ['PROJECT_ADMIN', 'PROJECT_MEMBER'];
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private svc: TeamService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.parent?.snapshot.paramMap.get('id'));
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['PROJECT_MEMBER', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.error = null;
    const { email, role } = this.form.value;
    this.svc.addOrInviteMember(this.projectId, email, role).subscribe({
      next: () => this.router.navigate(['/projects', this.projectId, 'team']),
      error: (err) =>
        (this.error = err.error?.message || 'Error procesando solicitud'),
    });
  }

  cancel(): void {
    this.router.navigate(['/projects', this.projectId, 'team']);
  }
}
