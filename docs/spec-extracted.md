
===== PAGE 1 =====
PROYECTO FINAL: TECHCUP FÚTBOL
Escuela Colombiana de Ingeniería Julio Garavito – DOSW
OBJETIVO:
El presente proyecto tiene como objetivo aplicar las diferentes fases del ciclo de
vida del desarrollo de software a partir de un caso de estudio práctico y real,
permitiendo a los estudiantes apropiar los temas vistos en clase.
REGLAS:
I. Sobre el equipo de trabajo:
● El proyecto se construirá en equipos de 4 o 5 personas. Los grupos no
podrán disolverse ni modificarse a lo largo del semestre.
● Si un integrante cancela la materia, el grupo se verá reducido, pero el
alcance del proyecto y los criterios de calificación no serán alterados.
● Si un grupo presenta problemas y desea disolverse, cada integrante
trabajará por separado. El alcance y criterios de calificación no cambian.
II. Sobre las herramientas de trabajo:
● La estructura del proyecto se debe construir con Maven.
○ Pueden usar la misma estructura de los laboratorios 4 y 5.
○ Definan el artifactId basado en el nombre del proyecto.
● El versionamiento del código se realizará por medio de GitHub.
○ Definan un nombre adecuado basado en la reglas entregadas en
clase.
○ Cree el repositorio con las opciones de incluir los archivos README
y gitignore.
○ No olviden la correcta creación y gestión de las ramas.
● La planeación del proyecto se realizará en Jira.
● El framework de gestión del proyecto será: Scrum + Kanban.
● No olviden incluir al profesor y al monitor en los repositorios de GitHub.
● La duración de cada sprint es de una semana.
● No olvide el uso de patrones de diseño.
===== PAGE 2 =====
CRONOGRAMA:
El proyecto se debe ejecutar durante el tercer tercio: Semana 13 a Semana 16.
Tener en cuenta los siguientes hitos:
Hito Semana Actividad Responsable
1 13 Entrega de enunciado del proyecto a Profesor
(Laboratorio) estudiantes y distribución de
dominios funcionales.
2 13 ● Selección de diseño para Estudiantes
(Laboratorio) frontend.
● Selección de herramientas para
diseño de arquitectura.
● Creación de espacio en Jira.
● Creación de repositorios en
GitHub.
3 13 ● Sprint 1: Levantamiento de Estudiantes
requerimientos y diseños de
arquitectura
5 14 ● Implementación Sprint 1. Estudiantes
(Asíncrono)
6 14 ● Sprint Review Sprint 1. Estudiantes y
(Sesión ● Planeación Sprint 2. profesor
laboratorio)
7 15 ● Implementación Sprint 2. Estudiantes
(Asíncrono)
8 15 ● Sprint Review: Sprint 2. Estudiantes y
(Sesión ● Planeación Sprint 3. profesor
Laboratorio) ● Preparación presentación final.
9 16 ● Sprint Review: Sprint 3. Estudiantes y
(Sesión ● Preparación presentación final. profesor
Laboratorio)
[TABLE]
Hito | Semana | Actividad | Responsable
1 | 13
(Laboratorio) | Entrega de enunciado del proyecto a
estudiantes y distribución de
dominios funcionales. | Profesor
2 | 13
(Laboratorio) | ● Selección de diseño para
frontend.
● Selección de herramientas para
diseño de arquitectura.
● Creación de espacio en Jira.
● Creación de repositorios en
GitHub. | Estudiantes
3 | 13 | ● Sprint 1: Levantamiento de
requerimientos y diseños de
arquitectura | Estudiantes
5 | 14
(Asíncrono) | ● Implementación Sprint 1. | Estudiantes
6 | 14
(Sesión
laboratorio) | ● Sprint Review Sprint 1.
● Planeación Sprint 2. | Estudiantes y
profesor
7 | 15
(Asíncrono) | ● Implementación Sprint 2. | Estudiantes
8 | 15
(Sesión
Laboratorio) | ● Sprint Review: Sprint 2.
● Planeación Sprint 3.
● Preparación presentación final. | Estudiantes y
profesor
9 | 16
(Sesión
Laboratorio) | ● Sprint Review: Sprint 3.
● Preparación presentación final. | Estudiantes y
profesor

===== PAGE 3 =====
TECHCUP FÚTBOL
Plataforma digital para la gestión del torneo semestral de fútbol del Programa de
Ingeniería de Sistemas, Ingeniería de Inteligencia Artificial, Ingeniería de
Ciberseguridad e Ingeniería Estadística de la Escuela Colombiana de Ingeniería.
1. Introducción
Los programas de Ingeniería de Sistemas, IA, Ciberseguridad y Estadística realizan
cada semestre un torneo interno de fútbol en el que participan estudiantes de
distintos semestres. Aunque la actividad tiene alta acogida, su organización
actualmente depende de procesos manuales: mensajes por WhatsApp,
formularios aislados y hojas de cálculo.
Esta forma de manejo genera desorden, retrasos y confusión entre participantes y
organizadores. Muchos estudiantes no conocen el proceso de inscripción, los
equipos se completan tarde, los pagos se verifican manualmente y la información
del torneo (fechas, reglas, resultados) se encuentra dispersa.
TECHCUP FÚTBOL propone desarrollar una plataforma web que centralice toda la
gestión del torneo, permitiendo que estudiantes, capitanes y organizadores
interactúen en un solo sistema organizado y transparente.
2. Problema
Actualmente, el torneo presenta las siguientes dificultades:
● El proceso de inscripción no es claro para los participantes.
● Los capitanes tienen problemas para completar sus equipos.
● Los pagos no se verifican de forma rápida y es un proceso manual.
● Los resultados y la tabla de posiciones se actualizan manualmente.
● Las llaves eliminatorias se organizan a mano.
● No existe historial ni estadísticas del torneo.
● La información oficial está dispersa.
● Esto ocasiona retrasos, errores administrativos y conflictos entre los
participantes.
===== PAGE 4 =====
3. Objetivo General
Diseñar e implementar una plataforma web que permita gestionar de forma
organizada, centralizada y transparente el torneo semestral de fútbol de los
programas de Ingeniería de Sistemas, IA, Ciberseguridad y Estadística.
4. Objetivos Específicos
1. Permitir la inscripción individual de estudiantes.
2. Facilitar la creación y administración de equipos.
3. Permitir a los equipos buscar jugadores por posición.
4. Registrar y verificar pagos mediante comprobantes de consignación.
5. Publicar calendario, reglamento y fechas importantes.
6. Registrar partidos y resultados.
7. Mostrar tabla de posiciones y llaves eliminatorias.
8. Permitir visualizar las alineaciones de los equipos.
5. Actores del Sistema
Actor Función
Estudiante Se registra como jugador y puede ser capitán.
Graduado Se registra como jugador y puede ser capitán.
Profesor Se registra como jugador y puede ser capitán.
Personal Administrativo Se registra como jugador y puede ser capitán.
Familiares Se registra como jugador o invitado y puede ser capitán.
Capitán Se registra como jugador y el organizador del torneo
adiciona el rol de Capitán permitiendo crear y administrar
un equipo.
Organizador Se registra como invitado y el administrador del sistema
asigna el rol de Organizador, permitiendo administrar el
torneo.
[TABLE]
Actor | Función
Estudiante | Se registra como jugador y puede ser capitán.
Graduado | Se registra como jugador y puede ser capitán.
Profesor | Se registra como jugador y puede ser capitán.
Personal Administrativo | Se registra como jugador y puede ser capitán.
Familiares | Se registra como jugador o invitado y puede ser capitán.
Capitán | Se registra como jugador y el organizador del torneo
adiciona el rol de Capitán permitiendo crear y administrar
un equipo.
Organizador | Se registra como invitado y el administrador del sistema
asigna el rol de Organizador, permitiendo administrar el
torneo.

===== PAGE 5 =====
El Organizador no podrá aumentar privilegios a
administrador a ningún usuario,
Árbitro Es creado por el organizador y podrá visualizar la
información de los partidos a arbitrar.
Administrador Control total del sistema.
6. Arquitectura funcional del sistema
El sistema está compuesto por los siguientes dominios funcionales
(microservicios):
● Servicio de identidad.
● Servicio de usuarios y jugadores.
● Servicio de equipos.
● Servicio de torneos.
● Servicio de competencia.
● Orquestador (Middleware / API Gateway).
7. Funcionalidades del Sistema
1.1 Servicio de identidad
● Descripción: Este servicio es el responsable de la gestión de autenticación,
autorización y control de acceso al sistema. Es el punto central de
seguridad y permite garantizar que solo usuarios autorizados accedan a los
recursos.
Funcionalidad Descripción
Registro de El sistema permitirá el registro de usuarios con los
usuarios siguientes tipos de correo:
● Institucional: estudiante, profesor, administrativo
o graduado.
● Personal: familiar.
Durante el registro se deberá capturar:
● Nombre completo.
● Correo electrónico.
● Contraseña.
[TABLE]
 | El Organizador no podrá aumentar privilegios a
administrador a ningún usuario,
Árbitro | Es creado por el organizador y podrá visualizar la
información de los partidos a arbitrar.
Administrador | Control total del sistema.

[TABLE]
Funcionalidad | Descripción
Registro de
usuarios | El sistema permitirá el registro de usuarios con los
siguientes tipos de correo:
● Institucional: estudiante, profesor, administrativo
o graduado.
● Personal: familiar.
Durante el registro se deberá capturar:
● Nombre completo.
● Correo electrónico.
● Contraseña.

===== PAGE 6 =====
● Relación con la Escuela: estudiante, profesor,
administrativo, graduado o familiar.
● Programa académico con el cual tiene relación.
● Semestre (si es estudiante).
● Estado: Activo o Inactivo. Por defecto, se crea
activo.
● Fecha de nacimiento.
● Identificación y tipo.
El administrador se registrará directamente a nivel de
base de datos con todos los permisos habilitados.
Todos los usuarios al momento de ser registrados
pueden seleccionar el rol de jugador o invitado,
excepto el árbitro. A los árbitros los creará el
organizador del torneo.
Inactivar usuario Un usuario se puede inactivar siempre y cuando no
esté vinculado a un equipo que esté inscrito a un
torneo activo o en progreso.
Autenticación El sistema permitirá a los usuarios autenticarse
(Login) mediante correo electrónico y contraseña.
Como resultado, se generará un token JWT para
permitir el acceso a los demás servicios.
La contraseña debe estar cifrada.
Autorización (Roles El sistema manejará los siguientes roles: invitado,
y Permisos) jugador, capitán, organizador, árbitro y administrador.
Cada rol tendrá permisos específicos sobre los
diferentes servicios (En cada servicio se detalla qué
acción puede hacer cada rol).
El administrador del sistema podrá asignar, remover y
consultar el rol de un usuario.
Gestión de sesión ● Generación de JTW al autenticarse.
● Validación de token en cada solicitud.
● Expiración de la sesión.
Control de acceso El servicio debe validar:
● Que el usuario esté autenticado.
[TABLE]
 | ● Relación con la Escuela: estudiante, profesor,
administrativo, graduado o familiar.
● Programa académico con el cual tiene relación.
● Semestre (si es estudiante).
● Estado: Activo o Inactivo. Por defecto, se crea
activo.
● Fecha de nacimiento.
● Identificación y tipo.
El administrador se registrará directamente a nivel de
base de datos con todos los permisos habilitados.
Todos los usuarios al momento de ser registrados
pueden seleccionar el rol de jugador o invitado,
excepto el árbitro. A los árbitros los creará el
organizador del torneo.
Inactivar usuario | Un usuario se puede inactivar siempre y cuando no
esté vinculado a un equipo que esté inscrito a un
torneo activo o en progreso.
Autenticación
(Login) | El sistema permitirá a los usuarios autenticarse
mediante correo electrónico y contraseña.
Como resultado, se generará un token JWT para
permitir el acceso a los demás servicios.
La contraseña debe estar cifrada.
Autorización (Roles
y Permisos) | El sistema manejará los siguientes roles: invitado,
jugador, capitán, organizador, árbitro y administrador.
Cada rol tendrá permisos específicos sobre los
diferentes servicios (En cada servicio se detalla qué
acción puede hacer cada rol).
El administrador del sistema podrá asignar, remover y
consultar el rol de un usuario.
Gestión de sesión | ● Generación de JTW al autenticarse.
● Validación de token en cada solicitud.
● Expiración de la sesión.
Control de acceso | El servicio debe validar:
● Que el usuario esté autenticado.

===== PAGE 7 =====
● Que tenga permisos para ejecutar la acción
solicitada.
Auditoria Se registrarán las siguientes acciones:
● Inicio de sesión.
● Registro de usuario.
● Cierre de sesión.
7.2 Servicio de usuarios y Jugadores
● Descripción: Gestiona la información de los participantes del torneo y su
perfil deportivo.
Funcionalidad Descripción
Actualizar usuario El usuario y el administrador del sistema podrán
actualizar la información básica del usuario: Nombre
completo, relación con la Escuela: estudiante, profesor,
administrativo, graduado o familiar, programa
académico con el cual tiene relación, semestre (si es
estudiante).
El correo y la contraseña no se podrán modificar.
Crear perfil Cada jugador podrá crear perfil deportivo indicando:
deportivo. posición de juego predefinida (portero, defensa,
volante, delantero), número dorsal predefinido, foto.
Actualizar perfil El jugador podrá actualizar de su perfil todos los datos
deportivo siempre y cuando no esté asignado a un equipo.
Eliminar perfil El sistema no permitirá eliminar un perfil deportivo.
deportivo
Solicitud de Los jugadores podrán enviar solicitudes de vinculación
vinculación a a un equipo disponible. (1 Solicitud)
equipo
Invitaciones Los capitanes de los equipos podrán:
● Recibir invitaciones de jugadores.
● Aceptar o rechazar invitaciones.
[TABLE]
 | ● Que tenga permisos para ejecutar la acción
solicitada.
Auditoria | Se registrarán las siguientes acciones:
● Inicio de sesión.
● Registro de usuario.
● Cierre de sesión.

[TABLE]
Funcionalidad | Descripción
Actualizar usuario | El usuario y el administrador del sistema podrán
actualizar la información básica del usuario: Nombre
completo, relación con la Escuela: estudiante, profesor,
administrativo, graduado o familiar, programa
académico con el cual tiene relación, semestre (si es
estudiante).
El correo y la contraseña no se podrán modificar.
Crear perfil
deportivo. | Cada jugador podrá crear perfil deportivo indicando:
posición de juego predefinida (portero, defensa,
volante, delantero), número dorsal predefinido, foto.
Actualizar perfil
deportivo | El jugador podrá actualizar de su perfil todos los datos
siempre y cuando no esté asignado a un equipo.
Eliminar perfil
deportivo | El sistema no permitirá eliminar un perfil deportivo.
Solicitud de
vinculación a
equipo | Los jugadores podrán enviar solicitudes de vinculación
a un equipo disponible. (1 Solicitud)
Invitaciones | Los capitanes de los equipos podrán:
● Recibir invitaciones de jugadores.
● Aceptar o rechazar invitaciones.

===== PAGE 8 =====
Auditoria Registrar las acciones de actualización e inactivación
de usuarios. Y de gestión del perfil.
7.3 Servicio de Equipos
● Descripción: Gestiona la creación, administración y validación de equipos.
Funcionalidad Descripción
Creación de Un capitán podrá crear un equipo indicando: nombre y
equipos colores.
Validaciones:
● Mínimo debe tener 7 jugadores.
● Máximo debe tener 12 jugadores.
● Un jugador no puede estar en dos equipos.
● Un equipo no debería tener más de un jugador
con el mismo dorsal.
● Más de la mitad de los miembros sean de los
programas de Ingeniería de Sistemas, IA,
Ciberseguridad y Estadística. Durante cada partido,
participarán 7 estudiantes por equipo.
Actualización de Un capitán podrá actualizar el nombre siempre y cuando
equipos no esté inscrito en un torneo Activo o En Progreso.
Gestión de Un capitán podrá gestionar miembros del equipo.
jugadores
Importante:
● Un jugador no se puede eliminar de un equipo si
hay un torneo Activo o En Progreso.
● El capitán aceptará la solicitud de vinculación al
equipo.
Auditoría Registrar las acciones de creación, actualización e
inactivación de equipos.
[TABLE]
Auditoria | Registrar las acciones de actualización e inactivación
de usuarios. Y de gestión del perfil.

[TABLE]
Funcionalidad | Descripción
Creación de
equipos | Un capitán podrá crear un equipo indicando: nombre y
colores.
Validaciones:
● Mínimo debe tener 7 jugadores.
● Máximo debe tener 12 jugadores.
● Un jugador no puede estar en dos equipos.
● Un equipo no debería tener más de un jugador
con el mismo dorsal.
● Más de la mitad de los miembros sean de los
programas de Ingeniería de Sistemas, IA,
Ciberseguridad y Estadística. Durante cada partido,
participarán 7 estudiantes por equipo.
Actualización de
equipos | Un capitán podrá actualizar el nombre siempre y cuando
no esté inscrito en un torneo Activo o En Progreso.
Gestión de
jugadores | Un capitán podrá gestionar miembros del equipo.
Importante:
● Un jugador no se puede eliminar de un equipo si
hay un torneo Activo o En Progreso.
● El capitán aceptará la solicitud de vinculación al
equipo.
Auditoría | Registrar las acciones de creación, actualización e
inactivación de equipos.

===== PAGE 9 =====
7.4 Servicio de Torneos
● Descripción: Administra la configuración general del torneo.
Funcionalidad Descripción
Registro de torneo El organizador podrá crear un torneo con la siguiente
información: fecha inicial, fecha final, fecha cierre de
inscripciones, cantidad de equipos, costo y estado.
Los estados posibles para un torneo son: Borrador,
Activo, En progreso o Finalizado.
Al crear el torneo, el estado por defecto es borrador.
Adicionalmente, debe incluir:
● Reglamento: PDF con el reglamento.
● Canchas: De cada cancha se conoce su nombre,
imagen y descripción.
Activar torneo El organizador podrá activar un torneo cambiando el
estado a Activo.
Iniciar torneo El organizador podrá iniciar un torneo cambiando el
estado a En Progreso. Solamente se puede iniciar un
torneo si la fecha inicial es igual a la fecha actual.
Finalizar torneo El organizador podrá finalizar un torneo cambiando el
estado a Finalizado. Solamente se puede finalizar un
torneo si la fecha final es igual o posterior a la fecha
actual.
Eliminar torneo El organizador podrá eliminar un torneo siempre y
cuando el estado sea en Borrador.
Inscripción y Cada capitán del equipo realiza la inscripción del
pagos equipo a un torneo cargando el comprobante de pago.
Al cargar el comprobante, el estado de la inscripción
será Revisión.
El organizador del torneo revisará el comprobante de
pago y aprobará o no la inscripción.
[TABLE]
Funcionalidad | Descripción
Registro de torneo | El organizador podrá crear un torneo con la siguiente
información: fecha inicial, fecha final, fecha cierre de
inscripciones, cantidad de equipos, costo y estado.
Los estados posibles para un torneo son: Borrador,
Activo, En progreso o Finalizado.
Al crear el torneo, el estado por defecto es borrador.
Adicionalmente, debe incluir:
● Reglamento: PDF con el reglamento.
● Canchas: De cada cancha se conoce su nombre,
imagen y descripción.
Activar torneo | El organizador podrá activar un torneo cambiando el
estado a Activo.
Iniciar torneo | El organizador podrá iniciar un torneo cambiando el
estado a En Progreso. Solamente se puede iniciar un
torneo si la fecha inicial es igual a la fecha actual.
Finalizar torneo | El organizador podrá finalizar un torneo cambiando el
estado a Finalizado. Solamente se puede finalizar un
torneo si la fecha final es igual o posterior a la fecha
actual.
Eliminar torneo | El organizador podrá eliminar un torneo siempre y
cuando el estado sea en Borrador.
Inscripción y
pagos | Cada capitán del equipo realiza la inscripción del
equipo a un torneo cargando el comprobante de pago.
Al cargar el comprobante, el estado de la inscripción
será Revisión.
El organizador del torneo revisará el comprobante de
pago y aprobará o no la inscripción.

===== PAGE 10 =====
El capitán de un equipo podrá cancelar la inscripción
solamente si no se ha aprobado o rechazado
previamente.
Los estados posibles de la inscripción son: En revisión,
Aprobado, Rechazado o Cancelado.
Importante:
● El pago no se realiza dentro de la plataforma.
● Solo los equipos aprobados podrán participar en el
torneo.
Estadísticas / El sistema generará automáticamente:
Llaves
eliminatorias ● Partidos iniciales de manera aleatoria.
● Cuartos de final.
● Semifinal.
● Final.
Todos los usuarios podrán ver esta información.
Estadísticas Todos los usuarios podrán consultar:
generales
● Máximos goleadores.
● Historial de partidos.
● Resultados por equipo.
Auditoria Registrar las acciones de creación, actualización e
inactivación de torneos e inscripciones.
7.5 Servicio de Competencia
● Descripción: Gestiona el desarrollo deportivo del torneo.
Funcionalidad Descripción
Registro de El organizador podrá editar los partidos indicando:
partidos Fecha y hora..
Los datos de los equipos, canchas y árbitros se
determinan de manera automática.
[TABLE]
 | El capitán de un equipo podrá cancelar la inscripción
solamente si no se ha aprobado o rechazado
previamente.
Los estados posibles de la inscripción son: En revisión,
Aprobado, Rechazado o Cancelado.
Importante:
● El pago no se realiza dentro de la plataforma.
● Solo los equipos aprobados podrán participar en el
torneo.
Estadísticas /
Llaves
eliminatorias | El sistema generará automáticamente:
● Partidos iniciales de manera aleatoria.
● Cuartos de final.
● Semifinal.
● Final.
Todos los usuarios podrán ver esta información.
Estadísticas
generales | Todos los usuarios podrán consultar:
● Máximos goleadores.
● Historial de partidos.
● Resultados por equipo.
Auditoria | Registrar las acciones de creación, actualización e
inactivación de torneos e inscripciones.

[TABLE]
Funcionalidad | Descripción
Registro de
partidos | El organizador podrá editar los partidos indicando:
Fecha y hora..
Los datos de los equipos, canchas y árbitros se
determinan de manera automática.

===== PAGE 11 =====
Eliminación de El organizador podrá eliminar un partido siempre y
partidos cuando los equipos sean descalificados o no se
presenten.
Actualización de El organizador podrá actualizar un partido siempre y
partidos cuando la fecha en la que está programado sea
posterior a la fecha actual. Solo se podrá actualizar:
Fecha, hora, cancha y/o árbitro.
Alineaciones El capitán de cada equipo podrá organizar su formación
antes de cada partido:
● Seleccionar titulares.
● Elegir formación: 3-2-1, 2-3-1, 4-1-1 o 1-3-2
Predeterminada (2-3-1)
Las alineaciones podrán ser consultadas por jugadores y
capitanes del mismo equipo.
Resultados El organizador del torneo registrará: marcadores, goles
(relacionando el jugador quien marcó), tarjetas amarillas
y tarjetas rojas.
Consultas El árbitro podrá consultar: partidos asignados, fecha y
hora de los partidos, cancha del partido y equipos que
jugarán, jugadores sancionados.
Estadísticas / Tabla El sistema calculará automáticamente por equipo:
de posiciones
● Partidos jugados.
● Partidos ganados.
● Partidos empatados.
● Partidos perdidos.
● Goles a favor.
● Goles en contra.
● Diferencia de gol.
● Puntos.
Todos los usuarios podrán ver esta información.
Auditoría Registrar las acciones de creación, actualización o
eliminación de un partido.
[TABLE]
Eliminación de
partidos | El organizador podrá eliminar un partido siempre y
cuando los equipos sean descalificados o no se
presenten.
Actualización de
partidos | El organizador podrá actualizar un partido siempre y
cuando la fecha en la que está programado sea
posterior a la fecha actual. Solo se podrá actualizar:
Fecha, hora, cancha y/o árbitro.
Alineaciones | El capitán de cada equipo podrá organizar su formación
antes de cada partido:
● Seleccionar titulares.
● Elegir formación: 3-2-1, 2-3-1, 4-1-1 o 1-3-2
Predeterminada (2-3-1)
Las alineaciones podrán ser consultadas por jugadores y
capitanes del mismo equipo.
Resultados | El organizador del torneo registrará: marcadores, goles
(relacionando el jugador quien marcó), tarjetas amarillas
y tarjetas rojas.
Consultas | El árbitro podrá consultar: partidos asignados, fecha y
hora de los partidos, cancha del partido y equipos que
jugarán, jugadores sancionados.
Estadísticas / Tabla
de posiciones | El sistema calculará automáticamente por equipo:
● Partidos jugados.
● Partidos ganados.
● Partidos empatados.
● Partidos perdidos.
● Goles a favor.
● Goles en contra.
● Diferencia de gol.
● Puntos.
Todos los usuarios podrán ver esta información.
Auditoría | Registrar las acciones de creación, actualización o
eliminación de un partido.

===== PAGE 12 =====
7.6 Servicio Orquestador
● Descripción: Es el punto único de entrada al sistema.
Funcionalidad Descripción
Enrutamiento Redirigir las solicitudes a los microservicios.
Seguridad ● Validación del token (está bien formado y no está
expirado?
● Filtro de acceso
Manejo de errores Manejo de excepciones del sistema.
Página inicial El sistema presentará una página inicial (home) después
de que el usuario inicie sesión, mostrando información
general sobre el torneo vigente.
8. Seguridad
El sistema tiene los siguientes requerimientos de seguridad:
● Autenticación con correo institucional (para personal de la Escuela,
graduados y estudiantes).
● Autenticación con correo personal (familiares).
● Control de roles y permisos.
● Registro de acciones (auditoría).
Tener en cuenta esta diferencia entre los servicios de identidad y orquestador:
[TABLE]
Funcionalidad | Descripción
Enrutamiento | Redirigir las solicitudes a los microservicios.
Seguridad | ● Validación del token (está bien formado y no está
expirado?
● Filtro de acceso
Manejo de errores | Manejo de excepciones del sistema.
Página inicial | El sistema presentará una página inicial (home) después
de que el usuario inicie sesión, mostrando información
general sobre el torneo vigente.

===== PAGE 13 =====
9. Arquitectura Tecnológica
El sistema tiene los siguientes requerimientos de arquitectura:
● Backend: Spring Boot separado por capas: controladores, adaptadores,
lógica y datos.
● Manejo de API REST con Spring Boot.
● Frontend: React (aplicación web) con Typescript
● Base de datos: PostgreSQL.
● Almacenamiento de imágenes: MongoDB
10. Impacto Esperado
Con la implementación de TECHCUP FÚTBOL se espera:
● Eliminar la organización manual por medio de formularios de google
● Facilitar la participación estudiantil y de graduados.
● Mayor claridad en reglas y fechas.
● Resultados transparentes.
● Conservar un historial del torneo.
El torneo pasará de ser una actividad informal a una competición organizada
apoyada por tecnología desarrollada dentro del mismo programa académico.