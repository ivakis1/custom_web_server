package org.softuni;

import java.io.IOException;
import org.softuni.javache.*;

public class StartUp {
    public static void main(String[] args) {
        start(args);
    }

    private static void start(String[] args) {
        int port = WebConstants.DEFAULT_SERVER_PORT;

        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        Server server = new Server(port);

        System.out.println(WebConstants.SERVER_ROOT_FOLDER_PATH);
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

