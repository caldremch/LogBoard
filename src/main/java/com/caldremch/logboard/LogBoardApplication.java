package com.caldremch.logboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class LogBoardApplication implements ServletContextInitializer, ApplicationListener<WebServerInitializedEvent> {
    private static final Logger log = LoggerFactory.getLogger(LogBoardApplication
            .class);

    public static void main(String[] args) {
        SpringApplication.run(LogBoardApplication.class, args);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","52428800");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","52428800");
    }


    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        WebServer server = event.getWebServer();
        WebServerApplicationContext context = event.getApplicationContext();
        Environment env = context.getEnvironment();
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int port = server.getPort();
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null) {
            contextPath = "";
        }
        log.info("\n---------------------------------------------------------\n" +
                "\tApplication is running! Access address:\n" +
                "\tLocal:\t\thttp://localhost:{}" +
                "\n\tExternal:\thttp://{}:{}{}" +
                "\n---------------------------------------------------------\n", port, ip, port, contextPath);

    }


}
