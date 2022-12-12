package com.sap.hcp.cf.logging.servlet.filter;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.request.HttpHeader;
import com.sap.hcp.cf.logging.common.request.HttpHeaders;

/**
 * Extracts the HTTP headers from the request and adds them to the logging
 * context. It defaults to {@link HttpHeaders#propagated()}. Custom headers can
 * be used.
 */
public class AddHttpHeadersToLogContextFilter extends AbstractLoggingFilter {

    private List<HttpHeader> headers;
    private List<String> fields;

    /**
     * The default constructor uses {@link HttpHeaders#propagated()} to define
     * the HTTP headers, that are added to the logging context.
     */
    public AddHttpHeadersToLogContextFilter() {
        this(HttpHeaders.propagated());
    }

    public AddHttpHeadersToLogContextFilter(HttpHeader... headers) {
        this(Collections.emptyList(), headers);
    }

    /**
     * Use this constructor to add your own HTTP headers to the default list.
     * You need to implement {@link HttpHeader}. Note, that
     * {@link HttpHeader#isPropagated} needs to be true. All other headers will
     * be ignored. Usage to add your own header would be:
     * {@code new AddHttpHeadersToLogContextFilter(HttpHeaders.propagated, yourHeader)}
     * 
     * @param list
     *            a list of {@link HttpHeader}, can be default
     *            {@link HttpHeaders#propagated()}
     * @param custom
     *            a single {@link HttpHeader} to add to the list
     */
    public AddHttpHeadersToLogContextFilter(List<? extends HttpHeader> list, HttpHeader... custom) {
        Stream<HttpHeader> allHeaders = Stream.concat(list.stream(), Arrays.stream(custom));
        this.headers = unmodifiableList(allHeaders.filter(HttpHeader::isPropagated).collect(toList()));
        this.fields = unmodifiableList(headers.stream().map(HttpHeader::getField).filter(Objects::nonNull).collect(
                                                                                                                   toList()));
    }

    @Override
    protected void beforeFilter(HttpServletRequest request, HttpServletResponse response) {
        for (HttpHeader header: headers) {
            String headerValue = HttpHeaderUtilities.getHeaderValue(request, header);
            if (header.getField() != null && headerValue != null) {
                LogContext.add(header.getField(), headerValue);
            }
        }
    }

    @Override
    protected void cleanup(HttpServletRequest request, HttpServletResponse response) {
        fields.forEach(LogContext::remove);
    }
}
