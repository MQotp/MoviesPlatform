# MoviesPlatform – Full Dev Setup & Troubleshooting Guide
---

# 📘 Table of Contents

## 🔧 Dev Setup Guide
- [1. Project Requirements](#1-project-requirements)
- [2. Backend Setup (Spring Boot)](#2-backend-setup-spring-boot)
- [3. Frontend Setup (Angular 7 + Local Node)](#3-frontend-setup-angular-7--local-node)
- [4. IntelliJ Run Configurations](#4-intellij-run-configurations)
- [5. Running the Full Project](#5-running-the-full-project)

---

# 🚨 Problems & Solutions
- [P1 — Spring Boot annotationProcessorPath Lombok version empty](#p1--lombok-version-is-empty)
- [P2 — JUnit Jupiter (JUnit 5) not found → switch to JUnit 4](#p2--junit-5-errors)
- [P3 — Failed to configure DataSource / Missing DB URL](#p3--datasource-auto-config-error)
- [P4 — Tomcat port 8080 already in use](#p4--tomcat-port-conflict)
- [P5 — IntelliJ cannot resolve symbol 'springframework'](#p5--intellij-cannot-resolve-symbol-springframework)
- [P6 — Angular 7 fails on Node 14 (node-sass, node-gyp, Python errors)](#p6--angular7-node14-errors)
- [P7 — Running Angular using local Node version](#p7--running-angular-with-local-node)
- [P8 — IntelliJ cannot Run/Debug backend or frontend](#p8--intellij-run--debug-not-working)

---

# ============================================
# 🔧 1. Project Requirements
# ============================================

### **Backend**
- Java 8
- Maven 3.6+
- Spring Boot 2.1.x
- IntelliJ IDEA (recommended)

### **Frontend**
- Angular 7.0.3
- **Local Node 10.x inside the project (not system Node!)**
- npm 6.x

---

# ============================================
# 🔧 2. Backend Setup (Spring Boot)
# ============================================

## ✔ Clone the repository

```bash
git clone https://github.com/<username>/MoviesPlatform.git
cd MoviesPlatform/backend

mvn clean install
mvn spring-boot:run

