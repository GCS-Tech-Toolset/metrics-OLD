# connecting to influx:
docker exec -it influxdb influx

# usefull commands
```bash
`show databases` shows databases
`use XXX` connect to that database
`show MEASUREMENTS` shows measurements
`delete FROM "xxx"` deletes the whole table of measurements
```

# Config sections

## Influx properties
```xml
  <AppMetrics>
    <Enabled>true</Enabled>
    <Registry>DataDog</Registry>
    <ReportingFrequencyInSeconds>5</ReportingFrequencyInSeconds>
    
    <Datadog>
        <HostIP>${env:DATADOG_HOST_IP}</HostIP>
        <Port>8125</Port>
    </Datadog>
  </AppMetrics>
```
## Datadog properties
```xml
  <AppMetrics>
    <Enabled>true</Enabled>
    <Registry>Influx</Registry>
    <ReportingFrequencyInSeconds>5</ReportingFrequencyInSeconds>

    <Influx>
        <Name>SomeName_${env:ENVIRONMENT}</Name>
        <UserName>${env:INFLUX_USERNAME}</UserName>
        <Pasword>${env:INFLUX_PASSWORD}</Pasword>
        <URI>${env:INFLUX_HOST_AND_PORT}</URI>
        <AutoCreateDb>true</AutoCreateDb>
        <BatchSizing>1000</BatchSizing>
    </Influx>
  </AppMetrics>
```


add the following to `AppProps.java`:

```java
@Getter @Setter private AppMetrics _metrics
...
...
_metrics = AppMetrics.initFromCong(configFile);
```

# Maven

```xml
    <dependency>
        <groupId>gcs.toolset</groupId>
        <artifactId>metrics</artifactId>
        <version>1.0.3</version>
    </dependency>
```
