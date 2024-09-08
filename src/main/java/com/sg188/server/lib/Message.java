package com.sg188.server.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

public class Message implements Cloneable {

    public byte cmd;
    public Reader reader = null;
    public Writer writer = null;
    public boolean inflate;

    public static Message a(byte var0) throws java.io.IOException {
        Message var1;
        (var1 = new Message((byte) -125)).writeByte(var0);
        return var1;
    }

    public static Message b(byte var0) throws java.io.IOException {
        Message var1;
        (var1 = new Message((byte) -124)).writeByte(-128);
        return var1;
    }

    public static Message c(byte var0) throws java.io.IOException {
        Message var1;
        (var1 = new Message((byte) -123)).writeByte(var0);
        return var1;
    }

    public static Message d(byte var0) throws java.io.IOException {
        Message var1;
        (var1 = new Message((byte) -122)).writeByte(var0);
        return var1;
    }

    public static Message e(byte var0) throws java.io.IOException {
        Message var1;
        (var1 = new Message((byte) -112)).writeByte(var0);
        return var1;
    }

    public static Message f(byte var0) throws java.io.IOException {
        Message var1;
        (var1 = new Message((byte) -111)).writeByte(var0);
        return var1;
    }

    public Message(byte var1) {
        this.cmd = var1;
        this.writer = new Writer();
    }

    public Message(byte var1, Writer writer) {
        this.cmd = var1;
        this.writer = writer;
    }

    public Message(byte var1, byte[] var2) {
        this.cmd = var1;
        this.reader = new Reader(var2);
    }

    public byte[] getData() {
        if (this.writer == null) {
            return null;
        }
        try {
            this.writer.dos.flush();
            return this.writer.baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void close() {
        if (this.reader != null) {
            this.reader.close();
        }

        if (this.writer != null) {
            this.writer.close();
        }
    }

    public boolean readBoolean() throws java.io.IOException {
        return this.reader.readBoolean();
    }

    public byte readByte() throws java.io.IOException {
        return this.reader.readByte();
    }
    
    public int Avali() throws IOException
    {
        return this.reader.Avali();
    }

    public byte[] read() throws java.io.IOException {
        return this.reader.read();
    }

    public short readShort() throws java.io.IOException {
        return this.reader.readShort();
    }

    public int readUnsignedShort() throws java.io.IOException {
        return this.reader.readUnsignedShort();
    }

    public long readLong() throws java.io.IOException {
        return this.reader.readLong();
    }

    public int readInt() throws java.io.IOException {
        return this.reader.readInt();
    }

    public String readUTF() throws java.io.IOException {
        return this.reader.readUTF();
    }
    public String readUTF2() throws IOException
    {
        return this.reader.readUTF2();
    }

    public void writeBoolean(boolean var1) throws java.io.IOException {
        this.writer.writeBoolean(var1);
    }

    public void writeByte(int var1) throws java.io.IOException {
        this.writer.writeByte(var1);
    }

    public void write(byte[] var1) throws java.io.IOException {
        this.writer.write(var1);
    }

    public void writeShort(int var1) throws java.io.IOException {
        this.writer.writeShort(var1);
    }

    public void writeInt(int var1) throws java.io.IOException {
        this.writer.writeInt(var1);
    }

    public void writeLong(long var1) throws java.io.IOException {
        this.writer.writeLong(var1);
    }

    public void writeUTF(String var1) throws java.io.IOException {
        this.writer.writeUTF(var1);
    }

    // public void createSession() {
//      while(Session.gI().isConnected()) {
//         Session.gI().close();
//         Utlis.sleep(100L);
//      }
//
//      Session.gI().vMessage.clear();
//      this.newSession();
//   }
//
//   public void send() {
//      if (Session.gI().isConnected()) {
//         Session.gI().sendMessage(this);
//         this.close();
//      }
//   }
//
//   private void newSession() {
//      try {
//         if (!Session.gI().isConnecting) {
//            DataCenter.gI().createSession();
//         }
//
//         int var1 = 20;
//
//         while(var1 > 0 && !Session.gI().isConnected()) {
//            --var1;
//            Utlis.sleep(500L);
//         }
//      } catch (Exception var2) {
//         Utlis.println(var2);
//      }
//
//      Session.gI().sendMessage(this);
//      this.close();
//   }
}
