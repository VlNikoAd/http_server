package ru.netology.interfaces;

import ru.netology.Request;

import java.io.BufferedOutputStream;

public interface Handler {

    void handle(Request request, BufferedOutputStream responseStream);
}
