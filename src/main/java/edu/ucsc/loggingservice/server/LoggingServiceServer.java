package edu.ucsc.loggingservice.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import edu.ucsc.loggingservice.*;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LoggingServiceServer{
    private static final Logger logger = Logger.getLogger(LoggingServiceServer.class.getName());
    private Server server;
    private ServerInfo serverInfo;
    private ServerConfiguration config;
    private static List<LogEntry> logEntries;

    public LoggingServiceServer(int clusterId, int replicaID){
        try {
            config = new ServerConfiguration();
            serverInfo = config.getServerInfo(clusterId, replicaID);
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(this.serverInfo.port).addService(new LoggingServiceImpl()).build().start();
        Config cfg = new Config();
        NetworkConfig networkConfig = cfg.getNetworkConfig();
        networkConfig.setPort(serverInfo.consensusPort);
        JoinConfig join = networkConfig.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig()
                .addMember("machine1")
                .addMember("localhost").setEnabled(true);
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        logEntries = instance.getList("logEntry");
        logger.info("Server started : " + this.serverInfo.port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.err.println("***** shutting down gRPC server since JVM is shutting down");
                LoggingServiceServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop () {
        if (server != null){
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if(server != null){
            server.awaitTermination();
        }
    }

    static class LoggingServiceImpl extends LoggingServiceGrpc.LoggingServiceImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void createLog(CreateLogRequest request, StreamObserver<CreateLogReply> responseObserver) {
            logger.info("GETTING LOG " + request.getLog().getKey());
            logEntries.add(request.getLog());
            CreateLogReply reply = CreateLogReply.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void getLog(GetLogRequest request, StreamObserver<GetLogResponse> responseObserver) {
            GetLogResponse response = GetLogResponse.newBuilder().addAllLog(logEntries).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
