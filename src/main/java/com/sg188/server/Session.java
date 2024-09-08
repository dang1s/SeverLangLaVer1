package com.sg188.server;

import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.handler.IMessageHandler;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Reader;
import com.sg188.server.lib.Writer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Session {

    // chat : -59=> loa;
    public Socket socket;
    public Reader reader;
    public Writer writer;
    public boolean isClean;
    public Thread threadSend;
    public Thread threadRecv;
    public ConcurrentLinkedQueue<Message> vecMessage = new ConcurrentLinkedQueue<>();

    public String IPAddress;
    private IMessageHandler controller;
    public User user;
    private Service service;
    public boolean isCreateChar = false;

    /**
     * ******* DB CLICK **********
     */
    public int indexClient;
    private boolean connected;
    private boolean isLoginSuccess;
    private boolean isLogin;
    private boolean isSendArrData;
    public boolean isSetClientType;


    public Session(Socket sc, int id) {
        try {
            this.socket = sc;
            indexClient = id;
            this.reader = new Reader(this.socket);
            this.writer = new Writer(this.socket);
            this.connected = true;
            setHandler(new Controller(this));
            setService(new Service(this));
            threadSend = new Thread(()
                    -> {
                while (this.isConnected() && isSetClientType) {
                    try {
                        Message message = vecMessage.poll();
                        while (message != null) {
                            byte cmd = message.cmd;
                            byte[] data = message.getData();
                            if (cmd == -86) {
                                writer.writeByte(cmd);
                            } else if (cmd == -84 || cmd == 123) {
                                writer.writeByte(cmd);
                                writer.dos.write(data);
                            } else {
                                if (!message.inflate) {
                                    if (data.length <= Short.MAX_VALUE) {
                                        writer.writeByte(cmd);
                                        writer.writeShort(data.length);
                                        for (int i = 0; i < data.length; i++) {

                                            this.writer.writeByte(data[i]);
                                        }
                                    } else {
                                        writer.writeByte(-128);
                                        writer.writeByte(cmd);
                                        writer.writeInt(data.length);
                                        for (int i = 0; i < data.length; i++) {

                                            this.writer.writeByte(data[i]);
                                        }
                                    }
                                } else {
                                    if (cmd == -122) {
                                        //   Log.debug("data trc khi nen " + data.length);
                                    }
                                    data = Utlis.deflateByteArray(data);
                                    if (cmd == -122) {
                                        //   Log.debug("data khi nen " + data.length);
                                    }
                                    if (data.length <= Short.MAX_VALUE) {
                                        writer.writeByte(-80);
                                        writer.writeByte(cmd);
                                        writer.writeShort(data.length);
                                        for (int i = 0; i < data.length; i++) {

                                            this.writer.writeByte(data[i]);
                                        }
                                    } else {
                                        //       Log.debug("ERR LENGTH TO LONG");
                                    }
                                }
                            }
                            writer.dos.flush();
                            message = vecMessage.poll();
                        }
                        Thread.sleep(10);
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        clean();
                        return;
                    }
                }
            });
            threadRecv = new Thread(new MessageCollector());
            isStart = true;
            threadSend.setName("Sender: " + socket.getRemoteSocketAddress());
            threadRecv.setName("reader: " + socket.getRemoteSocketAddress());
            threadRecv.start();
        } catch (Exception ex) {
//            ex.printStackTrace();
            clean();
        }
    }

    public void setHandler(IMessageHandler messageHandler) {
        this.controller = messageHandler;
    }

    public IMessageHandler getMessageHandler() {
        return this.controller;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return this.service;
    }


    public boolean isStart;


    public void sendMessage(Message message) {
        if (isConnected())
            vecMessage.offer(message);
    }

    private boolean CMD_MOVE(byte cmd) {
        return cmd == 123 || cmd == 124 || cmd == 125 || cmd == -82 || cmd == -83 || cmd == -84;
    }


    public boolean isConnected() {
        return !isClean && socket != null && connected && socket.isConnected();
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            Log.error("disconnect err", e);
        }
    }


    public void clean() {
        try {
            if (isClean) {
                return;
            }

            isClean = true;
            isStart = false;
            if (user != null) {
                try {
                    if (user.mChar != null) {
                        user.mChar.flush();
                        user.mChar.clean();
                    }
                    user.cleanUp();
                } catch (Exception e) {
                } finally {
                    ServerManager.removeUser(user);

                }
            }
            vecMessage.clear();
            connected = false;
            isLoginSuccess = false;
            if (socket != null) {
                try {

                    socket.close();
                } catch (Exception ex) {
                }

                socket = null;
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                }

                reader = null;
            }
            if (writer != null) {
                try {

                    writer.close();
                } catch (Exception ex) {
                }

                writer = null;
            }
            controller = null;
            service = null;
            if (threadSend != null) {
                try {
                    threadSend.interrupt();
                } catch (Exception ex) {

                }
                threadSend = null;
            }
            if (threadRecv != null) {
                try {
                    threadRecv.interrupt();
                } catch (Exception ex) {

                }
                threadRecv = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (threadSend != null) {
                try {
                    threadSend.interrupt();
                } catch (Exception ex) {

                }
                threadSend = null;
            }
            if (threadRecv != null) {
                try {
                    threadRecv.interrupt();
                } catch (Exception ex) {

                }
                threadRecv = null;
            }
        }
    }

    public void login(Message msg) {
        try {
            String username = msg.readUTF();
            String password = msg.readUTF();
//            int ver1 = msg.readInt();
//            int ver2 = msg.readInt();
//            msg.readBoolean();
            if (!isSendArrData) {
                isSendArrData = true;
                service.createData();

            }
            if (Main.BaoTri) {
                service.alertMessage("Server đang trong quá trình bảo trì vui lòng đặng nhập sau khi hoàn tất");
                return;
            }
            if (!isConnected() || Main.BaoTri || !isSetClientType) {
                disconnect();
                return;
            }
            if (this.isLoginSuccess) {
                return;
            }
            if (isLogin) {
                return;
            }
            isLogin = true;

            User us = new User(this, username, password);
            us.login();
            if (us.isLoadFinish) {
                this.isLoginSuccess = true;
                ServerManager.addUser(us);
                isLogin = false;
                if(us.mChar!=null){
                    disconnect();
                    return;
                }
                this.user = us;
                Controller controller = (Controller) getMessageHandler();
                controller.setUser(us);
                controller.setService((Service) service);
                service.sendTabSelectChar(us.numberChar, this.user);
            } else {
                this.isLoginSuccess = false;
                isLogin = false;
            }
        } catch (IOException ex) {
//            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
    class MessageCollector implements Runnable {

        @Override
        public void run() {
            try {
                while (connected) {
                    try {
                        byte cmd = reader.readByte();
                        boolean isDeflate;
                        int length;
                        if (CMD_MOVE(cmd)) {
                            boolean when_move = cmd == 123 || cmd == 124 || cmd == 125;
                            boolean xy = cmd == 123 || cmd == -84;
                            boolean send_x = cmd == 125 || cmd == -82;
                            int x = 0;
                            int y = 0;
                            if (xy) {
                                x = reader.readShort();
                                y = reader.readShort();
                                user.mChar.Info.setXY(x, y);
                            } else {
                                if (send_x) {
                                    x = reader.readByte();
                                } else {
                                    y = reader.readByte();
                                }
                                user.mChar.Info.setXY(user.mChar.Info.cx + x, user.mChar.Info.cy + y);
                            }
                            if (x != 0 || y != 0) {
                                user.mChar.zone.updateXYChar(user.mChar, when_move);
                            }
                            continue;
                        } else if (cmd == -128) {
                            cmd = reader.readByte();
                            length = reader.readByte() << 24 & 255 | reader.readByte() << 16 & 255 | reader.readByte() << 8 & 255 | reader.readByte() << 0 & 255;
                            isDeflate = true;
                        } else if (cmd == -80) {
                            cmd = reader.readByte();
                            length = reader.readByte() << 8 & 255 | reader.readByte() << 0 & 255;
                            isDeflate = true;
                        } else {
                            length = reader.readByte() << 8 & 255 | reader.readByte() << 0 & 255;
                            isDeflate = false;
                        }
                        byte[] data = new byte[length];
                        int off = 0;

                        while (length > 0) {
                            int len;
                            if (length - 2048 <= 0) {
                                len = length;
                            } else {
                                len = 2048;
                            }

                            int available = reader.dis.available();
                            if (available == 0) {
                                Utlis.sleep(1L);
                            } else {
                                if (len > available) {
                                    len = available;
                                }

                                reader.read(data, off, len);
                                off += len;
                                length -= len;
                            }
                        }
                        if (isDeflate) {
                            data = Utlis.inflateByteArray(data);
                        }
                        if (controller != null) {
                            controller.readMessage(new Message((byte) cmd, data));
                        }
                    } catch (Exception ex) {
                        clean();
                        return;
                    }
                }
            } catch (Exception ex) {
                clean();
            }
            clean();
        }
    }
}
