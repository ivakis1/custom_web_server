package org.softuni.broccolina.util;

import org.softuni.broccolina.solet.BaseHttpSolet;
import org.softuni.broccolina.solet.HttpSolet;
import org.softuni.broccolina.solet.WebSolet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ApplicationLoadingService {
    private JarUnzipService jarUnzipService;
    private Map<String, HttpSolet> loadedApplications;
    private List<URL> libraryClassUrls;

    public ApplicationLoadingService() {
        this.jarUnzipService = new JarUnzipService();
        this.loadedApplications = new HashMap<>();
    }

    public Map<String, HttpSolet> loadApplications(String applicationsFolderPath) throws IOException {
        try {
            File applicationsFolder = new File(applicationsFolderPath);

            if (applicationsFolder.exists() && applicationsFolder.isDirectory()) {
                List<File> allJarFiles = Arrays.stream(applicationsFolder.listFiles())
                        .filter(this::isJarFile)
                        .collect(Collectors.toList());

                for (File applicationJarFile : allJarFiles) {
                    this.jarUnzipService.unzipJar(applicationJarFile);

                    this.loadApplicationFromFolder(applicationJarFile.getCanonicalPath().replace(".jar", File.separator),
                            applicationJarFile.getName().replace(".jar", ""));
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException | InstantiationException
                | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return this.loadedApplications;
    }

    private boolean isJarFile(File file) {
        return file.isFile() && file.getName().endsWith(".jar");
    }

    private void loadApplicationFromFolder(String applicationRootFolderPath, String applicationName) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String classesRootFolderPath = applicationRootFolderPath + "classes" + File.separator;
        String librariesRootFolderPath = applicationRootFolderPath + "lib" + File.separator;

        this.loadApplicationLibraries(librariesRootFolderPath);

        this.loadApplicationClasses(classesRootFolderPath, applicationName);
    }

    private void loadApplicationLibraries(String librariesRootFolderPath)
            throws IOException {
        this.libraryClassUrls = new ArrayList<>();

        File libraryFolder = new File(librariesRootFolderPath);

        if (!libraryFolder.exists() || !libraryFolder.isDirectory()) {
            throw new IllegalArgumentException("Library folder is not a folder or does not exists.");
        }
        List<File> allJarFiles = Arrays.stream(libraryFolder.listFiles())
                .filter(this::isJarFile)
                .collect(Collectors.toList());

        for (File jarFile : allJarFiles) {
            if (jarFile != null) {
                JarFile fileAsJar = new JarFile(jarFile.getCanonicalFile());
                this.loadLibraryFile(fileAsJar, jarFile.getCanonicalPath());
            }
        }
    }

    private void loadLibraryFile(JarFile library, String canonicalPath)
            throws MalformedURLException {
        Enumeration<JarEntry> jarFileEntries = library.entries();

        while (jarFileEntries.hasMoreElements()) {
            JarEntry currentEntry = jarFileEntries.nextElement();

            if (!currentEntry.isDirectory() && currentEntry.getRealName().endsWith(".class")) {

                this.libraryClassUrls.add(new URL("jar:file:" + canonicalPath + "!/"));
            }
        }
    }

    private void loadApplicationClasses(String classesRootFolderPath, String applicationName) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        File classesRootDirectory = new File(classesRootFolderPath);

        if (!classesRootDirectory.exists() || !classesRootDirectory.isDirectory()) {
            return;
        }

        this.libraryClassUrls.add(new URL("file:/" + classesRootDirectory.getCanonicalPath()
                + File.separator));

        URL[] urls = this.libraryClassUrls.toArray(new URL[libraryClassUrls.size()]);
        URLClassLoader ucl = new URLClassLoader(urls);

        this.loadClass(classesRootDirectory, classesRootDirectory.getCanonicalPath(), ucl, applicationName);
    }

    private void loadClass(File currentFile, String canonicalPath, URLClassLoader ucl, String applicationName) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (currentFile.isDirectory()) {
            for (File childFile : currentFile.listFiles()) {
                this.loadClass(childFile, childFile.getCanonicalPath(), ucl, applicationName);
            }
        } else {
            if (!currentFile.getName().endsWith("class")) return;

            String className = currentFile.getName()
                    .replace(".class", "")
                    .replace("/", ".");

            Class currentHandlerClassFile = ucl.loadClass(className);
            this.loadSolet(currentHandlerClassFile, applicationName);
        }
    }

    private void loadSolet(Class soletClass, String applicationName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!BaseHttpSolet.class.getName().equals(soletClass.getSuperclass().getName())) {
            return;
        }

        Object soletObject = soletClass.getDeclaredConstructor().newInstance();

        Object soletAnnotation = Arrays.stream(soletClass.getAnnotations())
                .filter(x -> x.annotationType()
                        .getSimpleName()
                        .equals(WebSolet.class.getSimpleName()))
                .findFirst().orElse(null);

        String route = soletAnnotation.getClass().getMethod("route").invoke(soletAnnotation).toString();

        if(!applicationName.equals("ROOT")){
            route = "/" + applicationName + "/" + route;
        }

        HttpSolet httpSoletProxy = (HttpSolet) Proxy.newProxyInstance(
                HttpSolet.class.getClassLoader(),
                new Class[] {HttpSolet.class},
                (proxy, method, args) -> {
                    Method extractedMethod = Arrays.stream(soletClass.getMethods())
                            .filter(x -> x.getName().equals(method.getName())).findFirst().orElse(null);

                    Class<?>[] requestedParameters = extractedMethod.getParameterTypes();

                    Object proxyRequest = Proxy.newProxyInstance(
                            requestedParameters[0].getClassLoader(),
                            new Class[]{requestedParameters[0]},
                            (requestProxy, requestMethod, requestArgs) -> {
                                Method extractedRequestMethod = Arrays.stream(args[0].getClass().getMethods())
                                        .filter((x -> x.getName().equals(requestMethod.getName())))
                                        .findFirst()
                                        .orElse(null);

                                return extractedRequestMethod.invoke(args[0], requestArgs);
                            }
                    );

                    Object proxyResponse = Proxy.newProxyInstance(
                            requestedParameters[1].getClassLoader(),
                            new Class[]{requestedParameters[1]},
                            (responseProxy, responseMethod, responseArgs) -> {
                                Method extractedResponseMethod = Arrays.stream(args[1].getClass().getMethods())
                                        .filter((x -> x.getName().equals(responseMethod.getName())))
                                        .findFirst()
                                        .orElse(null);

                                return extractedResponseMethod.invoke(args[1], responseArgs);
                            }
                    );

                    return extractedMethod.invoke(soletObject, proxyRequest, proxyResponse);
                }
        );

        this.loadedApplications.put(route, httpSoletProxy);
    }
}
