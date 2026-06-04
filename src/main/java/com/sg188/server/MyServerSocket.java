package com.sg188.server;

import com.sg188.lib.Log;
import com.sg188.server.handler.ServerSocketHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerSocket {

    public int PORT = -1;
    public ServerSocketHandler handler;
    public ServerSocket server;
    public boolean RUN = false;
    public Thread thread;

    public MyServerSocket(int PORT, ServerSocketHandler handler) {
        this.PORT = PORT;
        this.handler = handler;
    }

    public void open() {
        if (RUN) {
            return;
        }
        RUN = true;
        thread = new Thread(()
                -> {
            try {
                if (PORT == -1) {
                    Log.info("CHUA NHAP PORT");
                    return;
                }
                server = new ServerSocket(PORT);
                Log.info("MO SERVER: " + PORT);
                while (server != null && !server.isClosed() && RUN) {
                    try {
                        Socket socket = server.accept();
                        optimizeSocket(socket);
                        if (handler != null) {
                            handler.socketConnet(socket);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        close();
                        return;
                    }
                }
            } catch (IOException ex) {
                Log.info("KHONG THE MO PORT: " + PORT);
                close();
                return;
            }
        });
        thread.setName("Server Socket");
        thread.start();
    }

    private void optimizeSocket(Socket socket) throws IOException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(30000);
        socket.setSendBufferSize(65536);
        socket.setReceiveBufferSize(65536);
        socket.setKeepAlive(true);
        socket.setPerformancePreferences(0, 1, 2);
    }

    private void close() {
        if (!RUN) {
            return;
        }
        RUN = false;
        if (server != null) {
            try {
                server.close();
            } catch (Exception ex) {

            }
            server = null;
        }
        if (thread != null) {
            try {
                thread.interrupt();
            } catch (Exception ex) {

            }
            thread = null;
        }
        Log.info("CLOSE SERVER: " + PORT);
        if (handler != null) {
            handler.serverClose();
        }
    }
}
