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

    @Override
    public void setTargetClimateSettings(
            TargetClimateSetting request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        targetTemperature = request.getTargetTemperature();
        targetHumidity = request.getTargetHumidity();

        OperationResponse operationResponse = OperationResponse.newBuilder()
                .setMessage("Target Temperature and Humidity set successfully to '" + targetTemperature
                        + "' degrees and '" + targetHumidity + "' percent")
                .setIsSuccessful(true)
                .setOperationName("setTargetClimateSettings")
                .build();

        responseObserver.onNext(operationResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void respondToHumidityReading(
            HumidityChangeEvent request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        if (request.getHumidity() < targetHumidity) {
            humidifierDehumidifierState = HumidifierDehumidifierState.HUMIDIFIER;
        } else if (request.getHumidity() > targetHumidity) {
            humidifierDehumidifierState = HumidifierDehumidifierState.DEHUMIDIFIER;
        } else {
            humidifierDehumidifierState = HumidifierDehumidifierState.HUMIDIFIER_DEHUMIDIFIER_OFF;
        }

        HumidityReading humidityReading = HumidityReading.newBuilder()
                .setTime(Instant.now().toString())
                .setHumidity(request.getHumidity())
                .build();

        humidityReadings.add(humidityReading);

        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    @Override
    public void respondToTemperatureReading(
            TemperatureChangeEvent request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
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

        TemperatureReading temperatureReading = TemperatureReading.newBuilder()
                .setTime(Instant.now().toString())
                .setTemperature(request.getTemperature())
                .build();

        temperatureReadings.add(temperatureReading);

        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    @Override
    public void setHeatingState(
            HeatingStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        heatingState = request.getHeatingState();

        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    @Override
    public void setACState(
            ACStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        acState = request.getAcState();

        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    @Override
    public void setHumidifierDehumidifierState(
            HumidifierDehumidifierStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        humidifierDehumidifierState = request.getState();

        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    @Override
    public void getClimateDevicesState(
            ClimateDevicesStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        ClimateDevicesState climateDevicesState = ClimateDevicesState.newBuilder()
                .setAcState(acState)
                .setHeatingState(heatingState)
                .setHumidityDeviceState(humidifierDehumidifierState)
                .build();

        responseObserver.onNext(climateDevicesState);
        responseObserver.onCompleted();
    }

    @Override
    public void getTemperatureHistory(
            TemperatureHistoryRequest request,
            StreamObserver<TemperatureReading> responseObserver
    ) {
        if (temperatureReadings.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No entries found for Temperature readings at the moment.")
                    .asRuntimeException()
            );
        } else {
            int numberOfHistoryEntries = temperatureReadings.size();

            if (request.getMaxNoOfReadings() > 0) {
                numberOfHistoryEntries = Math.min(request.getMaxNoOfReadings(), temperatureReadings.size());
            }

            for (int i = 0; i < numberOfHistoryEntries; i++) {
                responseObserver.onNext(temperatureReadings.get(i));
            }

            responseObserver.onCompleted();
        }
    }

    @Override
    public void getHumidityHistory(
            HumidityHistoryRequest request,
            StreamObserver<HumidityReading> responseObserver
    ) {
        if (humidityReadings.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No entries found for Humidity readings at the moment.")
                    .asRuntimeException()
            );
        } else {

            int numberOfHistoryEntries = humidityReadings.size();

            if (request.getMaxNoOfReadings() > 0) {
                numberOfHistoryEntries = Math.min(request.getMaxNoOfReadings(), humidityReadings.size());
            }

            for (int i = 0; i < numberOfHistoryEntries; i++) {
                responseObserver.onNext(humidityReadings.get(i));
            }

            responseObserver.onCompleted();
        }
    }
    
}
