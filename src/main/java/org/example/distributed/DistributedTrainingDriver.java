package org.example.distributed;

/**
 * 分布式训练的主入口，可以通过命令行参数决定角色(PS/Worker)，传入端口、主机和数据路径等参数。
 */
public class DistributedTrainingDriver {
    public static void main(String[] args) throws Exception {
        String role = null;
        String serverHost = "localhost";
        int serverPort = 9999;
        int numWorkers = 1;
        String dataPath = null;
        int workerIndex = -1; // 默认为无效值
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        // 简单的参数解析（实际中应使用更健壮的方式，例如 Apache Commons CLI）
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--role")) {
                role = args[++i];
            } else if (args[i].equals("--port")) {
                serverPort = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--numWorkers")) {
                numWorkers = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--serverHost")) {
                serverHost = args[++i];
            } else if (args[i].equals("--serverPort")) {
                serverPort = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--dataPath")) {
                dataPath = args[++i];
            } else if (args[i].equals("--workerIndex")) {
                workerIndex = Integer.parseInt(args[++i]);
            }
        }

        if ("server".equals(role)) {
            ParameterServer ps = new ParameterServer(serverPort, numWorkers);
            ps.start();
        } else if ("worker".equals(role)) {
            if (dataPath == null) {
                System.err.println("Worker 必须指定 dataPath");
                return;
            }
            if (workerIndex < 0 || workerIndex >= numWorkers) {
                System.err.println("Worker 必须指定一个有效的 workerIndex，范围为 0 到 numWorkers-1");
                return;
            }
            Worker w = new Worker(serverHost, serverPort, dataPath, workerIndex, numWorkers);
            w.start();
        } else {
            System.err.println(role);
            System.err.println("未知角色。请使用 --role=server 或 --role=worker.");
        }
    }
}
