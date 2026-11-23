# Evidencias de pruebas de seguridad - Sprint 3

## Objetivo
Verificar los mecanismos de autenticacion y autorizacion de los servicios del sistema Banco de Preguntas.

## Resultados

| ID | Prueba | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| ESC-S1-01 | Acceso sin token | 401 Unauthorized | 401 Unauthorized | Aprobada |
| ESC-S1-02 | Token invalido | 401 Unauthorized | 401 Unauthorized | Aprobada |
| ESC-S1-03 | Token valido | 200 OK | 200 OK | Aprobada |
| ESC-S1-04 | Usuario AUTHOR accede a endpoint exclusivo ADMIN | 403 Forbidden | 403 Forbidden | Aprobada |

## Conclusion
Las pruebas realizadas permitieron comprobar que los servicios rechazan las solicitudes sin autenticacion, validan los tokens recibidos y restringen las operaciones segun los roles asignados. Los cuatro escenarios evaluados obtuvieron los resultados esperados.
