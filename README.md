# 🧪 Desafío Técnico - Microservicio de Registro y Login de Usuarios

Este proyecto es un microservicio desarrollado en **Java 11**, utilizando **Spring Boot 2.5.14** y **Gradle**, que permite el registro y consulta de usuarios mediante una API REST con autenticación JWT y validaciones personalizadas.

## 📋 Requisitos

- Java 8 u 11 (usa características como `var`, `Optional`, `LocalDateTime`, `Stream API`)
- Spring Boot 2.5.14
- Gradle hasta 7.4
- Base de datos en memoria H2
- Manejo de excepciones estandarizado
- Autenticación JWT
- Cobertura mínima del 80% en pruebas unitarias (con JUnit 5)
- Diagrama de componentes y secuencia UML incluidos en la carpeta `/diagrams`
- Formato de entrada y salida exclusivamente JSON
- Contraseñas cifradas

## 🚀 Endpoints

### 🔐 POST /sign-up

**Descripción**: Crea un nuevo usuario.

### Curl 
```bash
curl --location 'http://localhost:8080/users/sign-up' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=F1ED759A2507B977AEC4D0A95F3E7A19' \
--data-raw '{
  "name": "Julio Gonzalez",
  "email": "julio@testssw.cl",
  "password": "a2asfGfdfdf4",
  "phones": [
    {
      "number": 87650009,
      "citycode": 7,
      "contrycode": "25"
    }
  ]
}'
```

### Request Body

```json
{
  "name": "Julio Gonzalez",
  "email": "julio@testssw.cl",
  "password": "a2asfGfdfdf4",
  "phones": [
    {
      "number": 87650009,
      "citycode": 7,
      "contrycode": "25"
    }
  ]
}
```

### Response
``` json

{
  "id": "UUID_GENERADO",
  "created": "2025-06-04T12:00:00",
  "lastLogin": "2025-06-04T12:00:00",
  "token": "JWT_GENERADO",
  "isActive": true
}
``` 

## posibles errores
``` json
{
  "error": [
    {
      "timestamp": "2025-06-04T12:00:00",
      "codigo": 400,
      "detail": "Email format is invalid: correo@dominio"
    }
  ]
}
``` 

``` json
{
  "error": [
    {
      "timestamp": "2025-06-04T12:00:00",
      "codigo": 400,
      "detail": "Password format is invalid: must contain exactly 1 uppercase letter, 2 digits, only lowercase letters, and be 8–12 characters long."
    }
  ]
}
``` 

``` json
{
  "error": [
    {
      "timestamp": "2025-06-04T12:00:00",
      "codigo": 409,
      "detail": "User already exist"
    }
  ]
}
``` 

``` json
{
    "error": [
        {
            "timestamp": "2025-06-06T16:16:11.054161200Z",
            "codigo": 400,
            "detail": "email cannot be empty"
        }
    ]
}
``` 

``` json
{
    "error": [
        {
            "timestamp": "2025-06-06T16:17:19.696428100Z",
            "codigo": 400,
            "detail": "password cannot be empty"
        }
    ]
}
``` 

### 🔓 GET /login/{id}
#### Descripción: Consulta los datos del usuario autenticado con su JWT.

### Curl 

```bash
curl --location 'http://localhost:8080/users/login' \
--header 'Authorization: Bearer {TOKEN}'  \
--header 'Cookie: JSESSIONID=F1ED759A2507B977AEC4D0A95F3E7A19'
```

### Authorization: Bearer {jwt_token} response

``` json
{
  "id": "UUID_DEL_USUARIO",
  "created": "2025-06-04T12:00:00",
  "lastLogin": "2025-06-04T12:30:00",
  "token": "NUEVO_JWT_GENERADO",
  "isActive": true,
  "name": "Julio Gonzalez",
  "email": "julio@testssw.cl",
  "password": "$2a$10$...HASHED",
  "phones": [
    {
      "number": 87650009,
      "citycode": 7,
      "contrycode": "25"
    }
  ]
}
``` 

## Posibles errores 

``` json
{
  "error": [
    {
      "timestamp": "2025-06-04T12:00:00",
      "codigo": 401,
      "detail": "Invalid or expired token"
    }
  ]
}
``` 

## 📦 Instrucciones de descarga

### Clona el repositorio

- git clone https://github.com/Nvatillo/technique.git
- cd nombre-proyecto

### Compila el proyecto

- ./gradlew build

### Ejecuta la aplicación

- ./gradlew bootRun o vamos a edit configurations agregamos un nuevo gradle en Run agregamos run y con eso podriamos levantar igual.

### Ejecuta las pruebas 

- ./gradlew test

## ✅ Validaciones importantes

##### Email: Debe cumplir con formato válido (ej: nombre@dominio.cl)

##### Contraseña: Largo: 8 a 12 caracteres, Solo una letra mayúsculam, Exactamente 2 números y Resto en minúsculas

##### Campos opcionales: name, phones


### Caracteristicas Test

- Framework: JUnit 5
- Cobertura mínima requerida: ≥ 80%
- Reporte generado en: build/reports/tests/test/index.html

## 🧰 Tecnologías utilizadas

| Herramienta     | Versión |
| --------------- | ------- |
| Java            | 11      |
| Spring Boot     | 2.5.14  |
| Gradle          | 7.4     |
| H2 Database     | 1.4.x   |
| Spring Security | JWT     |
| MapStruct       | 1.4.x   |
| Lombok          | ✓       |
| JUnit           | 5.x     |



