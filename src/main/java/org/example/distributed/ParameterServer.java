package org.example.distributed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.example.distributed.messages.GradientMessage;
import org.example.distributed.messages.ParameterMessage;

/**
 * 简单的参数服务器示例
 */
public class ParameterServer {
    private double k = 0.0;
    private double b = 0.0;
    private double learningRate = 0.01;
    private int port;
    private int numWorkers;
    private volatile boolean running = true;

    // 用于存储从Worker接收到的梯度消息
    private ConcurrentLinkedQueue<GradientMessage> gradientQueue = new ConcurrentLinkedQueue<>();

    public ParameterServer(int port, int numWorkers) {
        this.port = port;
        this.numWorkers = numWorkers;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Parameter Server started on port: " + port);

            // 等待所有Worker连接
            List<ParameterServerHandler> handlers = new ArrayList<>();
            for (int i = 0; i < numWorkers; i++) {
                Socket client = serverSocket.accept();
                ParameterServerHandler handler = new ParameterServerHandler(client, this);
                handlers.add(handler);
                new Thread(handler).start();
                System.out.println("Worker connected: " + client.getRemoteSocketAddress());
            }
            System.out.println("All workers connected.");

            // 简单的训练循环
            int epochs = 1000;
            for (int i = 0; i < epochs; i++) {
                // 请求Worker发送梯度
                broadcastParameters(handlers);

                // 等待所有Worker这一轮的梯度
                List<GradientMessage> gradients = waitGradients(numWorkers);

                // 聚合梯度
                double sumKGrad = 0.0;
                double sumBGrad = 0.0;
                for (GradientMessage g : gradients) {
                    sumKGrad += g.getGradientK();
                    sumBGrad += g.getGradientB();
                }
                double avgKGrad = sumKGrad / numWorkers;
                double avgBGrad = sumBGrad / numWorkers;

                // 更新参数
                k -= learningRate * avgKGrad;
                b -= learningRate * avgBGrad;

                if (i % 100 == 0) {
                    System.out.println("Epoch " + i + ": k = " + k + ", b = " + b);
                }
            }
            System.out.println("Training completed!");

            // 训练结束后，广播最终参数（可选）
            broadcastParameters(handlers);
            running = false;
        }
    }

    private void broadcastParameters(List<ParameterServerHandler> handlers) {
        ParameterMessage paramMsg = new ParameterMessage(k, b);
        for (ParameterServerHandler h : handlers) {
            h.sendParameters(paramMsg);
        }
    }

    // 等待所有Worker的梯度上报
    private List<GradientMessage> waitGradients(int num) {
        List<GradientMessage> list = new ArrayList<>();
        while (list.size() < num) {
            GradientMessage g = gradientQueue.poll();
            if (g != null) {
                list.add(g);
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        }
        return list;
    }

    public void addGradient(GradientMessage g) {
        gradientQueue.add(g);
    }

    public boolean isRunning() {
        return running;
    }
}
