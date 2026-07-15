nombre de integrantes :
Walter Vergara
Magdalena zuñiga
Alonso contreras

contexto:
una veterinaria tenia problemas con el manejo de informacion en el establecimiento, para solucionar ese problema requieren un proyecto con minimo 10 microservicios conectados entre si 
y todas la herramientas necesarias

microservicios:

1. Citas
2. Empleados
3. Facturacion
4. Historial Mascotas
5. Identificacion
6. Inventario
7. Registro
8. Tratamiento
9. Hospitalizacion
10. Laboratorio


Documentacion Swagger

1. Citas    http://localhost:8082/swagger-ui.html
2. Empleados    http://localhost:8081/swagger-ui.html
3. Facturacion    http://localhost:8088/swagger-ui.html
4. Historial Mascotas    http://localhost:8099/swagger-ui.html
5. Identificacion    http://localhost:8085/swagger-ui.html
6. Inventario    http://localhost:8083/swagger-ui.html
7. Registro    http://localhost:8080/swagger-ui.html
8. Tratamiento    http://localhost:8089/swagger-ui.html
9. Hospitalizacion    http://localhost:8086/swagger-ui.html
10. Laboratorio    http://localhost:8087/swagger-ui.html



Introducciones basica de ejecución de proyecto

Aplicaciones necesarias:

1-Dockker
2-Compilador o editor de codigo que acepte java
3-Base de datos local en la ruta 3036

Una vez descargado el archivo zip se descomprime y le damos clic derecho y lo abrimos con el editor de codigo , despues al momento de levantar los dockers vamos a la terminal y usamos lo siguiente

-docker compose up --build

Esto permite levantar todos los dockers a la vez y despues en postamn se pueden hacer las respectivas pruebas





