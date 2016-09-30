package com.wikia.dropwizard.logstash.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;

import java.net.InetSocketAddress;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@JsonTypeName("logstash-tcp")
public class LogstashTcpAppenderFactory extends AbstractLogstashAppenderFactory {

  @Min(1)
  @Max(65535)
  private int queueSize = LogstashTcpSocketAppender.DEFAULT_QUEUE_SIZE;

  public LogstashTcpAppenderFactory() {
    this.port = LogstashTcpSocketAppender.DEFAULT_PORT;
  }

  @JsonProperty
  public int getQueueSize() {
    return queueSize;
  }

  @JsonProperty
  public void setQueueSize(int queueSize) {
    this.queueSize = queueSize;
  }

  @Override
  public Appender build(LoggerContext context, String s, LayoutFactory layoutFactory,
                        LevelFilterFactory levelFilterFactory,
                        AsyncAppenderFactory asyncAppenderFactory) {
    final LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();
    final LogstashEncoder encoder = new LogstashEncoder();

    appender.setName("logstash-tcp-appender");
    appender.setContext(context);
    appender.addDestinations(new InetSocketAddress(host, port));
    appender.setIncludeCallerData(includeCallerData);
    appender.setQueueSize(queueSize);

    encoder.setIncludeContext(includeContext);
    encoder.setIncludeMdc(includeMdc);
    encoder.setIncludeCallerData(includeCallerData);

    if (customFields != null) {
      LogstashAppenderFactoryHelper
          .getCustomFieldsFromHashMap(customFields)
          .ifPresent(encoder::setCustomFields);
    }

    if (fieldNames != null) {
      encoder.setFieldNames(LogstashAppenderFactoryHelper.getFieldNamesFromHashMap(fieldNames));
    }

    appender.setEncoder(encoder);
    appender.addFilter(levelFilterFactory.build(threshold));
    encoder.start();
    appender.start();

    return wrapAsync(appender, asyncAppenderFactory);
  }
}
