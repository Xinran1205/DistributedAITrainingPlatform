package org.example.distributed;

import java.io.*;
import java.net.Socket;
import org.example.serial.data.DataSet;
import org.example.distributed.messages.GradientMessage;
import org.example.distributed.messages.ParameterMessage;
import org.example.serial.utils.DataParser;
/**
 * 简单的Worker示例
 */
public class Worker {
    private String serverHost;
    private int serverPort;
    private String dataPath;
    private int workerIndex;
    private int numWorkers;

    private double k = 0.0;
    private double b = 0.0;
    private DataSet dataSet;

    public Worker(String serverHost, int serverPort, String dataPath, int workerIndex, int numWorkers) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.dataPath = dataPath;
        this.workerIndex = workerIndex;
        this.numWorkers = numWorkers;
        this.dataSet = loadDataSet();
    }

    private DataSet loadDataSet() {
        // 首先获取data.txt的总行数
        int totalLines = DataParser.countLines(dataPath);
        int linesPerWorker = totalLines / numWorkers;
        int startLine = workerIndex * linesPerWorker;
        int endLine = startLine + linesPerWorker - 1;

        return DataParser.parseFromFile(dataPath, startLine, endLine);
    }


    public void start() {
        try (Socket socket = new Socket(serverHost, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to Parameter Server.");

            while (true) {
                // 等待参数服务器发送新的参数
                Object obj = in.readObject();
                if (obj instanceof ParameterMessage) {
                    ParameterMessage pm = (ParameterMessage) obj;
                    k = pm.getK();
                    b = pm.getB();

                    // 计算梯度并发送回给PS
                    GradientMessage g = computeGradient();
                    out.writeObject(g);
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("Worker error: " + e.getMessage());
        }
    }

    private GradientMessage computeGradient() {
        double[] x = dataSet.getX();
        double[] y = dataSet.getY();
        int m = x.length;
        double cost_k = 0.0;
        double cost_b = 0.0;
        for (int j = 0; j < m; j++) {
            double y_pred = k * x[j] + b;
            double error = y_pred - y[j];
            cost_k += error * x[j];
            cost_b += error;
        }
        // 返回梯度，不在这里更新参数
        return new GradientMessage(cost_k / m, cost_b / m);
    }
}
