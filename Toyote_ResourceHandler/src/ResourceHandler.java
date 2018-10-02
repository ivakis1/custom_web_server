import org.softuni.javache.api.RequestHandler;
import org.softuni.javache.http.*;
import org.softuni.javache.io.Reader;
import org.softuni.javache.io.Writer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceHandler implements RequestHandler {
    private static final String APPLICATION_RESOURCES_FOLDER_NAME = "resources";

    private final String serverRootFolderPath;

    private boolean hasIntercepted;

    public ResourceHandler(String serverRootFolderPath) {
        this.serverRootFolderPath = serverRootFolderPath;
    }

    private String getApplicationName(String requestUrl){
        requestUrl = requestUrl.substring(1);

        if(requestUrl.contains("/")){
            return requestUrl.substring(0, requestUrl.indexOf("/"));
        }
        return "ROOT";
    }

    private String getResourceName(String requestUrl){
        return requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
    }

    private void handleResourceRequest(String resourcesFolder, String resourceName, HttpResponse response) {
        try{
            Path resourcePath = Paths.get(new URL("file:/"
                    + new File(resourcesFolder + File.separator + resourceName)
                    .getCanonicalPath())
                    .toURI());

            byte[] resourceContent = Files.readAllBytes(resourcePath);

            response.setStatusCode(HttpStatus.OK);
            response.addHeader("Content-Type", Files.probeContentType(resourcePath));
            response.addHeader("Content-Length", resourceContent.length + "");
            response.addHeader("Content-Disposition", "inline");

            response.setContent(resourceContent);


        }catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        try{
            HttpRequest request = new HttpRequestImpl(Reader.readAllLines(inputStream));
            HttpResponse response = new HttpResponseImpl();

            String resourcesFolder = this.serverRootFolderPath
                    + "webapps"
                    + File.separator
                    + this.getApplicationName(request.getRequestUrl())
                    + File.separator
                    + APPLICATION_RESOURCES_FOLDER_NAME;

            String resourceName = this.getResourceName(request.getRequestUrl());

            this.handleResourceRequest(resourcesFolder, resourceName, response);

            Writer.writeBytes(response.getBytes(), outputStream);

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