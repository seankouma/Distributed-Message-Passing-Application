package cs455.scaling.server;

import java.util.TimerTask;

public class PrintStats extends TimerTask {
    Server server;

    PrintStats(Server server) {
        this.server = server;
    }
    @Override
    public void run() {
        server.printStatistics();
    }

}
