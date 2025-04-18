package smarthome.server;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Intercepts the all calls incoming to any of the server services and logs the request and response.
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        logger.info("Method: {}", call.getMethodDescriptor().getFullMethodName());
        logger.info("Headers: {}", headers);

        // Log the request and its parameters
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(call, headers)
        ) {
            @Override
            public void onMessage(ReqT message) {
                logger.info("Request Parameters: {}", message);
                super.onMessage(message);
            }
        };
    }
}
