package org.softuni.broccolina;

import org.softuni.broccolina.solet.*;
import org.softuni.broccolina.util.ApplicationLoadingService;
import org.softuni.javache.api.RequestHandler;
import org.softuni.javache.io.Reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private void initializeSoletMap() {
        //TODO: Redeploy all all application on every 10 seconds without affecting the currently deployed apps
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
        try {
            HttpSoletRequest request = new HttpSoletRequestImpl(Reader.readAllLines(inputStream), inputStream);
            HttpSoletResponse response = new HttpSoletResponseImpl(outputStream);

            HttpSolet soletObject = findSoledCandidate(request, response);

            if (request.isResource() || soletObject == null) {
                hasIntercepted = false;
                return;
            }

            Class[] soletServiceMethodParameters = Arrays.stream(soletObject.getClass()
                    .getMethods())
                    .filter(x -> x.getName().equals("service"))
                    .findFirst()
                    .orElse(null)
                    .getParameterTypes();

            soletObject.getClass()
                    .getDeclaredMethod("service", soletServiceMethodParameters[0], soletServiceMethodParameters[1])
                    .invoke(soletObject, request, response);

            this.hasIntercepted = true;

        } catch (IOException e) {
            e.printStackTrace();
            this.hasIntercepted = false;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private HttpSolet findSoledCandidate(HttpSoletRequest request, HttpSoletResponse response) {
        Matcher applicationRouteMatcher = Pattern.compile("/[a-zA-Z0-9]+/").matcher(request.getRequestUrl());

        String requestUrl = request.getRequestUrl();

        if (this.soletMap.containsKey(requestUrl)) {
            return this.soletMap.get(requestUrl);
        }
        if (applicationRouteMatcher.find()) {
            String applicationRoute = applicationRouteMatcher.group(0) + "*";
            if (this.soletMap.containsKey(applicationRoute)) {
                return this.soletMap.get(applicationRoute);
            }
        }
        if (this.soletMap.containsKey("/*")) {
            return this.soletMap.get("/*");
        }

        return null;
    }

    @Override
    public boolean hasIntercepted() {
        return this.hasIntercepted;
    }
}
