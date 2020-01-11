package edu.ucsc.loggingservice;

import edu.ucsc.loggingservice.client.LoggingServiceClient;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;
import edu.ucsc.loggingservice.server.LoggingServiceServer;

import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger logger = Logger.getLogger(App.class.getName());
    public static void main( String[] args )
    {
        for(int i = 0; i < args.length; i++){
            switch (args[i]) {
                case "-server":
                    i++;
                    try {
                        int clusterId = Integer.parseInt(args[i++]);
                        int nodeId = Integer.parseInt(args[i++]);
                        edu.ucsc.loggingservice.server.LoggingServiceServer loggingServer = new LoggingServiceServer(clusterId, nodeId);
                        loggingServer.start();
                        clusterId = Integer.parseInt(args[i++]);
                        nodeId = Integer.parseInt(args[i++]);
                        edu.ucsc.loggingservice.server.LoggingServiceServer loggingServer2 = new LoggingServiceServer(clusterId, nodeId);
                        loggingServer2.start();
                        loggingServer.blockUntilShutdown();
                        loggingServer2.start();
                    }
                    catch(Exception e){
                        System.err.println(e.getMessage());
                        return;
                    }
                    break;
                case "-client":
                    i++;
                    int clusterId = Integer.parseInt(args[i]);
                    int nodeId = Integer.parseInt(args[i+1]);
                    try {
                        ServerConfiguration config = new ServerConfiguration();
                        ServerInfo serverInfo = config.getServerInfo(clusterId, nodeId);
                        LoggingServiceClient client = new LoggingServiceClient(serverInfo);
                        if(nodeId == 0){
                            logger.info("HI");
                            client.addLogEntry();
                        }
                        else {
                            client.getLogs();
                        }
                        client.shutdown();
                    }
                    catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    break;
                default:
                    logger.info("Wrong Arguments passed");
            }
        }
    }
}
