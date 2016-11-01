# Popular-Movies
A simple Android app that helps user to discover structured info about various movies, from themoviedb.org. This is a 
Project 1 and Project 2 of Udacity's Android Developer Nanodegree.

### Features
- Discover the most popular, highest rated or most rated movies.
- Watch movies trailers and teasers
- Mark movies as favorites
- offline work
- Material Design
- UI optimized for tablet

## Screenshots


![movies_grid_phone](https://cloud.githubusercontent.com/assets/13984005/19886351/d241d806-a048-11e6-8cd0-d03fb5644872.png)  ![movies_detail_phone](https://cloud.githubusercontent.com/assets/13984005/19886382/0194a5fc-a049-11e6-94eb-8551dd7dee92.png)

![screen-tablet](https://cloud.githubusercontent.com/assets/13984005/19427612/bb2d6118-9462-11e6-97ee-3b918466fefc.png)

## Developer Setup

### Requirements
- Java 7 or Above
- Latest Version of Android SDK and Android Build Tools

### API Key
1. The app uses themoviedb.org API to get movie information and posters. You must provide your own API key
    in order to build the app.
2. Please register and request your own API Key from https://www.themoviedb.org/. 
3. After obtaining the key, just paste it in line number 9, of file PopConstants.java as a string value.

#### Building 
  You can build the App using Latest version of Android Studio
  
### Testing
This project integrates a combination of local unit tests, instrumented tests and code analysis tools(such as stetho library).  

Just define your app package name in Test Andoid App Configuration to ensure that project code is valid and stable.
Then hit run command. This will run Unit tests on the JVM, instrumented tests 
on the connected device(or emulator).
