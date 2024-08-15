# Weather API

## Introduction
The Weather API is a RESTful service built with Spring Boot that provides real-time weather information for various locations.
The API uses IP2Location to automatically determine the user's location based on their IP address, delivering localized 
weather data seamlessly. Additionally, a caching mechanism is employed to enhance performance by reducing redundant external API calls.

## Role and Authority
- **SUPER_ADMIN**: Manages the overall system, including user access.
- **ADMIN**: Manage Users, Location, Weather Data.
- **WEATHER_STATION**: Update Weather Data.
- **WEB_USER**: Search Location, View Weather Forecast.

## API Endpoints 

Create user and signin
**Request URI: /api/v1/auth/signup**
**Request URI: /api/v1/auth/signin**

To Create user with WEB_USER(Normal Visitor)
```json
    {
      "firstName": "Sudhanshu",
      "lastName": "Arya",
      "email": "sudhanshu@gmail.com",
      "password": "#4Ypu*78H026"
    }
```
To Create Administrative User(ADMIN,WEATHER_STATION)
**Request URI: /api/v1/auth/create-administrative-user** (Only SUPER_ADMIN can invoke)
```json
    {
      "firstName": "admin",
      "lastName": "admin",
      "email": "admin@gmail.com",
      "password": "*97u*78H026",
      "role" : "ADMIN"
    }
```

