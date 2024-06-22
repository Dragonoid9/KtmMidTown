package com.rac.ktm.config;

import com.rac.ktm.filter.JwtTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public JwtTokenFilter jwtTokenFilterBean() {
        return new JwtTokenFilter();
    }

    @Bean
    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilterRegistrationBean(JwtTokenFilter jwtTokenFilterBean) {
        FilterRegistrationBean<JwtTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtTokenFilterBean);
        registrationBean.addUrlPatterns("/rac/admin", "/rac/profile", "/rac/updateProfile"); // Adjust URL patterns as needed
        return registrationBean;
    }
}
