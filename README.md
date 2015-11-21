# ReliableTransferProtocol

You will be graded on the correctness of the code and its readability and structure.
As you write the documentation, imagine that I intend to implement your protocol in my own client that will interoperate with your server.
Make sure I don't have to read your source code to figure out how to do this.

Justin Luk & Hamilton Greene
jluk3@gatech.edu

CS 3251 Section A
November 25th, 2015
Programming Assignment 2

Files Submitted

Protocols Package
* FXAClient.java
- Handle user input and instantiate an RXPClient instance

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

RxpPacket Package
* RXPHeader.java
- Define header format, handling, and transformations from primitive data type to object

* RXPPacket.java
- Data model to hold packet header and data

Instructions to run

Updated Protocol

API Description

Bugs & Limitations