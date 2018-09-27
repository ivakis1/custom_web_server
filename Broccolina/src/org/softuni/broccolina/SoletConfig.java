package org.softuni.broccolina;

import java.util.Map;

public interface SoletConfig {
    Map<String, Object> getAllAttributes();

    Object getAttribute(String name);

    void setAttribute(String name, Object attribute);

    void deleteAttribute(String name);
}
