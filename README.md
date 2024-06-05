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

The anomaly detection algorithm for streaming data involves using statistical models to continuously monitor and evaluate data points in real-time. Here’s a step-by-step explanation:

1. Moving Average Calculation

    The moving average helps establish the expected range of values in the data stream. It is calculated as follows:

    $` a(t) = \frac{1}{m} \sum\limits_{i=1}^{m-1} y(t-i) `$

    where y(t) represents the value of the data stream at time t, and m is the length of the moving window.

2. Defining the Normal Value Range

    The normal value range [a, b] is defined based on the moving average and standard deviation (σ) to capture typical data variations. For example:

    $`a(t)= `$ moving average at time t

    $`σ(t)= `$ standard deviation over the window

    Normal range $`=[a(t)−kσ(t),a(t)+kσ(t)]`$

    where k is a coefficient (e.g., 2 or 3) determining the range width.

3. Exponential Smoothing Factor (ESF)

    The ESF helps determine if the current data point deviates significantly from the normal range:

    $`0.2 \ ​if \ a≤y(t)≤b `$ 
  
    $` 0.2 ​\ if \ a≤y(t)≤b `$
  
    The ESF takes a value of 0.2 if the data point $`y(t)`$ is within the normal range [a, b], and 0.8 if it is outside this range.

4. Updating the Moving Average and Variance

5. Anomaly Detection and Alarm
