package org.softuni.javache.util;

import org.softuni.javache.WebConstants;
import org.softuni.javache.api.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class RequestHandlerLoadingService {
    private static final String LIB_FOLDER_PATH = WebConstants.SERVER_ROOT_FOLDER_PATH + "lib/";

    private Set<RequestHandler> requestHandlers;

    public Set<RequestHandler> getRequestHandlers() {
        return Collections.unmodifiableSet(this.requestHandlers);
    }

    private String getFileNameWithoutExtension(File file){
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    private boolean isJarFile(File file) {
        return file.isFile() && file.getName().endsWith(".jar");
    }

    private void loadRequestHandlerFromJar(Class<?> requestHandlerClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        RequestHandler requestHandler =
                (RequestHandler) requestHandlerClass.getDeclaredConstructor(String.class)
                        .newInstance(WebConstants.SERVER_ROOT_FOLDER_PATH);

        this.requestHandlers.add(requestHandler);
    }

    private void loadJarFile(JarFile jarFile, String canonicalPath) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Enumeration<JarEntry> jarFileEntries = jarFile.entries();

        while (jarFileEntries.hasMoreElements()) {
            JarEntry currentEntry = jarFileEntries.nextElement();

            if(!currentEntry.isDirectory() && currentEntry.getRealName().endsWith(".class")){

                URL[] urls = new URL[] { new URL("jar:file:" + canonicalPath + "!/")};

                URLClassLoader classLoader = new URLClassLoader(urls);

                String className = currentEntry.getRealName()
                        .replace(".class", "")
                        .replace("/", ".");

                Class currentHandlerClassFile = classLoader.loadClass(className);

                if(RequestHandler.class.isAssignableFrom(currentHandlerClassFile)){
                   this.loadRequestHandlerFromJar(currentHandlerClassFile);
                }
            }

        }
    }

    private void loadLibraryFiles(Set<String> requestHandlerPriority) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        File libraryFolder = new File(LIB_FOLDER_PATH);

        if (!libraryFolder.exists() || !libraryFolder.isDirectory()) {
            throw new IllegalArgumentException("Library folder is not a folder or does not exists.");
        }
        List<File> allJarFiles = Arrays.stream(libraryFolder.listFiles())
                .filter(this::isJarFile)
                .collect(Collectors.toList());

        for (String currentHandlerName : requestHandlerPriority) {
            File jarFile = allJarFiles.stream()
                    .filter(x -> this.getFileNameWithoutExtension(x )
                            .equals(currentHandlerName))
                    .findFirst().orElse(null);

            if (jarFile != null) {
                JarFile fileAsJar = new JarFile(jarFile.getCanonicalFile());
                this.loadJarFile(fileAsJar, jarFile.getCanonicalPath());
            }
        }
    }

    public void loadRequestHandlers(Set<String> requestHandlerPriority) throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        this.requestHandlers = new LinkedHashSet<>();

            this.loadLibraryFiles(requestHandlerPriority);

    }
}
