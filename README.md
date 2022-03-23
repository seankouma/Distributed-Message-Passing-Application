# CS455 HW2

cs455.scaling is a package designed to create a multithreaded server as well as several clients and have the clients write to the server. Statistics about message passing will be printed by the server and all the clients. 

### Setup
To setup this project, clone this repo, then run gradle build.

### Run Programs
##### Server
To run the server, run ServerStart.sh <port number> <thread pool size> <batch size> <batch time>. Port number is the port the server will run on. Thread pool size is the number of threads that will be responsible for processing connections. Batch size and batch time are both related to when responses will be sent to the clients. If either the number of messages recieved exceeds batch size or the time since the last send exceeds batch time, responses are sent to the clients. This structure is designed to minimize thread context switching, thereby increasing efficiency.
##### Client
  To run the clients, run ClientStart.sh <hosname of server> <port of server> <messages to send per second>
  ### Directories
  ##### Server
  This contains the code for our server as well as our thread pool and the code that each thread in the thread pool will execute.
  ##### Client
  This contains the code for our client as well as a code for a thread to send messages to the server on a scheduled basis.
  ##### Task
  This contains several classes, each representing a task that a thread is capable of preforming. Each task implements the Task interface, an interface which specifies a runTask method. This method is called by a thread to preform the task.
  ##### Util
  This directory contains helper classes including a class for printing statistics on a timer as well as a class for computing hash.
  ##### Main
  This contains a main script used to start the other classes.