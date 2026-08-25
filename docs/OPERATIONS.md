# Entrega y operación

## Trunk-based para tres personas

`main` es el único trunk. Cada cambio usa una rama corta, una persona responsable y
vida máxima de dos días. No hay ramas `develop` o `release` permanentes.

Cada Pull Request exige:

- aceptación o issue enlazado y un slice pequeño;
- build, pruebas y controles de seguridad en verde;
- una aprobación ajena al autor;
- validación de QA cuando cambia una aceptación;
- squash merge y eliminación de la rama.

El ruleset de `main` deberá impedir push directo, force-push y borrado, y exigir PR,
conversaciones resueltas y checks. Se configurará al crear el remoto y conocer los
usuarios del equipo.

## Camino de entrega

1. Maven Wrapper verifica código y pruebas.
2. Docker Compose reproduce aplicación y PostgreSQL.
3. La imagen se acompaña de SBOM, scan y smoke test.
4. Kubernetes local valida Deployment, Service, probes y rollback.
5. AKS es opcional y solo añade evidencia cloud después del laboratorio local.

No se necesita una cuenta cloud para Docker, CI ni Kubernetes local. Un despliegue
en AKS consume recursos y no convierte el laboratorio en experiencia profesional.

## SonarQube local

El build genera `target/site/jacoco/jacoco.xml`. SonarQube Community Build y su
PostgreSQL se levantan con imágenes fijadas en `compose.sonar.yml`; el servidor solo
se publica en `127.0.0.1:9000`.

```powershell
Copy-Item .env.sonar.example .env.sonar
# Reemplazar el valor de SONAR_DB_PASSWORD por una contraseña aleatoria local.
docker compose --env-file .env.sonar -f compose.sonar.yml up -d
```

Después del primer ingreso, cambiar la credencial inicial, crear el proyecto/token y
mantener el token solo en memoria:

```powershell
$env:SONAR_TOKEN = Read-Host 'SONAR_TOKEN' -MaskInput
.\mvnw.cmd clean verify sonar:sonar `
  '-Dsonar.host.url=http://localhost:9000' `
  "-Dsonar.token=$env:SONAR_TOKEN" `
  '-Dsonar.qualitygate.wait=true'
Remove-Item Env:SONAR_TOKEN
```

`sonar.qualitygate.wait=true` hace fallar el comando cuando el Quality Gate falla.
Para detener el servicio preservando sus volúmenes:

```powershell
docker compose --env-file .env.sonar -f compose.sonar.yml down
```

El análisis de PR en GitHub se conectará después a SonarQube Cloud o a un servidor
alcanzable por el runner. Un SonarQube local no es accesible desde un runner hospedado.

## Dependencias y supply chain

`clean verify` exige Maven 3.9.11+, Java 17+, ejecuta pruebas y genera
`target/bom.json` en formato CycloneDX. CI conserva por 14 días el SBOM, resultados
JUnit y cobertura. Dependabot propone actualizaciones de Maven y GitHub Actions;
`Dependency Review` rechaza en PR dependencias nuevas con severidad alta o crítica.

El perfil costoso de SCA queda fuera del ciclo rápido:

```powershell
.\mvnw.cmd -Psecurity clean verify
```

GitHub lo ejecuta cada lunes o bajo demanda y falla desde CVSS 7.0. Para acelerar y
evitar límites de NVD, configurar opcionalmente el secreto `NVD_API_KEY`; nunca se
versiona. Una supresión futura deberá indicar vulnerabilidad, justificación,
responsable, compensación y fecha de vencimiento.

## Medición

Se registrarán las cinco métricas DORA para este servicio: change lead time,
deployment frequency, failed deployment recovery time, change fail rate y deployment
rework rate. Durante el laboratorio serán métricas de ensayo, claramente separadas
de resultados productivos. También se observarán tiempo hasta primera revisión,
ciclos de review, defectos escapados y éxito del rollback.

Las métricas sirven para encontrar fricción y mejorar; no son cuotas personales ni
objetivos que deban maquillarse.

Fuentes: [SonarQube con Docker](https://docs.sonarsource.com/sonarqube-community-build/server-installation/from-docker-image/set-up-and-start-container),
[cobertura Java](https://docs.sonarsource.com/sonarqube-community-build/analyzing-source-code/test-coverage/java-test-coverage)
y [Quality Gate en CI](https://docs.sonarsource.com/sonarqube-community-build/analyzing-source-code/ci-integration/overview).
