package com.sg188.server.lib;

import com.sg188.server.Main;
import com.sg188.server.Session;

import java.net.Socket;

public class Client {
    public Session session;
    public Socket socket;

    public boolean isClean;

    public int indexClient;
    public boolean isSendArrData;

//    public Client(Socket socket) {
//        this.socket = socket;
//        this.session = new Session(socket);
//
//    }

    public boolean isConnected() {
        return !isClean && this.session.isConnected();
    }

    public void clean() {
        if (isClean) {
            return;
        }
        isClean = true;
        if (session != null) {
            try {
                session.clean();
            } catch (Exception ex) {

            }
            session = null;
        }
        Main.removeClient(this);
    }

    public void create() {
    }

}
