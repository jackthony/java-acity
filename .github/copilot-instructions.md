# Java Acity

- Lee `specs/001-unified-case.md`, `AGENTS.md` y la arquitectura antes de editar.
- Implementa una aceptación por cambio y conserva el contrato observable.
- Mantén dominio, aplicación y adaptadores separados.
- En rutas WebFlux compón con Reactor; no uses `block()` ni esperas bloqueantes.
- Aísla cualquier API bloqueante detrás de un puerto y un scheduler acotado.
- Escribe o ajusta pruebas y ejecuta `./mvnw clean verify`.
- Reporta comandos reales, riesgos y rollback; nunca inventes evidencia.
- No hagas merge, despliegues ni cambios de secretos sin aprobación humana.

