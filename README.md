# 🗂️ Task Manager API
API de gestión de tareas y tableros, desarrollada con **Spring Boot** y siguiendo **arquitectura hexagonal** (Ports & Adapters).

---

## 🚀 Tecnologías utilizadas
- **Java 21**
- **Spring Boot 4.1.0**
- **Spring Data JPA**
- **Hibernate**
- **Spring Security + JWT**
- **Maven**
- **H2 / MySQL**
- **Arquitectura Hexagonal**

---

## 🧩 Arquitectura Hexagonal (Ports & Adapters)

La aplicación está dividida en tres capas principales:

### 🔹 Domain (Núcleo de negocio)
- Entidades del dominio (sin anotaciones JPA)
- Interfaces de repositorios
- Servicios de negocio
- Lógica pura, sin dependencias de frameworks

### 🔹 Application (Casos de uso)
- Casos de uso (orquestación de operaciones)
- DTOs
- Mappers
- Define *qué* hace la aplicación, no *cómo*

### 🔹 Infrastructure (Adaptadores)
- Controladores REST
- Repositorios JPA
- Configuración
- Seguridad (JWT)
- Persistencia
- Frameworks externos

---
  
## 🧱 Estructura del proyecto

```text
src/main/java/es.neila.daw.taskmanagerapi/
    ├── domain/
    │     ├── model/
    │     ├── repository/
    │     └── service/
    │
    ├── application/
    │     ├── dto/
    │     ├── mapper/
    │     └── usecase/
    │
    └── infrastructure/
          ├── controller/
          ├── repository/
          ├── config/
          └── security/

```
---

## 🗄️ Modelo de datos

### 👤 User
- id  
- name  
- email  
- password  

### 📋 Board (Tablero)
- id  
- name  
- user_id  

### 🧱 Column (Lista)
- id  
- name  
- position  
- board_id  

### 📝 Task (Tarea)
- id  
- title  
- description  
- due_date  
- position  
- column_id  

---

## 🔐 Autenticación
La API utiliza **JWT** para gestionar la autenticación:

- Registro de usuario  
- Login  
- Generación de token  
- Validación en cada petición protegida  

---

## ▶️ Cómo ejecutar el proyecto

### 1. Clonar el repositorio
```bash
git clone https://github.com/janlo-dev/task-manager-api.git
```
---
## 👨‍💻 Autor
**Juan Antonio Neila Lorenzo**

---


