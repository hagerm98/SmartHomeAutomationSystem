package smarthome.services.climate;

import io.grpc.stub.StreamObserver;
import smarthome.generated.climate.*;
import smarthome.generated.climate.ClimateServiceGrpc.ClimateServiceImplBase;

public class ClimateService extends ClimateServiceImplBase {

    int targetTemperature = 21;
    int targetHumidity = 50;
    DeviceState heatingState = DeviceState.OFF;
    DeviceState acState = DeviceState.OFF;
    HumidifierDehumidifierState humidifierDehumidifierState = HumidifierDehumidifierState.HUMIDIFIER_DEHUMIDIFIER_OFF;

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
        // TODO
    }

    @Override
    public void respondToTemperatureReading(
            TemperatureChangeEvent request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // TODO
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
        // TODO
    }

    @Override
    public void getHumidityHistory(
            HumidityHistoryRequest request,
            StreamObserver<HumidityReading> responseObserver
    ) {
        // TODO
    }
    
}
