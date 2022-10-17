package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    final static int COUNT_POOL = 64;
    final static int PORT = 9999;
    final static String getMsg = "GET";
    final static String postMsg = "POST";

    public static void main(String[] args) {
        final var server = new Server(COUNT_POOL);

        server.addHandler(getMsg, "/app.js", Main::processFile);
        server.addHandler(getMsg, "/events.html", Main::processFile);
        server.addHandler(getMsg, "/events.js", Main::processFile);
        server.addHandler(getMsg, "/forms.html", Main::processFile);
        server.addHandler(getMsg, "/index.html", Main::processFile);
        server.addHandler(getMsg, "/links.html", Main::processFile);
        server.addHandler(getMsg, "/resources.html", Main::processFile);
        server.addHandler(getMsg, "/spring.png", Main::processFile);
        server.addHandler(getMsg, "/spring.svg", Main::processFile);
        server.addHandler(getMsg, "/links.html", Main::processFile);
        server.addHandler(getMsg, "/styles.css", Main::processFile);

        server.addHandler("GET", "/messages", (request, out) ->
                processMsg(request, out, getMsg));

        server.addHandler("POST", "/messages", (request, out) ->
                processMsg(request, out, postMsg));

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


