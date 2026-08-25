# 001 — Expediente Unificado

Version: 1.0.0  
Status: in-progress

## Intención

Construir una API Spring pequeña y defendible para recibir, verificar y decidir un
expediente con idempotencia, autorización e historial auditable.

## Aceptación

1. Repetir clave y payload devuelve el mismo expediente; otra carga con esa clave
   produce conflicto.
2. Solo se permiten transiciones definidas y todas registran actor y motivo.
3. Un fallo o timeout del verificador no deja un estado parcialmente actualizado.
4. PostgreSQL conserva expediente e historial después de reiniciar.
5. Solo un actor autorizado puede aprobar o rechazar.
6. Health, OpenAPI, logs correlacionables y métricas están disponibles.
7. Docker reproduce la solución y CI verifica código, imagen y manifiestos.

## No objetivos

- copiar lógica o datos de una empresa;
- más de un servicio de negocio, mensajería o RAG;
- desplegar automáticamente sin revisión humana;
- declarar producción, SLA o experiencia profesional no medidos;
- usar AKS antes de superar Docker y Kubernetes local.

