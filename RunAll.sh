#!/bin/bash
USERNAME=skouma

# can add any number of 120 machines to scale your solution
CLIENT_HOSTS="kinshasa.cs.colostate.edu hong-kong.cs.colostate.edu hanoi.cs.colostate.edu dhaka.cs.colostate.edu damascus.cs.colostate.edu cairo.cs.colostate.edu bogota.cs.colostate.edu bangkok.cs.colostate.edu baghdad.cs.colostate.edu kabul.cs.colostate.edu lima tehran singapore seoul riyadh pyongyang moscow moscow mexico-city madrid"
# the port and ip of the Server
# Note: this assumes that the server is already running SERVER_HOSTNAME at SERVER_PORT
SERVER_HOSTNAME="basil.cs.colostate.edu"
SERVER_PORT=5000
MESSAGING_RATE=2

# the outer loop goes through the hosts
for VARIABLE in 1 2 3 4 5
do
    for HOSTNAME in ${CLIENT_HOSTS}
    do
        ssh -l skouma ${HOSTNAME} 'cd ./cs455/hw2/cs455-hw2; ./ClientStart.sh basil 5000 2' &
    done
done