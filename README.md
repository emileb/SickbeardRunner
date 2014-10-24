Sickbeard On Android app.

There are 3 project to the app:

SickbeardRunner - This is the Python interpreter, and Android service. This acutally runs the Sickbeard python files in a service, and you can connect to the service and read the logs etc.

Sickbeard - This is a Library project, it contains most of the code for communicating to the SickbeardRunner and the Fragment for displaying the information.

SickbeardOnAndoid - This is the actual Android app, it reference the Library project 'Sickbeard' above. All it does is instantiate the Fragment in the Sickbeard project.

The reason for a Library project was so the Sickbeard Fragment can also be in the the PowerNZB app, AND you can have a standalone app with the SickbeardOnAndroid project.

SickbeardRunner is a separate project due it's size, it would have made the PowerNZB app far too large if it were incorperated.
