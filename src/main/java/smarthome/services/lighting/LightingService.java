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
        return new StreamObserver<MotionEvent>() {
            @Override
            public void onNext(MotionEvent motionEvent) {
                for (LightingDeviceDetails lightingDeviceDetails : lightingDetails.values()) {
                    if (lightingDeviceDetails.getLightingDevice().getRoomNumber() == motionEvent.getRoomNumber()) {
                        LightingDeviceDetails newLightingDeviceDetails;
                        if (motionEvent.getMotionState()) {
                            newLightingDeviceDetails = LightingDeviceDetails
                                    .newBuilder(lightingDeviceDetails)
                                    .setLightingDeviceState(DeviceState.ON)
                                    .build();
                        } else {
                            newLightingDeviceDetails = LightingDeviceDetails
                                    .newBuilder(lightingDeviceDetails)
                                    .setLightingDeviceState(DeviceState.OFF)
                                    .build();
                        }
                        lightingDetails.put(newLightingDeviceDetails.getLightingDevice().getDeviceNumber(), newLightingDeviceDetails);

                        responseObserver.onNext(newLightingDeviceDetails);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(new Exception("Server received Error from Client: " + throwable.getMessage()));
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
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
        return new StreamObserver<LightingDevice>() {
            @Override
            public void onNext(LightingDevice lightingDevice) {
                if (!lightingDetails.containsKey(lightingDevice.getDeviceNumber())) {
                    responseObserver.onError(new Exception("No lighting device with the given number '"
                            + lightingDevice.getDeviceNumber() + "' exists."));
                    responseObserver.onCompleted();
                } else {
                    LightingDeviceDetails oldLightingDeviceDetails = lightingDetails.get(lightingDevice.getDeviceNumber());

                    LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder(oldLightingDeviceDetails)
                            .setLightingDeviceState(DeviceState.OFF)
                            .build();

                    lightingDetails.put(lightingDevice.getDeviceNumber(), lightingDeviceDetails);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(false)
                        .setOperationName("turnOffLights")
                        .setMessage("Received Client Error: " + throwable.getMessage())
                        .build();

                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(true)
                        .setOperationName("turnOffLights")
                        .setMessage("All given lights are turned off successfully")
                        .build();

                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();
            }
        };
    }
    
}
