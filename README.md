# STAR BUS

The aim of our project is to create a way to be able to see various bus times from the nearest stops without needing to check each individually every time, the idea is to use a Raspberry Pi and a screen which will communicate with a telephone app.

Using a router as a server and checking the bus times constantly with the Raspberry Pi, the Telephone app enables us to easily change the chosen bus stops and also change the server IP and Port.

In this project we use 3 different technologies who all interact.

First, the Raspberry Pi equipped with a LCD screen to display the next bus times and also the time, there are a maximum of 3 different bus stops on display with the times for the next 3 buses of a given line displyed. The Raspberry must have an internet connection to be able to access the online API.

Secondly, the mobile phone who modifies the data that the Raspberry will use to search for the buses. We have to give a Slot, Line, Direction and Stop for each request. Which is sent to the server which relays this message to the Raspberry Pi. We can change the Server IP and the Port on which we send the information

Thirdly, the server which in our case was an Internet Router to which the Raspberry Pi was connected in permanance. The router listens on a given Port and relays the message.


We used 2 different IDEs to accomplish this, Android Studio for the mobile app and Atom for the Raspberry Pi code.
