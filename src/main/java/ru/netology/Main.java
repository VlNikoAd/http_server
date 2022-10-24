package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    final static int COUNT_POOL = 64;
    final static int PORT = 9999;
    final static String GET = "GET";
    final static String POST = "POST";

    public static void main(String[] args) {
        final var server = new Server(COUNT_POOL);

        server.addHandler(GET, "/app.js", Main::processFile);
        server.addHandler(GET, "/events.html", Main::processFile);
        server.addHandler(GET, "/events.js", Main::processFile);
        server.addHandler(GET, "/forms.html", Main::processFile);
        server.addHandler(GET, "/index.html", Main::processFile);
        server.addHandler(GET, "/links.html", Main::processFile);
        server.addHandler(GET, "/resources.html", Main::processFile);
        server.addHandler(GET, "/spring.png", Main::processFile);
        server.addHandler(GET, "/spring.svg", Main::processFile);
        server.addHandler(GET, "/links.html", Main::processFile);
        server.addHandler(GET, "/styles.css", Main::processFile);

        server.addHandler("GET", "/messages", (request, out) ->
                processMsg(request, out, GET));

        server.addHandler("POST", "/messages", (request, out) ->
                processMsg(request, out, POST));

        server.listen(PORT);

    }

    private static void processFile(Request request, BufferedOutputStream out) {
        try {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mineType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mineType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void processMsg(Request request, BufferedOutputStream out, String msg) {
        final var message = "Hello from " + msg + " messages";
        try {
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + "text/plain" + "\r\n" +
                            "Content-Length: " + message.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" + message
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


