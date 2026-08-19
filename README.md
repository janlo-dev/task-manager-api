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
## 📐 Arquitectura del Proyecto

Este proyecto está diseñado siguiendo los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)**. El objetivo principal es desacoplar la lógica de negocio pura de las tecnologías externas (frameworks, bases de datos, APIs de terceros), facilitando el mantenimiento y las pruebas unitarias.

A continuación se detalla la estructura de directorios y la responsabilidad de cada capa:

```text
src/main/java/es/neila/daw/taskmanagerapi/
├── TaskManagerApiApplication.java       # Clase principal de Spring Boot
│
├── domain/                              # 1. CAPA DE DOMINIO (El núcleo del negocio)
│   ├── model/                           # Entidades y objetos de valor del negocio (ej. Task)
│   ├── repository/                      # PUERTOS DE SALIDA: Interfaces para el acceso a datos
│   └── service/                         # Servicios de dominio con reglas de negocio puras
│
├── application/                         # 2. CAPA DE APLICACIÓN (Orquestación y Casos de Uso)
│   ├── usecase/                         # PUERTOS DE ENTRADA: Acciones del sistema (ej. CreateTaskUseCase)
│   ├── dto/                             # Objetos de Transferencia de Datos para el exterior
│   └── mapper/                          # Conversores para transformar Modelos <-> DTOs
│
└── infrastructure/                      # 3. CAPA DE INFRAESTRUCTURA (Detalles tecnológicos)
    ├── controller/                      # ADAPTADORES DE ENTRADA: Controladores REST (API)
    ├── repository/                      # ADAPTADORES DE SALIDA: Implementaciones de BD (JPA, SQL)
    ├── config/                          # Configuración técnica del framework (Spring Beans, Beans del Dominio)
    └── security/                        # Configuración de seguridad, filtros y manejo de JWT
```

### 🧠 Regla de Dependencia

La dependencia siempre fluye **hacia adentro**:
1. El **Dominio** es el núcleo y es completamente agnóstico a la tecnología. No tiene dependencias externas ni anotaciones de frameworks (como Spring).
2. La **Aplicación** depende únicamente del Dominio para ejecutar las acciones del sistema.
3. La **Infraestructura** envuelve al sistema y comunica el mundo exterior con la Aplicación y el Dominio mediante el patrón de Inversión de Dependencias.

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
cd task-manager-api
```

### 2. Compilar e instalar dependencias
```bash
./mvnw clean install
```

### 3. Levantar la aplicación
```bash
./mvnw spring-boot:run
```
---
## 👨‍💻 Autor
**Juan Antonio Neila Lorenzo**

---


