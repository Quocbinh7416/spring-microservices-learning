package com.example.product.filters;

import com.example.product.dto.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

@Component
@Slf4j
//@Order(1)
public class RequestResponseLogger implements Filter {
    private final ObjectMapper objectMapper;

    public RequestResponseLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MyCustomHttpRequestWrapper requestWrapper = new MyCustomHttpRequestWrapper((HttpServletRequest) servletRequest);
        String requestData = new String(requestWrapper.getByteArray()).replaceAll("\n", " ");
        String uri = requestWrapper.getRequestURI();
        if("/v1/addProduct".equalsIgnoreCase(uri)){
            Product product = objectMapper.readValue(requestData, Product.class);
            product.setCurrency("****");
            requestData = objectMapper.writeValueAsString(product);
        }
        log.info("Request URI: {}", uri);
        log.info("Request Method: {}", requestWrapper.getMethod());
        log.info("Request Body: {}", requestData);

        MyCustomHttpResponseWrapper responseWrapper = new MyCustomHttpResponseWrapper((HttpServletResponse) servletResponse);

        filterChain.doFilter(requestWrapper, responseWrapper);

        String responseResult = responseWrapper.getByteArrayOutputStream().toString();
        if("/v1/addProduct".equalsIgnoreCase(uri)){
            Product product = objectMapper.readValue(responseResult, Product.class);
            product.setCurrency("****");
            responseResult = objectMapper.writeValueAsString(product);
        }
        log.info("Response status - {}", responseWrapper.getStatus());
        log.info("Response body - {}", responseResult);
    }

    private static class MyCustomHttpRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] byteArray;

        public MyCustomHttpRequestWrapper(HttpServletRequest request) {
            super(request);
            try {
                byteArray = IOUtils.toByteArray(request.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Issue while reading request stream");
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            return new MyDelegatingServletInputStream(new ByteArrayInputStream(byteArray));
        }

        public byte[] getByteArray() {
            return byteArray;
        }
    }

    private static class MyCustomHttpResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        private final PrintStream printStream = new PrintStream(byteArrayOutputStream);

        public ByteArrayOutputStream getByteArrayOutputStream() {
            return byteArrayOutputStream;
        }

        public MyCustomHttpResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new MyDelegatingServletOutputStream(new TeeOutputStream(
                    super.getOutputStream(),
                    printStream));
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(new TeeOutputStream(
                    super.getOutputStream(),
                    printStream));
        }
    }
}
