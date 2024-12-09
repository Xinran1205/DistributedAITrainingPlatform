# 分布式线性回归训练平台

## 项目简介

本项目是一个简单的分布式线性回归训练平台示例。  
它支持两种模式：
- 串行（Serial）：在单机环境下直接对一维线性回归数据进行训练。
- 分布式（Distributed）：采用参数服务器（Parameter Server）与多个工作节点（Worker）协同训练的方式，将数据集划分给多个Worker并行计算梯度，参数服务器负责参数聚合与更新。

当前实现仅支持一维线性回归（即模型形式为 `y = kx + b`），通过简单的梯度下降算法来训练参数 `k` 与 `b`。

## 目录结构

- README.md
- data.txt
- pom.xml
- src
    - main
        - java
            - org
                - example
                    - distributed
                        - DistributedTrainingDriver.java
                        - ParameterServer.java
                        - ParameterServerHandler.java
                        - Worker.java
                        - messages
                            - GradientMessage.java
                            - ParameterMessage.java
                    - serial
                        - Main.java
                        - data
                            - DataSet.java
                        - model
                            - LinearRegression.java
                        - utils
                            - DataParser.java
                            - InputHandler.java


- `distributed` 目录下是分布式模式的核心代码：
    - `ParameterServer`：参数服务器，实现参数更新和与Worker的通信。
    - `Worker`：工作节点，根据接收到的参数及本地数据计算梯度，返回给参数服务器。
    - `messages`：定义分布式通信中使用的消息（参数消息和梯度消息）。
    - `DistributedTrainingDriver`：分布式训练的入口类。
    - `ParameterServerHandler`：参数服务器的消息处理器。

- `serial` 目录下是串行运行的相关代码，用于对同样的数据集单机训练。

## 环境依赖

- JDK 1.8及以上
- Maven 用于构建项目（`pom.xml` 已提供）

## 构建与运行

1. **导入项目**  
   使用IDE（如IntelliJ IDEA）或在命令行下通过 `mvn clean package` 构建项目。

2. **数据准备**  
   请确保 `data.txt` 存在于项目根目录下，并采用 `x,y` 格式的单行数据。例如：
   ```
    1,2
    2,4
    3,6
    4,8
   ```
所有数据将用于串行或分布式训练。

3. **串行模式运行**  
   串行训练（仅单机，不需参数服务器和Worker）示例：
```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.serial.Main --dataPath=data.txt
```

4. **分布式模式运行步骤**

   分布式模式下需要先启动参数服务器，然后再启动多个 Worker，每个 Worker 处理部分数据：

- 启动参数服务器：

```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.distributed.DistributedTrainingDriver --role server --port 9999 --numWorkers 2 --dataPath data.txt
```
上述命令在 9999 端口启动参数服务器，并期望有 2 个 Worker 连接。

- 启动 Worker：
  假设有 2 个 Worker

worker 0：
```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.distributed.DistributedTrainingDriver --role worker --serverHost localhost --serverPort 9999 --workerIndex 0 --numWorkers 5 --dataPath data.txt
```
worker 1：
```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.distributed.DistributedTrainingDriver --role worker --serverHost localhost --serverPort 9999 --workerIndex 1 --numWorkers 5 --dataPath data.txt
```
当参数服务器检测到全部指定数目的 Worker 已连接后，会开始训练循环，不断向各 Worker 发送参数，Worker 根据本地数据计算梯度发送回参数服务器，并由参数服务器更新参数。

5. **结果查看**  
   训练结束后，可以在参数服务器查看训练得到的参数 `k` 和 `b`。

```bash
Parameter Server started on port: 9999
Worker connected: /127.0.0.1:52884
Worker connected: /127.0.0.1:52889
All workers connected.
Epoch 0: k = 3.85, b = 0.55
Epoch 100: k = 2.036978495605845, b = 0.10424747636887824
Epoch 200: k = 1.996524830774895, b = 0.033934560969086494
Epoch 300: k = 1.9983502831760938, b = 0.01174738534003543
Epoch 400: k = 1.9994139748667776, b = 0.004086868592305961
Epoch 500: k = 1.9997957199213585, b = 0.0014223519175859053
Epoch 600: k = 1.9999288935597064, b = 4.950355317646297E-4
Epoch 700: k = 1.9999752518126335, b = 1.7229262243010393E-4
Epoch 800: k = 1.9999913866101733, b = 5.9964893684481483E-5
Epoch 900: k = 1.999997002187125, b = 2.0870240834789877E-5
Training completed!
Final parameters: k = 1.999998945567916, b = 7.340768864807991E-6
```