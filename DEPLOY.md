# Deployment Guide

Deploy the Lunch App to a DigitalOcean Droplet with Docker and automatic HTTPS via Caddy + Let's Encrypt.

## Prerequisites

- A domain name (or subdomain) you control, e.g. `lunch.yourdomain.com`
- A [Docker Hub](https://hub.docker.com/) account
- A [DigitalOcean](https://www.digitalocean.com/) account

## 1. Build and push the Docker image

On your local machine:

```bash
# Build the OCI image (uses Cloud Native Buildpacks)
# On PowerShell, wrap -D args in quotes to avoid parsing issues
./mvnw spring-boot:build-image "-DskipTests" "-Dspring-boot.build-image.imageName=YOUR_DOCKERHUB_USER/lunch-app:1.0.0"

# Push to Docker Hub
docker login
docker push YOUR_DOCKERHUB_USER/lunch-app:1.0.0
```

> On Git Bash or Linux, quotes around `-D` arguments are optional.

## 2. Create a DigitalOcean Droplet

1. Log into DigitalOcean, click **Create > Droplets**
2. Choose **Ubuntu 24.04 LTS**
3. Plan: **Basic, Regular, $6/mo** (1 vCPU, 1 GB RAM) — plenty for this app
4. Region: pick one closest to you
5. Authentication: **SSH keys** (add your public key)
6. Click **Create Droplet**
7. Note the droplet's **public IP address**

## 3. Point your domain to the droplet

In your DNS provider, create an **A record**:

| Type | Name    | Value          | TTL |
|------|---------|----------------|-----|
| A    | lunch   | `<DROPLET_IP>` | 300 |

Wait a few minutes for DNS propagation. Verify with:

```bash
nslookup lunch.yourdomain.com
```

## 4. Set up the server

SSH into the droplet:

```bash
ssh root@<DROPLET_IP>
```

Install Docker:

```bash
curl -fsSL https://get.docker.com | sh

# Verify
docker --version
docker compose version
```

## 5. Deploy the application

Still on the server:

```bash
# Create app directory
mkdir -p /opt/lunch-app
cd /opt/lunch-app
```

Create the **Caddyfile** (`/opt/lunch-app/Caddyfile`):

```
lunch.yourdomain.com {
    reverse_proxy app:8080
}
```

Create **docker-compose.yml** (`/opt/lunch-app/docker-compose.yml`):

```yaml
services:
  app:
    image: YOUR_DOCKERHUB_USER/lunch-app:1.0.0
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/lunchapp
      SPRING_DATASOURCE_USERNAME: ${DB_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      LUNCH_APP_USERNAME: ${APP_USERNAME}
      LUNCH_APP_PASSWORD: ${APP_PASSWORD}
    depends_on:
      postgres:
        condition: service_healthy

  postgres:
    image: postgres:17
    restart: unless-stopped
    environment:
      POSTGRES_DB: lunchapp
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${DB_USERNAME} -d lunchapp"]
      interval: 5s
      timeout: 3s
      retries: 5

  caddy:
    image: caddy:2
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
      - "443:443/udp"
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile:ro
      - caddy_data:/data
      - caddy_config:/config
    depends_on:
      - app

volumes:
  pgdata:
  caddy_data:
  caddy_config:
```

Create the **.env** file (`/opt/lunch-app/.env`) with your actual values:

```
DB_USERNAME=appuser
DB_PASSWORD=GENERATE_A_STRONG_PASSWORD_HERE
APP_USERNAME=pavel
APP_PASSWORD=YOUR_APP_PASSWORD_HERE
```

Lock it down and start everything:

```bash
chmod 600 .env
docker compose up -d
```

## 6. Verify

```bash
# Check all 3 containers are running
docker compose ps

# Check app logs
docker compose logs app

# Check Caddy obtained the certificate
docker compose logs caddy
```

Open `https://lunch.yourdomain.com` in your browser. You should see the login page with a valid HTTPS certificate.

## 7. Updating the app

From your local machine:

```bash
# Build new image with a new tag
./mvnw spring-boot:build-image "-DskipTests" "-Dspring-boot.build-image.imageName=YOUR_DOCKERHUB_USER/lunch-app:1.1.0"
docker push YOUR_DOCKERHUB_USER/lunch-app:1.1.0
```

On the server:

```bash
cd /opt/lunch-app
# Update the image tag in docker-compose.yml, then:
docker compose pull app
docker compose up -d
```

## Firewall (recommended)

In the DigitalOcean console, create a firewall allowing only:

| Type  | Port | Source       |
|-------|------|--------------|
| SSH   | 22   | Your IP only |
| HTTP  | 80   | Anywhere     |
| HTTPS | 443  | Anywhere     |

Port 80 must stay open for Let's Encrypt HTTP challenge. Caddy automatically redirects HTTP to HTTPS.

## How it works

- **Caddy** automatically obtains and renews Let's Encrypt certificates — no extra configuration needed beyond the domain name. It also handles HTTP-to-HTTPS redirects.
- **Postgres data** is persisted in a Docker volume (`pgdata`), surviving container restarts.
- **Flyway** runs database migrations automatically on app startup.
- The dev-only `spring-boot-docker-compose` dependency is `optional`/`runtime`, so it does not activate in production. The app connects to Postgres via `SPRING_DATASOURCE_*` environment variables.
