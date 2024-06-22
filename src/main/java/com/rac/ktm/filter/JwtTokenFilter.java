package com.rac.ktm.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JwtTokenFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Cookie[] cookies = httpServletRequest.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("Found JWT token in cookie: {}", token);
                    break;
                }
            }
        }

        if (token != null) {
            final String finalToken = token; // Ensure the token is final

            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpServletRequest) {
                @Override
                public String getHeader(String name) {
                    if ("Authorization".equalsIgnoreCase(name)) {
                        return "Bearer " + finalToken;
                    }
                    return super.getHeader(name);
                }
            };
            chain.doFilter(requestWrapper, httpServletResponse);
        } else {
            chain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
