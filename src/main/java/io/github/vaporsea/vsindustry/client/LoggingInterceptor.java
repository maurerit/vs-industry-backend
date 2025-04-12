package io.github.vaporsea.vsindustry.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Intercepts HttpRequests and logs request/response information.
 */
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private final Logger log;
    private final LogFormatter formatter;

    /**
     * Constructor to allow setting logger and LogFormatter to be used
     *
     * @param log       logger implementation to be used
     * @param formatter LogFormatter implementation to be used
     */
    public LoggingInterceptor(Logger log, LogFormatter formatter) {
        this.log = log;
        this.formatter = formatter;
    }

    /**
     * Intercepts client side HttpRequests and returns a response.
     *
     * @param request   the request, containing method, URI, and headers
     * @param body      request body
     * @param execution context used to execute request so response can be logged
     * @return response
     * @throws IOException when I/O errors occur
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        if(log.isDebugEnabled()) {
            log.debug(formatter.formatRequest(request, body));
        }

        ClientHttpResponse response = execution.execute(request, body);

        if(log.isDebugEnabled()) {
            log.debug(formatter.formatResponse(response));
        }
        return response;
    }
}
