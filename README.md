# Java Acity

Backend de práctica en Java 17 y Spring para desarrollar un expediente auditable.
Es un laboratorio de portafolio: no representa un sistema productivo ni acredita
experiencia profesional por sí solo.

## Caso de uso

La API recibe un expediente con una clave de idempotencia, consulta un verificador
y controla el flujo:

    SUBMITTED -> VALIDATED -> PENDING_REVIEW -> APPROVED | REJECTED

Cada transición registra actor, fecha, motivo y versión. El dominio es neutral y no
copia procesos, datos ni nombres de una organización.

## Baseline verificado

- Java 17 y Spring Boot 3.5.16;
- Spring WebFlux y Project Reactor;
- frontera RxJava adaptada a Reactor con cancelación;
- validación, Problem Details, Actuator y Prometheus;
- cobertura JaCoCo, SBOM CycloneDX y análisis con SonarQube Community Build;
- Enforcer, Dependabot, revisión de dependencias por PR y SCA semanal;
- idempotencia, reglas de transición e historial en memoria;
- siete pruebas unitarias, de servicio y HTTP;
- CI reproducible con Maven Wrapper en GitHub Actions.

Spring es el producto fuente. La migración y evaluación agentic se desarrollan en
`Agentic-Acity`, tomando un tag inmutable de este repositorio; no se mantiene una
segunda copia del código Spring.

## Ejecutar

Requiere Java 17 o Docker.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

## Siguiente incremento

1. PostgreSQL y Spring Data JPA, aislando el acceso bloqueante del event loop.
2. OpenAPI, autenticación/autorización y pruebas de integración con Testcontainers.
3. Imagen Docker, scan de imagen, smoke test y Kubernetes local.
4. Release etiquetado como fuente para la migración a Quarkus.

El trabajo usa `main` como trunk, ramas de hasta dos días, PR pequeño, CI y revisión
ajena al autor. Los perfiles de agente ayudan a implementar o verificar; una persona
conserva la responsabilidad de alcance, revisión, merge y despliegue.

## Documentación mínima

- [Arquitectura](docs/ARCHITECTURE.md)
- [Entrega y operación](docs/OPERATIONS.md)
- [Especificación funcional](specs/001-unified-case.md)
