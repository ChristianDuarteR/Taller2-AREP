# Taller número 2: AREP - Christian Duarte - WEB FRAMEWORK DEVELOPMENT FOR REST SERVICES AND STATIC FILE MANAGEMENT

Este proyecto consiste en la creación de un servidor web simple en Java, capaz de manejar solicitudes REST, servir archivos estáticos y proporcionar información del sistema. El proyecto está diseñado para demostrar la implementación de un framework básico que simula algunas funcionalidades de Spark, utilizando JSON y HTML para manejar y mostrar datos.

## Getting Started

Estas instrucciones te guiarán para obtener una copia del proyecto y ejecutarlo en tu máquina local para desarrollo y pruebas.

### Prerequisites

Para instalar y ejecutar el software, necesitas tener instalado Java Development Kit (JDK) 8 o superior.

- **Java Development Kit (JDK):** Puedes descargarlo desde [la página oficial de Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) o utilizar OpenJDK.

### Installing

Sigue estos pasos para configurar el entorno de desarrollo:
1. Abrir una terminal git en su máquina. Puedes descargar Git desde [la página oficial](https://git-scm.com).
2. Clonar el repositorio con el comando: git clone https://github.com/ChristianDuarteR/Taller2-AREP.git Esto creará una copia del proyecto en tu máquina local.
3. Abrir el proyecto en tu IDE favorito (Visual Studio, IntelliJ, NetBeans, etc.).
4. Compilar el proyecto con Maven usando el comando: mvn package
5. Ejecutar el proyecto utilizando la clase que contiene el método `main`: `SimpleWebServer.java`.
6. Verás que el servidor está listo y corriendo en el puerto 8080.
7. Se recomienda que el primer llamado sea a **`/api/index`** 

## Running the tests

Para ejecutar las pruebas, utiliza el siguiente comando: mvn test

Esto ejecutará todas las pruebas en el proyecto y generará un informe detallado sobre las pruebas que se completaron con éxito.

## And coding style tests

1. testStartServices_apiWorking: Test para comprobar que la API esta encendida
2. testGreetService: Test al endpoint /greet para comprobar su funcionamiento
3. testCalculateService_add: Test al endPoint /calculate para comprobar sus operaciones
4. testCalculateService_divideByZero: Test al endPoint /calculate para comprobar sus errores
5. testSystemInfoService: Test al endPoint /info-service para comprobar que traiga la informacion
6. testIndexService: Test al endpoint /index para comprobar que traiga un HTML

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Architecture 

Este proyecto utiliza una arquitectura cliente-servidor, donde los clientes envían solicitudes HTTP al servidor. El servidor maneja la lógica de los servicios REST, responde con datos en formato JSON y HTML, y también proporciona archivos estáticos como HTML, CSS e imágenes.

## REST Endpoints

El servidor proporciona los siguientes endpoints REST:

- **`/api/greet?name=?&greeting=?`**: Devuelve un saludo personalizado.
- **`/api/calculate?operation=?&num1=?&num2=?`**: Calculadora con operaciones como (add, subtract, multiply,divide ).
- **`/api/system-info`**: Devuelve informacion del sistema en formate JSON.
- **`/api/index`**: Devuelve una pagina HTML con infomacion de la API.
- **`/api`**: Prueba para confirmar que API is working

## Static Files

El servidor también puede servir archivos estáticos, como HTML, CSS, e imágenes, ubicados en una carpeta especificada por el desarrolador gracias al metodo staticFiles(), por defecto /webroot. 

## Authors

* **Christian Duarte



