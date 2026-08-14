# Deploy en Railway

Este proyecto tiene varios servicios independientes. Cada uno debe desplegarse como un servicio separado en Railway (o usar Railway con monorepo y `railway.toml`).

## 1. Requisitos previos

- Cuenta en [Railway](https://railway.app/)
- CLI de Railway instalado (`npm i -g @railway/cli`) o usar el dashboard web
- Variables de entorno definidas en `.env`

## 2. Variables de entorno obligatorias (por servicio)

Con una sola instancia de Postgres (`postgres-railway`), las URLs solo cambian el nombre de la base:

```
# Ejemplo URL de Railway Postgres
DATABASE_URL=postgresql://postgres:postgres@postgres-railway.railway.internal:5432/postgres
```

### ms-a
```
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8081
SPRING_DATASOURCE_URL=postgresql://postgres:postgres@postgres-railway.railway.internal:5432/dwint_msa
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://idgs15:8761/eureka/
```

### ms-b
```
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8082
SPRING_DATASOURCE_URL=postgresql://postgres:postgres@postgres-railway.railway.internal:5432/dwint_msb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://idgs15:8761/eureka/
```

### api-gateway
```
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://idgs15:8761/eureka/
```

### authserver
```
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8083
SPRING_DATASOURCE_URL=postgresql://postgres:postgres@postgres-railway.railway.internal:5432/dwint_auth
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://idgs15:8761/eureka/
```

### idgs15 (Eureka)
```
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8761
EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
EUREKA_CLIENT_FETCH_REGISTRY=false
```

### Bases de datos (una sola instancia)
1. En Railway, crea un servicio `Database` (`PostgreSQL`). Esto genera una URL (`DATABASE_URL`).
2. Crea las 3 bases manualmente (o con un script de inicialización):
   ```sql
   CREATE DATABASE dwint_msa;
   CREATE DATABASE dwint_msb;
   CREATE DATABASE dwint_auth;
   ```
3. Usa la misma URL base (`postgresql://postgres:postgres@postgres-railway...:5432`) pero cambia el nombre de la base (`/dwint_msa`, `/dwint_msb`, `/dwint_auth`) en cada servicio.

## 3. Pasos de deploy (por servicio)

### Opción A: Dashboard Web (recomendado)

1. Ve a [railway.app](https://railway.app/) y crea un nuevo proyecto (`New Project`).
2. Selecciona `Deploy from GitHub repo` o `Empty Service`.
3. Para cada servicio (`ms-a`, `ms-b`, `api-gateway`, `authserver`, `idgs15`):
   - Crea un servicio (`+ New Service`).
   - Elige `Deploy from GitHub repo` (selecciona tu repo `DWINT/back`).
   - **Importante**: En `Root Directory`, pon la ruta del servicio (ej: `ms-a`, `ms-b`, `ms_b`, `api-gateway`, `authserver`, `idgs15`). Si no, Railway busca `Dockerfile` en la raíz del repo y falla con:
     `couldn't locate the dockerfile at path Dockerfile`.
   - Railway usará el `Dockerfile` que está en esa carpeta (`ms-a/Dockerfile`, etc.).
4. Base de datos (una sola instancia):
   - Crea un servicio `Database` (`PostgreSQL`).
   - Una vez creada, conectate con `psql` o con la interfaz de Railway y creá las 3 bases:
     ```sql
     CREATE DATABASE dwint_msa;
     CREATE DATABASE dwint_msb;
     CREATE DATABASE dwint_auth;
     ```
   - Copiá la `DATABASE_URL` de Railway y usala como base para las `SPRING_DATASOURCE_URL` (solo cambiando `/postgres` por `/dwint_msa`, etc.).

### Opción B: CLI

```bash
# Login
railway login

# Crear proyecto
railway init

# Linkear servicio ms-a (ejemplo)
cd ms-a
railway link
railway up

# Agregar variables
railway variables set SPRING_DATASOURCE_URL="..."
```

Repetir para cada servicio.

## 4. Configuración del gateway (`api-gateway`)

El gateway usa `lb://ms-a` y `lb://ms-b`, que se resuelven a través de Eureka (`idgs15`). Asegúrate de que:

- `idgs15` esté corriendo antes que los microservicios.
- `ms-a` y `ms-b` tengan `eureka.client.register-with-eureka=true`.
- El gateway tenga acceso a `idgs15` (`http://idgs15:8761/eureka/`).

## 5. Verificación post-deploy

Después de desplegar:

```bash
# Listar servicios
curl https://api-gateway-url>/api/entity-b

# Probar Feign (ms-b -> ms-a)
curl https://ms-b-url>/api/entity-b
```

Si `ms-b` no encuentra `ms-a`, verifica que:
- `clientFeignMSA` tenga `url` configurado (`http://ms-a:8081`) o que Eureka esté accesible.
- El `api-gateway` esté activo en Railway con la URL pública asignada.

## 6. Archivos generados

- `railway.json`: Configuración de deploy para Railway.
- Este archivo (`DEPLOY_RAILWAY.md`): Instrucciones de uso.
