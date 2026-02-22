# 🥩 Sistema Distribuido: Carnicería en Tiempo Real (Broadcasting)

Este proyecto es una implementación avanzada en **Java** de una arquitectura Cliente-Servidor multihilo. Desarrollado para la Práctica 3 de Sistemas Distribuidos en la ESCOM (IPN), el sistema evoluciona de transacciones efímeras a un modelo de **conexiones TCP persistentes con sincronización de estado en tiempo real**.

## 🚀 Arquitectura y Conceptos Clave

A diferencia de un modelo clásico de petición-respuesta (Pull), este servidor actúa como un despachador de eventos (Push model), manteniendo a todos los nodos actualizados simultáneamente. 

* **Conexiones Persistentes:** El `Socket` se mantiene vivo durante toda la sesión del cliente, permitiendo comunicación bidireccional continua sin la sobrecarga del *handshake* repetitivo.
* **Broadcasting (Difusión):** El servidor mantiene una lista global sincronizada (`Collections.synchronizedList`) de todos los clientes activos. Ante cualquier cambio en el inventario, el servidor inyecta el nuevo estado a toda la red de forma automática.
* **Escucha Asíncrona (Hilos en Cliente):** El cliente implementa multihilo. Un hilo principal se encarga de la captura de comandos de escritura, mientras que un hilo secundario (`Thread`) dedicado a la escucha pasiva procesa las notificaciones del servidor sin bloquear la interfaz de usuario.
* **Exclusión Mutua:** Se conservan los Monitores (`synchronized`) para proteger las secciones críticas y evitar condiciones de carrera durante el procesamiento de transacciones concurrentes.

## ⚙️ Cómo compilar y ejecutar el proyecto

Para compilar y ejecutar, asegúrate de estar dentro de la carpeta donde se encuentran los archivos `.java` (usando la terminal o CMD).

**1. Compilar el código fuente:**
```bash
javac *.java

2. Levantar el Servidor:
En la misma terminal, inicia el servidor:
java ServidorCarniceria


3. Levantar los Clientes:
java ClienteCarniceria
