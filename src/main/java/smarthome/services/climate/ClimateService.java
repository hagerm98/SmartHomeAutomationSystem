package smarthome.services.climate;

import io.grpc.stub.StreamObserver;
import smarthome.generated.climate.*;
import smarthome.generated.climate.ClimateServiceGrpc.ClimateServiceImplBase;

public class ClimateService extends ClimateServiceImplBase {

    @Override
    public void setTargetClimateSettings(
            TargetClimateSetting request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        // TODO
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
        // TODO
    }

    @Override
    public void setACState(
            ACStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // TODO
    }

    @Override
    public void setHumidifierDehumidifierState(
            HumidifierDehumidifierStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // TODO
    }

    @Override
    public void getClimateDevicesState(
            ClimateDevicesStateRequest request,
            StreamObserver<ClimateDevicesState> responseObserver
    ) {
        // TODO
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
