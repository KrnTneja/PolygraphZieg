# PolygraphZieg
ITSP Project 2016, IIT Bombay - Polygraph - Zieg

This repository contains the files from NetBeans coded up in Java Programming Language.

Serial Communication with Arduino:
This consists of two components: the Arduino code and the Java code.
The Arduino code simply reads the input from analog pins and writes it over Serial using Serial library of Arduino. The code used follows a protocol to tag the data to a particular sensor for computer to be able to distinguish the values according to sensor.
The Java code is a little complex. It uses the RxTxComm.jar for Serial connection. Official documentation and example code for this can be found easily on internet. The library provides easy access to ports, identification of ports by name, connection to ports, etc.

Processing of raw data to graph data at back end:
For the pulse circuit, the voltage is simply plotted against time. The digital output from DAC (say D) has to be scaled down to 5V using V_O = D / 1024 * 5.0

For skin conductivity and respiration rate, voltage divider is being used where the voltage across the resistance to be measured is given by V_O = R_unknown / (R_unknown + R_known) * V_CC

From above equation we can find the unknown resistance. Also, voltage has to be scaled down to 5V as it is done is pulse measurement.
Every data point is parallel to a corresponding time-stamp with precision of milliseconds in the program. When plotted, the time-stamps are used to get X co-ordinate of the point rather than directly dividing the length into equal parts. This gives the freedom to send more data for a particular value and less for another without making any explicit changes in the program.

Plotting of graph and other control features:
Suppose that resistance unknown resistance (say Rx) lies in the range R1 to R2 (R1 < R2) and this has to be zoomed to fit into say n pixels. Simple calculations show that the height h pixels above the base level is given by: h = (Rx - R1)/(R2 - R1) * n

So point is plotted with height h when resistance is Rx. This feature is used for both body resistance and respiration rate.
To get the pulse, we note the time of the graph at which it crosses a particular value (say t1) and the also the time when it crosses that same value next time (say t2). Pulse can be given by (if t1 and t2 are in milliseconds): pulse = 60000/(t2-t1)

That value at which time is recorded is called the ‘trigger value’.



Graphic User Interface

Serial Communication with Arduino:
Initially we thought of using Python to code up serial, GUI and graph plotting but later choose copy up the basic framework of a very similar code we wrote up in our Winter Project 2015 which was coded up in Java.
The Serial communication in Java requires RXTXcomml.jar and rxtxSerial.dll (from http://jlog.org/rxtx-win.html) to be put up in appropritate folder to work. We used NetBeans IDE.
When we were able to send and receive data using Arduino and out Java program, we saw that output being received was not what was expected. On observing the output, we concluded that there was some fault in synchronization. It seemed as if the Java program is processing data at a slower rate than the Arduino is sending it. So we put up some delay (1 to 3 ms) between sending two data points in the Arduino code. It worked. We later attributed each data point to a particular sensor code. To cover up the time loss in delay, we increased the baud rate and it still worked fine. We finalized on it.
Sensor code is made up of a alphabet and a number. For example ‘c1’ means skin conductivity sensor numbered 1, ‘r2’ means respiration rate sensor numbered 2. This code is just followed (without any space) by the 10 bit output of ADC and then by a newline character. For example “c1102\n” is understood as conductivity sensor numbered one sent a value of 102. This corresponds to scaled voltage as mentioned earlier.
When the code was finally deployed to form the final JAR executable file, we found out that we couldn’t find the ports. Inside NetBeans, everything seemed to work fine. The problem, as we found out later was that NetBeans needs 64-bit rxtxSerial.dll file while JAR file being executed needed 32-bit version of same file. So, the simplest solution was to put up the 32-bit rxtxSerial.dll in the ‘dist’ folder of the NetBeans project. And it worked!
To make the Java program faster, without getting delayed in receiving data, we used multi-threading using Thread class in Java to create a new thread for data as soon as minimal processing was done.

Processing of raw data to graph data at back end:
We just took care that there are minimum routines for data-processing and calculations to make it as fast as we could. 

Plotting of graph and other control features:
There was a need to simultaneously plot six graphs (including the blood pressure one). What the program does is that it plots at rate of 4 frames per second which seem okay. For every frame, program cleans up screen, plots reference lines, gets updated coordinate list of a sensor, plots it and similarly continues getting updated list and plotting for rest of them. 




