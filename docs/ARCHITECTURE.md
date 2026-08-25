# Arquitectura

## Slice vertical

    HTTP/WebFlux -> aplicación -> dominio -> puerto de repositorio
                                      |     -> puerto de verificación
                                      |
                                      +-> historial y reglas de estado

La API expone:

- `POST /api/v1/cases`: creación idempotente;
- `GET /api/v1/cases/{id}`: estado e historial;
- `POST /api/v1/cases/{id}/decisions`: aprobación o rechazo.

El dominio no depende de Spring. Los adaptadores actuales son un repositorio en
memoria y un verificador RxJava determinista; este último se adapta una sola vez a
`Mono` y propaga cancelación.

## Reactividad y persistencia

WebFlux/Reactor se usa en la orquestación I/O. No se mezcla `block()` con el event
loop. JPA/Hibernate es bloqueante: en el siguiente incremento se aislará detrás del
puerto de repositorio y en un scheduler acotado. El objetivo didáctico es poder
explicar el límite; no fingir que JPA se vuelve reactivo.

PostgreSQL deberá conservar expediente, clave idempotente e historial. Se mantendrá
un fake determinista para CI y Testcontainers cubrirá la persistencia real. No se
añade broker hasta que un escenario observable lo requiera.

## Decisiones que no se duplican

Este repositorio es la única fuente Spring. `Agentic-Acity` fija un tag y migra un
slice a Quarkus con la misma suite contractual. Los cambios funcionales se resuelven
aquí antes de actualizar el tag de migración.

