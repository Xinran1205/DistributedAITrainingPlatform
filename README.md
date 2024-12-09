# Distributed Linear Regression Training Platform

- zh_CN [简体中文](/README.zh_CN.md)
## Project Overview

This project is a simple example of a distributed linear regression training platform.  
It supports two modes:
- Serial: Trains on one-dimensional linear regression data in a single-machine environment.
- Distributed: Utilizes a Parameter Server and multiple Worker nodes to train collaboratively. The dataset is divided among multiple Workers to compute gradients in parallel, while the Parameter Server is responsible for aggregating and updating parameters.

The current implementation supports only one-dimensional linear regression (i.e., the model form is `y = kx + b`) using a simple gradient descent algorithm to train parameters `k` and `b`.

## Directory Structure

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

- The `distributed` directory contains core code for the distributed mode:
    - `ParameterServer`: Parameter server that handles parameter updates and communication with Workers.
    - `Worker`: Worker node that computes gradients based on received parameters and local data, then sends them back to the parameter server.
    - `messages`: Defines messages used in distributed communication (parameter messages and gradient messages).
    - `DistributedTrainingDriver`: Entry class for distributed training.
    - `ParameterServerHandler`: Message handler for the parameter server.

- The `serial` directory contains code for running in serial mode, suitable for training on the same dataset on a single machine.

## Environment Dependencies

- JDK 1.8 or higher
- Maven for building the project (`pom.xml` is provided)

## Building and Running

1. **Import the project**  
   Use an IDE (like IntelliJ IDEA) or build the project from the command line using `mvn clean package`.

2. **Data Preparation**  
   Ensure `data.txt` is located in the project root directory, and formatted as single-line data in `x,y` format. For example:

   ```
    1,2
    2,4
    3,6
    4,8
   ```

All data will be used for either serial or distributed training.

3. **Serial Mode Execution**  
   Example of serial training (single machine, no parameter server or Worker required):

```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.serial.Main --dataPath=data.txt
```

4. **Steps to Run in Distributed Mode**

    In distributed mode, start the parameter server first, then launch multiple Workers, each handling a portion of the data:

- Start the parameter server:

```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.distributed.DistributedTrainingDriver --role server --port 9999 --numWorkers 2 --dataPath data.txt
```
This command starts the parameter server on port 9999 and expects 2 Workers to connect.

- Start Workers (assuming 2 Workers):

worker 0：
```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.distributed.DistributedTrainingDriver --role worker --serverHost localhost --serverPort 9999 --workerIndex 0 --numWorkers 5 --dataPath data.txt
```
worker 1：
```bash
java -cp target/DistributedAITrainingPlatform-1.0-SNAPSHOT.jar org.example.distributed.DistributedTrainingDriver --role worker --serverHost localhost --serverPort 9999 --workerIndex 1 --numWorkers 5 --dataPath data.txt
```
When the parameter server detects that all specified Workers are connected, the training loop begins, continuously sending parameters to each Worker. Workers compute gradients based on their local data and send them back to the parameter server for parameter updates.
5. **Viewing Results**  
   After training completes, you can view the trained parameters k and b at the parameter server.

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