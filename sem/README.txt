All REST functions were implemented as GET ones, so it's easy to use them through browser URL bar.

A simple UI can be accessed at the root:
    
    http://localhost:8080/

It allows you to display the weather of any city from a specific day.
It can display multiple results from multiple cities. Results are grouped by city.
Each result can be expanded using the left mouse button to view temperatures for each hour.

To download data to the database, one can use /download?city={city_name}&date={YYYY-MM-DD}
    
    http://localhost:8080/download?city=London&date=2024-01-01

It returns raw data downloaded using the https://www.weatherapi.com/ API.

To receive data from the database, one can use /weather?city={city_name}&date={YYYY-MM-DD}
    
    http://localhost:8080/weather?city=London&date=2024-01-01

It returns a JSON file, where .city contains the 'correct' name of the city and .weather
contains an array of temperatures for each hour in the format (the weather array is sorted by timestamp):
    
    {
        float tempC;
        int condition;
        unsigned long timestamp;
    }

Conditions can be obtained through the /weather_conditions request.

To obtain average temperatures for the past week, one can use /prev_week?city={city_name}
    
    http://localhost:8080/prev_week?city=London

It returns average night temperatures and day temperatures as .avg_night_temp and .avg_day_temp as floats.
Daytime is considered the time between 6 and 20 hours. Also, it includes an array of temperatures for each day.
The day format is the same as in the case of the /weather request.

If desired, data can be deleted from the database using the /delete?city={city_name} request, which
deletes all weather records for the city from the database.
    
    http://localhost:8080/delete?city=London
