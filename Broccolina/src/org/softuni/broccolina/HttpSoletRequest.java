package org.softuni.broccolina;

import org.softuni.javache.http.HttpRequest;

import java.io.InputStream;

public interface HttpSoletRequest extends HttpRequest {

    InputStream getInputStream();
}
