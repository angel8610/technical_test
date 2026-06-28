**Donato Angel Bautista Dionicio**

# Demo Pet Service

Servicio Spring Boot que consulta y guarda mascotas usando una API externa (https://petstore.swagger.io/v2/pet). 
Expone endpoints: GET /api/pet/{id} y POST /api/pet.

Arquitectura: capa Controller -> Service -> Api externo. DTOs para entrada/salida y 
AdviceController para manejo centralizado de errores. Diseño simple, f&aacute;cil de probar y 
mantener, se podr&iacute;a implemntar la arquitectura Hexagonal, pero dado el tiempo y la 
complejidad que este implica, no se realiza de este modo, tambi&eacute;n se implementar patrones 
como el Builder para los DTOs. Lo que si se hizo fue los Tests Unitarios para los componentes 
que tienen alguna funcionalidad. El servicio representar la implementaci&nacute; de la 
l&oacute;gica de negocio y los VOs para los objectos inmutables

Conexi&oacute;n a la API externa: se usa java.net.http.HttpClient definido como bean
(HttpClientConfig) y propiedades (HttpClientProperties). Justificaci&oacute;n: no añadir 
dependencias externas, usar cliente est&aacute;ndar del JDK con soporte HTTP/2. Al inyectar 
HttpClientConfig se facilita el mocking en tests y la separaci&oacute;n de responsabilidades. 
DataUtil arma/parsea JSON y HttpRequestUtil crea la base para las solicitudes.

Pruebas: JUnit + Mockito (mock de HttpClient/HttpResponse).