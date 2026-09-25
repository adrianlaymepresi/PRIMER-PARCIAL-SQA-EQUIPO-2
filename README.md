# Inventory SP3 — Primer Parcial SQA

Proyecto desarrollado para el **Primer Parcial de Software Quality Assurance (SQA)**.

Inventory SP3 es un backend desarrollado con **Spring Boot** para la gestión de categorías y productos de un inventario. El sistema permite realizar operaciones CRUD, búsquedas, manejo de imágenes y exportación de información a archivos Excel.

Para el parcial se implementaron pruebas automatizadas sobre las capas **Controller, Service y Utils**, además de pruebas funcionales de API utilizando Postman y una base de datos MySQL ejecutada mediante Docker.

---

## Integrantes

- IGNACIO ADRIAN LAYME DELGADO
- JHOMAYRA MAMANI CHOQUETICLLA
- ANDRES SEBASTIAN SOTO ASTETE
- NICOLAS ARTURO VARGAS SILVA
- ALEX JOSHUA VILLEGAZ IBAÑEZ

**Materia:** Software Quality Assurance  
**Docente:** Ing. Bergman Villarroel Juan Carlos  
**Gestión:** 2026  

---

## Objetivo del proyecto

Validar mediante pruebas automatizadas el correcto funcionamiento del backend Inventory SP3, considerando escenarios positivos, negativos y excepcionales.

Las pruebas implementadas verifican los criterios de aceptación definidos en las historias de usuario **HU-01 a HU-07**, utilizando JUnit, Mockito, MockMvc, JaCoCo y Postman.

---

## Funcionalidades evaluadas

### Categorías

- Consultar todas las categorías.
- Consultar una categoría por ID.
- Registrar categorías.
- Actualizar categorías.
- Eliminar categorías.
- Exportar categorías a Excel.

### Productos

- Consultar todos los productos.
- Consultar productos por ID.
- Buscar productos por nombre.
- Registrar productos con imagen.
- Actualizar productos.
- Eliminar productos.
- Exportar productos a Excel.

### Utilidades

- Compresión de imágenes.
- Descompresión de imágenes.
- Generación de archivos Excel para categorías.
- Generación de archivos Excel para productos.

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal |
| Spring Boot 3.1.0 | Backend REST |
| Maven Wrapper 3.9.2 | Gestión de dependencias y compilación |
| MySQL | Base de datos |
| Docker Desktop | Ejecución del servidor MySQL |
| JUnit 5 | Pruebas automatizadas |
| Mockito | Mocking de dependencias |
| MockMvc | Pruebas de Controllers |
| JaCoCo 0.8.13 | Medición de cobertura |
| Postman | Pruebas funcionales de API |
| IntelliJ IDEA | Entorno de desarrollo |
| Git / GitHub | Control de versiones |
| Jira | Organización y seguimiento del Sprint |

---

## Requisitos previos

Antes de ejecutar el proyecto se requiere:

- JDK 17.
- Docker Desktop.
- Git.
- IntelliJ IDEA u otro IDE compatible con Maven.
- Postman para ejecutar las pruebas funcionales.

El proyecto fue probado utilizando:

```text
Java 17.0.19
Maven Wrapper 3.9.2
Spring Boot 3.1.0
Windows 11
```

Para verificar las versiones instaladas:

```powershell
java --version
.\mvnw.cmd --version
docker --version
```

---

## Base de datos MySQL con Docker

El proyecto utiliza una base de datos denominada:

```text
db_inventory
```

Primero descargar la imagen de MySQL:

```powershell
docker pull mysql:latest
```

Crear el contenedor:

```powershell
docker run -d `
  --name mysql-container `
  -p 3306:3306 `
  -e MYSQL_ROOT_PASSWORD=root `
  -e MYSQL_DATABASE=db_inventory `
  mysql:latest
```

Verificar que el contenedor esté ejecutándose:

```powershell
docker ps
```

Si el contenedor ya fue creado anteriormente y se encuentra detenido:

```powershell
docker start mysql-container
```

Para comprobar que la base de datos existe:

```powershell
docker exec -it mysql-container mysql -uroot -proot -e "SHOW DATABASES;"
```

Debe aparecer:

```text
db_inventory
```

---

## Configuración de la base de datos

La aplicación utiliza la siguiente configuración de conexión:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_inventory?allowPublicKeyRetrieval=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## Ejecutar el proyecto

Con MySQL ejecutándose mediante Docker, iniciar la aplicación desde IntelliJ IDEA ejecutando:

```text
InventorySp3Application.java
```

También puede ejecutarse desde PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

La API estará disponible por defecto en:

```text
http://localhost:8080
```

---

## Pruebas automatizadas

Las pruebas se encuentran organizadas en:

```text
src/test/java/com/company/inventory/
```

Las principales capas evaluadas son:

```text
controller/
services/
util/
```

El proyecto contiene dos grupos de pruebas:

### Suite estable

Los archivos `*Test.java` contienen los casos positivos, negativos y excepcionales utilizados para validar el funcionamiento esperado del sistema.

Esta suite debe finalizar correctamente.

### Pruebas de defectos

Los archivos `*BugTest.java` reproducen comportamientos considerados incorrectos encontrados durante la evaluación.

Estas pruebas pueden permanecer en estado **FAILED** mientras el defecto correspondiente continúe presente en el código de producción.

Los defectos encontrados se encuentran documentados en el **Reporte de Bugs** del Primer Parcial.

---

## Cobertura de pruebas con JaCoCo

El proyecto utiliza **JaCoCo 0.8.13** para medir la cobertura de código de las capas evaluadas.

Para generar la cobertura oficial se ejecutan únicamente los 7 archivos correspondientes a la suite estable:

```powershell
.\mvnw.cmd "-Dtest=CategoryServiceImplTest,ProductServiceImplTest,CategoryRestControllerTest,ProductRestControllerTest,CategoryExcelExporterTest,ProductExcelExporterTest,UtilTest" clean test jacoco:report
```

Al finalizar correctamente debe mostrarse:

```text
BUILD SUCCESS
```

El reporte HTML se genera automáticamente en:

```text
target/site/jacoco/index.html
```

En caso de reabrirlo en la terminal
```powershell
start .\target\site\jacoco\index.html
```

Para abrirlo en Windows:

```powershell
Start-Process .\target\site\jacoco\index.html
```

### Cobertura obtenida en las capas evaluadas

| Capa | Cobertura |
|---|---|
| Controllers | 100 % de instrucciones |
| Services | 100 % de instrucciones y branches |
| Utils | 99 % de instrucciones y 100 % de branches |
| CategoryExcelExporter | 100 % |
| ProductExcelExporter | 100 % |
| Util | 95 % de instrucciones y 100 % de branches |

> La carpeta `target/` no se almacena en GitHub porque contiene archivos generados automáticamente por Maven y JaCoCo. El reporte puede reproducirse utilizando los comandos anteriores.

---

## Pruebas funcionales con Postman

Además de las pruebas automatizadas con JUnit, se desarrolló una colección de Postman para validar los endpoints REST utilizando el backend ejecutándose realmente y la base de datos MySQL almacenada en Docker.

Las pruebas funcionales permiten comprobar operaciones relacionadas con:

```text
POST    Crear recursos
GET     Consultar recursos
PUT     Actualizar recursos
DELETE  Eliminar recursos
```

Las pruebas de Postman complementan las pruebas realizadas con MockMvc, permitiendo validar el comportamiento del sistema en un entorno de ejecución real.

---

## Flujo de trabajo con Git

El equipo utiliza las siguientes ramas:

```text
main
dev

feature/IGNACIO-ADRIAN-LAYME-DELGADO
feature/JHOMAYRA-MAMANI-CHOQUETICLLA
feature/ANDRES-SEBASTIAN-SOTO-ASTETE
feature/NICOLAS-ARTURO-VARGAS-SILVA
feature/ALEX-JOSHUA-VILLEGAZ-IBÁÑEZ
```

Cada integrante desarrolla sus pruebas en su propia rama `feature`.

Los cambios se integran posteriormente en:

```text
feature → dev → main
```

Esto permite mantener un flujo de trabajo colaborativo y conservar evidencia de los commits realizados por cada integrante.

---

## Organización del trabajo

Para la organización del Primer Parcial se utiliza un enfoque Scrum simplificado.

Se definieron:

- Product Backlog con las historias HU-01 a HU-07.
- Sprint de 2 días.
- Sprint Backlog.
- Distribución de tareas entre los cinco integrantes.
- Seguimiento mediante Jira.
- Control de versiones mediante GitHub.

Las historias de usuario representan las funcionalidades que fueron seleccionadas para ser verificadas durante el Sprint.

---

## Evidencias del proyecto

Las evidencias del Primer Parcial incluyen:

- Ejecución de pruebas JUnit.
- Casos positivos y negativos.
- Uso de Mockito y MockMvc.
- Reporte de cobertura generado con JaCoCo.
- Pruebas funcionales ejecutadas en Postman.
- Reporte de bugs.
- Historias de usuario y criterios de aceptación.
- Sprint Backlog y seguimiento en Jira.
- Ramas y commits individuales en GitHub.

---

## Resultado

El proyecto permite demostrar la aplicación de buenas prácticas de Software Quality Assurance mediante pruebas automatizadas y funcionales sobre un backend Spring Boot.

La evaluación incluye pruebas sobre las capas **Controller, Service y Utils**, medición de cobertura con JaCoCo, reproducción de defectos mediante pruebas específicas, pruebas funcionales con Postman y trabajo colaborativo utilizando GitHub y Jira.