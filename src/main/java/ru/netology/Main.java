package ru.netology;

import java.io.IOException;

public class Main {

    final static int COUNT_POOL = 64;
    final static int PORT = 9999;

    public static void main(String[] args) throws IOException {

        final var server = new Server(COUNT_POOL);
        server.listen(PORT);

    }
}


