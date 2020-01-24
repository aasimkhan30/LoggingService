package edu.ucsc.loggingservice.server;

import edu.ucsc.loggingservice.*;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;
import edu.ucsc.loggingservice.server.consensus.ConsensusServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

public class LoggingServiceServer{
    private static final Logger logger = Logger.getLogger(LoggingServiceServer.class.getName());
    private Server server;
    private ServerInfo serverInfo;
    private ServerConfiguration config;
    public static ConsensusServer cServer;

    public LoggingServiceServer(int clusterId, int replicaID){
        try {
            config = new ServerConfiguration();
            serverInfo = config.getServerInfo(clusterId, replicaID);
            serverInfo.clusterId = clusterId;
            serverInfo.replicaId = replicaID;
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(this.serverInfo.port).addService(new LoggingServiceImpl()).build().start();
        cServer = new ConsensusServer(serverInfo, config);
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
            cServer.addLogEntry(request.getLog());
            CreateLogReply reply = CreateLogReply.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void getLog(GetLogRequest request, StreamObserver<GetLogResponse> responseObserver) {

        }

        @Override
        public void getLedger(GetLedgerRequest request, StreamObserver<GetLedgerReply> responseObserver) {
            super.getLedger(request, responseObserver);
        }

        @Override
        public void verifyLog(VerifyLogRequest request, StreamObserver<VerifyLogRequest> responseObserver) {
            super.verifyLog(request, responseObserver);
        }

        @Override
        public void verifyLedger(VerifyLedgerRequest request, StreamObserver<VerifyLedgerReply> responseObserver) {
            super.verifyLedger(request, responseObserver);
        }

        @Override
        public void createTag(CreateTagRequest request, StreamObserver<CreateTagReply> responseObserver) {
            super.createTag(request, responseObserver);
        }

        @Override
        public void deleteTag(DeleteTagRequest request, StreamObserver<DeleteTagReply> responseObserver) {
            super.deleteTag(request, responseObserver);
        }

        @Override
        public void updateTag(UpdateTagRequest request, StreamObserver<UpdateTagReply> responseObserver) {
            super.updateTag(request, responseObserver);
        }

        @Override
        public void getLogEntry(GetLogEntryRequest request, StreamObserver<GetLogEntryReply> responseObserver) {
            super.getLogEntry(request, responseObserver);
        }
    }
}
