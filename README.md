# 🚀 KimStore 2.0

**KimStore 2.0** es un sistema web de gestión de inventario de computadoras, desarrollado como proyecto de portafolio con una arquitectura moderna en formato **Monorepo**. La aplicación permite administrar registros mediante un **CRUD completo**: crear, consultar, actualizar y eliminar equipos desde una interfaz web conectada a una API REST.

El proyecto separa responsabilidades entre un backend robusto con **Java + Spring Boot** y un frontend rápido, tipado y moderno construido con **Astro + TypeScript + Tailwind CSS**.

Actualmente el sistema evoluciona hacia una arquitectura más profesional, incorporando una capa de servicios, DTOs para separar la entrada de datos de las entidades JPA y validaciones declarativas con `jakarta.validation`.

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
| ✅ **Jakarta Validation** | Validación de datos con `@Valid`, `@NotBlank`, `@Min`, `@DecimalMin` |
| 🧱 **DTO Pattern** | Separación entre datos externos y entidades internas |

### 🎨 Frontend - `pc-frontend`

| Tecnología | Uso principal |
| --- | --- |
| 🚀 **Astro 6.x** | Framework frontend ligero y performante |
| 🔷 **TypeScript** | Tipado estricto con interfaces y aserciones del DOM |
| 🎨 **Tailwind CSS v4** | Sistema de estilos moderno y utilitario |
| 📦 **pnpm v11** | Gestor de paquetes eficiente y seguro |
| 🔌 **Fetch API** | Consumo de endpoints `GET`, `POST`, `PUT` y `DELETE` |
| 🧩 **Componentes Astro** | Separación visual en `ProductCard` y `ProductForm` |

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

### Backend actualizado: arquitectura con Service y DTO

A medida que el proyecto crece, el backend se está organizando con una separación más profesional:

```text
Controller  →  Service  →  Repository  →  Entity  →  MariaDB
    ↑             ↑
    │             └── Convierte ProductoDTO en Producto
    └── Recibe JSON validado con @Valid
```

#### **DTO - Entrada limpia de datos**

`ProductoDTO` representa el paquete de datos que llega desde el frontend. No es una entidad de base de datos y no usa `@Entity`; su responsabilidad es transportar datos de entrada de forma clara y validable.

#### **Service - Lógica de negocio**

`ProductoService` concentra la lógica del CRUD. El controller delega aquí las operaciones de crear, actualizar, listar y eliminar productos. Esta capa también transforma el DTO externo en la entidad interna `Producto`.

#### **Validación - Datos confiables**

El backend usa `@Valid` en el controller y restricciones como `@NotBlank`, `@DecimalMin` y `@Min` para impedir productos inválidos antes de guardarlos en MariaDB.

### Frontend: consumo de API

El frontend construido con Astro consume la API del backend mediante `fetch`, utilizando los métodos HTTP estándar:

| Método | Acción |
| --- | --- |
| `GET` | Obtener computadoras registradas |
| `POST` | Crear una nueva computadora |
| `PUT` | Actualizar información existente |
| `DELETE` | Eliminar un registro del inventario |

### Endpoints principales

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `GET` | `/api/productos` | Lista todos los productos |
| `POST` | `/api/productos` | Crea un producto usando `ProductoDTO` |
| `PUT` | `/api/productos/{id}` | Actualiza un producto existente usando `ProductoDTO` |
| `DELETE` | `/api/productos/{id}` | Elimina un producto por ID |

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
│       │   │               ├── ProductoController.java
│       │   │               ├── dto/
│       │   │               │   └── ProductoDTO.java
│       │   │               └── service/
│       │   │                   └── ProductoService.java
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
        │   ├── ProductCard.astro
        │   ├── ProductForm.astro
        │   └── Welcome.astro
        ├── layouts/
        │   └── Layout.astro
        ├── pages/
        │   └── index.astro
        ├── styles/
        │   └── global.css
        └── types/
            └── producto.ts
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
  ↓ Controller con @Valid
ProductoDTO
  ↓ Service
Producto Entity
  ↓ Repository + Spring Data JPA / Hibernate
MariaDB
```

El frontend actúa como cliente de la API: captura datos del usuario, valida estructuras mediante TypeScript y envía solicitudes HTTP al backend. El backend procesa las operaciones del inventario y persiste la información en MariaDB.

### Flujo de creación y actualización

```text
Formulario Astro
  ↓
fetch(POST / PUT)
  ↓
ProductoController
  ↓
ProductoDTO validado
  ↓
ProductoService
  ↓
ProductoRepository
  ↓
MariaDB
```

Este flujo evita que el frontend trabaje directamente con la entidad JPA, reduciendo acoplamiento y dejando el backend preparado para crecer con reglas de negocio más complejas.

---

## 🛡️ Buenas Prácticas Aplicadas

- ✅ **Separación de responsabilidades:** backend y frontend viven en carpetas independientes dentro del monorepo.
- ✅ **Arquitectura por capas:** entidades, repositorios y controladores mantienen el backend ordenado y fácil de extender.
- ✅ **Capa de servicio:** `ProductoService` centraliza la lógica de negocio y mantiene el controller ligero.
- ✅ **Uso de DTOs:** `ProductoDTO` separa los datos recibidos por la API de la entidad persistida en la base de datos.
- ✅ **Validaciones declarativas:** `@Valid` y `jakarta.validation` ayudan a rechazar datos incompletos o inválidos.
- ✅ **Tipado fuerte en frontend:** TypeScript permite trabajar con interfaces claras y reduce errores en tiempo de desarrollo.
- ✅ **Aserciones del DOM controladas:** el frontend puede interactuar con elementos HTML de forma explícita y segura.
- ✅ **API REST estándar:** uso de métodos HTTP semánticos para operaciones CRUD.
- ✅ **Persistencia desacoplada:** Spring Data JPA abstrae el acceso a MariaDB.
- ✅ **Seguridad en dependencias:** `pnpm install --ignore-scripts` limita la ejecución automática de scripts externos durante la instalación.
- ✅ **Documentación de configuración:** las credenciales se muestran como placeholders para evitar exposición accidental.

---

## 🧪 Validaciones del Producto

El backend valida los datos antes de guardarlos:

| Campo | Regla |
| --- | --- |
| `nombre` | Obligatorio y con longitud máxima controlada |
| `descripcion` | Obligatoria |
| `precio` | Debe ser mayor a `0` |
| `stock` | No puede ser negativo |

Estas reglas protegen la base de datos y evitan que el frontend envíe información incompleta o inconsistente.

---

## 🧠 Estado Actual del Proyecto

KimStore 2.0 ya cuenta con:

- ✅ Monorepo con backend y frontend separados.
- ✅ API REST para CRUD de productos.
- ✅ Entidad `Producto` conectada a MariaDB mediante JPA.
- ✅ `ProductoRepository` usando `JpaRepository`.
- ✅ `ProductoService` como capa de lógica de negocio.
- ✅ `ProductoDTO` para entrada limpia de datos.
- ✅ Validaciones con `@Valid` y Jakarta Validation.
- ✅ Frontend Astro consumiendo la API con `fetch`.
- ✅ Componentes visuales `ProductCard` y `ProductForm`.
- ✅ Tipos TypeScript en `src/types/producto.ts`.
- ✅ Manejo visual cuando el backend no está disponible.

---

## 🧭 Próximas Mejoras Sugeridas

- 🔁 Convertir también las respuestas del backend de `Producto` a `ProductoDTO`.
- ⚠️ Agregar manejo global de errores con `@ControllerAdvice`.
- 🔍 Implementar búsqueda y filtros por nombre, precio o stock.
- 🖼️ Añadir campo de imagen para productos.
- 🔐 Preparar autenticación y roles para un futuro panel administrativo.
- 📦 Separar lógica frontend en archivos TypeScript dedicados.
- 🧪 Agregar pruebas unitarias para service y controller.

---

## 📌 Resumen

**KimStore 2.0** demuestra una implementación full stack moderna para la administración de inventario de computadoras. Su estructura como monorepo facilita el desarrollo coordinado entre frontend y backend, mientras que Spring Boot, MariaDB, Astro, TypeScript y Tailwind CSS ofrecen una base sólida para un sistema escalable, mantenible y presentable en un portafolio profesional.

Con la incorporación de `ProductoService`, `ProductoDTO` y validaciones con `@Valid`, el proyecto deja de ser solo un CRUD básico y empieza a tomar forma de una aplicación con arquitectura limpia, preparada para crecer hacia funcionalidades más avanzadas como catálogo, carrito, usuarios y panel administrativo.
