# 🛡️ Authentication Server (Spring Boot + JWT)
> Secure authentication & authorization using Spring Boot, JWT, and Spring Security.

This project is a **Spring Boot-based Authentication Server** that provides **JWT-based authentication** with login, signup, token refresh, and user role management.

---

## 🚀 Features
- ✅ **User Signup & Login**
- ✅ **JWT Token Generation & Validation**
- ✅ **Role-Based Access Control**
- ✅ **Token Refresh Support**
- ✅ **Single Active Token Per User**
- ✅ **Spring Profiles (`dev`, `prod`) Support**

---

## ⚙️ **Installation & Setup**

### 1️⃣ **Clone the Repository**
```sh
git clone https://github.com/inspire17/authentication_server.git
cd authentication_server
```

### 2️⃣ **Configure Environment Variables**
Modify application.properties or use application-dev.properties for development:

```properties
spring.application.name=authentication-server
server.servlet.context-path=/api

spring.datasource.url=jdbc:postgresql://localhost:5432/****
spring.datasource.username=postgres
spring.datasource.password=****
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.jwt.secret="*******************************************************"
spring.jwt.token-validity=36000000


# Enable Spring logs - Log level - DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.DispatcherServlet=DEBUG
logging.level.org.springframework.boot.web.servlet=DEBUG
logging.level.org.springframework.security=DEBUG
```

### 3️⃣ **Run the Application**
```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
or
```shell
mvn clean package && java -jar target/authentication_server.jar --spring.profiles.active=dev
```

## 📌 API Endpoints

### 🔑 Authentication Endpoints

| Method | Endpoint                              | Description                          |
|--------|--------------------------------------|--------------------------------------|
| POST   | `/api/auth/signup`                   | Register a new user                 |
| POST   | `/api/auth/login`                    | Authenticate user & return JWT      |
| POST   | `/api/auth/refresh_token`            | Refresh expired token               |
| GET    | `/api/auth/username_check?text={username}` | Check if a username is available |

```shell
curl --location 'http://localhost:8080/api/auth/signup' \
--header 'Content-Type: application/json' \
--data '{
    "username":"achu",
    "password":"ichu"
}'

```

```shell
curl --location 'http://localhost:8080/api/auth/user_login' \
--header 'Content-Type: application/json' \
--data '{
    "username":"abhi",
    "password":"ichu"
}'
```

```shell
curl --location 'http://localhost:8080/api/auth/refresh_token' \
--header 'Content-Type: text/plain' \
--data 'eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiVVNFUlMifV0sInN1YiI6ImFiaGkiLCJpYXQiOjE3MzgxNTA3MDAsImV4cCI6MTczODE4NjcwMH0.DTcpIy1ICLQ7iHhTMBGYtOu_n2WIrnhLgJkGTP6zIZk'
```

```shell
curl --location 'http://localhost:8080/api/auth/username_check?q=ab'
```

---
# *Happy coding! 🚀🔥*
---