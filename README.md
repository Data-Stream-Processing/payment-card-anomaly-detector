# payment-card-anomaly-detector

## Build & Run locally
Build java code:
```bash
  cd <repository_path>/payment-card-anomaly-detector/
  mvn package
```
Add jar as job to Flink cluster:
```bash
  <flink_path>/bin/flink run <repository_path>/payment-card-anomaly-detector/target/payment-card-anomaly-detector-0.1.jar
```

## Anomaly Detection Algorithm for Streaming Data

This anomaly detection algorithm uses the moving average to determine the expected range of values and continuously updates its parameters with each new data point. By comparing the current data point to this range and using the ESF, the algorithm can effectively detect and signal anomalies in real-time. Proper selection of the normal value range is crucial for balancing sensitivity and stability in anomaly detection. Here’s a step-by-step explanation:

### 1. Moving Average Calculation

  The moving average helps establish the expected range of values in the data stream. It is calculated as follows:

  $` a(t) = \frac{1}{m} \sum\limits_{i=1}^{m-1} y(t-i) `$

  where y(t) represents the value of the data stream at time t, and m is the length of the moving window.

### 2. Defining the Normal Value Range

  The normal value range [a, b] is defined based on the moving average and standard deviation (σ) to capture typical data variations. For example:

  $`a(t)= `$ moving average at time t

  $`σ(t)= `$ standard deviation over the window

  Normal range $`=[a(t)−kσ(t),a(t)+kσ(t)]`$

  where k is a coefficient (e.g., 2 or 3) determining the range width.

### 3. Exponential Smoothing Factor (ESF)

  The ESF helps determine if the current data point deviates significantly from the normal range:

  $`0.2 \ ​if \ a≤y(t)≤b `$ 
  
  $`0.8 ​\ if \ y(t) < a \ or \  y(t) > b​ `$
  
  The ESF takes a value of 0.2 if the data point $`y(t)`$ is within the normal range [a, b], and 0.8 if it is outside this range.

### 4. Updating the Moving Average and Variance

   For real-time processing, the moving average and variance estimators need to be updated with each new data point:

   - Moving Average Update:

     $` y^ˉ​:=yˉ​+ \frac{1}{n}​(y_{n}​−yˉ​) `$

   - Variance Update:

     $` s^{2}:=s^{2}+(y_{n}−yˉ_{n−1})⋅(y_{n}−yˉ_{n}) `$
   

### 5. Anomaly Detection and Alarm

  Based on the ESF, the algorithm decides whether to raise an alarm:

  $` 0 \ ​if \ ESF(t) < 0.5 `$ 
  
  $` 1 ​\ if \ ESF(t) ≥ 0.5  `$

  An alarm is raised (alarm(t) = 1) if ESF(t) is 0.5 or higher, indicating a likely anomaly. Otherwise, no alarm is raised (alarm(t) = 0).
 
