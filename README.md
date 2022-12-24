# Android-UGR-Exams
Es una aplicación Android con diversas funcionalidades relacionadas con la Universidad de Granada, especialmente las de informática.
Las funcionalidades se veen descritas a continuación:
1. Consultor de Examenes: Haciendo uso de un script [(Get-Exams-DatesUGR) ](https://github.com/CodeBoy-source/Get-Exams-DatesUGR) para obtener las fechas
de los exámenes de los grados relacionados con informática hemos desarrollado una base
de datos que nos permite a los usuarios consultar sus dudas respectos a sus exámenes.
2. DialogFlow: La continuación de la página anterior se realiza por medio del
uso de herramientas de GoogleCloud. Disponemos de un bot entrenado para guiar un
usuario a resolver dudas respecto los exámenes.
3. Navegador: La última pantalla desarrollad consiste en el uso de los
sensores de los dispositovs móviles. De esta forma se ha intentado crear
una especie de 'Google Maps' de la ugr para ciertos 'checkpoints'.
    - Para el uso de esta pantalla es necesario utilizar 'QR's' que indique
    el checkpoint en el cual nos encontramos representados por números (1-5)

Además, tenemos un cojunto de gestos permitidos en la última pantalla:
- Girar el móvil sobre el eje y para confirmar la llegada a un checkpoint.
- Girar el móvil hacia arriba para activar el lector QR.

Es un mini-prototipo desarrollado en muy pocas semanas.
