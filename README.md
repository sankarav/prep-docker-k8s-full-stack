# Docker & Kubernetes Lab — Spring Boot Full-Stack Backend

A hands-on interview-prep lab demonstrating a production-style Spring Boot REST API containerized with Docker and deployed on Kubernetes. The app manages a **Products** catalog backed by PostgreSQL and is structured to explore real-world Docker Compose and K8s deployment patterns.

---

## Tech Stack

| Layer         | Technology                              |
|---------------|-----------------------------------------|
| Language      | Java 26                                 |
| Framework     | Spring Boot 4.0.6 (Web MVC, Data JDBC) |
| Database      | PostgreSQL 16                           |
| Container     | Docker (multi-stage build)              |
| Orchestration | Kubernetes (local via Docker Desktop / Minikube) |
| Build Tool    | Maven (via `./mvnw` wrapper)            |

---

## Project Structure

```
docker-k8s/
├── src/
│   └── main/
│       ├── java/edu/san/prep/dockerk8s/
│       │   ├── products/           # Product entity, controller, service, repository
│       │   └── exceptions/         # NotFoundException
│       └── resources/              # application.yml, data.sql
├── k8s/
│   └── app/                        # Kubernetes manifests
│       ├── app-configmap.yaml
│       ├── app-secrets.yaml
│       ├── backend-app-deployment.yaml
│       ├── postgres-deployment.yaml
│       └── postgres-service.yaml
├── Dockerfile                      # Multi-stage build
├── docker-compose.yaml             # Local dev stack (app + postgres)
├── samples.http                    # Sample HTTP requests
└── FIXME.md                        # Known issues tracker
```

---

## API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint              | Description         |
|--------|-----------------------|---------------------|
| GET    | `/api/products`       | List all products   |
| GET    | `/api/products/{id}`  | Get product by ID   |

**Quick test:**
```bash
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
```

---

## Getting Started

### Prerequisites

- Java 26 (for local run)
- Docker Desktop (for Docker Compose and Kubernetes)
- `kubectl` configured to your local cluster

---

### Option 1 — Run Locally (no Docker)

Start a local PostgreSQL instance first, then:

```bash
./mvnw spring-boot:run
```

The app connects to `localhost:5432` using the default `local` profile. Make sure `application.yml` has a matching local datasource config.

---

### Option 2 — Docker Compose (recommended for local dev)

Brings up both the Spring Boot app and PostgreSQL together.

```bash
# Build image and start all services
docker compose up --build

# Stop and remove containers
docker compose down
```

The app waits for Postgres to pass its health check before starting (`depends_on: condition: service_healthy`).

Services exposed:
- App → `http://localhost:8080`
- Postgres → `localhost:5432`

To rebuild only the app image (e.g. after code changes):
```bash
docker compose build app
docker compose up
```

---

### Option 3 — Kubernetes (Docker Desktop or Minikube)

#### 1. Build and load the image

The K8s deployment references `san.edu/backend:0.0.1`. Build and load it into your local cluster:

```bash
# Build the image
docker build -t san.edu/backend:0.0.1 .

# If using Minikube
minikube image load san.edu/backend:0.0.1

# If using Docker Desktop K8s — the image is already available (shared daemon)
```

#### 2. Apply manifests in order

```bash
# Config and secrets first
kubectl apply -f k8s/app/app-configmap.yaml
kubectl apply -f k8s/app/app-secrets.yaml

# Postgres (deployment + service)
kubectl apply -f k8s/app/postgres-deployment.yaml
kubectl apply -f k8s/app/postgres-service.yaml

# Backend app
kubectl apply -f k8s/app/backend-app-deployment.yaml
```

Or apply all at once:
```bash
kubectl apply -f k8s/app/
```

#### 3. Verify pods are running

```bash
kubectl get pods
kubectl get services
```

#### 4. Access the app

The backend deployment does not define a NodePort or LoadBalancer service yet. Use port-forward to reach it:

```bash
kubectl port-forward deployment/backend-deployment 8080:8080
```

Then hit `http://localhost:8080/api/products`.

---

## Docker Image Details

The `Dockerfile` uses a **multi-stage build**:

| Stage       | Base Image                    | Purpose                            |
|-------------|-------------------------------|------------------------------------|
| `builder`   | `eclipse-temurin:26-jdk-alpine` | Compile and package the JAR       |
| `deployment`| `eclipse-temurin:26-jre-alpine` | Lean runtime image (JRE only)     |

The final image only contains the packaged JAR — no source code or build tools — keeping it minimal and production-safe.

---

## Configuration

Environment variables used across profiles:

| Variable                    | Description                         |
|-----------------------------|-------------------------------------|
| `SPRING_PROFILES_ACTIVE`    | `local`, `docker`, or `k8s`         |
| `SPRING_DATASOURCE_URL`     | Full JDBC URL to PostgreSQL         |
| `SPRING_DATASOURCE_USERNAME`| DB username                         |
| `SPRING_DATASOURCE_PASSWORD`| DB password                         |

In Kubernetes, non-sensitive config is in `app-configmap.yaml` and sensitive values in `app-secrets.yaml`. The datasource URL in the backend deployment uses K8s variable substitution:

```yaml
value: jdbc:postgresql://postgres-service:5432/$(POSTGRES_DB)
```

> **Note:** K8s variable substitution uses `$(VAR)` syntax, not `${VAR}` like Spring.

---

## Known Issues

See [`FIXME.md`](./FIXME.md) for the full tracker.

| # | Issue | Workaround |
|---|-------|------------|
| 1 | Backend replicas must stay at **1** | `data.sql` tries to insert duplicate seed rows on each pod startup, causing additional replicas to crash-loop. Fix: use `INSERT ... ON CONFLICT DO NOTHING` or migrate to Flyway/Liquibase. |

---

## Key Concepts Demonstrated

- **Multi-stage Docker builds** — separate build and runtime layers
- **Docker Compose health checks** — `depends_on: condition: service_healthy` prevents race conditions
- **K8s ConfigMap vs Secret** — separating non-sensitive config from credentials
- **K8s variable substitution** — composing env vars inside a manifest (`$(VAR)`)
- **Spring profiles** — `local`, `docker`, `k8s` — one codebase, environment-specific config
- **Spring Data JDBC** — lightweight data access without full JPA overhead

---

## Next Steps / TODOs

- [ ] Add a `NodePort` or `LoadBalancer` service for the backend in K8s
- [ ] Fix replica scaling issue (migrate seed data to Flyway)
- [ ] Add `PersistentVolumeClaim` for Postgres to survive pod restarts
- [ ] Add liveness and readiness probes to the backend deployment
- [ ] Explore Horizontal Pod Autoscaler (HPA) once replica issue is fixed
- [ ] Add POST / PUT / DELETE endpoints to the Products API
