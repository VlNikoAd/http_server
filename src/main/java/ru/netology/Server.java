package ru.netology;

import ru.netology.interfaces.Handler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    final ExecutorService threadPool;

    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers =
            new ConcurrentHashMap<>();

    Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.submit(() -> requestProcess(socket));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void requestProcess(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {

            //System.out.println(Thread.currentThread().getName());

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                badRequest(out);
                return;
            }

            final var request = new Request(parts[0], parts[1]);

            if (!handlers.containsKey(request.getMethod())) {
                notFound(out);
                return;
            }

            var methodHandlers = handlers.get(request.getMethod());
            if (!methodHandlers.containsKey(request.getPath())) {
                notFound(out);
                return;
            }

            var handler = methodHandlers.get(request.getPath());
            if (handler == null) {
                notFound(out);
                return;
            }

            handler.handle(request, out);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    public void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(method, new ConcurrentHashMap<>());
        handlers.get(method).put(path, handler);
    }

    private void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}
