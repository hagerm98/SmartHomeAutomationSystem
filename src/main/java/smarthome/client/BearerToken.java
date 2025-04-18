package smarthome.client;

import io.grpc.*;

import java.util.concurrent.Executor;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class BearerToken implements CallCredentials {

    public static final String BEARER_TYPE = "Bearer";
    public static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);

    private final String value;

    public BearerToken(String value) {
        this.value = value;
    }

    /**
     * Applies the request metadata to the gRPC calls from the client stubs.
     */
    @Override
    public void applyRequestMetadata(MethodDescriptor<?, ?> methodDescriptor, Attributes attributes, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            try {
                // Create a new Metadata object and add the authorization header
                Metadata headers = new Metadata();
                headers.put(AUTHORIZATION_METADATA_KEY, String.format("%s %s", BEARER_TYPE, value));
                metadataApplier.apply(headers);
            } catch (Throwable e) {
                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
            }
        });
    }

    @Override
    public void thisUsesUnstableApi() {}
}
