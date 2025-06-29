package com.muro.projectmanager.backend;

import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.model.*;
import com.muro.projectmanager.backend.repository.*;
import com.muro.projectmanager.backend.security.JwtTokenProvider;
import com.muro.projectmanager.backend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackendApplicationTests {

    // --- AuthService ---
    @Mock AuthenticationManager authManager;
    @Mock UserRepository userRepo;
    @Mock RoleRepository roleRepo;
    @Mock PasswordEncoder pwdEncoder;
    @Mock JwtTokenProvider jwtProvider;
    @InjectMocks AuthService authService;

    // --- DocumentService ---
    @Mock DocumentRepository docRepo;
    @Mock ProjectRepository projectRepo;
    @InjectMocks DocumentService docService;

    // --- InvitationService ---
    @Mock InvitationRepository invRepo;
    @Mock ProjectMemberService pmServiceForInvite;
    @Mock JavaMailSender mailSender;

    // --- ProjectMemberService (tests propios) ---
    @Mock ProjectMemberRepository pmRepo;
    @InjectMocks ProjectMemberService projectMemberService;

    // --- ProjectService ---
    @InjectMocks ProjectService projectService;

    // --- TaskService ---
    @Mock TaskRepository taskRepo;
    @InjectMocks TaskService taskService;

    private User sampleUser;
    private Project sampleProject;
    private Task sampleTask;
    private Document sampleDocument;

    @BeforeEach
    void setup() {
        sampleUser = User.builder()
                .id(1L)
                .name("Ana")
                .email("ana@mail.com")
                .password("encoded")
                .build();

        sampleProject = Project.builder()
                .id(10L)
                .title("Test Project")
                .description("Desc")
                .build();

        sampleTask = Task.builder()
                .id(100L)
                .title("Task")
                .description("Desc")
                .project(sampleProject)
                .build();

        sampleDocument = Document.builder()
                .id(200L)
                .fileName("file.pdf")
                .fileType("application/pdf")
                .data(new byte[]{1,2,3})
                .project(sampleProject)
                .build();
    }

    // --- AuthService tests ---

    @Test
    void authRegister_success() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Ana");
        req.setEmail("ana@mail.com");
        req.setPassword("pass");

        when(userRepo.existsByEmail("ana@mail.com")).thenReturn(false);
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        when(roleRepo.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(pwdEncoder.encode("pass")).thenReturn("encoded");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            u.setRoles(Set.of(userRole));
            return u;
        });

        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtProvider.generateToken(auth)).thenReturn("token123");

        JwtAuthResponse resp = authService.register(req);

        assertThat(resp.getAccessToken()).isEqualTo("token123");
        assertThat(resp.getTokenType()).isEqualTo("Bearer");
        verify(userRepo).save(any());
        verify(jwtProvider).generateToken(auth);
    }

    // --- DocumentService tests ---

    @Test
    void documentStore_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(projectRepo.findById(10L)).thenReturn(Optional.of(sampleProject));
        when(file.getOriginalFilename()).thenReturn("a.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getBytes()).thenReturn(new byte[]{9,8,7});
        when(docRepo.save(any(Document.class))).thenAnswer(inv -> {
            Document d = inv.getArgument(0);
            d.setId(300L);
            return d;
        });

        DocumentDto dto = docService.store(10L, file);

        assertThat(dto.getId()).isEqualTo(300L);
        assertThat(dto.getFileName()).isEqualTo("a.pdf");
        assertThat(dto.getFileType()).isEqualTo("application/pdf");
    }

    // --- InvitationService tests ---

    // --- ProjectMemberService tests ---

    @Test
    void projectMemberAddCreatorAsAdmin_success() {
        when(projectRepo.findById(10L)).thenReturn(Optional.of(sampleProject));
        when(userRepo.findById(1L)).thenReturn(Optional.of(sampleUser));

        projectMemberService.addCreatorAsAdmin(10L, 1L);

        ArgumentCaptor<ProjectMember> capt = ArgumentCaptor.forClass(ProjectMember.class);
        verify(pmRepo).save(capt.capture());
        ProjectMember pm = capt.getValue();
        assertThat(pm.getId()).isEqualTo(new ProjectMemberId(10L, 1L));
        assertThat(pm.getRole()).isEqualTo(ProjectRoleName.PROJECT_ADMIN);
    }

    // --- ProjectService tests ---

    @Test
    void projectServiceCreate_success() {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setTitle("P1");
        req.setDescription("D1");

        when(projectRepo.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(20L);
            return p;
        });

        ProjectDto dto = projectService.create(1L, req);

        assertThat(dto.getId()).isEqualTo(20L);
        assertThat(dto.getTitle()).isEqualTo("P1");
        // Aquí verificamos el mock pmServiceForInvite, que fue inyectado en ProjectService
        verify(pmServiceForInvite).addCreatorAsAdmin(20L, 1L);
    }

    // --- TaskService tests ---

    @Test
    void taskServiceCreate_success() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setTitle("T1");
        req.setDescription("D1");

        when(projectRepo.findById(10L)).thenReturn(Optional.of(sampleProject));
        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(400L);
            return t;
        });

        TaskDto dto = taskService.create(10L, req);

        assertThat(dto.getId()).isEqualTo(400L);
        assertThat(dto.getTitle()).isEqualTo("T1");
    }
}
