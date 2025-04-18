package smarthome.server;

import io.grpc.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class AuthorizationServerInterceptor implements ServerInterceptor {

    // Implementing authentication on server side using JWT token validated on every incoming request using Server Interceptor
    public static final String JWT_SIGNING_KEY = "5idSuLeuVN5xGHVbwQyExrr1HWSBTfndgLtF5m3UzTo=";
    public static final String BEARER_TYPE = "Bearer";

    public static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);
    public static final Context.Key<String> CLIENT_ID_CONTEXT_KEY = Context.key("clientId");

    private final JwtParser parser = Jwts.parser().setSigningKey(JWT_SIGNING_KEY);

    /**
     * Intercepts the all calls incoming to any of the server services and checks the authorization token.
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String value = metadata.get(AUTHORIZATION_METADATA_KEY);

        // Check the authorization token
        Status status;
        if (value == null) {
            status = Status.UNAUTHENTICATED.withDescription("Authorization token is missing");
        } else if (!value.startsWith(BEARER_TYPE)) {
            status = Status.UNAUTHENTICATED.withDescription("Unknown authorization type");
        } else {
            // Parse the JWT token
            try {
                String token = value.substring(BEARER_TYPE.length()).trim();
                Jws<Claims> claims = parser.parseClaimsJws(token);
                Context ctx = Context.current().withValue(CLIENT_ID_CONTEXT_KEY, claims.getBody().getSubject());
                return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
            } catch (Exception e) {
                status = Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e);
            }
        }

        // If the token is invalid, close the call with an error status
        serverCall.close(status, metadata);
        return new ServerCall.Listener<ReqT>() {};
    }
}