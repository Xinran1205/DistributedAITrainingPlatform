package org.example.distributed;

import java.io.*;
import java.net.Socket;
import org.example.distributed.messages.GradientMessage;
import org.example.distributed.messages.ParameterMessage;

/**
 * 处理单个Worker连接的Handler
 */
public class ParameterServerHandler implements Runnable {
    private Socket socket;
    private ParameterServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ParameterServerHandler(Socket socket, ParameterServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 不断等待Worker发送梯度消息
            while (server.isRunning()) {
                Object obj = in.readObject();
                if (obj instanceof GradientMessage) {
                    server.addGradient((GradientMessage) obj);
                }
            }
        } catch (Exception e) {
            System.err.println("Worker disconnected: " + e.getMessage());
        }
    }

    public void sendParameters(ParameterMessage msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send parameters to worker: " + e.getMessage());
        }
    }
}
