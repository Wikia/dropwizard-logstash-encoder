package com.wikia.dropwizard.logstash.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.net.SyslogConstants;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.appender.LogstashSocketAppender;

@JsonTypeName("logstash-socket")
public class LogstashSocketAppenderFactory extends AbstractLogstashAppenderFactory {
  public LogstashSocketAppenderFactory() {
    port = SyslogConstants.SYSLOG_PORT;
  }

  @Override
  public Appender build(LoggerContext context, String s, LayoutFactory layoutFactory,
                        LevelFilterFactory levelFilterFactory,
                        AsyncAppenderFactory asyncAppenderFactory) {
    final LogstashSocketAppender appender = new LogstashSocketAppender();

    appender.setName("logstash-socket-appender");
    appender.setContext(context);
    appender.setHost(host);
    appender.setPort(port);

    appender.setIncludeCallerData(includeCallerData);
    appender.setIncludeMdc(includeMdc);
    appender.setIncludeContext(includeContext);

    if (customFields != null) {
      LogstashAppenderFactoryHelper
          .getCustomFieldsFromHashMap(customFields)
          .ifPresent(appender::setCustomFields);
    }

    if (fieldNames != null) {
      appender.setFieldNames(LogstashAppenderFactoryHelper.getFieldNamesFromHashMap(fieldNames));
    }

    appender.addFilter(levelFilterFactory.build(threshold));
    appender.start();

    return wrapAsync(appender, asyncAppenderFactory);
  }
}
