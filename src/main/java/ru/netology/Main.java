package ru.netology;

public class Main {

    final static int COUNT_POOL = 64;
    final static int PORT = 9999;

    public static void main(String[] args) {

        Server server = new Server(COUNT_POOL);
        server.startServer(PORT);

    }
}


