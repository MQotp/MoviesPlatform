# Securing the Connection with HTTPS — A Step-by-Step Guide

> A learning guide for the Movies Platform project: **why plain HTTP leaks your
> credentials, how HTTPS fixes it, and exactly what we changed to enable it in dev.**

---

## 1. The Problem: Why HTTP Is Unsafe

When the Angular app logs a user in, it sends this to the backend:

```json
POST /api/auth/login
{ "username": "admin", "password": "adminpass" }
```

The password is sent **in plaintext**. That is *by design* — the server needs the
raw password to verify it against the BCrypt hash stored in the database.

> 🔑 **Key idea:** BCrypt protects the password *at rest* (in the DB).
> It does **nothing** to protect the password *in transit* (on the network).

Over **HTTP**, that request travels unencrypted. Anyone positioned between the
browser and the server can read it:

- Someone on the same Wi-Fi / coffee-shop network
- A compromised router, proxy, or ISP
- An attacker performing a man-in-the-middle (MITM) attack

This is called a **leak**: the credentials are exposed on the wire. The same
applies to the **JWT** we send back — if stolen, an attacker can impersonate the
user until it expires.

### ❌ The wrong fix: hashing the password in the browser

A common misconception is "let's encrypt/hash the password in Angular before
sending it." This does **not** help:

- The hash simply *becomes* the new password — steal it, replay it.
- It breaks Spring Security, which expects the raw password to verify the hash.

**Client-side hashing is not a substitute for a secure channel.**

---

## 2. The Solution: HTTPS (TLS)

**HTTPS = HTTP over TLS.** TLS encrypts the *entire* connection before any data
is sent, so even if someone intercepts the traffic, they see only ciphertext.

This gives us three guarantees:

| Guarantee        | What it means                                              |
|------------------|------------------------------------------------------------|
| **Confidentiality** | Nobody on the network can read the password or JWT.     |
| **Integrity**       | The data can't be tampered with in transit.             |
| **Authenticity**    | The client confirms it's talking to the real server.    |

### How the two layers fit together

```
Browser  ──(1) TLS encrypts the channel──►  Server
         ── plaintext password travels  ──►  (safe: channel is encrypted)
                                              (2) BCrypt verifies & stores hash
```

1. **TLS** protects the password **in transit**.
2. **BCrypt** protects the password **at rest**.

You need *both*. Neither replaces the other.

---

## 3. What We Need to Enable HTTPS in Dev

Our stack has **two** servers, so both must speak HTTPS:

1. **Spring Boot backend** (the API) → port `8443`
2. **Angular dev server** (the app) → port `4200`

HTTPS requires a **certificate + private key** (a "keystore"). In production this
comes from a trusted Certificate Authority (e.g. Let's Encrypt). In **dev** we use
a **self-signed certificate** — free and instant, with a one-time browser warning.

---

## 4. Step-by-Step: Backend (Spring Boot)

### Step 4.1 — Generate a self-signed keystore

Run from the `backend/` folder (adjust the `keytool` path to your JDK if needed):

```bash
keytool -genkeypair -alias movies -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore src/main/resources/keystore.p12 \
  -validity 3650 -storepass changeit \
  -dname "CN=localhost, OU=dev, O=MoviesPlatform, L=Cairo, C=EG"
```

This creates `keystore.p12` containing the cert + private key.

### Step 4.2 — Configure Spring Boot

In `src/main/resources/application.properties`:

```properties
# HTTPS / TLS (dev)
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD:changeit}
server.ssl.key-alias=movies
```

> 🔒 The password uses `${SSL_KEYSTORE_PASSWORD:changeit}` — it reads an
> environment variable and falls back to `changeit` only for dev. Never hardcode
> real secrets.

### Step 4.3 — Update the CORS origin

The Angular app is now HTTPS too, so the allowed origin must match:

```properties
ANGULAR_APP_URL=https://localhost:4200
```

---

## 5. Step-by-Step: Frontend (Angular)

### Step 5.1 — Put the API URL in the environment files

Instead of hardcoding the URL, we keep dev and prod values separate.

`src/environments/environment.ts` (dev):

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'https://localhost:8443/api'
};
```

`src/environments/environment.prod.ts` (prod):

```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://your-domain.example/api'
};
```

### Step 5.2 — Read it in `api-urls.ts`

```typescript
import { environment } from '../../environments/environment';

export const API_BASE_URL = environment.apiBaseUrl;
```

### Step 5.3 — Serve the dev server over HTTPS

In `angular.json`, under `architect → serve → options`:

```json
"options": {
  "browserTarget": "frontend:build",
  "ssl": true
}
```

Angular auto-generates a temporary cert for `https://localhost:4200`.

---

## 6. Step-by-Step: Avoid Leaking the Keystore

A certificate's **private key is a secret**. Committing `keystore.p12` to git
would leak it. Add to `.gitignore`:

```gitignore
### TLS keystore (do not commit) ###
src/main/resources/keystore.p12
*.p12
```

> 💡 Each developer generates their own dev keystore (Step 4.1). It is never
> shared or committed.

### Externalize the other secrets too

The keystore password is not the only secret. The database password and the JWT
signing secret must **never** sit in `application.properties` either. We read them
from environment variables instead:

```properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

Note there is **no fallback** here (unlike the keystore's `:changeit`). The dev
cert is a throwaway, but the DB password and JWT secret are real — the app should
**refuse to start** rather than run with a hardcoded secret. Provide them via your
IDE run configuration or OS environment variables (see Step 7-style setup).

> ⚠️ Externalizing now does **not** erase a secret already pushed to git history.
> If a real secret was ever committed, **rotate it** (change the actual password /
> key) — that is the only way to make the leaked value worthless.

---

## 7. Running & Trusting the Certs

```bash
# Backend  → https://localhost:8443
./mvnw spring-boot:run

# Frontend → https://localhost:4200
ng serve
```

Because the certs are self-signed, the browser shows a security warning.

> ⚠️ **Important first-run step:** Visit **`https://localhost:8443`** directly
> once and accept the warning. Otherwise the browser **silently blocks** the API
> calls and login fails with no clear error. Then open `https://localhost:4200`
> and accept that warning too.

**No-warning option:** [`mkcert`](https://github.com/FiloSottile/mkcert) creates
locally-trusted dev certificates so the browser stops complaining.

---

## 8. Verification Checklist

- [ ] Backend starts on `https://localhost:8443` (note the **s**).
- [ ] Frontend loads on `https://localhost:4200`.
- [ ] Login works; browser DevTools → Network shows the request scheme as **HTTPS**.
- [ ] `keystore.p12` does **not** appear in `git status`.

---

## 9. Dev vs Production — Know the Difference

| Aspect            | Dev (this guide)              | Production                                  |
|-------------------|-------------------------------|---------------------------------------------|
| Certificate       | Self-signed (`keytool`)       | Trusted CA (e.g. Let's Encrypt, auto-renew) |
| TLS termination   | Inside Spring Boot            | Usually a reverse proxy (Nginx/Caddy/LB)    |
| Browser warning   | Yes (must accept once)        | None                                        |

In production you typically **don't** terminate TLS in Spring Boot. A reverse
proxy handles the real certificate and forwards traffic internally. Spring then
needs:

```properties
server.forward-headers-strategy=framework
```

so it correctly reads `X-Forwarded-Proto` from the proxy, plus an **HSTS** policy
to force HTTPS.

---

## 10. Summary — What You Learned

1. **HTTP leaks credentials** because data travels unencrypted.
2. **Hashing in the browser does not fix this** — you need a secure channel.
3. **HTTPS/TLS encrypts the channel**, protecting data *in transit*; **BCrypt**
   protects it *at rest*. Use both.
4. Enabling HTTPS in dev = **a keystore + config on both servers**.
5. **Never commit the private key** — gitignore the keystore.
6. **Production differs**: trusted CA certs, usually via a reverse proxy.