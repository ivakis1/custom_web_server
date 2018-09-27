package org.softuni.javache.util;

public class LoggingService {

    public void info(String content){
        System.out.println("\u001B[44m" + "[INFO] " + content);
    }

    public void warning(String content){
        System.out.println("\u001B[45m" + "[WARN] " + content);
    }

    public void error(String content){
        System.out.println("\u001B[41m" + "[ERROR] " + content);
    }
}
