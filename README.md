# TxDxAI  🔐🤖💼
*Empowering Secure Operations with AI-Driven Ticketing*  
## CS 2031 Desarrollo Basado en Plataformas. 💻

## Project Members 🤝


| Name                                  | GitHub User                                                           | Email                                                         |
|---------------------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------|
| Mario Angel Urpay Enriquez              | [Chumayito](https://github.com/Ronal-Jesus-Condor-Blas) | [ronal.condor@utec.edu.pe](mailto:ronal.condor@utec.edu.pe)   |
| Steve | [steve-ricapa](https://github.com/steve-ricapa)                         | [steve.ricapa@utec.edu.pe](mailto:steve.ricapa@utec.edu.pe)   |
| Adrian       | [LuissBarahona](https://github.com/LuissBarahona)                     | [luis.barahona@utec.edu.pe](mailto:luis.barahona@utec.edu.pe) |
| Diego       | [LuissBarahona](https://github.com/LuissBarahona)                     | [luis.barahona@utec.edu.pe](mailto:luis.barahona@utec.edu.pe) |





## Índice
- [Introducción](#introducción)
- [Identificación del Problema o Necesidad](#identificación-del-problema-o-necesidad)
- [Descripción de la Solución](#descripción-de-la-solución)
- [Modelo de Entidades](#modelo-de-entidades)
- [Testing y Manejo de Errores](#testing-y-manejo-de-errores)
- [Medidas de Seguridad Implementadas](#medidas-de-seguridad-implementadas)
- [Eventos y Asincronía](#eventos-y-asincronía)
- [GitHub](#github)
- [Conclusiones](#conclusiones)
- [Apéndice](#apéndice)

---

## Introducción

### Contexto ✏️  
En el ámbito de la ciberseguridad, la gestión de credenciales, tickets e historiales de atención es clave para mantener la continuidad operativa y la trazabilidad. **TxDxAI** ofrece un backend unificado que integra múltiples herramientas (Meraki, Splunk, Wazuh) y un asistente conversacional potenciado por IA para agilizar respuestas y facilitar el seguimiento.

### Objetivos del proyecto 🎯  
1. **Autenticación y autorización** segura con JWT.  
2. **Gestión de usuarios y empresas**: CRUD completo, roles ADMIN/USER.  
3. **Cifrado y administración de credenciales** (Meraki, Splunk, Wazuh) mediante Jasypt, accesibles solo por admins.  
4. **Gestión de tickets** con estados PENDING, EXECUTED, FAILED y DERIVED.  
5. **Persistencia de historial de chat** con MessageWindowChatMemory (20 mensajes).  
6. **Notificaciones asíncronas**: envío de correo al derivar un ticket.  
7. **Manejo centralizado de errores** con un `GlobalExceptionHandler` y excepciones específicas.

---

## Identificación del Problema o Necesidad

### Descripción del problema 🤔  
Las organizaciones suelen tener:
- Múltiples sistemas de monitoreo aislados.
- Flujos de ticketing manuales y poco trazables.
- Gestión dispersa de credenciales, con riesgos de seguridad.
- Poco uso de las bondades de comunicacion via API con las plataformas de seguridad.
- Complejidad de entendimiento y curva de aprendizaje para consultar datos sobre la seguridad
- Falta de privacidad para consultas de seguridad con data real(falta de un chatbot privado).
- Falta de automatizacion de ejecucion de Tickets que involucren provissioning lo que sobrecarga al equipo de TI.

Esto genera ineficiencias, demoras en la atención y potenciales brechas de seguridad.

---

## Descripción de la Solución

### Funcionalidades Implementadas 🛠️  
1. **Autenticación y Registro**  
   - `/auth/register` (primer admin) y `/auth/login`.  
   - JWT para proteger endpoints.  

2. **Gestión de Usuarios y Empresas**  
   - CRUD en `/api/users` y `/api/companies`.  
   - Solo **ADMIN** puede crear usuarios y empresas.  

3. **Gestión de Credenciales**  
   - Endpoint `/admin/credentials` para añadir credenciales cifradas.  
   - Jasypt cifra API keys, IP, puerto, usuario y contraseña.  
   - Solo admins pueden crear y leer credenciales.

4. **Gestión de Tickets**  
   - CRUD en `/api/tickets`.  
   - Estados: **PENDING**, **EXECUTED**, **FAILED**, **DERIVED**.  
   - Al derivar, se dispara un evento que envía un correo asíncrono.

5. **Asistente Conversacional**  
   - Endpoint `/api/chat` que llama a OpenAI GPT-4 vía langchain4j.  
   - Historial de hasta 20 mensajes cargado desde BD (entidad `ChatMemoryEntry`).

6. **Manejo de Errores y Validación**  
   - Validaciones con Hibernate Validator (`@Valid`).  
   - `GlobalExceptionHandler` maneja:
     - `ResourceNotFoundException` → 404  
     - `ResourceConflictException` → 409  
     - `UserAlreadyExistsException` → 409  
     - `UnauthorizeOperationException` → 403  
     - `IllegalArgumentException` → 400  

---

## Modelo de Entidades

![ER Diagram](docs/images/ERD.png)

| Entidad             | Tabla             | Atributos clave                                                                                         | Relaciones                                                                                                                                 |
|---------------------|-------------------|---------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| **Company**         | `companies`       | `id`, `name`, `created_at`                                                                              | 1─* Users<br>1─* Credentials<br>1─* Tickets                                                                                                 |
| **User**            | `users`           | `id`, `username` (único), `email` (único), `password_hash`, `role` (ADMIN/USER), `created_at`           | *─1 Company<br>1─* ChatMemoryEntry<br>1─* Ticket (createdBy)                                                                                 |
| **Credential**      | `credentials`     | `id`, `type` (MERAKI/SPLUNK/WAZUH), `api_key_encrypted`, `manager_ip`, `api_port`, `api_user`, `api_password_encrypted` | *─1 Company                                                                                                                                 |
| **Ticket**          | `tickets`         | `id`, `subject`, `description`, `status`, `created_at`, `executed_at`, `company_id`, `created_by_user_id` | *─1 Company<br>*─1 User                                                                                                                     |
| **ChatMemoryEntry** | `chat_memory`     | `id`, `message`, `sender` (USER/AGENT), `timestamp`, `user_id`                                           | *─1 User                                                                                                                                     |

---

## Testing y Manejo de Errores

- **Testing**: JUnit 5, Mockito, Testcontainers (PostgreSQL).  
- **Cobertura**: pruebas unitarias de servicios y controladores, pruebas de integración con base de datos en contenedor.  
- **Errores manejados**:
  - `ResourceNotFoundException` → 404  
  - `ResourceConflictException` → 409  
  - `UserAlreadyExistsException` → 409  
  - `UnauthorizeOperationException` → 403  
  - `IllegalArgumentException` → 400  

---

## Medidas de Seguridad Implementadas

- **JWT** para autenticación de todas las rutas protegidas.  
- **Roles y jerarquía** (`ROLE_ADMIN > ROLE_USER`).  
- Uso de `@PreAuthorize` en controladores.  
- **Cifrado** de credenciales con Jasypt (algoritmo AES-256).  
- Password hashing con BCrypt.

---

## Eventos y Asincronía

- **Envío de correos asíncrono** mediante Spring Events y `@Async`.  
- Caso de uso principal: al cambiar un ticket a estado **DERIVED**, se publica un `EmailEvent` y un listener envía el correo en background al personal de TI.

---

## GitHub

El proyecto utiliza GitHub con flujo GitFlow:  
- **Branches**: `main`, `develop`, características en ramas `feature/*`.  
- **Issues** para seguimiento de bugs y tareas.  
- **Pull Requests** con revisiones de código antes de merge.

---

## Conclusiones

- Se centralizó la gestión de usuario, empresas, credenciales y tickets.  
- Se integró un asistente conversacional con historial persistido.  
- Se implementaron notificaciones asíncronas para eventos críticos.  
- El manejo de errores y la seguridad cumplen con buenas prácticas en aplicaciones críticas.

---



## Apéndice
### Licencia
Foodtales está bajo la licencia MIT.

### Referencias
- Documentación de Node.js: https://nodejs.org/
- Documentación de PostgreSQL: https://www.postgresql.org/docs/
