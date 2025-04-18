package smarthome.client;

import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smarthome.generated.climate.*;
import smarthome.generated.general.DeviceState;
import smarthome.generated.general.OperationResponse;
import smarthome.generated.lighting.*;
import smarthome.generated.security.*;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class SmartHomeClient {

    private static final Logger logger = LoggerFactory.getLogger(SmartHomeClient.class);

    private final LightingServiceGrpc.LightingServiceBlockingStub lightingServiceBlockingStub;
    private final LightingServiceGrpc.LightingServiceStub lightingServiceStub;

    private final ClimateServiceGrpc.ClimateServiceBlockingStub climateServiceBlockingStub;
    private final ClimateServiceGrpc.ClimateServiceStub climateServiceStub;

    private final SecurityServiceGrpc.SecurityServiceBlockingStub securityServiceBlockingStub;
    private final SecurityServiceGrpc.SecurityServiceStub securityServiceStub;

    private final Context.CancellableContext cancellableContext;

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

        this.cancellableContext = Context.current().withCancellation();
    }

    public void cancelOperation() {
        if (cancellableContext.isCancelled()) {
            logger.info("Operation cancelled");
        } else {
            cancellableContext.cancel(new RuntimeException("Operation cancelled by user"));
            logger.info("Operation cancelled by user");
        }
    }

    // Methods to interact with the lighting service

    // Wrapper method to call lighting service: setLightingState
    public LightingDeviceDetails setLightingState(int deviceNumber, DeviceState state) {
        logger.info("Setting lighting state {} for device number: {}", state, deviceNumber);
        return lightingServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).setLightingState(
                LightingStateRequest.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setLightingState(state)
                        .build()
        );
    }

    // Wrapper method to call lighting service: setLightingBrightness
    public LightingDeviceDetails setLightingBrightness(int deviceNumber, int brightness) {
        logger.info("Setting lighting brightness {} for device number: {}", brightness, deviceNumber);
        return lightingServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).setLightingBrightness(
                LightingBrightnessRequest.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setBrightness(brightness)
                        .build()
        );
    }

    // Wrapper method to call lighting service: respondToMotionDetection
    public StreamObserver<MotionEvent> respondToMotionDetection(StreamObserver<LightingDeviceDetails> responseObserver) {
        logger.info("Setting up motion detection response observer");
        return lightingServiceStub.respondToMotionDetection(responseObserver);
    }

    // Wrapper method to call lighting service: registerLightingDevice
    public LightingDeviceDetails registerLightingDevice(int deviceNumber, int roomNumber) {
        logger.info("Registering lighting device number: {} in room number: {}", deviceNumber, roomNumber);
        return lightingServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).registerLightingDevice(
                LightingDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setRoomNumber(roomNumber)
                        .build()
        );
    }

    // Wrapper method to call lighting service: deregisterLightingDevice
    public LightingDeviceDetails deregisterLightingDevice(int deviceNumber) {
        logger.info("Deregistering lighting device number: {}", deviceNumber);
        return lightingServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).deregisterLightingDevice(
                LightingDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .build()
        );
    }

    // Wrapper method to call lighting service: turnOffLights
    public StreamObserver<LightingDevice> turnOffLights(StreamObserver<OperationResponse> responseObserver) {
        logger.info("Setting up turn off lights response observer");
        return lightingServiceStub.turnOffLights(
                responseObserver
        );
    }

    // Methods to interact with the climate service

    // Wrapper method to call climate service: setTargetClimateSettings
    public OperationResponse setTargetClimateSettings(int targetTemperature, int targetHumidity) {
        logger.info("Setting target climate settings: temperature {} and humidity {}", targetTemperature, targetHumidity);
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).setTargetClimateSettings(
                TargetClimateSetting.newBuilder()
                        .setTargetTemperature(targetTemperature)
                        .setTargetHumidity(targetHumidity)
                        .build()
        );
    }

    // Wrapper method to call climate service: respondToHumidityReading
    public ClimateDevicesState respondToHumidityReading(int humidity) {
        logger.info("Sending to server humidity reading: {}", humidity);
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).respondToHumidityReading(
                HumidityChangeEvent.newBuilder()
                        .setHumidity(humidity)
                        .build()
        );
    }

    // Wrapper method to call climate service: respondToTemperatureReading
    public ClimateDevicesState respondToTemperatureReading(int temperature) {
        logger.info("Sending to server temperature reading: {}", temperature);
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).respondToTemperatureReading(
                TemperatureChangeEvent.newBuilder()
                        .setTemperature(temperature)
                        .build()
        );
    }

    // Wrapper method to call climate service: setHeatingState
    public ClimateDevicesState setHeatingState(DeviceState state) {
        logger.info("Setting heating state: {}", state);
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).setHeatingState(
                HeatingStateRequest.newBuilder()
                        .setHeatingState(state)
                        .build()
        );
    }

    // Wrapper method to call climate service: setACState
    public ClimateDevicesState setACState(DeviceState state) {
        logger.info("Setting AC state: {}", state);
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).setACState(
                ACStateRequest.newBuilder()
                        .setAcState(state)
                        .build()
        );
    }

    // Wrapper method to call climate service: setHumidifierDehumidifierState
    public ClimateDevicesState setHumidifierDehumidifierState(HumidifierDehumidifierState state) {
        logger.info("Setting humidifier/dehumidifier state: {}", state);
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).setHumidifierDehumidifierState(
                HumidifierDehumidifierStateRequest.newBuilder()
                        .setState(state)
                        .build()
        );
    }

    // Wrapper method to call climate service: getClimateDevicesState
    public ClimateDevicesState getClimateDevicesState() {
        logger.info("Getting climate devices state");
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).getClimateDevicesState(
                ClimateDevicesStateRequest.newBuilder().build()
        );
    }

    // Wrapper method to call climate service: getTemperatureHistory (Sync)
    public Iterator<TemperatureReading> getTemperatureHistory() {
        logger.info("Getting temperature history");
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).getTemperatureHistory(
                TemperatureHistoryRequest.newBuilder().build()
        );
    }

    // Wrapper method to call climate service: getTemperatureHistory (Async)
    public void getTemperatureHistoryAsync(StreamObserver<TemperatureReading> responseObserver) {
        logger.info("Getting temperature history asynchronously");
        climateServiceStub.withDeadlineAfter(5, TimeUnit.SECONDS).getTemperatureHistory(
                TemperatureHistoryRequest.newBuilder().build(),
                responseObserver
        );
    }

    // Wrapper method to call climate service: getHumidityHistory
    public Iterator<HumidityReading> getHumidityHistory() {
        logger.info("Getting humidity history");
        return climateServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).getHumidityHistory(
                HumidityHistoryRequest.newBuilder().build()
        );
    }

    // Wrapper method to call climate service: getHumidityHistory (Async)
    public void getHumidityHistoryAsync(StreamObserver<HumidityReading> responseObserver) {
        logger.info("Getting humidity history asynchronously");
        climateServiceStub.withDeadlineAfter(5, TimeUnit.SECONDS).getHumidityHistory(
                HumidityHistoryRequest.newBuilder().build(),
                responseObserver
        );
    }

    // Methods to interact with the security service

    // Wrapper method to call security service: lockDoor
    public OperationResponse lockDoor(int doorNumber) {
        logger.info("Locking door number: {}", doorNumber);
        return securityServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).lockDoor(
                LockDoorRequest.newBuilder()
                        .setDoorNumber(doorNumber)
                        .build()
        );
    }

    // Wrapper method to call security service: unlockDoor
    public OperationResponse unlockDoor(int doorNumber) {
        logger.info("Unlocking door number: {}", doorNumber);
        return securityServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).unlockDoor(
                UnlockDoorRequest.newBuilder()
                        .setDoorNumber(doorNumber)
                        .build()
        );
    }

    // Wrapper method to call security service: registerSecurityDevice
    public OperationResponse registerSecurityDevice(int deviceNumber, SecurityDeviceType deviceType) {
        logger.info("Registering security device number: {} of type: {}", deviceNumber, deviceType);
        return securityServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).registerSecurityDevice(
                SecurityDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .setDeviceType(deviceType)
                        .build()
        );
    }

    // Wrapper method to call security service: deregisterSecurityDevice
    public OperationResponse deregisterSecurityDevice(int deviceNumber) {
        logger.info("Deregistering security device number: {}", deviceNumber);
        return securityServiceBlockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).deregisterSecurityDevice(
                SecurityDevice.newBuilder()
                        .setDeviceNumber(deviceNumber)
                        .build()
        );
    }

    // Wrapper method to call security service: respondToSecurityEvent
    public void respondToSecurityEvent(SecurityEvent securityEvent, StreamObserver<SecurityEventAction> responseObserver) {
        logger.info("Responding to security event: {}", securityEvent);
        securityServiceStub.withDeadlineAfter(5, TimeUnit.SECONDS).respondToSecurityEvent(securityEvent, responseObserver);
    }

    // Wrapper method to call security service: lockDoors
    public StreamObserver<LockDoorRequest> lockDoors(StreamObserver<OperationResponse> responseObserver) {
        logger.info("Setting up lock doors response observer");
        return securityServiceStub.lockDoors(responseObserver);
    }

    // Wrapper method to call security service: unlockDoors
    public StreamObserver<UnlockDoorRequest> unlockDoors(StreamObserver<OperationResponse> responseObserver) {
        logger.info("Setting up unlock doors response observer");
        return securityServiceStub.unlockDoors(responseObserver);
    }

}
