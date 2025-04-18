package smarthome.server.services.climate;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import smarthome.generated.climate.*;
import smarthome.generated.climate.ClimateServiceGrpc.ClimateServiceImplBase;
import smarthome.generated.general.DeviceState;
import smarthome.generated.general.OperationResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ClimateService extends ClimateServiceImplBase {

    int targetTemperature = 21;
    int targetHumidity = 50;
    DeviceState heatingState = DeviceState.OFF;
    DeviceState acState = DeviceState.OFF;
    HumidifierDehumidifierState humidifierDehumidifierState = HumidifierDehumidifierState.HUMIDIFIER_DEHUMIDIFIER_OFF;

    List<TemperatureReading> temperatureReadings = new ArrayList<>();
    List<HumidityReading> humidityReadings = new ArrayList<>();

    /**
     * Set the target temperature and humidity settings for the climate control system.
     * This method is called by the client to set the desired temperature and humidity levels.
     */
    @Override
    public void setTargetClimateSettings(
            TargetClimateSetting request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // Set the target temperature and humidity based on the request
        targetTemperature = request.getTargetTemperature();
        targetHumidity = request.getTargetHumidity();

        // Create a response indicating success
        OperationResponse operationResponse = OperationResponse.newBuilder()
                .setMessage("Target Temperature and Humidity set successfully to '" + targetTemperature
                        + "' degrees and '" + targetHumidity + "' percent")
                .setIsSuccessful(true)
                .setOperationName("setTargetClimateSettings")
                .build();

        // Send the response back to the client
        responseObserver.onNext(operationResponse);
        responseObserver.onCompleted();
    }

    /**
     * Respond to a humidity reading from the client.
     * This method is called by the client to send humidity readings and ask the server to act based on them
     */
    @Override
    public void respondToHumidityReading(
            HumidityChangeEvent request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // Determine the needed state of the humidifier/dehumidifier based on the humidity reading
        if (request.getHumidity() < targetHumidity) {
            humidifierDehumidifierState = HumidifierDehumidifierState.HUMIDIFIER;
        } else if (request.getHumidity() > targetHumidity) {
            humidifierDehumidifierState = HumidifierDehumidifierState.DEHUMIDIFIER;
        } else {
            humidifierDehumidifierState = HumidifierDehumidifierState.HUMIDIFIER_DEHUMIDIFIER_OFF;
        }

        // Create a new HumidityReading object with the current time and humidity value
        HumidityReading humidityReading = HumidityReading.newBuilder()
                .setTime(Instant.now().toString())
                .setHumidity(request.getHumidity())
                .build();

        // Add the humidity reading to the list of readings
        humidityReadings.add(humidityReading);

        // Create a ClimateDevicesState object with the current states of the devices
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        // Send the ClimateDevicesState object back to the client
        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    /**
     * Respond to a temperature reading from the client.
     * This method is called by the client to send temperature readings and ask the server to act based on them
     */
    @Override
    public void respondToTemperatureReading(
            TemperatureChangeEvent request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // Determine the needed state of the heating and AC devices based on the temperature reading
        if (request.getTemperature() < targetTemperature) {
            heatingState = DeviceState.ON;
            acState = DeviceState.OFF;
        } else if (request.getTemperature() > targetTemperature) {
            heatingState = DeviceState.OFF;
            acState = DeviceState.ON;
        } else {
            heatingState = DeviceState.OFF;
            acState = DeviceState.OFF;
        }

        // Create a new TemperatureReading object with the current time and temperature value
        TemperatureReading temperatureReading = TemperatureReading.newBuilder()
                .setTime(Instant.now().toString())
                .setTemperature(request.getTemperature())
                .build();

        // Add the temperature reading to the list of readings
        temperatureReadings.add(temperatureReading);

        // Create a ClimateDevicesState object with the current states of the devices
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        // Send the ClimateDevicesState object back to the client
        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    /**
     * Set the heating state of the climate control system.
     * This method is called by the client to set the heating state of the system.
     * It updates the heating state and sends the updated state of all climate devices back to the client.
     */
    @Override
    public void setHeatingState(
            HeatingStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // Set the heating state based on the request
        heatingState = request.getHeatingState();

        // Create a ClimateDevicesState object with the current states of the devices
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        // Send the ClimateDevicesState object back to the client
        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    /**
     * Set the Air conditioner state of the climate control system.
     * This method is called by the client to set the Air conditioner state of the system.
     */
    @Override
    public void setACState(
            ACStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // Set the Air conditioner state based on the request
        acState = request.getAcState();

        // Create a ClimateDevicesState object with the current states of the devices
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        // Send the ClimateDevicesState object back to the client
        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    /**
     * Set the humidifier/dehumidifier state of the climate control system.
     * This method is called by the client to set the humidifier/dehumidifier state of the system.
     */
    @Override
    public void setHumidifierDehumidifierState(
            HumidifierDehumidifierStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // Set the humidifier/dehumidifier state based on the request
        humidifierDehumidifierState = request.getState();

        // Create a ClimateDevicesState object with the current states of the devices
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        // Send the ClimateDevicesState object back to the client
        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    /**
     * Get the current state of all climate devices.
     * This method is called by the client to retrieve the current state of the climate devices.
     * It sends the current state of the heating, Air conditioner, and humidifier/dehumidifier devices back to the client.
     */
    @Override
    public void getClimateDevicesState(
            ClimateDevicesStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // Create a ClimateDevicesState object with the current states of the devices
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        // Send the ClimateDevicesState object back to the client
        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    /**
     * Get the temperature history.
     * This method is called by the client to retrieve the history of temperature readings.
     */
    @Override
    public void getTemperatureHistory(
            TemperatureHistoryRequest request,
            StreamObserver<TemperatureReading> responseObserver
    ) {
        // Check if there are any temperature readings available
        if (temperatureReadings.isEmpty()) {
            // If no readings are available, send an error response
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No entries found for Temperature readings at the moment.")
                    .asRuntimeException()
            );
        } else {
            // If there are readings available, send them back to the client
            int numberOfHistoryEntries = temperatureReadings.size();

            // Limit the number of readings to either the requested maximum or the available readings
            if (request.getMaxNoOfReadings() > 0) {
                numberOfHistoryEntries = Math.min(request.getMaxNoOfReadings(), temperatureReadings.size());
            }

            // Send the requested number of temperature readings to the client
            for (int i = 0; i < numberOfHistoryEntries; i++) {
                responseObserver.onNext(temperatureReadings.get(i));
            }

            // Complete the response
            responseObserver.onCompleted();
        }
    }

    /**
     * Get the humidity history.
     * This method is called by the client to retrieve the history of humidity readings.
     */
    @Override
    public void getHumidityHistory(
            HumidityHistoryRequest request,
            StreamObserver<HumidityReading> responseObserver
    ) {
        // Check if there are any humidity readings available
        if (humidityReadings.isEmpty()) {
            // If no readings are available, send an error response
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No entries found for Humidity readings at the moment.")
                    .asRuntimeException()
            );
        } else {
            // If there are readings available, send them back to the client
            int numberOfHistoryEntries = humidityReadings.size();

            // Limit the number of readings to either the requested maximum or the available readings
            if (request.getMaxNoOfReadings() > 0) {
                numberOfHistoryEntries = Math.min(request.getMaxNoOfReadings(), humidityReadings.size());
            }

            // Send the requested number of humidity readings to the client
            for (int i = 0; i < numberOfHistoryEntries; i++) {
                responseObserver.onNext(humidityReadings.get(i));
            }

            // Complete the response
            responseObserver.onCompleted();
        }
    }
    
}
