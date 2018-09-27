package org.softuni.javache;

import org.softuni.javache.api.RequestHandler;
import org.softuni.javache.util.InputStreamCachingService;
import org.softuni.javache.util.LoggingService;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ConnectionHandler extends Thread {
    private static final int CONNECTION_KILL_LIMIT = 5000;

    private static final String REQUEST_CONTENT_LOADING_FAILURE_EXCEPTION_MESSAGE = "Failed loading request content.";

    private Socket clientSocket;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    private InputStreamCachingService inputStreamCachingService;

    private LoggingService loggingService;

    private Set<RequestHandler> requestHandlers;

    public ConnectionHandler(Socket clientSocket, InputStreamCachingService inputStreamCachingService, LoggingService loggingService, Set<RequestHandler> requestHandlers) {
        this.inputStreamCachingService = inputStreamCachingService;
        this.loggingService = loggingService;
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
    }

    private void initializeConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.clientSocketInputStream = this.clientSocket.getInputStream();
            this.clientSocketOutputStream = this.clientSocket.getOutputStream();
        } catch (IOException e) {
            loggingService.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        try {
            processClientConnection();
            this.clientSocketInputStream.close();
            this.clientSocketOutputStream.close();
            this.inputStreamCachingService.evictCache();
            this.clientSocket.close();
        } catch (IOException e) {
            loggingService.error(e.getLocalizedMessage());
        }
    }

    private void processClientConnection() throws IOException {
        for (RequestHandler requestHandler : this.requestHandlers) {
            requestHandler.handleRequest(this.inputStreamCachingService.
                    getOrCacheInputStream(this.clientSocketInputStream), this.clientSocketOutputStream);

            if(requestHandler.hasIntercepted()) break;
        }
    }
}






