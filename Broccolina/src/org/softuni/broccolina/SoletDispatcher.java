package org.softuni.broccolina;

import org.softuni.broccolina.solet.*;
import org.softuni.broccolina.util.ApplicationLoadingService;
import org.softuni.javache.api.RequestHandler;
import org.softuni.javache.io.Reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class SoletDispatcher implements RequestHandler {
    private final String serverRootFolderPath;

    private ApplicationLoadingService applicationLoadingService;

    private boolean hasIntercepted;

    private Map<String, HttpSolet> soletMap;

    public SoletDispatcher(String serverRootFolderPath) {
        this.serverRootFolderPath = serverRootFolderPath;
        applicationLoadingService = new ApplicationLoadingService();
        this.initializeSoletMap();
    }

    private void initializeSoletMap(){
        try {
            this.soletMap = this.applicationLoadingService
                    .loadApplications(serverRootFolderPath
                            + "webapps"
                            + File.separator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        try{
            HttpSoletRequest request = new HttpSoletRequestImpl(Reader.readAllLines(inputStream),
                    inputStream);
            HttpSoletResponse response = new HttpSoletResponseImpl(outputStream);

            String requestUrl = request.getRequestUrl();

            HttpSolet soletObject = null;

            if(this.soletMap.containsKey(requestUrl)){
                soletObject = this.soletMap.get(requestUrl);
            }

            if(soletObject == null){
                hasIntercepted = false;
                return;
            }

            soletObject.service(request, response);

            this.hasIntercepted = true;
        }catch (IOException e){
            e.printStackTrace();
            this.hasIntercepted = false;
        }
    }

    @Override
    public boolean hasIntercepted() {
        return this.hasIntercepted;
    }
}
