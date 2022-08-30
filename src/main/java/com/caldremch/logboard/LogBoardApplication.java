package com.caldremch.logboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
public class LogBoardApplication implements ServletContextInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LogBoardApplication.class, args);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","52428800");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","52428800");
    }
}
