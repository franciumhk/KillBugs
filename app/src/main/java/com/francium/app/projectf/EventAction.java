package com.francium.app.projectf;

/**
 * Created by francium on 11/12/2015.
 */
public interface EventAction {
    void run();

    void start();

    void end();

    boolean isRun();

    void setToken(int token);

    int getToken();

    void sendMsg();
}
