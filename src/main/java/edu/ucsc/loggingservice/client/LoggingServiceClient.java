package edu.ucsc.loggingservice.client;

import edu.ucsc.loggingservice.*;
import edu.ucsc.loggingservice.models.ServerInfo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingServiceClient {
    private static final Logger logger = Logger.getLogger(LoggingServiceClient.class.getName());
    private final ManagedChannel channel;
    private final LoggingServiceGrpc.LoggingServiceBlockingStub blockingStub;

    public LoggingServiceClient(ServerInfo server){
        this(ManagedChannelBuilder.forAddress(server.host, server.port).usePlaintext().build());
    }

    LoggingServiceClient(ManagedChannel channel){
        this.channel = channel;
        blockingStub = LoggingServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String name){
        logger.info("Will try to greet");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

    public void addLogEntry(String data, String value){
        LogEntry demoEntry = LogEntry.newBuilder().addTags("Demo").build();
        CreateLogRequest request = CreateLogRequest.newBuilder().setLog(demoEntry).build();
        CreateLogResponse reply;
        try {
            reply = blockingStub.createLog(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + reply.getSuccess());
    }

    public void getLog(String data){

    }

    public void getLogFile(String tag){

    }

    public void verifyLogEntry(LogEntry entry){

    }

    public void verifyLogFile(String tag){

    }
}
