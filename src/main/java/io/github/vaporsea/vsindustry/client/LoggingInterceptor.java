/*
 * MIT License
 *
 * Copyright (c) 2025 VaporSea
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
