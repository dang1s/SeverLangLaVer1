package com.sg188.server.handler;

import java.net.Socket;

public interface ServerSocketHandler {

    void socketConnet(Socket socket);

    void serverClose();
   
}
