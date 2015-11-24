# ReliableTransferProtocol

You will be graded on the correctness of the code and its readability and structure.
As you write the documentation, imagine that I intend to implement your protocol in my own client that will interoperate with your server.
Make sure I don't have to read your source code to figure out how to do this.

Justin Luk & Hamilton Greene
jluk3@gatech.edu

CS 3251 Section A
November 25th, 2015
Programming Assignment 2

##Files Submitted

###Protocols Package
* FXAClient.java
  * Handle user input and instantiate an RXPClient instance

* RXPClient.java
  - Define all Reliable Transfer Protocol logic to be called by FXAClient

* RXPClientPacketFactory.java
  - Factory object to handle client-side RXPPacket creation and response logic based on connection codes

* RXPServerPacketFactory.java
  - Factory object to handle server-side RXPPacket creation and response logic based on connection codes

* RXPServer.java
  - Define all Reliable Transfer Protocol logic to be called by FXAServer

* FXAServer.java
  - Instantiation and handling of user requests utilizing an RXPServer instance

###RxpPacket Package
* RXPHeader.java
  - Define header format, handling, and transformations from primitive data type to object

* RXPPacket.java
  - Data model to hold packet header and data

##Instructions to run
In this example we use 15888 and 15889 as example client and server ports

1. Open up a terminal and run NetEmu with python NetEmu.py 5000. 
2. Open up another terminal and navigate to the location of FxA-server.jar. 
3. Open up another terminal and navigate to the location of FxA-client.jar. 
4. In the first terminal, type: "java -jar FxA-server.jar FxA-server 15888 127.0.0.1 5000". 
5. In the second terminal, type: "java -jar FxA-client.jar FxA-client 15889 127.0.0.1 5000". 
6. To connect the client to the server, type "connect" in the client's terminal. 
7. To get a file from the server, type: "get example.txt". 
8. To post a file to the server, type: "post example.txt". To disconnect the client from the server, type: "disconnect".
**Note: files to transport must be in the same directory as the respective jar file.**

##Updated Protocol
* RxPClient(String sourceIP, String destIP, short sourceDest, short destPort)
  -This constructor is call by a host to create a client RxP instance. The method tries to initializes and returns the RxPClient object.
* RxPClient.connect()
  -This method is called by the RxP client instance to try and connect to the RxP server instance at destIP:destPort.
* int RxPClient.sendData( byte data[])
  -This method will try to send an array of bytes from the data buffer over the connection made by the RxP client and server. If successful, it will return 0, otherwise it will return with an error code.
* Byte[] RxPClient.getData(byte data[])
  -This method will request to receive data from the RxP server instance. If successful the data will be returned in a byte array.
* void RxPClient.close()
  -This method will send a close request to the RxP server instance and close itself.
* RxPServer(String sourceIP, String destIP, short sourceDest, short destPort)
  -This constructor is call by a host to create a server RxP instance. The method tries to initializes and returns the RxPServer object.
* RxPServer.startRxPServer()
  -This method is called by the RxP server instance to bind to sourcePort and wait to receive a connection.
* int RxPServer.runServer( byte data[])
  -Once a connection is established, this method is called to listen to requests from the RxP client.
* int RxPServer.sendData(byte data[])
  -Upon a getData request from the RxP client instance, this method send the requested data to the client.
* void RxPServer.close()
  -This method will send a close request to the RxP client instance and close itself.

##API Description

##Bugs & Limitations