# TxDxAI  🔐🤖💼
*Empowering Secure Operations with AI-Driven Ticketing*  
## CS 2031 Desarrollo Basado en Plataformas. 💻

## Project Members 🤝


| Name                                  | GitHub User                                                           | Email                                                         |
|---------------------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------|
| Mario Angel Urpay Enriquez              | [Chumayito](https://github.com/Chumayito) | [mario.urpay@utec.edu.pe](mailto:mario.urpay@utec.edu.pe)   |
| Steve Bryan Ricapa Montoya | [steve-ricapa](https://github.com/steve-ricapa)                         | [steve.ricapa@utec.edu.pe](mailto:steve.ricapa@utec.edu.pe)   |
| Adrian       | [Adrian-UTEC](https://github.com/Adrian-UTEC)                     | [adrian.poma@utec.edu.pe](mailto:adrian.poma@utec.edu.pe) |
| Diego       | [datocher](https://github.com/datocher)                     | [diego.atoche@utec.edu.pe](mailto:diego.atoche@utec.edu.pe) |





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
En el ámbito de la ciberseguridad, la gestión de credenciales, tickets e historiales de atención es clave para mantener la continuidad operativa y la trazabilidad. **TxDxAI** ofrece un backend unificado que integra múltiples herramientas (Meraki, Wazuh ,Splunk, ...) y un asistente conversacional potenciado por IA para agilizar respuestas y facilitar el seguimiento.

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

### Tecnologías Utilizadas

   - Lenguaje y Plataforma

   - Java 17, Spring Boot 3.x

   - Dependencias Principales

   - langchain4j-open-ai-spring-boot-starter (v1.0.1-beta6): integración con OpenAI GPT-4.

   - Jasypt: cifrado de credenciales sensibles (AES-256).

   - Spring Security: autenticación y autorización con JWT.

   - Hibernate Validator: validaciones (@NotNull, @Email, etc.).

   - Spring Data JPA: ORM y repositorios para PostgreSQL.

   - JavaMailSender: envío asíncrono de correos.

   - Base de Datos

   - PostgreSQL 14

   - Herramientas de Testing

   - JUnit 5, Mockito, Testcontainers (PostgreSQL).

   - Construcción y Gestión de Dependencias

   - Maven 3.x

## Modelo de Entidades

![Imagen de WhatsApp 2025-05-30 a las 16 31 29_7d36f2de](https://github.com/user-attachments/assets/06b709e1-6631-45fd-b89e-2644a3013617)


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
- A futuro se plantea implementar un segundo agente que ejecute accionesa partir de los tickets creados por sophia, tendra un conjunto de tools al igual que SOPHIA para poder ejecutar acciones automatizadas que resuelvan el ticket, si es que se clasifica el ticket como algo fuera de las capacidades del agente, el ticket se deriva recien al personal de TI para que lo resuelvan y vean a detalle, es ahi cuando se les notifica via correo(funcion ya implementado).
- Esto automatiza un poco mas la labor de soporte IT aligerando su carga debido a que un gran porcentaje de los tickets que se suelen generar en las empresas son de  provisioning.

---

## Detalle(Prueba):

- Dado que el lab de Wazuh que usamos de prueba tiene que estar en la red local para poder hacerle solicitudes, adjuntamos pruebas de como Sophia podia acceder a este recurso de Wazuh localmente, para un caso empresarial se haria uso de una VPN a traves del firewall de esa empresa restringiendo todas las direcciones excepto la del servidor AWS donde estara desplegada la aplicación, caso contrario y a diferencia de Wazuh, la solucion de Cisco Meraki al ser centralizada en cloud si es configurable en caunto a sus credenciales y se puede probar.
![image](https://github.com/user-attachments/assets/c73f39aa-392a-44ea-9687-4a3ec74b39fa)
![Imagen de WhatsApp 2025-05-30 a las 23 44 04_439995fe](https://github.com/user-attachments/assets/3c535d92-15c7-41f0-af6d-2caab227fcbb)
- La solucion de Meaki a la que le hacemos consultas coorresponde a una empresa llamada BVS (con oficina en San Isidro (en la que estan los equipos Cisco)) que nos cedio su ApiKey para hacer las pruebas del caso, manejar con precaución.

[TXDXAI.postman_collection.json](https://github.com/user-attachments/files/20531390/TXDXAI.postman_collection.json){
	"info": {
		"_postman_id": "75934ff7-3c7c-4129-8038-8329bac30289",
		"name": "TXDXAI",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
		"_exporter_id": "34244820",
		"_collection_link": "https://crimson-star-188955.postman.co/workspace/00820dcd-208c-462e-8894-2ec108bff78a/collection/34244820-75934ff7-3c7c-4129-8038-8329bac30289?action=share&source=collection_link&creator=34244820"
	},
	"item": [
		{
			"name": "Register Backend",
			"request": {
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"username\": \"prueba1\",\r\n  \"email\": \"prueba1@ejemplo.com\",\r\n  \"password\": \"secreto123\",\r\n  \"company\": { \"name\": \"MiEmpresa1\" }\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "{{baseUrl}}/auth/register",
					"host": [
						"{{baseUrl}}"
					],
					"path": [
						"auth",
						"register"
					]
				}
			},
			"response": []
		},
		{
			"name": "CHAT SOPHIA",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcnVlYmExIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDg2MDEzMDYsImV4cCI6MTc0ODY4NzcwNn0.CWftWzz_gXfeF3VkDbQ-W6k-FEUAET2VPiuIGlRheQY",
							"type": "string"
						}
					]
				},
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"message\":\"Listame los agentes de Wazuh\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "{{baseUrl}}/api/chat",
					"host": [
						"{{baseUrl}}"
					],
					"path": [
						"api",
						"chat"
					]
				}
			},
			"response": []
		},
		{
			"name": "Añadir credenciales a Empresa WAZUH",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcnVlYmExIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDg1OTc2NjksImV4cCI6MTc0ODY4NDA2OX0.NVhtVy053p4Rxn16MX_Bq5KA3Ok28SQZa85TBkIoeEI",
							"type": "string"
						}
					]
				},
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"type\": \"WAZUH\",\r\n  \"managerIp\": \"192.168.1.198\",\r\n  \"apiPort\":  \"55000\",\r\n  \"apiUser\":  \"wazuh-wui\",\r\n  \"apiPassword\": \"lVTjEW6wmk8RG5G*28l9QYniiPU0.HS*\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "{{baseUrl}}/admin/credentials",
					"host": [
						"{{baseUrl}}"
					],
					"path": [
						"admin",
						"credentials"
					]
				}
			},
			"response": []
		},
		{
			"name": "Añadir credenciales a Empresa Meraki",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcnVlYmExIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDg1OTc2NjksImV4cCI6MTc0ODY4NDA2OX0.NVhtVy053p4Rxn16MX_Bq5KA3Ok28SQZa85TBkIoeEI",
							"type": "string"
						}
					]
				},
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"type\": \"WAZUH\",\r\n  \"managerIp\": \"192.168.1.198\",\r\n  \"apiPort\":  \"55000\",\r\n  \"apiUser\":  \"wazuh-wui\",\r\n  \"apiPassword\": \"lVTjEW6wmk8RG5G*28l9QYniiPU0.HS*\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "{{baseUrl}}/admin/credentials",
					"host": [
						"{{baseUrl}}"
					],
					"path": [
						"admin",
						"credentials"
					]
				}
			},
			"response": []
		},
		{
			"name": "Admin crea Users con rol user",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcnVlYmExIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDg1OTc2NjksImV4cCI6MTc0ODY4NDA2OX0.NVhtVy053p4Rxn16MX_Bq5KA3Ok28SQZa85TBkIoeEI",
							"type": "string"
						}
					]
				},
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"username\": \"usuario2\",\r\n  \"email\": \"user2@ejemplo.com\",\r\n  \"password\": \"secretoUser\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "{{baseUrl}}/admin/users",
					"host": [
						"{{baseUrl}}"
					],
					"path": [
						"admin",
						"users"
					]
				}
			},
			"response": []
		},
		{
			"name": "Solicitar history chat",
			"request": {
				"method": "GET",
				"header": [],
				"url": {
					"raw": "/api/chat/history",
					"path": [
						"api",
						"chat",
						"history"
					]
				}
			},
			"response": []
		},
		{
			"name": "LOGIN",
			"request": {
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"username\": \"prueba1\",\r\n  \"password\": \"secreto123\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "{{baseUrl}}/auth/login",
					"host": [
						"{{baseUrl}}"
					],
					"path": [
						"auth",
						"login"
					]
				}
			},
			"response": []
		},
		{
			"name": "New Request",
			"request": {
				"method": "GET",
				"header": []
			},
			"response": []
		}
	]
}



## Apéndice
### Licencia
Foodtales está bajo la licencia MIT.

### Referencias
- Documentación de Node.js: https://nodejs.org/
- Documentación de PostgreSQL: https://www.postgresql.org/docs/
