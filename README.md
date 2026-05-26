# 🚀 KimStore 2.0

**KimStore 2.0** es un sistema web de gestión de inventario de computadoras, desarrollado como proyecto de portafolio con una arquitectura moderna en formato **Monorepo**. La aplicación permite administrar registros mediante un **CRUD completo**: crear, consultar, actualizar y eliminar equipos desde una interfaz web conectada a una API REST.

El proyecto separa responsabilidades entre un backend robusto con **Java + Spring Boot** y un frontend rápido, tipado y moderno construido con **Astro + TypeScript + Tailwind CSS**.

Actualmente el sistema evoluciona hacia una arquitectura más profesional, incorporando una capa de servicios, DTOs para separar la entrada de datos de las entidades JPA, validaciones declarativas con `jakarta.validation`, búsqueda por nombre, paginación desde el backend y carga de imágenes con Cloudinary.

También incorpora un flujo inicial de e-commerce: carrito persistente en el navegador, modal de resumen, eliminación de productos solo del carrito, finalización de compra autenticada, descuento automático de stock e historial de pedidos por usuario.

---

## 🛠️ Tecnologías Utilizadas

### 🧩 Backend - `pc-backend`

| Tecnología | Uso principal |
| --- | --- |
| ☕ **Java 17** | Lenguaje principal del backend |
| 🍃 **Spring Boot** | Framework para crear la API REST |
| 🗃️ **Spring Data JPA** | Abstracción para acceso a datos |
| 🔄 **Hibernate** | ORM para mapear entidades Java a tablas relacionales |
| 🐬 **MariaDB** | Base de datos relacional para desarrollo local |
| 🐘 **PostgreSQL / Aiven** | Base de datos relacional para producción |
| 🌐 **REST API** | Comunicación HTTP con el frontend |
| 📦 **Maven** | Gestión de dependencias y ciclo de vida del proyecto |
| ✅ **Jakarta Validation** | Validación de datos con `@Valid`, `@NotBlank`, `@Min`, `@DecimalMin` |
| 🧱 **DTO Pattern** | Separación entre datos externos y entidades internas |
| 📄 **Spring Data Page** | Respuestas paginadas con `Page`, `PageRequest` y `Pageable` |
| ☁️ **Cloudinary** | Almacenamiento externo de imágenes de productos |
| 📎 **MultipartFile** | Recepción de archivos mediante `multipart/form-data` |
| 🔐 **Spring Security** | Protección de rutas privadas del CRUD |
| 🪪 **JWT** | Autenticación stateless mediante tokens Bearer |
| 🔒 **BCrypt** | Encriptación segura de contraseñas de administradores |
| 🌍 **CORS configurado** | Permite comunicación entre Astro local/Vercel y Spring Boot |
| 🛒 **Endpoint de compra** | Procesa carritos, descuenta stock y registra pedidos de forma transaccional |

### 🎨 Frontend - `pc-frontend`

| Tecnología | Uso principal |
| --- | --- |
| 🚀 **Astro 6.x** | Framework frontend ligero y performante |
| 🔷 **TypeScript** | Tipado estricto con interfaces y aserciones del DOM |
| 🎨 **Tailwind CSS v4** | Sistema de estilos moderno y utilitario |
| 📦 **pnpm v11** | Gestor de paquetes eficiente y seguro |
| 🔌 **Fetch API** | Consumo de endpoints `GET`, `POST`, `PUT` y `DELETE` |
| 🧩 **Componentes Astro** | Separación visual en `ProductCard` y `ProductForm` |
| 🔎 **URLSearchParams** | Lectura de `buscar` y `page` desde la URL |
| 🍬 **SweetAlert2** | Alertas visuales para respuestas del formulario |
| 🖼️ **FormData + Blob** | Envío de DTO JSON e imagen opcional en una sola petición |
| 🔑 **LocalStorage** | Almacenamiento del token JWT del administrador |
| 🚪 **Login Astro** | Pantalla `/login` para iniciar sesión y guardar token/rol |
| 📝 **Register Astro** | Pantalla `/register` para crear usuarios |
| 🧠 **Nano Stores** | Estado global ligero para el carrito |
| 💾 **@nanostores/persistent** | Persistencia del carrito en `localStorage` |

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
    ↑             ↑             ↑
    │             │             └── Consultas derivadas y paginadas
    │             └── Convierte ProductoDTO en Producto y prepara PageRequest
    └── Recibe JSON validado, multipart, imágenes y parámetros buscar/page/size

CloudinaryService → Cloudinary → URL pública → Producto.imageUrl
```

#### **DTO - Entrada limpia de datos**

`ProductoDTO` representa el paquete de datos que llega desde el frontend. No es una entidad de base de datos y no usa `@Entity`; su responsabilidad es transportar datos de entrada de forma clara y validable.

#### **Service - Lógica de negocio**

`ProductoService` concentra la lógica del CRUD. El controller delega aquí las operaciones de crear, actualizar, listar y eliminar productos. Esta capa también transforma el DTO externo en la entidad interna `Producto`.

#### **CloudinaryService - Gestión de imágenes**

`CloudinaryService` recibe un `MultipartFile`, sube la imagen a Cloudinary y devuelve una URL pública. Esa URL se guarda en el campo `imageUrl` de la entidad `Producto`, evitando almacenar archivos binarios directamente en la base de datos.

#### **Repository - Búsqueda y paginación**

`ProductoRepository` extiende `JpaRepository` y aprovecha las consultas derivadas de Spring Data JPA. El método `findByNombreContainingIgnoreCase(String nombre, Pageable pageable)` permite buscar productos por nombre ignorando mayúsculas y minúsculas, devolviendo resultados paginados.

#### **Paginación - Respuestas controladas**

El endpoint principal devuelve un `Page<Producto>`, por lo que la respuesta incluye tanto los productos en `content` como metadatos útiles: página actual, total de páginas, cantidad de elementos y si es la primera o última página.

#### **Validación - Datos confiables**

El backend usa `@Valid` en el controller y restricciones como `@NotBlank`, `@DecimalMin` y `@Min` para impedir productos inválidos antes de guardarlos en MariaDB.

#### **Seguridad - Login, JWT y roles**

El backend incorpora Spring Security con una configuración stateless basada en JWT. Las rutas públicas permiten consultar productos, registrarse, iniciar sesión y procesar una compra, mientras que las operaciones sensibles del CRUD requieren autenticación y rol de administrador.

El flujo de seguridad está compuesto por:

- `Usuario`: entidad JPA para administradores.
- `UsuarioRepository`: búsqueda de usuarios por `username`.
- `ApplicationConfig`: configuración de `UserDetailsService`, `AuthenticationProvider`, `AuthenticationManager`, `PasswordEncoder` y usuario administrador inicial.
- `SecurityConfig`: reglas de autorización, CORS, sesión stateless y registro del filtro JWT.
- `JwtService`: generación y validación de tokens.
- `JwtAuthenticationFilter`: lectura del header `Authorization: Bearer <token>`.
- `AuthController`: endpoints de login en `/api/auth/login` y registro en `/api/auth/register`.
- `AuthResponse`: respuesta de autenticación con `token` y `role`.
- `RegisterRequest`: DTO para crear usuarios desde el frontend.

Actualmente, el backend inicializa un administrador de desarrollo:

```text
usuario: kim
contraseña: 123456
rol: ROLE_ADMIN
```

> Esta credencial facilita las pruebas locales. En un despliegue real debe reemplazarse por un mecanismo seguro de creación de administradores y secretos configurados por entorno.

### Frontend: consumo de API

El frontend construido con Astro consume la API del backend mediante `fetch`, utilizando los métodos HTTP estándar:

| Método | Acción |
| --- | --- |
| `GET` | Obtener computadoras registradas |
| `POST` | Crear una nueva computadora |
| `PUT` | Actualizar información existente |
| `DELETE` | Eliminar un registro del inventario |

Como el frontend se ejecuta en Astro con salida estática, la carga del catálogo se realiza desde el navegador. La página lee `window.location.search` con `URLSearchParams` para detectar parámetros como `buscar` y `page`, y después consulta al backend con `fetch`.

La navegación actual separa la experiencia pública y el panel de productos:

- `/`: landing page de bienvenida.
- `/login`: inicio de sesión.
- `/register`: creación de cuenta.
- `/productos`: catálogo, carrito y panel de inventario.
- `/mis-compras`: historial de pedidos del usuario autenticado.

El frontend guarda dos llaves importantes en `localStorage`:

```text
kimstore_token = JWT devuelto por el backend
kimstore_role = ROLE_ADMIN o ROLE_USER
```

Con `kimstore_role`, la interfaz muestra u oculta acciones administrativas. Los botones **Editar**, **Eliminar** y el formulario de creación aparecen únicamente cuando el rol es `ROLE_ADMIN`. Aun así, la seguridad real se mantiene en el backend con Spring Security.

### Endpoints principales

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `GET` | `/api/productos` | Lista productos paginados |
| `GET` | `/api/productos?buscar=hp&page=0&size=6` | Busca por nombre y pagina resultados |
| `POST` | `/api/productos` | Crea un producto usando `multipart/form-data` con `ProductoDTO` e imagen opcional |
| `PUT` | `/api/productos/{id}` | Actualiza un producto usando `multipart/form-data` con `ProductoDTO` e imagen opcional |
| `PUT` | `/api/productos/{id}` | Actualiza datos básicos usando `application/json` desde la edición rápida |
| `DELETE` | `/api/productos/{id}` | Elimina un producto por ID |
| `GET` | `/api/productos/dashboard/metrics` | Devuelve métricas del inventario para el dashboard admin |
| `POST` | `/api/auth/login` | Valida credenciales y devuelve un token JWT |
| `POST` | `/api/auth/register` | Crea un usuario nuevo con contraseña encriptada |
| `POST` | `/api/pedidos/comprar` | Procesa el carrito autenticado, registra el pedido y descuenta inventario |
| `GET` | `/api/pedidos/mis-compras` | Devuelve el historial de compras del usuario autenticado |

### Endpoints públicos y protegidos

| Endpoint | Acceso |
| --- | --- |
| `GET /api/productos/**` | Público |
| `POST /api/auth/login` | Público |
| `POST /api/auth/register` | Público |
| `GET /api/productos/dashboard/metrics` | Requiere `Authorization: Bearer <token>` y `ROLE_ADMIN` |
| `POST /api/productos` | Requiere `Authorization: Bearer <token>` y `ROLE_ADMIN` |
| `PUT /api/productos/{id}` | Requiere `Authorization: Bearer <token>` y `ROLE_ADMIN` |
| `DELETE /api/productos/{id}` | Requiere `Authorization: Bearer <token>` y `ROLE_ADMIN` |
| `POST /api/pedidos/comprar` | Requiere `Authorization: Bearer <token>` |
| `GET /api/pedidos/mis-compras` | Requiere `Authorization: Bearer <token>` |

> Nota: el endpoint anterior `POST /api/productos/comprar` fue reemplazado por `POST /api/pedidos/comprar` para poder asociar cada compra con el usuario autenticado y guardar historial.

Ejemplo de login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "kim",
  "password": "123456"
}
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_ADMIN"
}
```

Ejemplo de operación protegida:

```http
DELETE /api/productos/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Ejemplo de compra:

```http
POST /api/pedidos/comprar
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

[
  { "id": 1, "cantidad": 2 },
  { "id": 5, "cantidad": 1 }
]
```

Respuesta exitosa:

```json
{
  "mensaje": "Compra registrada exitosamente"
}
```

Si no hay stock suficiente, el backend responde `400`:

```json
{
  "error": "No hay suficiente stock para: Laptop HP Pavilion"
}
```

### Parámetros de consulta

| Parámetro | Ejemplo | Descripción |
| --- | --- | --- |
| `buscar` | `hp` | Texto opcional para buscar dentro del nombre del producto |
| `page` | `0` | Página actual, empezando en `0` |
| `size` | `6` | Cantidad de productos por página |

Ejemplos:

```text
http://localhost:8080/api/productos?page=0&size=6
http://localhost:8080/api/productos?page=1&size=6
http://localhost:8080/api/productos?buscar=laptop&page=0&size=6
```

Respuesta esperada de Spring Data `Page`:

```json
{
  "content": [
    {
      "id": 11,
      "nombre": "Laptop HP Pavilion 15",
      "descripcion": "AMD Ryzen 7, 16GB RAM, 512GB SSD, Windows 11",
      "precio": 16800.0,
      "stock": 6,
      "imageUrl": "https://res.cloudinary.com/..."
    }
  ],
  "number": 0,
  "size": 6,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

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
│       │   │               ├── AuthController.java
│       │   │               ├── PcBackendApplication.java
│       │   │               ├── ItemPedido.java
│       │   │               ├── Pedido.java
│       │   │               ├── PedidoController.java
│       │   │               ├── PedidoRepository.java
│       │   │               ├── Producto.java
│       │   │               ├── ProductoRepository.java
│       │   │               ├── ProductoController.java
│       │   │               ├── Usuario.java
│       │   │               ├── UsuarioRepository.java
│       │   │               ├── config/
│       │   │               │   ├── ApplicationConfig.java
│       │   │               │   ├── JwtAuthenticationFilter.java
│       │   │               │   ├── JwtService.java
│       │   │               │   └── SecurityConfig.java
│       │   │               ├── dto/
│       │   │               │   ├── AuthResponse.java
│       │   │               │   ├── DashboardMetricsDTO.java
│       │   │               │   ├── ItemCompraDTO.java
│       │   │               │   ├── ItemPedidoDTO.java
│       │   │               │   ├── LoginRequest.java
│       │   │               │   ├── PedidoResponseDTO.java
│       │   │               │   ├── ProductoDTO.java
│       │   │               │   └── RegisterRequest.java
│       │   │               └── service/
│       │   │                   ├── CloudinaryService.java
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
        │   ├── index.astro
        │   ├── login.astro
        │   ├── mis-compras.astro
        │   ├── productos.astro
        │   └── register.astro
        ├── store/
        │   └── cartStore.ts
        ├── styles/
        │   └── global.css
        └── types/
            └── producto.ts
```

### Actualización de estructura por autenticación

Con la incorporación de login y JWT, el monorepo también contiene estos archivos relevantes:

```text
pc-backend/
└── src/main/java/com/kimstore/pc_backend/
    ├── AuthController.java
    ├── ItemPedido.java
    ├── Pedido.java
    ├── PedidoController.java
    ├── PedidoRepository.java
    ├── Usuario.java
    ├── UsuarioRepository.java
    ├── config/
    │   ├── ApplicationConfig.java
    │   ├── JwtAuthenticationFilter.java
    │   ├── JwtService.java
    │   └── SecurityConfig.java
    └── dto/
        ├── AuthResponse.java
        ├── DashboardMetricsDTO.java
        ├── ItemCompraDTO.java
        ├── ItemPedidoDTO.java
        ├── LoginRequest.java
        ├── PedidoResponseDTO.java
        └── ProductoDTO.java

pc-frontend/
└── src/
    ├── layouts/
    │   └── Layout.astro
    ├── pages/
    │   ├── index.astro
    │   ├── mis-compras.astro
    │   ├── productos.astro
    │   ├── register.astro
    │   └── login.astro
    └── store/
        └── cartStore.ts
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

### 2. Configurar la base de datos local

Crea una base de datos para el proyecto:

```sql
CREATE DATABASE kimstore_db;
```

En desarrollo local, el proyecto usa MariaDB por defecto. En producción, puede usar PostgreSQL en Aiven mediante variables de entorno.

---

### 3. Configurar el backend

El archivo:

```text
pc-backend/src/main/resources/application.properties
```

está preparado para trabajar con variables de entorno y valores por defecto:

```properties
spring.application.name=pc-backend

spring.datasource.url=${DB_URL:jdbc:mariadb://localhost:3306/kimstore_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=${DB_DRIVER:org.mariadb.jdbc.Driver}

spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
spring.jpa.show-sql=${SHOW_SQL:true}

cloudinary.cloud_name=${CLOUDINARY_CLOUD_NAME:}
cloudinary.api_key=${CLOUDINARY_API_KEY:}
cloudinary.api_secret=${CLOUDINARY_API_SECRET:}

jwt.secret=estaEsUnaClaveSecretaMuyLargaYComplejaParaProtegerMiAPIKimStore2026
jwt.expiration=86400000
```

Esto significa que Spring Boot primero intentará leer variables de entorno. Si no existen, usará los valores locales después de `:`.

Para ejecutar en IntelliJ con MariaDB local, agrega esta variable en **Run/Debug Configurations → Environment variables**:

```text
DB_PASSWORD=TU_PASSWORD_LOCAL
```

Ejemplo:

```text
DB_PASSWORD=KimJesus21
```

> Por seguridad, no subas credenciales reales al repositorio. Las contraseñas locales y de producción deben vivir fuera del código.

Para producción, la clave JWT también debe tratarse como secreto. La versión actual define `jwt.secret` y `jwt.expiration` en `application.properties`; como mejora recomendada, puede migrarse a variables de entorno:

```properties
jwt.secret=${JWT_SECRET:clave-local-larga-para-desarrollo}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

El valor de `JWT_SECRET` debe ser largo y suficientemente aleatorio para firmar tokens HS256.

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

## ☁️ Despliegue en Render con Aiven PostgreSQL

El backend está preparado para desplegarse en Render usando PostgreSQL administrado por Aiven.

### Variables de entorno para producción

En Render, configura estas variables en el servicio del backend:

| Variable | Valor esperado |
| --- | --- |
| `DB_URL` | URL JDBC de Aiven PostgreSQL |
| `DB_USERNAME` | Usuario de la base de datos Aiven |
| `DB_PASSWORD` | Contraseña de la base de datos Aiven |
| `DB_DRIVER` | `org.postgresql.Driver` |
| `DDL_AUTO` | `update` para crear/actualizar tablas automáticamente, o `validate` si ya gestionas migraciones |
| `SHOW_SQL` | `false` en producción |
| `CLOUDINARY_CLOUD_NAME` | Nombre del cloud de Cloudinary |
| `CLOUDINARY_API_KEY` | API key de Cloudinary |
| `CLOUDINARY_API_SECRET` | API secret de Cloudinary |
| `JWT_SECRET` | Clave secreta larga para firmar tokens JWT |
| `JWT_EXPIRATION` | Duración del token en milisegundos |

Ejemplo:

```text
DB_URL=jdbc:postgresql://HOST:PUERTO/NOMBRE_DB?sslmode=require
DB_USERNAME=avnadmin
DB_PASSWORD=TU_PASSWORD_DE_AIVEN
DB_DRIVER=org.postgresql.Driver
DDL_AUTO=update
SHOW_SQL=false
CLOUDINARY_CLOUD_NAME=TU_CLOUD_NAME
CLOUDINARY_API_KEY=TU_API_KEY
CLOUDINARY_API_SECRET=TU_API_SECRET
JWT_SECRET=UNA_CLAVE_MUY_LARGA_Y_SEGURA
JWT_EXPIRATION=86400000
```

### Separación dev/prod

```text
Desarrollo local
  ↓
MariaDB local
  ↓
Valores por defecto en application.properties

Producción
  ↓
Render
  ↓
Variables de entorno
  ↓
Aiven PostgreSQL
```

### Configuración sugerida en Render

| Campo | Valor |
| --- | --- |
| Root Directory | `pc-backend` |
| Build Command | `./mvnw clean package -DskipTests` |
| Start Command | `java -jar target/pc-backend-0.0.1-SNAPSHOT.jar` |
| Runtime | Java |

Si Render detecta Windows scripts, usa los comandos de Linux porque Render ejecuta el build en un entorno Linux.

### Driver PostgreSQL

El backend incluye el driver:

```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
```

Esto permite que la misma aplicación funcione con MariaDB en desarrollo y PostgreSQL en producción, cambiando únicamente variables de entorno.

### Variables de Cloudinary

Las credenciales de Cloudinary no deben subirse al repositorio. En local se configuran desde IntelliJ, terminal o variables de usuario de Windows. En Render se configuran como environment variables.

```text
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
```

### CORS para frontend local y producción

`SecurityConfig` habilita CORS para permitir que Astro consuma el backend desde distintos orígenes:

```text
http://localhost:4321
https://kim-store2-0.vercel.app
```

También permite los métodos necesarios para el CRUD y el preflight del navegador:

```text
GET, POST, PUT, DELETE, OPTIONS
```

Y acepta headers clave para autenticación:

```text
Authorization, Content-Type
```

---

## 🔌 Flujo de Comunicación

```text
Usuario
  ↓
Interfaz Astro + TypeScript
  ↓ window.location.search + URLSearchParams
Parámetros buscar/page/size
  ↓ fetch()
API REST Spring Boot
  ↓ Controller con @Valid y @RequestParam
ProductoDTO / Parámetros de consulta
  ↓ Service
Producto Entity
  ↓ Repository + Spring Data JPA / Hibernate + PageRequest
MariaDB
```

El frontend actúa como cliente de la API: captura datos del usuario, valida estructuras mediante TypeScript y envía solicitudes HTTP al backend. El backend procesa las operaciones del inventario y persiste la información en MariaDB.

En producción, la capa de persistencia apunta a Aiven PostgreSQL mediante variables de entorno, sin modificar el código fuente.

### Flujo de autenticación JWT

```text
Administrador
  ↓
/login en Astro
  ↓ POST /api/auth/login
LoginRequest(username, password)
  ↓
AuthenticationManager + UsuarioRepository
  ↓
BCrypt valida la contraseña encriptada
  ↓
JwtService genera token
  ↓
AuthResponse(token, role)
  ↓
localStorage.kimstore_token
localStorage.kimstore_role
  ↓
La UI muestra acciones admin solo si role = ROLE_ADMIN
  ↓
Authorization: Bearer <token> en POST / PUT / DELETE
  ↓
JwtAuthenticationFilter valida el token
  ↓
Spring Security valida token y rol
```

En el frontend, `ProductForm.astro` envía el token al crear productos. La vista principal `/productos` también adjunta el token al editar y eliminar productos. Además, la interfaz oculta los controles administrativos con `hidden` y los revela únicamente para `ROLE_ADMIN`, mientras el backend mantiene la autorización real con `hasRole("ADMIN")`.

### Flujo de carrito y compra

```text
Usuario
  ↓
Añadir al carrito desde una tarjeta
  ↓
cartStore.ts con persistentAtom tipado
  ↓
localStorage.kimstore_cart
  ↓
Modal del carrito
  ↓
Quitar producto solo del carrito o Finalizar Compra
  ↓
POST /api/pedidos/comprar
  ↓
PedidoController.procesarCompra()
  ↓
Validación de stock por producto
  ↓
Descuento de stock con @Transactional
  ↓
Creación de Pedido + ItemPedido
  ↓
Respuesta de éxito o error de stock
  ↓
limpiarCarrito() y recarga para mostrar inventario actualizado
```

El carrito vive en el navegador y no modifica la base de datos hasta que el usuario presiona **Finalizar Compra**. El botón **Quitar** elimina productos únicamente de `kimstore_cart`; no borra productos de la tienda ni llama al endpoint `DELETE`.

El backend recibe el carrito mediante `ItemCompraDTO`, que contiene solo los datos mínimos necesarios para procesar la compra:

```java
public record ItemCompraDTO(
    Long id,
    int cantidad
) {}
```

Con el nuevo módulo de pedidos, cada compra queda asociada al usuario autenticado:

```text
Usuario autenticado
  ↓
Pedido(fecha, total, usuario)
  ↓
ItemPedido(productoId, nombreProducto, cantidad, precioUnitario)
  ↓
PedidoRepository
  ↓
GET /api/pedidos/mis-compras
  ↓
PedidoResponseDTO + ItemPedidoDTO
  ↓
/mis-compras en Astro
```

`ItemPedido` guarda una copia del nombre y precio unitario al momento de comprar. Esto permite que el historial conserve el ticket real aunque después cambie el nombre o precio del producto en el inventario.

### Flujo de creación y actualización

```text
Formulario Astro
  ↓
FormData con ProductoDTO como Blob + imagen opcional
  ↓
fetch(POST / PUT multipart/form-data)
  ↓
ProductoController
  ↓
ProductoDTO validado
  ↓
CloudinaryService si hay imagen
  ↓
URL pública guardada en imageUrl
  ↓
ProductoService
  ↓
ProductoRepository
  ↓
MariaDB
```

Este flujo evita que el frontend trabaje directamente con la entidad JPA, reduciendo acoplamiento y dejando el backend preparado para crecer con reglas de negocio más complejas.

Además, el backend acepta una actualización rápida con JSON:

```text
productos.astro
  ↓
fetch(PUT /api/productos/{id})
Content-Type: application/json
Authorization: Bearer <token>
  ↓
ProductoController.actualizarJson()
  ↓
ProductoService.actualizar()
```

Esto permite editar datos básicos sin subir imagen, mientras que el flujo `multipart/form-data` queda disponible para actualizaciones con archivo.

### Flujo de búsqueda y paginación

```text
URL del navegador
  ↓
/productos?buscar=hp&page=0
  ↓
JavaScript cliente con URLSearchParams
  ↓
fetch(GET /api/productos?buscar=hp&page=0&size=6)
  ↓
ProductoController
  ↓
ProductoService.buscarPorNombrePagina()
  ↓
ProductoRepository.findByNombreContainingIgnoreCase(..., Pageable)
  ↓
Respuesta Page<Producto>
  ↓
Render dinámico de tarjetas y controles Anterior/Siguiente
```

### Flujo de imagen de producto

```text
Usuario selecciona imagen
  ↓
ProductForm.astro crea FormData
  ↓
producto = Blob JSON
imagen = archivo opcional
  ↓
ProductoController recibe @RequestPart
  ↓
CloudinaryService sube la imagen
  ↓
Cloudinary devuelve URL pública
  ↓
Producto.imageUrl se guarda en la base de datos
  ↓
ProductCard muestra la imagen o un placeholder
```

---

## 🛡️ Buenas Prácticas Aplicadas

- ✅ **Separación de responsabilidades:** backend y frontend viven en carpetas independientes dentro del monorepo.
- ✅ **Arquitectura por capas:** entidades, repositorios y controladores mantienen el backend ordenado y fácil de extender.
- ✅ **Capa de servicio:** `ProductoService` centraliza la lógica de negocio y mantiene el controller ligero.
- ✅ **Uso de DTOs:** `ProductoDTO` separa los datos recibidos por la API de la entidad persistida en la base de datos.
- ✅ **Validaciones declarativas:** `@Valid` y `jakarta.validation` ayudan a rechazar datos incompletos o inválidos.
- ✅ **Búsqueda semántica con Spring Data JPA:** el método `findByNombreContainingIgnoreCase` genera la consulta automáticamente.
- ✅ **Paginación desde backend:** `PageRequest` limita la cantidad de productos enviados al frontend.
- ✅ **Render dinámico del catálogo:** búsqueda y paginación leen la URL del navegador y actualizan la lista con `fetch`.
- ✅ **Carga de imágenes externa:** Cloudinary almacena las imágenes y la base de datos conserva solo la URL.
- ✅ **Envío multipart robusto:** el frontend usa `FormData` y `Blob` para enviar DTO + archivo en una sola petición.
- ✅ **Tipado fuerte en frontend:** TypeScript permite trabajar con interfaces claras y reduce errores en tiempo de desarrollo.
- ✅ **Aserciones del DOM controladas:** el frontend puede interactuar con elementos HTML de forma explícita y segura.
- ✅ **API REST estándar:** uso de métodos HTTP semánticos para operaciones CRUD.
- ✅ **Persistencia desacoplada:** Spring Data JPA abstrae el acceso a MariaDB.
- ✅ **Configuración por entorno:** `application.properties` usa `${VARIABLE:valor_por_defecto}` para separar desarrollo y producción.
- ✅ **Credenciales fuera del repositorio:** las contraseñas se configuran desde IntelliJ, terminal, Render o el proveedor de nube.
- ✅ **Rutas protegidas:** crear, editar y eliminar productos requiere autenticación con JWT.
- ✅ **Autenticación stateless:** Spring Security no crea sesiones y valida cada petición protegida con `Authorization: Bearer <token>`.
- ✅ **Contraseñas encriptadas:** los administradores se guardan con BCrypt, no en texto plano.
- ✅ **CORS explícito:** el backend permite únicamente los orígenes esperados del frontend local y desplegado.
- ✅ **Carrito persistente:** Nano Stores guarda el carrito en `localStorage` con la llave `kimstore_cart`.
- ✅ **Compra transaccional:** `@Transactional` evita compras parciales si un producto no tiene stock suficiente.
- ✅ **Descuento de inventario:** `/api/pedidos/comprar` resta stock y guarda el pedido solo cuando la compra es válida.
- ✅ **Separación carrito/tienda:** quitar productos del carrito no elimina productos del inventario.
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

## 🖼️ Imágenes de Producto

Los productos pueden incluir una imagen opcional.

### Backend

- `Producto` contiene el campo `imageUrl`.
- `ProductoDTO` acepta `imageUrl` como string opcional.
- `ProductoController` recibe `multipart/form-data` en `POST` y `PUT`.
- `CloudinaryService` sube la imagen y devuelve la URL pública.
- `ProductoService` guarda `imageUrl` en la entidad.

### Frontend

- `ProductForm.astro` incluye un input `type="file"` opcional.
- El formulario crea un `FormData`.
- El DTO se envía como `Blob` con `type: "application/json"`.
- La imagen se envía como parte `imagen`.
- `ProductCard.astro` muestra la imagen si existe.
- Si no hay imagen, se muestra un placeholder visual con 💻.

Ejemplo conceptual de la petición:

```text
POST /api/productos
Authorization: Bearer <token>
Content-Type: multipart/form-data

producto: Blob JSON con nombre, descripcion, precio, stock, imageUrl
imagen: archivo opcional
```

Para edición básica sin cambiar imagen, el frontend puede enviar JSON:

```text
PUT /api/productos/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Laptop HP",
  "descripcion": "Equipo actualizado",
  "precio": 15000,
  "stock": 5,
  "imageUrl": "https://res.cloudinary.com/..."
}
```

---

## 🔎 Búsqueda y Paginación

El catálogo permite buscar productos por nombre y navegar resultados en páginas de 6 tarjetas.

### Backend

- `ProductoController` recibe `buscar`, `page` y `size` con `@RequestParam`.
- `ProductoService` usa `buscarPorNombrePagina(String texto, int page, int size)`.
- `ProductoRepository` devuelve `Page<Producto>` para resultados paginados.
- Si `buscar` viene vacío, se usa `findAll(PageRequest)`.
- Si `buscar` contiene texto, se usa `findByNombreContainingIgnoreCase(texto, pageRequest)`.

### Frontend

- `productos.astro` renderiza la estructura base del catálogo e inventario.
- El catálogo se carga en cliente con `fetch`.
- `URLSearchParams(window.location.search)` lee `buscar` y `page`.
- La respuesta del backend se lee desde `data.content`.
- `data.totalPages` alimenta los controles **Anterior** y **Siguiente**.
- Si no hay resultados, la interfaz muestra un mensaje contextual.

Ejemplos de navegación en frontend:

```text
http://localhost:4321/productos?page=0
http://localhost:4321/productos?page=1
http://localhost:4321/productos?buscar=hp
http://localhost:4321/productos?buscar=laptop&page=0
```

---

## 🛒 Carrito y Compra

El frontend incluye un carrito persistente para simular el flujo inicial de una tienda en línea.

### Frontend

- `src/store/cartStore.ts` crea un store persistente y tipado con `persistentAtom`.
- El carrito se guarda en el navegador bajo la llave `kimstore_cart`.
- `agregarAlCarrito(producto)` agrega productos o incrementa cantidad si ya existen.
- `eliminarDelCarrito(productoId)` quita un producto solo del carrito.
- `limpiarCarrito()` vacía el carrito después de una compra exitosa.
- `Layout.astro` muestra un carrito flotante con contador reactivo.
- El modal del carrito permite revisar productos, quitar artículos y finalizar compra.

### Backend

- `ItemCompraDTO` representa cada producto comprado con `id` y `cantidad`.
- `PedidoController.procesarCompra()` recibe la lista del carrito.
- El backend valida que cada producto exista.
- Si no hay stock suficiente, responde con error y no completa la compra.
- Si todo está correcto, descuenta el stock de cada producto.
- `Pedido` registra fecha, total y usuario comprador.
- `ItemPedido` guarda el detalle congelado de cada producto comprado.
- `PedidoRepository` permite consultar pedidos por usuario ordenados del más reciente al más antiguo.
- `GET /api/pedidos/mis-compras` devuelve el historial usando `PedidoResponseDTO` e `ItemPedidoDTO`.

Ejemplo del carrito guardado en el navegador:

```json
[
  {
    "id": 1,
    "nombre": "Laptop HP Pavilion",
    "precio": 15000,
    "cantidad": 2
  }
]
```

Payload enviado al backend:

```json
[
  {
    "id": 1,
    "cantidad": 2
  }
]
```

Respuesta del historial de compras:

```json
[
  {
    "id": 7,
    "fecha": "26/05/2026 09:00",
    "total": 32000.0,
    "items": [
      {
        "nombreProducto": "Laptop HP Pavilion",
        "cantidad": 2,
        "precioUnitario": 16000.0
      }
    ]
  }
]
```

> Nota técnica: el store del carrito ya fue migrado a `cartStore.ts`, con interfaces `Producto` y `CartItem` para mantener consistencia con TypeScript.

---

## 🧠 Estado Actual del Proyecto

KimStore 2.0 ya cuenta con:

- ✅ Monorepo con backend y frontend separados.
- ✅ API REST para CRUD de productos.
- ✅ Entidad `Producto` conectada a MariaDB mediante JPA.
- ✅ `ProductoRepository` usando `JpaRepository`.
- ✅ `ProductoService` como capa de lógica de negocio.
- ✅ `ProductoDTO` para entrada limpia de datos.
- ✅ Campo `imageUrl` en productos.
- ✅ Servicio `CloudinaryService` para subir imágenes.
- ✅ Validaciones con `@Valid` y Jakarta Validation.
- ✅ Búsqueda por nombre ignorando mayúsculas/minúsculas.
- ✅ Paginación backend con `Page`, `Pageable` y `PageRequest`.
- ✅ Frontend Astro consumiendo la API con `fetch`.
- ✅ Componentes visuales `ProductCard` y `ProductForm`.
- ✅ Tipos TypeScript en `src/types/producto.ts`.
- ✅ Visualización de imagen real o placeholder por producto.
- ✅ Manejo visual cuando el backend no está disponible.
- ✅ Controles de paginación **Anterior** y **Siguiente**.
- ✅ Carga dinámica del catálogo desde el cliente para respetar query params.
- ✅ Preparación para despliegue en Render con Aiven PostgreSQL.
- ✅ Variables de entorno para separar base de datos local y producción.
- ✅ Entidad `Usuario` y `UsuarioRepository` para administradores.
- ✅ Login en `/login` desde Astro.
- ✅ Registro en `/register` conectado a `POST /api/auth/register`.
- ✅ Endpoint `POST /api/auth/login` para generar JWT.
- ✅ `AuthResponse` devuelve `token` y `role`.
- ✅ `JwtService` y `JwtAuthenticationFilter` para validar tokens.
- ✅ `SecurityConfig` con rutas públicas, rutas protegidas por rol, CORS y sesión stateless.
- ✅ Dashboard admin con métricas de inventario en `/api/productos/dashboard/metrics`.
- ✅ Usuario administrador inicial `kim` con contraseña `123456` para desarrollo.
- ✅ Operaciones protegidas con header `Authorization: Bearer <token>`.
- ✅ Control visual por rol con `kimstore_role` en `localStorage`.
- ✅ Acciones de administrador ocultas para usuarios no administradores.
- ✅ Store de carrito persistente con Nano Stores.
- ✅ Carrito migrado a `cartStore.ts` con interfaces TypeScript.
- ✅ Botón **Añadir al carrito** en tarjetas del catálogo.
- ✅ Carrito flotante con contador reactivo.
- ✅ Modal de carrito con productos, cantidades, subtotales y total general.
- ✅ Botón **Quitar** para remover productos solo del carrito.
- ✅ Botón **Finalizar Compra** conectado al backend.
- ✅ Descuento de stock después de compra exitosa.
- ✅ Entidades `Pedido` e `ItemPedido` para guardar historial de compras.
- ✅ `PedidoRepository` para consultar compras por usuario.
- ✅ `PedidoController` con compra autenticada e historial.
- ✅ Página `/mis-compras` para mostrar pedidos del usuario.

---

## 🧭 Próximas Mejoras Sugeridas

- 🔁 Convertir también las respuestas del backend de `Producto` a `ProductoDTO`.
- ⚠️ Agregar manejo global de errores con `@ControllerAdvice`.
- 🔍 Agregar filtros avanzados por precio, stock o categoría.
- 🖼️ Agregar edición visual de imagen desde el modal o flujo de actualización.
- 🧹 Eliminar imágenes antiguas de Cloudinary cuando se reemplacen o borren productos.
- 🔐 Reemplazar el administrador de desarrollo por un flujo seguro de registro/gestión de usuarios.
- 🔐 Mover `jwt.secret` a variable de entorno (`JWT_SECRET`) antes de un despliegue público serio.
- 🚪 Ampliar el panel de administración con vistas separadas para usuarios, productos y pedidos.
- 📦 Separar lógica frontend en archivos TypeScript dedicados.
- 🔷 Crear un cliente API compartido para centralizar `API_URL`, headers y manejo de errores.
- 🎨 Mejorar visualmente `/productos` con filtros, estados vacíos y experiencia de usuario más cercana a e-commerce.
- 🧾 Crear una página dedicada `/carrito` o checkout completo en lugar de depender solo de SweetAlert.
- 🧮 Permitir aumentar/disminuir cantidades desde el modal del carrito.
- 🧪 Agregar pruebas unitarias para service y controller.
- 🔢 Agregar selector de tamaño de página (`size`) en la interfaz.
- 🚀 Desplegar backend en Render y conectar el frontend al dominio público de la API.

---

## 📌 Resumen

**KimStore 2.0** demuestra una implementación full stack moderna para la administración de inventario de computadoras. Su estructura como monorepo facilita el desarrollo coordinado entre frontend y backend, mientras que Spring Boot, MariaDB, Astro, TypeScript y Tailwind CSS ofrecen una base sólida para un sistema escalable, mantenible y presentable en un portafolio profesional.

Con la incorporación de `ProductoService`, `ProductoDTO`, validaciones con `@Valid`, búsqueda por nombre, paginación con Spring Data, carga de imágenes con Cloudinary, autenticación con Spring Security + JWT, carrito persistente, finalización de compra con descuento de stock y configuración por variables de entorno, el proyecto deja de ser solo un CRUD básico y empieza a tomar forma de una aplicación con arquitectura limpia, preparada para crecer hacia funcionalidades más avanzadas como catálogo, checkout, usuarios, panel administrativo y despliegue real en la nube.
