package org.softuni.javache.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface RequestHandler {
    void handleRequest(InputStream requestStream, OutputStream outputStream);

    boolean hasIntercepted();
}