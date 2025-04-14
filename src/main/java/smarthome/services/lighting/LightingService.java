package smarthome.services.lighting;

import io.grpc.stub.StreamObserver;
import smarthome.generated.lighting.*;
import smarthome.generated.lighting.LightingServiceGrpc.LightingServiceImplBase;

import java.util.HashMap;
import java.util.Map;

public class LightingService extends LightingServiceImplBase {

    Map<Integer, LightingDeviceDetails> lightingDetails = new HashMap<>();

    @Override
    public void setLightingState(
            LightingStateRequest request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        if (!lightingDetails.containsKey(request.getDeviceNumber())) {
            responseObserver.onError(new Exception("No lighting device with the given number '"
                    + request.getDeviceNumber() + "' exists."));
        } else {
            LightingDeviceDetails oldLightingDeviceDetails = lightingDetails.get(request.getDeviceNumber());

            LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder(oldLightingDeviceDetails)
                    .setLightingDeviceState(request.getLightingState())
                    .build();

            lightingDetails.put(request.getDeviceNumber(), lightingDeviceDetails);

            responseObserver.onNext(lightingDeviceDetails);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void setLightingBrightness(
            LightingBrightnessRequest request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        if (!lightingDetails.containsKey(request.getDeviceNumber())) {
            responseObserver.onError(new Exception("No lighting device with the given number '"
                    + request.getDeviceNumber() + "' exists."));
        } else {
            LightingDeviceDetails oldLightingDeviceDetails = lightingDetails.get(request.getDeviceNumber());

            LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder(oldLightingDeviceDetails)
                    .setBrightness(request.getBrightness())
                    .build();

            lightingDetails.put(request.getDeviceNumber(), lightingDeviceDetails);

            responseObserver.onNext(lightingDeviceDetails);
        }

        responseObserver.onCompleted();
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

        if (lightingDetails.containsKey(request.getDeviceNumber())) {
            responseObserver.onError(new Exception("There's a lighting device with the same number '"
                    + request.getDeviceNumber() + "' already exists."));
        } else {
            LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder()
                    .setLightingDevice(request)
                    .setLightingDeviceState(DeviceState.OFF)
                    .setBrightness(0.0f)
                    .build();

            lightingDetails.put(request.getDeviceNumber(), lightingDeviceDetails);

            responseObserver.onNext(lightingDeviceDetails);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void deregisterLightingDevice(
            LightingDevice request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        if (!lightingDetails.containsKey(request.getDeviceNumber())) {
            responseObserver.onError(new Exception("No lighting device with the given number '"
                    + request.getDeviceNumber() + "' exists."));
        } else {
            LightingDeviceDetails lightingDeviceDetails = lightingDetails.remove(request.getDeviceNumber());

            responseObserver.onNext(lightingDeviceDetails);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LightingDevice> turnOffLights(
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
        return null;
    }
    
}
