# CS455 HW2

cs455.scaling is a package designed to create a multithreaded server as well as several clients and have the clients write to the server. Statistics about message passing will be printed by the server and all the clients. 

### Setup
To setup this project, clone this repo, then run gradle build.

### Run Programs
##### Server
To run the server, run ServerStart.sh <port number> <thread pool size> <batch size> <batch time>. Port number is the port the server will run on. Thread pool size is the number of threads that will be responsible for processing connections. Batch size and batch time are both related to when responses will be sent to the clients. If either the number of messages recieved exceeds batch size or the time since the last send exceeds batch time, responses are sent to the clients. This structure is designed to minimize thread context switching, thereby increasing efficiency.
##### Client
  To run the clients ...
  
