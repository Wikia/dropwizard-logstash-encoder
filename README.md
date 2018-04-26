# dropwizard-logback-logstash-encoder

[Dropwizard](http://dropwizard.io/) logging addon for sending logs using the [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder). This is needed because Dropwizard overwrites the default mechanism for loading logback configuration (logback.xml) in favor of its application.yml files.

## Installation
Maven:
```xml
<dependency>
  <groupId>com.wikia</groupId>
  <artifactId>dropwizard-logstash-encoder</artifactId>
  <version>2.0.0</version>
</dependency>
```

Use version:

`1.0.2` for Dropwizard `<1.0`

`2.0.0` for Dropwizard `1.0`

## Usage
You must configure dropwizard to use these appenders in your application.yml file:
```yml
logging:
  appenders:
    - type: logstash-socket # LogstashSocketAppender, for LogstashTcpSocketAppender use logstash-tcp
      ...
```

Additional configuration keys for the appender, see [logstash-logback-encoder#usage](https://github.com/logstash/logstash-logback-encoder/blob/master/README.md#usage) for info. All configs apply to both `logstash-socket` and `logstash-tcp`, unless otherwise noted:
* `host` - string - maps to `syslogHost` when using `logstash-socket`, and `remoteHost` when using `logstash-tcp`
* `port` - int
* `includeCallerInfo` - boolean
* `includeContext` - boolean
* `includeMdc` - boolean
* `customFields` - hashmap - the configuration differs from the original logstash-logback-encoder config in that this is not a raw json string (see example below)
* `fieldNames` - hashmap
* `queueSize` - int - only valid for `logstash-tcp`
* `includeCallerData` - bool - only valid for `logstash-tcp`

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


## Development

### Deploying Artifacts

#### On Workstation

To make the artifacts available on you local workstation, in `~/.m2/`, execute:

```bash
$ mvn install
```


#### Local Artifact Repository

Update your Maven settings (`~/.m2/settings.xml`) so that is contains
`<servers>` and `<profile>`blocks similar to the following.  Replace
`[USERNAME]`, `[PASSWORD]` and `[REPO_URL]` with the appropriate values.

```xml
  <servers>
    <server>
      <id>local-repo</id>
      <username>[USERNAME]</username>
      <password>[PASSWORD]</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>local-repo</id>
      <properties>
        <altDeploymentRepository>local-repo::default::[REPO_URL]</altDeploymentRepository>
      </properties>
    </profile>
  </profiles>
```

The following command will be able to deploy to your Maven artifacts
repository.

```bash
$ mvn3 -P local-repo deploy
```

The [Apache Maven Deploy
Plugin](http://maven.apache.org/plugins/maven-deploy-plugin/) documentation
contains more information configuration.


#### Sonatype OSS Nexus Repository

This is intended for the maintainers of this project.  Use the `ossrh-deploy`
Maven profile to deploy the artifacts to [Sonatype's OSS Nexus
Repository](https://oss.sonatype.org/).

```bash
$ mvn -P ossrh-deploy clean deploy
```

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
