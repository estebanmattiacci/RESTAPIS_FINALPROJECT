package security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);
    private static final String API_KEY_HEADER_NAME = "API-KEY";
    private static final String REQUIRED_API_KEY = "DISTRIBUTED"; // Replace with your actual API key

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestApiKey = request.getHeader(API_KEY_HEADER_NAME);
        logger.info("Received API Key: {}", requestApiKey);

        if (REQUIRED_API_KEY.equals(requestApiKey)) {
            logger.info("API Key is valid. Proceeding with request.");
            filterChain.doFilter(request, response);
        } else {
            logger.warn("Invalid API Key: {}", requestApiKey);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
        }
    }
}

