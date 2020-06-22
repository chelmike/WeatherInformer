# Weather Informer

This is my practice project on Java. It is based on Spring framework, Maven etc.
Application represents a simple web form in which You can select a weather service - **[OpenWeatherMap](https://openweathermap.org)**
or **[GridForeCast](https://gridforecast.com)** - and a city, for which the weather will be requested. At the moment, these are three cities:
Chelyabinsk, Saint Petersburg and Moscow (for a demo purpose). After submitting the form, the current weather data will be
received from the service and displayed on the page:

![A web form look](form.png)

If any errors occur, they will be displayed in a special block.

[UPD] A **GridForeCast** service has finished providing free keys. Thus, this service will be left here only for error handling example. 
