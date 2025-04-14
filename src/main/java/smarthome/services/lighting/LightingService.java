package smarthome.services.lighting;

import io.grpc.stub.StreamObserver;
import smarthome.generated.lighting.*;
import smarthome.generated.lighting.LightingServiceGrpc.LightingServiceImplBase;

public class LightingService extends LightingServiceImplBase {

    @Override
    public void setLightingState(
            LightingStateRequest request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // TODO
    }

    @Override
    public void setLightingBrightness(
            LightingBrightnessRequest request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // TODO
    }

    @Override
    public StreamObserver<MotionEvent> respondToMotionDetection(
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // TODO
        return null;
    }

    @Override
    public void registerLightingDevice(
            LightingDevice request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // TODO
    }

    @Override
    public void deregisterLightingDevice(
            LightingDevice request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // TODO
    }

    @Override
    public StreamObserver<LightingDevice> turnOffLights(
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
        return null;
    }
    
}
