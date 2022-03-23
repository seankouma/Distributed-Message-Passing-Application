package cs455.scaling.server;
import cs455.scaling.util.*;

import java.util.TimerTask;

public class PrintStats extends TimerTask {
    Node server;

    public PrintStats(Node server) {
        this.server = server;
    }
    @Override
    public void run() {
        server.printStatistics();
    }

}
