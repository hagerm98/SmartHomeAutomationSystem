package smarthome.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import smarthome.services.climate.ClimateService;
import smarthome.services.lighting.LightingService;
import smarthome.services.security.SecurityService;

import java.io.IOException;

public class SmartHomeServer {
    public static void main(String[] args) {
        LightingService lightingService = new LightingService();
        ClimateService climateService = new ClimateService();
        SecurityService securityService = new SecurityService();

        int port = 50051;

        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(lightingService)
                    .addService(climateService)
                    .addService(securityService)
                    .build()
                    .start();

            System.out.println("Smart Home Server started, listening on port: " + port);
            server.awaitTermination();

        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            throw new RuntimeException(e);

        } catch (InterruptedException e) {
            System.out.println("Server interrupted: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
