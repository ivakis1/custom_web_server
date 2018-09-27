package org.softuni.broccolina;

public interface HttpSolet {

    void init(SoletConfig soletConfig);

    SoletConfig getSoletConfig();

    void Service(HttpSoletRequest httpSoletRequest, HttpSoletResponse httpSoletResponse);

    void doGet(HttpSoletRequest httpSoletRequest, HttpSoletResponse httpSoletResponse);

    void doPost(HttpSoletRequest httpSoletRequest, HttpSoletResponse httpSoletResponse);

    void doDelete(HttpSoletRequest httpSoletRequest, HttpSoletResponse httpSoletResponse);

    void doPut(HttpSoletRequest httpSoletRequest, HttpSoletResponse httpSoletResponse);
}
