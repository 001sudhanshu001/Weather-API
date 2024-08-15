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

-----------------------------------------------------------------------------
## Location API Endpoints

Add Location 

**Request URI:**  POST /v1/locations

Request 
```json
    {
      "code": "PNP",
      "city_name": "Panipat",
      "region_name": "Haryana",
      "country_code": "IN",
      "country_name": "India",
      "enabled": true
    }
```



Get Location based on Location Code 

**Request URI:** GET /v1/locations/{code}
Response 
```json
    {
      "code": "CHD",
      "city_name": "Chandigarh",
      "region_name": "Chandigarh",
      "country_code": "IN",
      "country_name": "India",
      "enabled": true
    }
```

Get Location based on Location Code
Return a Pagination Response, The Pagination Options are :

**Request URI:** GET /v1/locations?page=1&size=5&sort=code
Response
```json
    {
      "_embedded": {
        "locations": [
          {
            "code": "CHD",
            "city_name": "Chandigarh",
            "region_name": "Chandigarh",
            "country_code": "IN",
            "country_name": "India",
            "enabled": true,
            "_links": {
              "self": {
                "href": "http://localhost:8080/v1/locations/CHD"
              }
            }
          },
          {
            "code": "LACA_US",
            "city_name": "Los Angeles II",
            "region_name": "California",
            "country_code": "US",
            "country_name": "United States of America",
            "enabled": true,
            "_links": {
              "self": {
                "href": "http://localhost:8080/v1/locations/LACA_US"
              }
            }
          },
          {
            "code": "MUB",
            "city_name": "Mumbai",
            "region_name": "Maharashtra",
            "country_code": "IN",
            "country_name": "India",
            "enabled": true,
            "_links": {
              "self": {
                "href": "http://localhost:8080/v1/locations/MUB"
              }
            }
          },
          {
            "code": "PNP",
            "city_name": "Panipat",
            "region_name": "Haryana",
            "country_code": "IN",
            "country_name": "India",
            "enabled": true,
            "_links": {
              "self": {
                "href": "http://localhost:8080/v1/locations/PNP"
              }
            }
          }
        ]
      },
      "_links": {
        "self": {
          "href": "http://localhost:8080/v1/locations?page=1&size=5&sort=code"
        }
      },
      "page": {
        "size": 5,
        "total_elements": 4,
        "total_pages": 1,
        "number": 1
      }
    }
```


------------------------------------------------------------------------
## Hourly Weather API Endpoints

Get Hourly Weather based on IP and Current Hour in the Request Header "X-Current-Hour"
It will return the hourly weather of current hour and upcoming hours

**Request URI:** GET /v1/hourly

Eg. For IP Address of Mumbai 

Response

```json
    {
        "location": "Mumbai, Maharashtra, India",
        "_links": {
            "self": {
                "href": "http://localhost:8080/v1/hourly"
            },
            "realtime_weather": {
                "href": "http://localhost:8080/v1/realtime"
            },
            "daily_forecast": {
                "href": "http://localhost:8080/v1/daily"
            },
            "full_forecast": {
                "href": "http://localhost:8080/v1/full"
            }
        },
        "hourly_forecast": [
            {
                "hour_of_day": 10,
                "temperature": 23,
                "precipitation": 60,
                "status": "Thunder Storm"
            },
            {
                "hour_of_day": 11,
                "temperature": 28,
                "precipitation": 40,
                "status": "cloudy"
            },
            {
                "hour_of_day": 12,
                "temperature": 30,
                "precipitation": 45,
                "status": "cloudy"
            }
        ]
    }
```


Similarly, the Location Code can be used to get Hourly Weather of any city

**Request URI:** GET /v1/hourly/MUB