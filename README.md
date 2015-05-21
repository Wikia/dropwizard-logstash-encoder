# dropwizard-logback-logstash-encoder

[Dropwizard](http://dropwizard.io/) logging addon for sending logs using the [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder). This is needed because Dropwizard overwrites the default mechanism for loading logback configuration (logback.xml) in favor of its application.yml files.

## Installation
Maven:
```xml
<dependency>
  <groupId>com.wikia</groupId>
  <artifactId>dropwizard-logstash-encoder</artifactId>
  <version>1.0.2</version>
</dependency>
```

## Usage
You must configure dropwizard to use these appenders in your application.yml file. There are 3 different 
appenders available: socket, tcp, and file. To configure each set the proper appender type:

## Logstash socket
```yml
logging:
  appenders:
    - type: logstash-socket
      ...
```

## Logstash tcp
```yml
logging:
  appenders:
    - type: logstash-tcp
      ...
```

## Logstash file 
```yml
logging:
  appenders:
    - type: logstash-file
      ...
```

# Common fields across all appenders

* `includeCallerInfo` - boolean
* `includeContext` - boolean
* `includeMdc` - boolean
* `customFields` - hashmap - the configuration differs from the original logstash-logback-encoder config in that this is not a raw json string (see example below)
* `fieldNames` - hashmap

# Settings available to logstash-tcp and logstash-socket

* `host` - string - maps to `syslogHost` when using `logstash-socket`, and `remoteHost` when using `logstash-tcp`
* `port` - int
* `queueSize` - int - only valid for `logstash-tcp`
* `includeCallerData` - bool - only valid for `logstash-tcp`

# Settings available to logstash-file

These settings are the same as the dropwizard [file appender](http://www.dropwizard.io/manual/configuration.html#file) settings except
without the log pattern. Defaults in parenthesis.

* `currentLogFilename` - (REQUIRED)	The filename where current events are logged.
* `threshold` -	(ALL) The lowest level of events to write to the file.
* `archive`	- (true) Whether or not to archive old events in separate files.
* `archivedLogFilenamePattern` - (none)	Required if archive is true. The filename pattern for archived files. %d is replaced with the date in yyyy-MM-dd form, and the fact that it ends with .gz indicates the file will be gzipped as it’s archived. Likewise, filename patterns which end in .zip will be filled as they are archived.
* `archivedFileCount` -	(5) The number of archived files to keep. Must be between 1 and 50.
* `timeZone` -	(UTC) The time zone to which event timestamps will be converted.

# Examples

Example config:
```yaml
logging:
  appenders:
    - type: logstash-socket
      host: 127.0.0.1
      fieldNames:
        message: msg
        timestamp: log_date
      customFields:
        myTestField1: myTestVal
        myTestField2: 2
```

Then, loggers can be used the same way as if they were configured using logback.xml for logstash-logback-encoder, example (using Guava):
```java
LoggerFactory.getLogger("myTestLogger").warn(
	appendEntries(
		new ImmutableMap.Builder<String, Object>()
			.put("some_key", 1)
			.put("some_other_key", "two")
			.build()
	),
	"warning! warning!");
```
## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
