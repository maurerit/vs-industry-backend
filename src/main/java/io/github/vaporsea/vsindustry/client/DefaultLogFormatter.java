package io.github.vaporsea.vsindustry.client;

import static org.springframework.util.StreamUtils.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Default log formatter that can be used to provide various functionality, including logging
 * request/responses, mask JSON body field elements, mask header elements and set max body length for messages
 */
@Data
@Accessors(fluent = true)
public class DefaultLogFormatter implements LogFormatter {
    private static final int DEFAULT_BODY_LENGTH = 1000;
    private static final int DEFAULT_MAX_BODY_LENGTH = 50 * 1000;
    private static final String AUTHORIZATION_HEADER_FIELD_NAME = "authorization";
    private static final String COOKIE_HEADER_FIELD_NAME = "cookie";
    private static final String MASK = "*******";

    private boolean logHeaders;
    private List<String> maskHeaderValues;
    private List<String> maskBodyValues;
    private int maxBodyLength = DEFAULT_BODY_LENGTH;
    private boolean truncateBody = true;

    /**
     * Creates a formatted message for an {@link HttpRequest} based on configuration.
     *
     * @param request intercepted {@link HttpRequest}
     * @param body    request body content
     * @return formatted message to be logged
     */
    @Override
    public String formatRequest(HttpRequest request, byte[] body) {
        HttpHeaders headers = request.getHeaders();
        String message = new String(body);
        return format(String.format("Request: %s %s ", request.getMethod(), request.getURI()), message, headers);
    }

    /**
     * Creates a formatted message for an {@link ClientHttpResponse} based on configuration.
     *
     * @param response intercepted {@link ClientHttpResponse}
     * @return formatted message to be logged
     * @throws IOException when response body cannot be obtained
     */
    @Override
    public String formatResponse(ClientHttpResponse response) throws IOException {
        HttpHeaders headers = response.getHeaders();
        String message = copyToString(response.getBody(), headers.getContentType().getCharset());
        return format(String.format("Response: %s ", response.getStatusCode().value()), message, headers);
    }

    // Formats message based on configured LogFormatter options. This can be further improved, but at least we're down
    // to one area in the code (less collaborators) that needs changed as formatting options are added in the future.
    private String format(String msgPrefix, String message, HttpHeaders headers) {
        if(truncateBody) {
            message = truncate(message, maxBodyLength);
        }

        String processedMessage = msgPrefix + message;

        if(logHeaders) {
            return String.format("Headers: %s %s", maskHeaders(headers, maskHeaderValues), processedMessage);
        }

        return processedMessage;
    }

    /**
     * Truncates a message body to the length provided or within bounds if provided length exceeds the max.
     *
     * @param body   message body to be truncated
     * @param length max body length as configured in {@link DefaultLogFormatter}
     * @return the truncated message to log
     */
    public static String truncate(String body, int length) {
        return body == null ?
                null :
                body.substring(0, Math.min(body.length(), Math.min(length, DEFAULT_MAX_BODY_LENGTH)));
    }

    /**
     * Method to mask any sensitive message header fields
     *
     * @param headers          contains the message headers
     * @param maskedHeaderList contains list of header fields to be masked
     * @return the header list with any necessary values masked
     */
    public static String maskHeaders(HttpHeaders headers, List<String> maskedHeaderList) {
        StringBuilder builder = new StringBuilder();

        builder.append("{");
        for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append("=[");
            for(String value : entry.getValue()) {
                if((entry.getKey().equalsIgnoreCase(AUTHORIZATION_HEADER_FIELD_NAME)) || (entry.getKey()
                        .equalsIgnoreCase(COOKIE_HEADER_FIELD_NAME)) || ((maskedHeaderList != null) && (maskedHeaderList
                        .contains(entry.getKey())))) {

                    builder.append(MASK).append(",");
                }
                else {
                    builder.append(value).append(",");
                }
            }
            builder.setLength(builder.length() - 1);
            builder.append("],");
        }

        if(builder.length() > 1) {
            builder.setLength(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }
}
