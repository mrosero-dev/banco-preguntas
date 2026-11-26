# Banco de Preguntas Saber PRO

Proyecto académico de Ingeniería de Software II. Incluye cuatro microservicios Spring Boot, un cliente de escritorio Java Swing, RabbitMQ y autenticación centralizada con Keycloak/JWT.

## Qué resuelve

Un banco de preguntas de selección múltiple con única respuesta, al estilo Saber Pro, con un ciclo de revisión entre docentes:

1. El **autor** crea la pregunta (contexto, pregunta directa, cuatro distractores, respuesta correcta, justificación, bibliografía, competencia, tema, subtema y dificultad). El sistema aplica una validación estructural y la guarda en BORRADOR.
2. El autor la envía a revisión (PENDIENTE DE REVISIÓN) y el **administrador** asigna de uno a tres revisores (EN REVISIÓN); los revisores reciben una notificación. El administrador también es notificado cuando un autor envía una pregunta a revisión.
3. Cada **revisor** aprueba o rechaza (rechazar exige observaciones). Con todas las evaluaciones, la pregunta pasa a APROBADA, o a RECHAZADA si hay al menos un rechazo, y se notifica al autor y al administrador.
4. El autor consulta el historial de revisiones y sus observaciones, crea una versión mejorada de una pregunta RECHAZADA y la reenvía en una nueva ronda.

Historias de usuario: HU-1.1 a HU-1.3 (segundo corte) y HU-2.1 a HU-2.3 (tercer corte).

## Arquitectura

- **Microservicios** con base de datos propia: `question-service`, `review-service`, `identity-service` y `notification-service`. Se comunican por eventos en RabbitMQ y por REST.
- **Arquitectura limpia / hexagonal** en cada servicio: paquetes `api` (controladores y DTO), `application` (casos de uso), `domain` (modelo, estados, validaciones y puertos) e `infrastructure` (JPA, mensajería y adaptadores). Las reglas de negocio viven en el dominio y la infraestructura depende de él mediante interfaces de repositorio.
- **Seguridad**: Keycloak emite JWT con los roles `ADMIN`, `AUTHOR` y `REVIEWER`; cada servicio valida el token como OAuth2 Resource Server y autoriza por rol y por propietario.
- **Despliegue**: Docker Compose con volúmenes independientes para cada base de datos.

La capa `application` define puertos de salida (por ejemplo `QuestionEventPort` y `ReviewEventPort`) que implementa la infraestructura con RabbitMQ, de modo que la dependencia apunta siempre hacia el dominio.

**Patrones de diseño:** Builder y Director (creación de preguntas), Template Method (validación), State (ciclo de vida de la pregunta), Factory Method y Decorator (notificaciones), Adapter (proveedor de correo) y Ports & Adapters (repositorios y mensajería).

Repositorio: https://github.com/mrosero-dev/banco-preguntas

## Tecnología

Java 17, Spring Boot 3.5, Spring Security (OAuth2 Resource Server), Spring Data JPA, H2, RabbitMQ, Keycloak 26, MapStruct, springdoc OpenAPI, JUnit 5 y Mockito, Maven multimódulo, Docker y Docker Compose, y Java Swing para el cliente.

## Estructura del repositorio

```text
question-service/      preguntas, validación estructural, estados y listado
review-service/        asignación de revisores, evaluaciones, rondas e historial
identity-service/      perfiles de usuario y consulta de identidad
notification-service/  notificaciones internas y correo por SMTP (MailHog en Compose)
frontend-client/       cliente de escritorio Swing
keycloak/              realm importable (roles, clientes y usuarios de demostración)
tests/                 colección Postman, prueba de carga k6 y evidencias de seguridad
docker-compose.yml     orquestación del backend
```

## Componentes y puertos

| Componente | Puerto | Función |
|---|---:|---|
| question-service | 8080 | Creación, validación, consulta y estados de preguntas |
| identity-service | 8081 | Perfiles locales (sin credenciales) y consulta de identidad; autenticación delegada a Keycloak |
| review-service | 8082 | Asignación, evaluación e historial de rondas de revisión |
| notification-service | 8083 | Notificaciones internas y correo por SMTP real (MailHog en Compose) |
| RabbitMQ | 5672 / 15672 | Mensajería asíncrona y consola de administración |
| MailHog | 1025 / 8025 | Servidor SMTP de pruebas y bandeja web donde se ven los correos reales enviados |
| Keycloak | 8180 | Emisión y validación de tokens JWT |
| frontend-client | escritorio | Aplicación Java Swing |

## Requisitos previos

- Docker Desktop con Docker Compose v2.
- JDK 17 o superior.
- Maven 3.9 o superior (para compilar/ejecutar el cliente Swing y las pruebas).

## Inicio recomendado: todo el backend con Docker Compose

Desde la carpeta raíz del proyecto:

```bash
cp .env.example .env
docker compose up --build -d
```

En Windows PowerShell, si no existe `cp`, usa `Copy-Item .env.example .env`.

Verifica el estado:

```bash
docker compose ps
docker compose logs -f identity-service question-service review-service notification-service
```

Keycloak: http://localhost:8180 (administración: `admin` / `admin_dev_2026`).
RabbitMQ: http://localhost:15672 (`banco` / `banco_dev_2026`).
Correos enviados: http://localhost:8025 (MailHog).
Swagger:
- http://localhost:8080/swagger-ui.html
- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html
- http://localhost:8083/swagger-ui.html

El realm `banco-preguntas` se importa automáticamente al crear por primera vez el volumen de Keycloak. El cliente de escritorio solicita el token a Keycloak usando Resource Owner Password Credentials (flujo de laboratorio); el backend valida firma, emisor y roles del JWT.

### Usuarios de demostración

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | admin@unicauca.edu.co | admin123 |
| Autor | autor1@unicauca.edu.co | autor123 |
| Autor | autor2@unicauca.edu.co | autor123 |
| Revisor | revisor1@unicauca.edu.co | revisor123 |
| Revisor | revisor2@unicauca.edu.co | revisor123 |
| Revisor | revisor3@unicauca.edu.co | revisor123 |

Los usuarios de demostración se importan en Keycloak y también se inicializan en Identity Service. Los identificadores locales 1–6 se corresponden con el claim `user_id` de los tokens de demostración.

## Ejecutar el cliente Swing

Con los servicios levantados por Compose, abre otra terminal en la raíz:

```bash
mvn -pl frontend-client exec:java
```

El cliente se conecta a `localhost` por defecto. Se pueden cambiar URLs con `IDENTITY_URL`, `QUESTION_URL`, `REVIEW_URL`, `NOTIFICATION_URL` y `KEYCLOAK_TOKEN_URL`.

## Persistencia

Cada microservicio usa su propia base H2 en modo archivo. Docker Compose monta volúmenes independientes:

- `identity_data`
- `question_data`
- `review_data`
- `notification_data`

RabbitMQ y Keycloak también tienen volúmenes persistentes. Los datos sobreviven a `docker compose down` y a reinicios normales.

Si actualizas desde una versión anterior, ejecuta `docker compose down -v`: `identity-service` ya no almacena contraseñas (solo Keycloak las gestiona) y el esquema de su base cambió.

Para borrar intencionalmente todos los datos y reiniciar las cuentas/realm desde cero:

```bash
docker compose down -v
```

**Advertencia:** `down -v` elimina las bases de datos, los mensajes de RabbitMQ y el estado del realm de Keycloak.

## HU-2.3: historial de revisiones

El autor puede seleccionar una pregunta y usar **HISTORIAL DE REVISIONES**. El endpoint `GET /api/reviews/{questionId}/history` exige rol AUTHOR y compara el claim `user_id` del JWT con el autor registrado. El historial conserva las decisiones, observaciones, fecha, revisor y número de ronda; al reenviar una pregunta rechazada, la ronda anterior ya no se elimina.

Versionado del contenido: al crear la versión mejorada (`POST /api/questions/{id}/return-to-draft`, solo desde RECHAZADA) Question Service guarda una copia inmutable del contenido revisado (tabla `question_versions`), incrementa el número de versión y conserva las anteriores. `GET /api/questions/{id}/versions` las devuelve de la más reciente a la más antigua. La versión N de la pregunta corresponde a la ronda de revisión N de Review Service, porque ambas avanzan una vez por cada ciclo de rechazo y reenvío.

## Seguridad

- Keycloak emite JWT para los roles `ADMIN`, `AUTHOR` y `REVIEWER`.
- Los cuatro microservicios actúan como OAuth2 Resource Server y validan los tokens.
- Los endpoints de creación/edición de preguntas requieren AUTHOR; la asignación requiere ADMIN; la evaluación requiere REVIEWER.
- El acceso a la historia de una pregunta valida además que el autor autenticado sea el propietario.
- El cliente Swing utiliza el token Bearer para las llamadas REST.

La cuenta de servicio `notification-service` tiene el rol `SERVICE` (declarado en el realm) para consultar a los administradores en Identity Service. El realm solo se importa al crear el volumen de Keycloak: si ya existía uno de una versión anterior, ejecuta `docker compose down -v` y vuelve a levantar para que se aplique.

El flujo de contraseña directa está habilitado únicamente para facilitar la demostración del cliente de escritorio en el entorno académico. Para un despliegue real debe migrarse a Authorization Code + PKCE y usar HTTPS, secretos fuera del repositorio y políticas de producción de Keycloak.

## Sesión del cliente

Al pulsar CERRAR SESIÓN el cliente elimina el token de memoria en ese momento y vuelve a la pantalla de inicio de sesión. Si un servicio responde 401 a una llamada autenticada (token expirado), el cliente descarta el token, cierra las ventanas y muestra el inicio de sesión con el aviso "La sesión expiró". La vigencia del token la define Keycloak (`accessTokenLifespan` del realm).

## Historial por versiones (HU-2.3)

HISTORIAL DE REVISIONES abre una ventana con una entrada por versión (Versión N, ronda N). Al seleccionar una versión se ven sus evaluaciones (revisor, decisión, fecha y observaciones) y la copia exacta del contenido que se evaluó. Las versiones anteriores son de solo lectura. CREAR NUEVA VERSIÓN (solo sobre preguntas RECHAZADAS) crea una versión nueva en BORRADOR sin modificar la anterior. Al reenviarla a revisión se notifica a los administradores y a los revisores de la ronda anterior.

## Compilar y ejecutar pruebas

```bash
mvn clean verify
```

Si el proyecto está en Windows, ubícalo en una carpeta de ruta corta (por ejemplo `D:\banco`): algunas rutas de paquetes Java son largas y el límite de 260 caracteres de Windows impide descomprimir o abrir el proyecto.

Las pruebas de controlador usan configuración de seguridad deshabilitada en `src/test/resources/application.properties`; la seguridad se verifica en la ejecución integrada con Keycloak. No se debe interpretar la ejecución unitaria aislada como prueba de integración de OAuth2.

## Pruebas de seguridad y de carga

- `tests/postman/banco-preguntas-seguridad.postman_collection.json`: obtiene tokens de Keycloak y comprueba 401 (sin token o token inválido), 403 por rol y 403 por propietario. Importar en Postman y ejecutar con el Collection Runner.
- `tests/load/k6-load-test.js`: prueba de carga del escenario de escalabilidad ESC-E1 (`k6 run tests/load/k6-load-test.js`, con `-e VUS=100` para otro nivel). Umbral: percentil 95 menor a 1 segundo.

## Prueba manual del flujo funcional

1. Inicia sesión como autor.
2. Crea una pregunta y confirma que queda en BORRADOR.
3. Envía la pregunta a revisión.
4. Inicia sesión como administrador y asigna entre uno y tres revisores.
5. Cada revisor registra su decisión; rechazar requiere observaciones.
6. Consulta el estado consolidado y las notificaciones.
7. Como autor, abre el historial de revisiones. Comprueba el revisor, decisión, fecha, observaciones y ronda.
8. Si fue rechazada, crea la versión mejorada y envíala de nuevo; verifica que la ronda anterior permanece en el historial.

## Limitaciones conocidas

- El correo se envía por SMTP real a MailHog en Docker Compose (`NOTIFICATION_EMAIL_MODE=smtp`). Para usar otro servidor SMTP basta con cambiar `SPRING_MAIL_HOST` y las demás propiedades `spring.mail.*`. Fuera de Compose el modo por defecto es simulado (consola).
- La escalabilidad se soporta arquitectónicamente mediante microservicios y mensajería, y se midió con k6 (`tests/load/k6-load-test.js`): 5 usuarios virtuales durante 30 s, 231 solicitudes, 0 % de fallos, 231 de 231 verificaciones correctas y percentil 95 de 13,76 ms (umbral: menor a 1 s). Con `-e VUS=<n>` se puede repetir con otro nivel de carga.
- H2 persistente por volumen es adecuado para el entorno académico. Para producción se recomienda una base de datos administrada (p. ej. PostgreSQL) por servicio.
- El cliente Swing muestra el historial de rondas; las versiones anteriores del contenido se consultan por la API (`/versions`) y aún no tienen pantalla.
- El flujo de contraseña de Keycloak y las credenciales de `docker-compose.yml` son de laboratorio; ver sección Seguridad.
