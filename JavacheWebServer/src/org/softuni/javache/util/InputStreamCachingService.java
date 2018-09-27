package org.softuni.javache.util;

import org.softuni.javache.io.Reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamCachingService {
    private String content;

    public InputStreamCachingService() {
    }

    public InputStream getOrCacheInputStream(InputStream inputStream) throws IOException {
        if(this.content == null){
            content = Reader.readAllLines(inputStream);
        }

        return new ByteArrayInputStream(content.getBytes());
    }

    public void evictCache(){
        this.content = null;
    }
}
