This is a server application having the REST Api to transfer money between clients.
The server is pretty light-weight , does not use any heavy frameworks such as spring and is a minimal POC as what such fund transfer servers might look like.
The server has 2 APIs - 

* __Register an account__
	* An account can be registsred with an inital starting balance. 
	* For registration, the inputs to the system are account ids and starting balance.
	* If not starting balance is mentioned, the starting balance is initialized to 0.

* __Transfer a specific amount between 2 accounts__
	* For fund transfer, the input are the account id to transfer from, transfer to and the amount to be transferred.

The system does not generate account id, as the assumption made here is that would be done by a seperate 
microservice which is beyond the scope of this problem.
The data is maintained in memory, which is basically a hashmap between account ids and their current 
balances.
I've used Semaphores as mutex locks while transfering or registering an account, to avoid 
race conditions in a multi-threaded environment and maintaining consistency.

How to start the Server - 
1. Via commandline
	* Simply git clone this server repo into a folder.
	* Navigate to the directory - FundTransferAPI/src/main/java
	* javac Application.java
	* java Application

2. Via Intellij
	* Simply add this as a new project in Intellij and run the main method in Application.java

To test the server use the testing framework [here](https://github.com/kvermun/FundTransferAPITests).

   