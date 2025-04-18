package smarthome.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import smarthome.server.services.climate.ClimateService;
import smarthome.server.services.lighting.LightingService;
import smarthome.server.services.security.SecurityService;

import java.io.IOException;

public class SmartHomeServer {
    public static void main(String[] args) {
        // Initialize the services
        LightingService lightingService = new LightingService();
        ClimateService climateService = new ClimateService();
        SecurityService securityService = new SecurityService();

        int port = 50051;

        try {
            // Create and start the gRPC server
            Server server = ServerBuilder.forPort(port)
                    .addService(lightingService)
                    .addService(climateService)
                    .addService(securityService)
                    .intercept(new AuthorizationServerInterceptor())
                    .intercept(new LoggingInterceptor())
                    .build()
                    .start();

            System.out.println("Smart Home Server started, listening on port: " + port);

            // for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down smart home server...");
                server.shutdown();

                try {
                    // Wait for the server to terminate
                    if (!server.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
                        server.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Shutdown interrupted: " + e.getMessage());
                    server.shutdownNow();
                }

                System.out.println("Server shut down.");
            }));

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
