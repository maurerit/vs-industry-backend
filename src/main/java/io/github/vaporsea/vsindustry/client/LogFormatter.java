package io.github.vaporsea.vsindustry.client;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Interface that defines the formatting methods for request/response message logging
 */
public interface LogFormatter {

    /**
     * Interface method to format request messages to prepare for logging
     *
     * @param request contains the http request object
     * @param body contains the message request body to be logged
     * @return a formatted string to use for logging the request message
     */
    String formatRequest(HttpRequest request, byte[] body);

    /**
     * Interface method to format response messages to prepare for logging
     *
     * @param response contains the http response object
     * @return a formatted string to use for logging the response message
     * @throws IOException if cannot read the body
     */
    String formatResponse(ClientHttpResponse response) throws IOException;
}
