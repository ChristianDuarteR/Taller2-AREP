# Taller numero 2:AREP- Christian Duarte - DISEÑO Y ESTRUCTURACIÓN DE APLICACIONES DISTRIBUIDAS EN INTERNET

Un servidor web simple en Java que maneja solicitudes REST para obtener, agregar y eliminar mensajes. Este proyecto está diseñado para demostrar la implementación de un servidor básico que utiliza JSON para almacenar y manejar datos.

## Getting Started

Estas instrucciones te guiarán para obtener una copia del proyecto y ejecutarlo en tu máquina local para desarrollo y pruebas.

### Prerequisites

Para instalar y ejecutar el software, necesitas tener instalado Java Development Kit (JDK) 8 o superior.

- **Java Development Kit (JDK):** Puedes descargarlo desde [la página oficial de Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) o utilizar OpenJDK.

### Installing

Sigue estos pasos para configurar el entorno de desarrollo:
1. Abrir una terminal git en su maquina, Puedes descargarlo desde [la pagina oficial de Git](https://git-scm.com)
2. Clonar el repositorio con el comando -git clone https://github.com/ChristianDuarteR/Taller1-AREP.git se creara una copia de el proyecto sobre tu maquina virtual
3. Abrir el proyecto sobre su tu idl favorito (visual studio, InteliJ, NetBeans, etc)
4. Compilar el proyecto con Maven usando el comando -mvn package
5. Ejecutar el proyecto sobre la clase que contiene el metodo MAIN: SimpleWebServer.java
6. Vera que el servidor esta listo y corriendo sobre el puerto 8080

## Running the tests

Para correr las pruebas es necesario ejecutar el comando 
  -mvn test
Se ejecutaran todas las pruebas que se encuentren sobre el proyecto y detallara un informe de las que pasaron exitosamente

## And coding style tests

1. TestHandleGet: Esta prueba simula una peticion GET al servidor y verifica que el estado sea el correcto de un respecto GET
2. TestHandlePost: Esta prueba simula una peticion POST al servidor enviando un mensaje sencillo, y verifica que se haya creado con su respectivo stateCode 201

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Architecture 

Este proyecto utiliza una arquitectura cliente-servidor, donde los clientes envían solicitudes HTTP al servidor. El servidor se encarga de manejar la lógica de los servicios REST y responde con datos en formato JSON. Además, el servidor puede proporcionar archivos estáticos como HTML, CSS e imágenes.

## Authors

* **Christian Duarte** 


