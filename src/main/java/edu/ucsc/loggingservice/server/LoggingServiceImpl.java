package edu.ucsc.loggingservice.server;

import edu.ucsc.loggingservice.*;
import io.grpc.stub.StreamObserver;

public class LoggingServiceImpl extends LoggingServiceGrpc.LoggingServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello "+ request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void createTag(CreateTagRequest request, StreamObserver<CreateTagReply> responseObserver) {
        super.createTag(request, responseObserver);
    }

    @Override
    public void updateTag(UpdateTagRequest request, StreamObserver<UpdateTagReply> responseObserver) {
        super.updateTag(request, responseObserver);
    }

    @Override
    public void deleteTag(DeleteTagRequest request, StreamObserver<DeleteTagReply> responseObserver) {
        super.deleteTag(request, responseObserver);
    }

    @Override
    public void createLog(CreateLogRequest request, StreamObserver<CreateLogReply> responseObserver) {
        super.createLog(request, responseObserver);
    }

    @Override
    public void getLogEntry(GetLogEntryRequest request, StreamObserver<GetLogEntryReply> responseObserver) {
        super.getLogEntry(request, responseObserver);
    }

    @Override
    public void getLedger(GetLedgerRequest request, StreamObserver<GetLedgerReply> responseObserver) {
        super.getLedger(request, responseObserver);
    }

    @Override
    public void verifyLedger(VerifyLedgerRequest request, StreamObserver<VerifyLedgerReply> responseObserver) {
        super.verifyLedger(request, responseObserver);
    }

    @Override
    public void verifyLog(VerifyLogRequest request, StreamObserver<VerifyLogRequest> responseObserver) {
        super.verifyLog(request, responseObserver);
    }
}
