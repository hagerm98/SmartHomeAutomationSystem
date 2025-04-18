package smarthome.server.services.security;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import smarthome.generated.general.OperationResponse;
import smarthome.generated.security.*;
import smarthome.generated.security.SecurityServiceGrpc.SecurityServiceImplBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityService extends SecurityServiceImplBase {

    // Locked state of doors, True=locked, False=unlocked
    Map<Integer, Boolean> doorLockedStates = new HashMap<>();

    // Security devices registered with the system
    Map<Integer, SecurityDevice> registeredDevices = new HashMap<>();

    List<Integer> requestedDoorNumbersLock = new ArrayList<>();
    List<Integer> requestedDoorNumbersUnlock = new ArrayList<>();

    /**
     * Lock a door given its door number.
     */
    @Override
    public void lockDoor(
            LockDoorRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // Get the door number from the request
        int doorNumber = request.getDoorNumber();

        // Check if the door exists and return an error if it doesn't
        if (!doorLockedStates.containsKey(doorNumber)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Door '" + doorNumber + "' does not exist.")
                    .asRuntimeException()
            );
            return;
        }

        // Update the door's lock state
        doorLockedStates.put(doorNumber, true);

        // Create a response
        OperationResponse response = OperationResponse.newBuilder()
                .setIsSuccessful(true)
                .setMessage("Door '" + doorNumber + "' is now locked.")
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Unlock a door given its door number.
     */
    @Override
    public void unlockDoor(
            UnlockDoorRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // Get the door number from the request
        int doorNumber = request.getDoorNumber();

        // Check if the door exists and return an error if it doesn't
        if (!doorLockedStates.containsKey(doorNumber)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Door '" + doorNumber + "' does not exist.")
                    .asRuntimeException()
            );
            return;
        }

        // Update the door's lock state
        doorLockedStates.put(doorNumber, false);

        // Create a response
        OperationResponse response = OperationResponse.newBuilder()
                .setIsSuccessful(true)
                .setMessage("Door '" + doorNumber + "' is now unlocked.")
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Register a new security device with the system.
     */
    @Override
    public void registerSecurityDevice(
            SecurityDevice request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // Get the device number from the request
        int deviceNumber = request.getDeviceNumber();

        // Check if the device is already registered
        if (registeredDevices.containsKey(deviceNumber)) {
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription("Device '" + deviceNumber + "' is already registered.")
                    .asRuntimeException()
            );
            return;
        }

        // Register the device
        registeredDevices.put(deviceNumber, request);
        
        // Update doorLockedStates if device is of type door
        if (request.getDeviceType() == SecurityDeviceType.DOOR) {
            doorLockedStates.put(deviceNumber, true); // Initialize door as locked
        }

        // Create a response
        OperationResponse response = OperationResponse.newBuilder()
                .setIsSuccessful(true)
                .setMessage("Device '" + deviceNumber + "' has been registered.")
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Deregister a security device from the system.
     */
    @Override
    public void deregisterSecurityDevice(
            SecurityDevice request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // Get the device number from the request
        int deviceNumber = request.getDeviceNumber();

        // Check if the device is registered
        if (!registeredDevices.containsKey(deviceNumber)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Device '" + deviceNumber + "' is not registered.")
                    .asRuntimeException()
            );
            return;
        }

        // Deregister the device
        registeredDevices.remove(deviceNumber);
        
        // Update doorLockedStates if device is of type door
        if (request.getDeviceType() == SecurityDeviceType.DOOR) {
            doorLockedStates.remove(deviceNumber); // Remove door from locked states
        }

        // Create a response
        OperationResponse response = OperationResponse.newBuilder()
                .setIsSuccessful(true)
                .setMessage("Device '" + deviceNumber + "' has been deregistered.")
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Respond to a security event based on the event type
     */
    @Override
    public void respondToSecurityEvent(
            SecurityEvent request,
            StreamObserver<SecurityEventAction> responseObserver
    ) {
        // Get the event type and timestamp from the request
        SecurityEventType eventType = request.getEventType();
        long timestamp = request.getTimestamp();

        switch (eventType) {
            case FIRE_DETECTED:
                // Respond with actions for fire detection
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.ACTIVATE_ALARM)
                        .build());
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.UNLOCK_DOORS)
                        .build());
                break;

            case BREAK_IN_DETECTED:
                // Respond with actions for break-in detection
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.ACTIVATE_ALARM)
                        .build());
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.LOCK_DOORS)
                        .build());
                break;

            case OUTDOOR_MOTION_DETECTED:
                // Notify the user for outdoor motion detection
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.NOTIFY_USER)
                        .build());
                break;

            case DOORBELL_RING:
                // Notify the user for doorbell ring
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.NOTIFY_USER)
                        .build());
                break;

            default:
                // No action for NO_EVENT
                responseObserver.onNext(SecurityEventAction.newBuilder()
                        .setTimestamp(timestamp)
                        .setAction(SecurityAction.NO_ACTION)
                        .build());
                break;
        }

        // Complete the response ending the open stream after sending all actions
        responseObserver.onCompleted();
    }

    /**
     * Lock multiple doors given their door numbers
     */
    @Override
    public StreamObserver<LockDoorRequest> lockDoors(
            StreamObserver<OperationResponse> responseObserver
    ) {
        return new StreamObserver<LockDoorRequest>() {
            @Override
            public void onNext(LockDoorRequest request) {
                // Get the door number from the request
                int doorNumber = request.getDoorNumber();

                // Check if the door exists and return an error if it doesn't
                if (!doorLockedStates.containsKey(doorNumber)) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Door '" + doorNumber + "' does not exist.")
                            .asRuntimeException()
                    );
                    return;
                }

                // Update the door's lock state
                doorLockedStates.put(doorNumber, true);

                // Add the door number to the list of requested doors
                requestedDoorNumbersLock.add(doorNumber);
            }

            @Override
            public void onError(Throwable t) {
                // Handle the error and send a response
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(false)
                        .setOperationName("lockDoors")
                        .setMessage("Received Client Error: " + t.getMessage())
                        .build();

                // Send the error response back to the client
                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();

                requestedDoorNumbersLock.clear();
            }

            @Override
            public void onCompleted() {
                // Send a response indicating all requested doors have been locked
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(true)
                        .setOperationName("lockDoors")
                        .setMessage("All requested doors " + requestedDoorNumbersLock + " have been locked.")
                        .build();

                // Send the response back to the client
                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();

                // Clear the list of requested doors
                requestedDoorNumbersLock.clear();
            }
        };
    }

    /**
     * Unlock multiple doors given their door numbers
     */
    @Override
    public StreamObserver<UnlockDoorRequest> unlockDoors(
            StreamObserver<OperationResponse> responseObserver
    ) {
        return new StreamObserver<UnlockDoorRequest>() {
            @Override
            public void onNext(UnlockDoorRequest request) {
                // Get the door number from the request
                int doorNumber = request.getDoorNumber();

                // Check if the door exists and return an error if it doesn't
                if (!doorLockedStates.containsKey(doorNumber)) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Door '" + doorNumber + "' does not exist.")
                            .asRuntimeException()
                    );
                    return;
                }

                // Update the door's lock state
                doorLockedStates.put(doorNumber, false);

                // Add the door number to the list of requested doors
                requestedDoorNumbersUnlock.add(doorNumber);
            }

            @Override
            public void onError(Throwable t) {
                // Handle the error and send a response
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(false)
                        .setOperationName("unlockDoors")
                        .setMessage("Received Client Error: " + t.getMessage())
                        .build();

                // Send the error response back to the client
                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();

                // Clear the list of requested doors
                requestedDoorNumbersUnlock.clear();
            }

            @Override
            public void onCompleted() {
                // Send a response indicating all requested doors have been unlocked
                OperationResponse operationResponse = OperationResponse.newBuilder()
                        .setIsSuccessful(true)
                        .setOperationName("unlockDoors")
                        .setMessage("All requested doors " + requestedDoorNumbersUnlock + " have been unlocked.")
                        .build();

                // Send the response back to the client
                responseObserver.onNext(operationResponse);
                responseObserver.onCompleted();

                // Clear the list of requested doors
                requestedDoorNumbersUnlock.clear();
            }
        };
    }
    
}
