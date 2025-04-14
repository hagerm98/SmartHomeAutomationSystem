package smarthome.services.security;

import io.grpc.stub.StreamObserver;
import smarthome.generated.security.*;
import smarthome.generated.security.SecurityServiceGrpc.SecurityServiceImplBase;

public class SecurityService extends SecurityServiceImplBase {

    @Override
    public void lockDoor(
            LockDoorRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
    }

    @Override
    public void unlockDoor(
            UnlockDoorRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
    }

    @Override
    public void registerSecurityDevice(
            SecurityDevice request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
    }

    @Override
    public void deregisterSecurityDevice(
            SecurityDevice request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
    }

    @Override
    public void respondToSecurityEvent(
            SecurityEvent request,
            StreamObserver<SecurityEventAction> responseObserver
    ) {
        // TODO
    }

    @Override
    public StreamObserver<LockDoorRequest> lockDoors(
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
        return null;
    }

    @Override
    public StreamObserver<UnlockDoorRequest> unlockDoors(
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
        return null;
    }
    
}
