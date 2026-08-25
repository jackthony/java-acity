---
name: reactive-service-change
description: Implementa o revisa cambios de un servicio Spring WebFlux/Reactor, preservando no bloqueo, contrato, pruebas y rollback. Úsala al modificar endpoints, orquestación reactiva o integraciones I/O de Java Acity.
---

# Reactive service change

1. Lee la aceptación, el flujo afectado y las pruebas existentes.
2. Declara entrada, salida, errores, cancelación y posible backpressure.
3. Mantén el pipeline en Reactor. No uses `block()`, `subscribe()` manual ni esperas
   en producción.
4. Si aparece una API bloqueante, colócala detrás de un puerto, aíslala en un
   scheduler acotado y añade una prueba que demuestre la frontera.
5. Cambia un slice vertical pequeño y evita dependencias sin caso observable.
6. Prueba happy path, error, timeout/cancelación e invariante relevante.
7. Ejecuta `./mvnw clean verify`.
8. Reporta contrato afectado, comandos ejecutados, riesgo y rollback.

Detente si falta una decisión de producto, seguridad o datos; si un gate falla; o
si el cambio requiere ampliar la aceptación.

