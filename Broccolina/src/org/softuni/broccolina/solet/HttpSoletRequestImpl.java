package org.softuni.broccolina.solet;

import org.softuni.javache.http.HttpRequestImpl;

import java.io.InputStream;

public class HttpSoletRequestImpl extends HttpRequestImpl implements HttpSoletRequest {
    private InputStream inputStream;

    public HttpSoletRequestImpl(String requestContent, InputStream inputStream) {
        super(requestContent);
        this.inputStream = inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return null;
    }
}
