CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE projects
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(2000)
);

CREATE TABLE project_members
(
    project_id BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    role       VARCHAR(20) NOT NULL,
    PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE tasks
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    status      VARCHAR(20)  NOT NULL DEFAULT 'TODO',
    due_date    DATE,
    project_id  BIGINT       NOT NULL,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
)

CREATE TABLE documents
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name  VARCHAR(255) NOT NULL,
    file_type  VARCHAR(100) NOT NULL,
    data       LONGBLOB     NOT NULL,
    project_id BIGINT       NOT NULL,
    CONSTRAINT fk_doc_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE invitations
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    project_id BIGINT       NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    token      VARCHAR(36)  NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP    NOT NULL,
    accepted   BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_inv_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);
