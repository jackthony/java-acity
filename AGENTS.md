# Instrucciones de trabajo

## Fuente de verdad

- Parte de una aceptación de `specs/001-unified-case.md` o de un issue.
- Conserva contrato HTTP, idempotencia, transiciones e historial.
- Haz un cambio pequeño, revisable y reversible por PR.
- Si falta una decisión de producto, datos o seguridad, detente y solicítala.

## Código

- Mantén el dominio independiente de Spring y de la persistencia.
- No uses `block()` ni esperas bloqueantes en rutas WebFlux.
- Si una dependencia es bloqueante, declara la frontera, aíslala y pruébala.
- No añadas infraestructura, patrones ni dependencias sin un escenario concreto.
- No expongas secretos, datos personales ni contenido interno.

## Evidencia

- Relaciona el cambio con una aceptación concreta.
- Añade o ajusta primero la prueba que demuestra el comportamiento.
- Ejecuta `./mvnw clean verify` y reporta el resultado real.
- Registra riesgo operativo y rollback cuando cambien datos, contrato o despliegue.
- Una persona revisa y autoriza merge y despliegue.

