package smarthome.server.services.lighting;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import smarthome.generated.general.DeviceState;
import smarthome.generated.general.OperationResponse;
import smarthome.generated.lighting.*;
import smarthome.generated.lighting.LightingServiceGrpc.LightingServiceImplBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LightingService extends LightingServiceImplBase {

    Map<Integer, LightingDeviceDetails> lightingDetails = new HashMap<>();

    List<Integer> turnedOffLights = new ArrayList<>();

    /**
     * Set the lighting state of a lighting device.
     * The lighting state can be either ON or OFF.
     */
    @Override
    public void setLightingState(
            LightingStateRequest request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // Check if the lighting device exists
        if (!lightingDetails.containsKey(request.getDeviceNumber())) {
            // If not, return an error
            responseObserver.onError(Status.NOT_FOUND.withDescription("No lighting device with the given number '"
                    + request.getDeviceNumber() + "' exists.").asRuntimeException());
        } else {
            // If it exists, update the lighting state
            LightingDeviceDetails oldLightingDeviceDetails = lightingDetails.get(request.getDeviceNumber());

            // Create a new LightingDeviceDetails object with the updated state
            LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder(oldLightingDeviceDetails)
                    .setLightingDeviceState(request.getLightingState())
                    .build();

            // Update the lighting details map with the new state
            lightingDetails.put(request.getDeviceNumber(), lightingDeviceDetails);

            // Send the updated lighting device details back to the client
            responseObserver.onNext(lightingDeviceDetails);
            responseObserver.onCompleted();
        }
    }

    /**
     * Set the brightness of a lighting device.
     */
    @Override
    public void setLightingBrightness(
            LightingBrightnessRequest request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // Check if the lighting device exists
        if (!lightingDetails.containsKey(request.getDeviceNumber())) {
            // If not, return an error
            responseObserver.onError(Status.NOT_FOUND.withDescription("No lighting device with the given number '"
                    + request.getDeviceNumber() + "' exists.").asRuntimeException());
        } else {
            // If it exists, update the brightness
            LightingDeviceDetails oldLightingDeviceDetails = lightingDetails.get(request.getDeviceNumber());

            // Create a new LightingDeviceDetails object with the updated brightness
            LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder(oldLightingDeviceDetails)
                    .setBrightness(request.getBrightness())
                    .build();

            // Update the lighting details map with the new brightness
            lightingDetails.put(request.getDeviceNumber(), lightingDeviceDetails);

            // Send the updated lighting device details back to the client
            responseObserver.onNext(lightingDeviceDetails);
            responseObserver.onCompleted();
        }
    }

    /**
     * Ask the server to respond to motion detection events.
     * The server will send the updated lighting device(s) details
     */
    @Override
    public StreamObserver<MotionEvent> respondToMotionDetection(
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // The server sets up a new observer that can gather each client request when it sees onNext()
        return new StreamObserver<MotionEvent>() {
            @Override
            public void onNext(MotionEvent motionEvent) {

                // Check if the lighting device exists
                for (LightingDeviceDetails lightingDeviceDetails : lightingDetails.values()) {

                    // Check if the lighting device is in the same room as the motion event
                    if (lightingDeviceDetails.getLightingDevice().getRoomNumber() == motionEvent.getRoomNumber()) {
                        LightingDeviceDetails newLightingDeviceDetails;

                        // Create a new LightingDeviceDetails object with the updated state
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

                        // Update the lighting details map with the new state
                        lightingDetails.put(newLightingDeviceDetails.getLightingDevice().getDeviceNumber(), newLightingDeviceDetails);

                        // Send the updated lighting device details back to the client
                        responseObserver.onNext(newLightingDeviceDetails);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle the case of any errors coming from the client
                responseObserver.onError(Status.CANCELLED
                        .withDescription("Server received Error from Client: " + throwable.getMessage())
                        .asRuntimeException()
                );
            }

            @Override
            public void onCompleted() {
                // Handle the case when the client has finished sending motion events
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * Register a new lighting device.
     * The lighting device must have a unique device number.
     */
    @Override
    public void registerLightingDevice(
            LightingDevice request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {

        // Check if the lighting device already exists
        if (lightingDetails.containsKey(request.getDeviceNumber())) {
            // If it does, return an error
            responseObserver.onError(
                    Status.ALREADY_EXISTS.withDescription("There's a lighting device with the same number '"
                            + request.getDeviceNumber() + "' already exists.").asRuntimeException()
            );
        } else {
            // If it doesn't, create a new LightingDeviceDetails object
            LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder()
                    .setLightingDevice(request)
                    .setLightingDeviceState(DeviceState.OFF)
                    .setBrightness(0.0f)
                    .build();

            // Add the new lighting device to the lighting details map
            lightingDetails.put(request.getDeviceNumber(), lightingDeviceDetails);

            // Send the new lighting device details back to the client
            responseObserver.onNext(lightingDeviceDetails);
            responseObserver.onCompleted();
        }
    }

    /**
     * Deregister a lighting device.
     * The lighting device must exist in the system.
     */
    @Override
    public void deregisterLightingDevice(
            LightingDevice request,
            StreamObserver<LightingDeviceDetails> responseObserver
    ) {
        // Check if the lighting device exists
        if (!lightingDetails.containsKey(request.getDeviceNumber())) {
            // If not, return an error
            responseObserver.onError(Status.NOT_FOUND.withDescription("No lighting device with the given number '"
                    + request.getDeviceNumber() + "' exists.").asRuntimeException());
        } else {
            // If it exists, remove it from the lighting details map
            LightingDeviceDetails lightingDeviceDetails = lightingDetails.remove(request.getDeviceNumber());

            // Send the removed lighting device details back to the client
            responseObserver.onNext(lightingDeviceDetails);
            responseObserver.onCompleted();
        }
    }

    /**
     * Turn off the lights of a lighting device. lights are given sequentially in the client stream
     * The lighting device must exist in the system.
     */
    @Override
    public StreamObserver<LightingDevice> turnOffLights(
            StreamObserver<OperationResponse> responseObserver
    ) {
        // The server sets up a new observer that can gather each client request when it sees onNext()
        return new StreamObserver<LightingDevice>() {
            @Override
            public void onNext(LightingDevice lightingDevice) {
                // Check if the lighting device exists
                if (!lightingDetails.containsKey(lightingDevice.getDeviceNumber())) {
                    // If not, return an error
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("No lighting device with the given number '"
                                    + lightingDevice.getDeviceNumber() + "' exists.")
                            .asRuntimeException()
                    );
                } else {
                    // If it exists, turn off the light
                    LightingDeviceDetails oldLightingDeviceDetails = lightingDetails.get(lightingDevice.getDeviceNumber());

                    // Create a new LightingDeviceDetails object with the updated state
                    LightingDeviceDetails lightingDeviceDetails = LightingDeviceDetails.newBuilder(oldLightingDeviceDetails)
                            .setLightingDeviceState(DeviceState.OFF)
                            .build();

                    // Update the lighting details map with the new state
                    lightingDetails.put(lightingDevice.getDeviceNumber(), lightingDeviceDetails);

                    // Send the updated lighting device details back to the client
                    turnedOffLights.add(lightingDevice.getDeviceNumber());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle the case of any errors coming from the client
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(false)
                        .setOperationName("turnOffLights")
                        .setMessage("Received Client Error: " + throwable.getMessage())
                        .build();

                // Send the error response back to the client
                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                // Handle the case when the client has finished sending lighting devices
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(true)
                        .setOperationName("turnOffLights")
                        .setMessage("All given lights " + turnedOffLights + " are turned off successfully")
                        .build();

                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();

                // Clear the list of turned off lights after sending the response
                turnedOffLights.clear();
            }
        };
    }
    
}
