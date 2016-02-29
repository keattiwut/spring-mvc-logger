package kziomek.filter;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Krzysztof Ziomek
 * @since 27/01/2016.
 */
public class MDCFilter extends OncePerRequestFilter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static String X_RID = "x-rid";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {


        String xRequestId = request.getHeader(X_RID);

        if (!StringUtils.isEmpty(xRequestId)) {
            MDC.put(X_RID, xRequestId);
            logger.debug("Header {} value put into MDC", X_RID);
        } else {
            xRequestId = UUID.randomUUID().toString();
            MDC.put(X_RID, xRequestId);
            logger.warn("Header {} is empty. Generated local {} value is {}", X_RID, X_RID, xRequestId );
        }

        filterChain.doFilter(request, response);
        MDC.remove(X_RID);
    }

}
