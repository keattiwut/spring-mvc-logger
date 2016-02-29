/*
 * spring-mvc-logger logs requests/responses
 *
 * Copyright (c) 2013. Israel Zalmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kziomek.filter.logging;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class MvcLoggingFilter extends OncePerRequestFilter {

    protected static final Logger logger = LoggerFactory.getLogger(MvcLoggingFilter.class);

    public static final String REQUEST_PREFIX = "Request: ";
    private static final String RESPONSE_PREFIX = "Response: ";

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 1000;

    private boolean includeQueryString = false;

    private boolean includeHeaders = false;

    private boolean includePayload = false;

    private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            request = new BufferedRequestWrapper(request);
            response = new TeeResponseWrapper(response);
        }
        try {
            if (logger.isDebugEnabled()) {
                logRequest((BufferedRequestWrapper) request);
            }
            filterChain.doFilter(request, response);
        } finally {
            if (logger.isDebugEnabled()) {
                logResponse((TeeResponseWrapper) response);
            }
        }
    }


    private void logRequest(final BufferedRequestWrapper request) {
        logger.debug(buildRequestMessage(request, REQUEST_PREFIX));
    }

    private void logResponse(final TeeResponseWrapper response) {
        StringBuilder msg = new StringBuilder();
        msg.append(RESPONSE_PREFIX);
        try {
            msg.append("; payload=").append(new String(response.toByteArray(), response.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            logger.warn("Failed to parse response payload", e);
        }
        logger.debug(msg.toString());
    }


    private String buildRequestMessage(final HttpServletRequest request, String prefix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);

        /* Query params */
        if (isIncludeQueryString()) {
            msg.append(request.getMethod()).append(" ").append(request.getRequestURI());
            if (request.getQueryString() != null) {
                msg.append('?').append(request.getQueryString());
            }
        }

        /* Headers */
        if (isIncludeHeaders()) {
            Map<String, String> headers = getHeadersInfo(request);
            msg.append(" headers=").append(headers);
        }

        /* Payload */
        if (isIncludePayload()) {
            if (request instanceof BufferedRequestWrapper && !isMultipart(request)) {
                BufferedRequestWrapper requestWrapper = (BufferedRequestWrapper) request;
                try {
                    int length = Math.min(requestWrapper.toByteArray().length, getMaxPayloadLength());
                    String charEncoding = requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding() : "UTF-8";
                    String payload = new String(requestWrapper.toByteArray(), 0, length, charEncoding).trim().replace("\n", "");
                    if (!StringUtils.isEmpty(payload)){
                        msg.append(" payload=").append(payload);
                    }

                } catch (UnsupportedEncodingException e) {
                    logger.warn("Failed to parse request payload", e);
                }
            }
        }
        return msg.toString();
    }


    private boolean isMultipart(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }

    //get request headers
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public void setMaxPayloadLength(int payloadLength) {
        this.maxPayloadLength = payloadLength;
    }

    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    public boolean isIncludeQueryString() {
        return includeQueryString;
    }

    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isIncludePayload() {
        return includePayload;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }
}
