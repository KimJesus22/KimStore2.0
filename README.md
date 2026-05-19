# 🚀 KimStore 2.0

**KimStore 2.0** es un sistema web de gestión de inventario de computadoras, desarrollado como proyecto de portafolio con una arquitectura moderna en formato **Monorepo**. La aplicación permite administrar registros mediante un **CRUD completo**: crear, consultar, actualizar y eliminar equipos desde una interfaz web conectada a una API REST.

El proyecto separa responsabilidades entre un backend robusto con **Java + Spring Boot** y un frontend rápido, tipado y moderno construido con **Astro + TypeScript + Tailwind CSS**.

---

## 🛠️ Tecnologías Utilizadas

### 🧩 Backend - `pc-backend`

| Tecnología | Uso principal |
| --- | --- |
| ☕ **Java 17** | Lenguaje principal del backend |
| 🍃 **Spring Boot 3.x** | Framework para crear la API REST |
| 🗃️ **Spring Data JPA** | Abstracción para acceso a datos |
| 🔄 **Hibernate** | ORM para mapear entidades Java a tablas relacionales |
| 🐬 **MariaDB** | Base de datos relacional |
| 🌐 **REST API** | Comunicación HTTP con el frontend |
| 📦 **Maven** | Gestión de dependencias y ciclo de vida del proyecto |

### 🎨 Frontend - `pc-frontend`

| Tecnología | Uso principal |
| --- | --- |
| 🚀 **Astro 6.x** | Framework frontend ligero y performante |
| 🔷 **TypeScript** | Tipado estricto con interfaces y aserciones del DOM |
| 🎨 **Tailwind CSS v4** | Sistema de estilos moderno y utilitario |
| 📦 **pnpm v11** | Gestor de paquetes eficiente y seguro |
| 🔌 **Fetch API** | Consumo de endpoints `GET`, `POST`, `PUT` y `DELETE` |

---

## 📐 Arquitectura

KimStore 2.0 utiliza una separación clara entre frontend y backend dentro de un mismo repositorio.

### Backend: arquitectura de 3 capas

```text
Controller  →  Repository  →  Entity  →  MariaDB
   ↑                              ↓
   └────────── API REST ──────────┘
```

#### 1. **Entity - Modelo de datos**

Define la estructura principal del dominio. Cada entidad representa una tabla en MariaDB y contiene los atributos necesarios para describir los equipos del inventario.

#### 2. **Repository - Acceso a base de datos**

Expone operaciones de persistencia mediante **Spring Data JPA**, reduciendo código repetitivo y permitiendo consultar, guardar, actualizar y eliminar registros de forma declarativa.

#### 3. **Controller - API REST**

Publica los endpoints HTTP mediante `@RestController`. Esta capa recibe las solicitudes del frontend y responde con datos en formato JSON.

### Frontend: consumo de API

El frontend construido con Astro consume la API del backend mediante `fetch`, utilizando los métodos HTTP estándar:

| Método | Acción |
| --- | --- |
| `GET` | Obtener computadoras registradas |
| `POST` | Crear una nueva computadora |
| `PUT` | Actualizar información existente |
| `DELETE` | Eliminar un registro del inventario |

---

## 📁 Estructura de Carpetas

```text
KimStore/
├── README.md
├── pc-backend/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── kimstore/
│       │   │           └── pc_backend/
│       │   │               ├── PcBackendApplication.java
│       │   │               ├── Producto.java
│       │   │               ├── ProductoRepository.java
│       │   │               └── ProductoController.java
│       │   └── resources/
│       │       ├── application.properties
│       │       ├── static/
│       │       └── templates/
│       └── test/
│           └── java/
│               └── com/
│                   └── kimstore/
│                       └── pc_backend/
│                           └── PcBackendApplicationTests.java
│
└── pc-frontend/
    ├── package.json
    ├── pnpm-lock.yaml
    ├── pnpm-workspace.yaml
    ├── astro.config.mjs
    ├── tsconfig.json
    ├── public/
    │   ├── favicon.ico
    │   └── favicon.svg
    └── src/
        ├── assets/
        │   ├── astro.svg
        │   └── background.svg
        ├── components/
        │   └── Welcome.astro
        ├── layouts/
        │   └── Layout.astro
        ├── pages/
        │   └── index.astro
        └── styles/
            └── global.css
```

---

## ⚙️ Instrucciones de Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/kimstore-2.0.git
cd kimstore-2.0
```

> Reemplaza la URL por la del repositorio real.

---

### 2. Configurar MariaDB

Crea una base de datos para el proyecto:

```sql
CREATE DATABASE kimstore_db;
```

---

### 3. Configurar el backend

Edita el archivo:

```text
pc-backend/src/main/resources/application.properties
```

Ejemplo de configuración:

```properties
spring.application.name=pc-backend

spring.datasource.url=jdbc:mariadb://localhost:3306/kimstore_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> Por seguridad, evita subir credenciales reales al repositorio. Para entornos productivos, usa variables de entorno o un gestor de secretos.

---

### 4. Levantar el backend

Desde la carpeta del backend:

```bash
cd pc-backend
./mvnw spring-boot:run
```

En Windows:

```powershell
cd pc-backend
.\mvnw.cmd spring-boot:run
```

Por defecto, la API estará disponible en:

```text
http://localhost:8080
```

---

### 5. Instalar dependencias del frontend

Desde la raíz del monorepo:

```bash
cd pc-frontend
pnpm install --ignore-scripts
```

El uso de `--ignore-scripts` ayuda a reducir riesgos de seguridad en la cadena de suministro, evitando la ejecución automática de scripts de instalación de dependencias.

---

### 6. Levantar el frontend

```bash
pnpm run dev
```

Astro iniciará el servidor de desarrollo normalmente en:

```text
http://localhost:4321
```

---

## 🔌 Flujo de Comunicación

```text
Usuario
  ↓
Interfaz Astro + TypeScript
  ↓ fetch()
API REST Spring Boot
  ↓ Spring Data JPA / Hibernate
MariaDB
```

El frontend actúa como cliente de la API: captura datos del usuario, valida estructuras mediante TypeScript y envía solicitudes HTTP al backend. El backend procesa las operaciones del inventario y persiste la información en MariaDB.

---

## 🛡️ Buenas Prácticas Aplicadas

- ✅ **Separación de responsabilidades:** backend y frontend viven en carpetas independientes dentro del monorepo.
- ✅ **Arquitectura por capas:** entidades, repositorios y controladores mantienen el backend ordenado y fácil de extender.
- ✅ **Tipado fuerte en frontend:** TypeScript permite trabajar con interfaces claras y reduce errores en tiempo de desarrollo.
- ✅ **Aserciones del DOM controladas:** el frontend puede interactuar con elementos HTML de forma explícita y segura.
- ✅ **API REST estándar:** uso de métodos HTTP semánticos para operaciones CRUD.
- ✅ **Persistencia desacoplada:** Spring Data JPA abstrae el acceso a MariaDB.
- ✅ **Seguridad en dependencias:** `pnpm install --ignore-scripts` limita la ejecución automática de scripts externos durante la instalación.
- ✅ **Documentación de configuración:** las credenciales se muestran como placeholders para evitar exposición accidental.

---

## 📌 Resumen

**KimStore 2.0** demuestra una implementación full stack moderna para la administración de inventario de computadoras. Su estructura como monorepo facilita el desarrollo coordinado entre frontend y backend, mientras que Spring Boot, MariaDB, Astro, TypeScript y Tailwind CSS ofrecen una base sólida para un sistema escalable, mantenible y presentable en un portafolio profesional.
