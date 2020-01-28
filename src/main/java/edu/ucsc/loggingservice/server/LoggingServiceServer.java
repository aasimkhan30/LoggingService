package edu.ucsc.loggingservice.server;

import edu.ucsc.loggingservice.*;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;
import edu.ucsc.loggingservice.server.consensus.ConsensusServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;
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
        /*
        Simple request to check if GRPC is working or not.
         */
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        /*
        Creates a log entry in the base ledger.
         */
        @Override
        public void createLog(CreateLogRequest request, StreamObserver<CreateLogResponse> responseObserver) {
            logger.info(String.format("Trying to add %s with tag %s", request.getLog().getKey(), request.getLog().getTags(0)));
            cServer.addLogEntry(request.getLog());
            CreateLogResponse reply = CreateLogResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        /*
        Get a log entry using a key and ledger level. Used to access only a single entry
         */
        @Override
        public void getLogEntry(GetLogEntryRequest request, StreamObserver<GetLogEntryResponse> responseObserver) {
            logger.info(String.format("Trying to read %s with tag %s", request.getKey(), request.getTag()));
            LogEntry responseEntry = cServer.getEntry(request.getKey(), request.getTag());
            GetLogEntryResponse response = GetLogEntryResponse.newBuilder().setLog(responseEntry).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /*
        Gets an entire ledger based on the tag.
         */
        @Override
        public void getLedger(GetLedgerRequest request, StreamObserver<GetLedgerResponse> responseObserver) {
            Tag requestTag = Tag.newBuilder().setName(request.getTag()).build();
            List<LogEntry> responseLedger = cServer.getTag(requestTag);
            GetLedgerResponse response = GetLedgerResponse.newBuilder().addAllLogs(responseLedger).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /*
        Used to verify the chain hash of an entry
         */
        @Override
        public void verifyLog(VerifyLogRequest request, StreamObserver<VerifyLogResponse> responseObserver) {
            LogEntry logEntry = cServer.getEntry(request.getKey(), request.getTag());
            VerifyLogResponse response = VerifyLogResponse.newBuilder().setDigest(logEntry.getDigest()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /*
        Used to verify the hash of tag
         */
        @Override
        public void verifyLedger(VerifyLedgerRequest request, StreamObserver<VerifyLedgerResponse> responseObserver) {
            Tag tag = cServer.getTag(request.getTag());
            VerifyLedgerResponse response = VerifyLedgerResponse.newBuilder().setDigest(tag.getDigest()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /*
        Creates a tag with user accessInfo
         */
        @Override
        public void createTag(CreateTagRequest request, StreamObserver<CreateTagResponse> responseObserver) {
            cServer.createTag(request.getNewTag());
            CreateTagResponse response = CreateTagResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /*
        Deletes a tag
         */
        @Override
        public void deleteTag(DeleteTagRequest request, StreamObserver<DeleteTagResponse> responseObserver) {
            cServer.deleteTag(request.getName());
            DeleteTagResponse response = DeleteTagResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /*
        Updates a tag with new accessInfo
         */
        @Override
        public void updateTag(UpdateTagRequest request, StreamObserver<UpdateTagResponse> responseObserver) {
            cServer.updateTag(request.getUpdatedTag());
            UpdateTagResponse response = UpdateTagResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
