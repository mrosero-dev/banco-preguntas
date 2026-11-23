# Cliente de escritorio (Java Swing)

Interfaz de escritorio separada del backend. No tiene base de datos ni reglas de negocio: cada pantalla consume por HTTP y JSON los microservicios del proyecto. Es un módulo Maven del proyecto raíz (`frontend-client`); las instrucciones para levantar el sistema completo están en el `README.md` de la raíz.

## Estructura

```text
presentation/  Vistas Swing (LoginView, MainMenuView, CreateQuestionView, GUIQuestions,
               AdminReviewView, ReviewerView, NotificationsView) y QuestionForm reutilizable
client/        Clientes REST: IdentityClient, QuestionClient, ReviewClient,
               NotificationClient (sobre RestClient con java.net.http y Jackson)
model/         Question, User, ReviewAssignment, Notification y enums de estado y rol
config/        ServiceUrls (URL base de cada microservicio)
```

Las llamadas HTTP se ejecutan fuera del hilo de Swing (`Background`, basado en `SwingWorker`).

## Autenticación

El inicio de sesión obtiene un token JWT directamente de Keycloak (flujo de contraseña, solo para laboratorio) y lo envía como `Bearer` en todas las llamadas. Después consulta `GET /api/users/me` en Identity Service para conocer el rol y el identificador del usuario.

## Pantalla y microservicio

| Pantalla | Rol | Servicio y endpoint |
|---|---|---|
| Inicio de sesión | todos | Keycloak (token) + Identity `GET /api/users/me` |
| Crear pregunta (HU-1.1) | Autor | Question `POST /api/questions` |
| Mis preguntas (HU-1.2, HU-1.3) | Autor | Question `GET /api/questions`, `PUT`, `POST …/submit-for-review`, `POST …/return-to-draft` |
| Ver pregunta, observaciones e historial (HU-2.3) | Autor | Question `GET /{id}` y Review `GET /api/reviews/{questionId}` y `/history` |
| Asignar revisores (HU-2.1) | Administrador | Review `GET /pending`, `POST /assign` y Identity `GET /api/users?role=REVIEWER` |
| Mis preguntas asignadas (HU-2.2) | Revisor | Review `GET /reviewer/{id}`, `POST /{questionId}/decision` |
| Mis notificaciones | todos | Notification `GET /api/notifications`, `PATCH /{id}/read` |

## Ejecución

Con el backend levantado con Docker Compose (ver README de la raíz), desde la carpeta raíz:

```bash
mvn -pl frontend-client exec:java
```

Las URLs se pueden cambiar con las variables `IDENTITY_URL`, `QUESTION_URL`, `REVIEW_URL`, `NOTIFICATION_URL` y `KEYCLOAK_TOKEN_URL`, o con propiedades de sistema (`-Dquestion.url=...`).

## Pruebas

```bash
mvn -pl frontend-client test
```

Ejecuta JUnit 5 sobre las capas `client`, `config` y `model` con un servidor HTTP simulado, sin necesidad del backend.
