package smarthome.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import smarthome.generated.climate.*;
import smarthome.generated.general.DeviceState;
import smarthome.generated.general.OperationResponse;
import smarthome.generated.lighting.*;
import smarthome.generated.security.*;

import java.util.Iterator;

public class SmartHomeClient {

    private final LightingServiceGrpc.LightingServiceBlockingStub lightingServiceBlockingStub;
    private final LightingServiceGrpc.LightingServiceStub lightingServiceStub;

    private final ClimateServiceGrpc.ClimateServiceBlockingStub climateServiceBlockingStub;
    private final ClimateServiceGrpc.ClimateServiceStub climateServiceStub;

    private final SecurityServiceGrpc.SecurityServiceBlockingStub securityServiceBlockingStub;
    private final SecurityServiceGrpc.SecurityServiceStub securityServiceStub;

    public static final String JWT_SIGNING_KEY = "5idSuLeuVN5xGHVbwQyExrr1HWSBTfndgLtF5m3UzTo=";

    public SmartHomeClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        BearerToken credentials = new BearerToken(Jwts.builder()
                .setSubject("SmartHomeClientGUI")
                .signWith(SignatureAlgorithm.HS256, JWT_SIGNING_KEY)
                .compact());

        this.lightingServiceBlockingStub = LightingServiceGrpc
                .newBlockingStub(channel)
                .withCallCredentials(credentials);
        this.lightingServiceStub = LightingServiceGrpc
                .newStub(channel)
                .withCallCredentials(credentials);

        this.climateServiceBlockingStub = ClimateServiceGrpc
                .newBlockingStub(channel)
                .withCallCredentials(credentials);
        this.climateServiceStub = ClimateServiceGrpc
                .newStub(channel)
                .withCallCredentials(credentials);

        this.securityServiceBlockingStub = SecurityServiceGrpc
                .newBlockingStub(channel)
                .withCallCredentials(credentials);
        this.securityServiceStub = SecurityServiceGrpc
                .newStub(channel)
                .withCallCredentials(credentials);
    }

    // Methods to interact with the lighting service

    // Wrapper method to call lighting service: setLightingState
    public LightingDeviceDetails setLightingState(int deviceNumber, DeviceState state) {
        return lightingServiceBlockingStub.setLightingState(
                LightingStateRequest.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setLightingState(state)
                        .build()
        );
    }

    // Wrapper method to call lighting service: setLightingBrightness
    public LightingDeviceDetails setLightingBrightness(int deviceNumber, int brightness) {
        return lightingServiceBlockingStub.setLightingBrightness(
                LightingBrightnessRequest.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setBrightness(brightness)
                        .build()
        );
    }

    // Wrapper method to call lighting service: respondToMotionDetection
    public StreamObserver<MotionEvent> respondToMotionDetection(StreamObserver<LightingDeviceDetails> responseObserver) {
        return lightingServiceStub.respondToMotionDetection(responseObserver);
    }

    // Wrapper method to call lighting service: registerLightingDevice
    public LightingDeviceDetails registerLightingDevice(int deviceNumber, int roomNumber) {
        return lightingServiceBlockingStub.registerLightingDevice(
                LightingDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setRoomNumber(roomNumber)
                        .build()
        );
    }

    // Wrapper method to call lighting service: deregisterLightingDevice
    public LightingDeviceDetails deregisterLightingDevice(int deviceNumber) {
        return lightingServiceBlockingStub.deregisterLightingDevice(
                LightingDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .build()
        );
    }

    // Wrapper method to call lighting service: turnOffLights
    public StreamObserver<LightingDevice> turnOffLights(StreamObserver<OperationResponse> responseObserver) {
        return lightingServiceStub.turnOffLights(
                responseObserver
        );
    }

    // Methods to interact with the climate service

    // Wrapper method to call climate service: setTargetClimateSettings
    public OperationResponse setTargetClimateSettings(int targetTemperature, int targetHumidity) {
        return climateServiceBlockingStub.setTargetClimateSettings(
                TargetClimateSetting.newBuilder()
                        .setTargetTemperature(targetTemperature)
                        .setTargetHumidity(targetHumidity)
                        .build()
        );
    }

    // Wrapper method to call climate service: respondToHumidityReading
    public ClimateDevicesState respondToHumidityReading(int humidity) {
        return climateServiceBlockingStub.respondToHumidityReading(
                HumidityChangeEvent.newBuilder()
                        .setHumidity(humidity)
                        .build()
        );
    }

    // Wrapper method to call climate service: respondToTemperatureReading
    public ClimateDevicesState respondToTemperatureReading(int temperature) {
        return climateServiceBlockingStub.respondToTemperatureReading(
                TemperatureChangeEvent.newBuilder()
                        .setTemperature(temperature)
                        .build()
        );
    }

    // Wrapper method to call climate service: setHeatingState
    public ClimateDevicesState setHeatingState(DeviceState state) {
        return climateServiceBlockingStub.setHeatingState(
                HeatingStateRequest.newBuilder()
                        .setHeatingState(state)
                        .build()
        );
    }

    // Wrapper method to call climate service: setACState
    public ClimateDevicesState setACState(DeviceState state) {
        return climateServiceBlockingStub.setACState(
                ACStateRequest.newBuilder()
                        .setAcState(state)
                        .build()
        );
    }

    // Wrapper method to call climate service: setHumidifierDehumidifierState
    public ClimateDevicesState setHumidifierDehumidifierState(HumidifierDehumidifierState state) {
        return climateServiceBlockingStub.setHumidifierDehumidifierState(
                HumidifierDehumidifierStateRequest.newBuilder()
                        .setState(state)
                        .build()
        );
    }

    // Wrapper method to call climate service: getClimateDevicesState
    public ClimateDevicesState getClimateDevicesState() {
        return climateServiceBlockingStub.getClimateDevicesState(
                ClimateDevicesStateRequest.newBuilder().build()
        );
    }

    // Wrapper method to call climate service: getTemperatureHistory (Sync)
    public Iterator<TemperatureReading> getTemperatureHistory() {
        return climateServiceBlockingStub.getTemperatureHistory(
                TemperatureHistoryRequest.newBuilder().build()
        );
    }

    // Wrapper method to call climate service: getTemperatureHistory (Async)
    public void getTemperatureHistoryAsync(StreamObserver<TemperatureReading> responseObserver) {
        climateServiceStub.getTemperatureHistory(
                TemperatureHistoryRequest.newBuilder().build(),
                responseObserver
        );
    }

    // Wrapper method to call climate service: getHumidityHistory
    public Iterator<HumidityReading> getHumidityHistory() {
        return climateServiceBlockingStub.getHumidityHistory(
                HumidityHistoryRequest.newBuilder().build()
        );
    }

    // Wrapper method to call climate service: getHumidityHistory (Async)
    public void getHumidityHistoryAsync(StreamObserver<HumidityReading> responseObserver) {
        climateServiceStub.getHumidityHistory(
                HumidityHistoryRequest.newBuilder().build(),
                responseObserver
        );
    }

    // Methods to interact with the security service

    // Wrapper method to call security service: lockDoor
    public OperationResponse lockDoor(int doorNumber) {
        return securityServiceBlockingStub.lockDoor(
                LockDoorRequest.newBuilder()
                        .setDoorNumber(doorNumber)
                        .build()
        );
    }

    // Wrapper method to call security service: unlockDoor
    public OperationResponse unlockDoor(int doorNumber) {
        return securityServiceBlockingStub.unlockDoor(
                UnlockDoorRequest.newBuilder()
                        .setDoorNumber(doorNumber)
                        .build()
        );
    }

    // Wrapper method to call security service: registerSecurityDevice
    public OperationResponse registerSecurityDevice(int deviceNumber, SecurityDeviceType deviceType) {
        return securityServiceBlockingStub.registerSecurityDevice(
                SecurityDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setDeviceType(deviceType)
                        .build()
        );
    }

    // Wrapper method to call security service: deregisterSecurityDevice
    public OperationResponse deregisterSecurityDevice(int deviceNumber) {
        return securityServiceBlockingStub.deregisterSecurityDevice(
                SecurityDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .build()
        );
    }

    // Wrapper method to call security service: respondToSecurityEvent
    public void respondToSecurityEvent(SecurityEvent securityEvent, StreamObserver<SecurityEventAction> responseObserver) {
        securityServiceStub.respondToSecurityEvent(securityEvent, responseObserver);
    }

    // Wrapper method to call security service: lockDoors
    public StreamObserver<LockDoorRequest> lockDoors(StreamObserver<OperationResponse> responseObserver) {
        return securityServiceStub.lockDoors(responseObserver);
    }

    // Wrapper method to call security service: unlockDoors
    public StreamObserver<UnlockDoorRequest> unlockDoors(StreamObserver<OperationResponse> responseObserver) {
        return securityServiceStub.unlockDoors(responseObserver);
    }

}
