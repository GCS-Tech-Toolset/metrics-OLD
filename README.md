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

```xml
	<AppMetrics>
		<Enabled>true</Enabled>
		<AutoCreateDb>true</AutoCreateDb>
		<BatchSizing>1000</BatchSizing>
		<ReportingFrequencyInSeconds>5</ReportingFrequencyInSeconds>


		<DB>
			<Name>stoch-osc</Name>
			<UserName>ee6SHf2cfaEgkYpJsQNZHUSPvpJ81ScU</UserName>
			<Pasword>qaBernNglWl3GmR7m7KUz1bQZstYXPpz</Pasword>
			<URI>http://localhost:8086</URI>
		</DB>
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
			<groupId>kagr</groupId>
			<artifactId>metrics</artifactId>
			<version>1.0.0</version>
		</dependency>
```
