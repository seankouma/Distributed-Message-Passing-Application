package cs455.scaling.main;

import cs455.scaling.server.Server;
import cs455.scaling.client.Client;

public class Main{

	public static void main(String[] args){

		String runClass = args[1];

		if(runClass.equals("Server")){
			Server.main(new String[]{"cs455.scaling.server.Server", args[2], args[3], args[4], args[5]});
		}
		else if(runClass.equals("Client")){
			Client.main(new String[]{"cs455.scaling.client.Client", args[2], args[3], args[4]});
		}
		else{
			throw new RuntimeException(args[0] + ": The class " + runClass + " doesn't exist.");
		}
	}
}
