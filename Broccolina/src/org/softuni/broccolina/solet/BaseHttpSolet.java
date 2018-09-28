package org.softuni.broccolina.solet;

import org.softuni.javache.http.HttpStatus;
import org.softuni.javache.io.Writer;

import java.io.IOException;

public abstract class BaseHttpSolet implements HttpSolet {
    private SoletConfig soletConfig;
    private boolean isInitialized;

    protected BaseHttpSolet(SoletConfig soletConfig) {
        this.soletConfig = soletConfig;
    }

    @Override
    public void init(SoletConfig soletConfig) {
        this.isInitialized = true;
    }

    @Override
    public boolean isInitialiazed() {
        return this.isInitialized;
    }

    @Override
    public SoletConfig getSoletConfig() {
        return this.soletConfig;
    }

    @Override
    public void service(HttpSoletRequest request, HttpSoletResponse response) throws IOException {
        if ("GET".equals(request.getMethod())) {
            this.doGet(request, response);
        } else if ("POST".equals(request.getMethod())) {
            this.doPost(request, response);
        } else if ("DELETE".equals(request.getMethod())) {
            this.doDelete(request, response);
        } else if ("PUT".equals(request.getMethod())) {
            this.doPut(request, response);
        }

        Writer.writeBytes(response.getBytes(), response.getOutputStream());
    }

    private void configureNotFound(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.addHeader("Content-Type", "text/html");
    }

    protected void doPut(HttpSoletRequest request, HttpSoletResponse response) {
        this.configureNotFound(request, response);
        response.setContent(("<h1>[ERROR] PUT "
                + request.getRequestUrl()
                + "</h1></br>"
                + "<h3>[MESSAGE]Page not found</h3>").getBytes());
    }

    protected void doDelete(HttpSoletRequest request, HttpSoletResponse response) {
        this.configureNotFound(request, response);
        response.setContent(("<h1>[ERROR] DELETE "
                + request.getRequestUrl()
                + "</h1></br>"
                + "<h3>[MESSAGE]Page not found</h3>").getBytes());
    }

    protected void doPost(HttpSoletRequest request, HttpSoletResponse response) {
        this.configureNotFound(request, response);
        response.setContent(("<h1>[ERROR] POST "
                + request.getRequestUrl()
                + "</h1></br>"
                + "<h3>[MESSAGE]Page not found</h3>").getBytes());
        ;
    }

    protected void doGet(HttpSoletRequest request, HttpSoletResponse response) {
        this.configureNotFound(request, response);
        response.setContent(("<h1>[ERROR] GET "
                + request.getRequestUrl()
                + "</h1></br>"
                + "<h3>[MESSAGE]Page not found</h3>").getBytes());
        ;
    }
}
