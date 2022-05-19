# android-things-project

## Project Description

The mobile application uses the MediaRecorder class in order to record audio and a runnable thread method that will query the amplitude difference since the last getMaxAmplitude() call. This difference is then turned into decibels using the formula:
20 * Math.log10(mRecorder.getMaxAmplitude() / decibelConst); where the decibelConst is 32767.0

If the decibel difference is too large and passes a certain threshold it will alert the user via a TextView element.

## Schematics
-


## Pre-requisites

Android phone with a working built-in microphone and at least Android version 8.0.

## Setup and build

The application is built within Android Studio 2021.2.1 then it is either installed with an .apk file or installed via Wi-fi pairing. After running the application it will request the necessary permissions required to run automatically and can begin running after the user has granted permissions.

## Running
The application is ran within the android operating system. The user will press the record button and the application will begin to monitor incoming audio.
Upon exiting the application the recording will stop automatically.

## Demo Video
Attached to teams assignment

## Companion Mobile App
The application is using audio from android's MediaRecorder that can broadcast audio and video data exposed by the phone's built-in sensors. 
Data is being collected every 200ms using a Handler that will start a new thread for the interogation. The real-time data is available within the debug version of the application, available via logcat.


