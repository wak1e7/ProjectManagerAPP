# ProjectManagerAPP

Aplicación de gestión de proyectos

---

## 📋 Tecnologías

- **Java**  
- **Spring Boot**  
  - spring-boot-starter-web  
  - spring-boot-starter-data-jpa  
  - spring-boot-starter-security  
  - spring-boot-starter-validation  
  - spring-boot-devtools  
  - springdoc-openapi-starter-webmvc-ui  
- **MySQL**  
- **JJWT**  
- **Maven**  
- **Lombok**  
- **Angular**   
- **Tailwind CSS**

---

## ⚙️ Requisitos Previos

1. Java 21 o superior  
2. Maven 3.6+  
3. Node.js 18+ y npm 8+  
4. Angular CLI 17 (`npm install -g @angular/cli@17`)  
5. MySQL 8 en ejecución  
6. Git  

---

## 🗄️ Creación de la base de datos

Conéctate a MySQL y ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS projectdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE projectdb;

-- Roles
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

-- Usuarios
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- Relación users ↔ roles
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Proyectos
CREATE TABLE IF NOT EXISTS projects (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(2000)
);

-- Miembros de proyecto
CREATE TABLE IF NOT EXISTS project_members (
  project_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  role VARCHAR(20) NOT NULL,
  PRIMARY KEY (project_id, user_id),
  CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Invitaciones
CREATE TABLE IF NOT EXISTS invitations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  project_id BIGINT NOT NULL,
  role VARCHAR(20) NOT NULL,
  token VARCHAR(36) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NOT NULL,
  accepted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_inv_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Documentos
CREATE TABLE IF NOT EXISTS documents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_type VARCHAR(100) NOT NULL,
  data LONGBLOB NOT NULL,
  project_id BIGINT NOT NULL,
  CONSTRAINT fk_doc_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Tareas
CREATE TABLE IF NOT EXISTS tasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(2000),
  status VARCHAR(20) NOT NULL DEFAULT 'TODO',
  due_date DATE,
  project_id BIGINT NOT NULL,
  CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Asignaciones de tareas a usuarios
CREATE TABLE IF NOT EXISTS task_assignees (
  task_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  PRIMARY KEY (task_id, user_id),
  CONSTRAINT fk_ta_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_ta_user FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 🔧 Configuración local

1. **Clonar el repositorio**  
   ```bash
   git clone https://github.com/wak1e7/ProjectManagerAPP.git
   cd ProjectManagerAPP
   ```

2. **Ajusta los valores a tu entorno(application.properties)**
   ```bash
    server.port=8080
    
    frontend.url=http://localhost:4200
    
    spring.datasource.url=jdbc:mysql://localhost:3306/projectdb?useSSL=false&serverTimezone=UTC
    spring.datasource.username=TU_USUARIO
    spring.datasource.password=TU_CONTRASEÑA
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    
    spring.jpa.hibernate.ddl-auto=none
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    
    spring.flyway.enabled=true
    spring.flyway.locations=classpath:db/migration
    
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=TU_GMAIL
    spring.mail.password=CODIGO_SECRETO
    spring.mail.properties.mail.transport.protocol=smtp
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    spring.mail.properties.mail.debug=false
    
    spring.servlet.multipart.max-file-size=20MB
    spring.servlet.multipart.max-request-size=20MB
    file.upload-dir=./uploads
    
    jwt.secret=CLAVE_SECRETA_MINIMO32CARACTERES
    jwt.expiration-ms=3600000
    
    app.frontend.url=http://localhost:4200
   ```
   
3. **Arrancar el backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **Arrancar el frontend**
   ```bash
   cd ../frontend
   npm install
   ng serve --open
   ```
