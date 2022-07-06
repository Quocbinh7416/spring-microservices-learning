package com.example.product.config;

import com.example.product.filters.RequestResponseLogger;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    FilterRegistrationBean<RequestResponseLogger> createLoggers(RequestResponseLogger requestResponseLogger) {
        FilterRegistrationBean<RequestResponseLogger> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(requestResponseLogger);
        registrationBean.addUrlPatterns("/v1/addProduct", "/v1/product/*");

        return registrationBean;
    }
}
