package org.softuni.broccolina.util;

import org.softuni.broccolina.solet.HttpSolet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationLoadingService {
    private JarUnzipService jarUnzipService;
    private Map<String, HttpSolet> loadedApplications;

    //TODO: LOAD CLASSES
    //TODO: LOAD LIBRARIES


    public ApplicationLoadingService() {
        this.jarUnzipService = new JarUnzipService();
        this.loadedApplications = new HashMap<>();
    }

    private boolean isJarFile(File file) {
        return file.isFile() && file.getName().endsWith(".jar");
    }

    private void loadApplicationFromFolder(String applicationRootFolderPath){
        String classesRootFolderPath = applicationRootFolderPath + "classes" + File.separator;
        System.out.println(classesRootFolderPath);
        String librariesRootFolderPath = applicationRootFolderPath + "lib" + File.separator;

        //TODO
    }

    public Map<String, HttpSolet> loadApplications(String applicationsFolderPath) throws IOException {
        File applicationsFolder = new File(applicationsFolderPath);

        if(applicationsFolder.exists() && applicationsFolder.isDirectory()){
            List<File> allJarFiles = Arrays.stream(applicationsFolder.listFiles())
                    .filter(this::isJarFile)
                    .collect(Collectors.toList());

            for (File applicationJarFile : allJarFiles) {
                this.jarUnzipService.unzipJar(applicationJarFile);

                this.loadApplicationFromFolder(applicationJarFile.getCanonicalPath().replace(".jar", File.separator));
            }
        }


        return this.loadedApplications;
    }
}
