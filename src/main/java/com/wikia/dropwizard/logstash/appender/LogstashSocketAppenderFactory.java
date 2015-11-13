package com.wikia.dropwizard.logstash.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogConstants;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.logstash.logback.appender.LogstashSocketAppender;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@JsonTypeName("logstash-socket")
public class LogstashSocketAppenderFactory extends AbstractLogstashAppenderFactory {
  protected int maxMessageSize = 0;
  protected int shortenedLoggerNameLength = -1;
  protected int stacktraceMaxLength = ShortenedThrowableConverter.DEFAULT_MAX_LENGTH;
  protected int stacktraceMaxDepth = ShortenedThrowableConverter.DEFAULT_MAX_DEPTH_PER_THROWABLE;
  protected boolean stacktraceRootCauseFirst = true;
  protected List<String> stacktraceExclude;

  protected boolean useShortenedStacktrace() {
    return stacktraceMaxLength > 0 || stacktraceMaxDepth > 0 || stacktraceRootCauseFirst ||
            !stacktraceExclude.isEmpty();
  }

  @JsonProperty
  public int getMaxMessageSize() {
    return maxMessageSize;
  }

  @JsonProperty
  public void setMaxMessageSize(int maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }

  @JsonProperty
  public int getShortenedLoggerNameLength() {
    return this.shortenedLoggerNameLength;
  }

  @JsonProperty
  public List<String> getStacktraceExclude() {
    return stacktraceExclude;
  }

  @JsonProperty
  public void setStacktraceExclude(List<String> stacktraceExclude) {
    this.stacktraceExclude = stacktraceExclude;
  }

  @JsonProperty
  public void setShortenedLoggerNameLength(int length) {
    this.shortenedLoggerNameLength = length;
  }

  @JsonProperty
  public int getStacktraceMaxLength() {
    return stacktraceMaxLength;
  }

  @JsonProperty
  public void setStacktraceMaxLength(int stacktraceMaxLength) {
    this.stacktraceMaxLength = stacktraceMaxLength;
  }

  @JsonProperty
  public int getStacktraceMaxDepth() {
    return stacktraceMaxDepth;
  }

  @JsonProperty
  public boolean isStacktraceRootCauseFirst() {
    return stacktraceRootCauseFirst;
  }

  @JsonProperty
  public void setStacktraceRootCauseFirst(boolean stacktraceRootCauseFirst) {
    this.stacktraceRootCauseFirst = stacktraceRootCauseFirst;
  }

  @JsonProperty

  public void setStacktraceMaxDepth(int stacktraceMaxDepth) {
    this.stacktraceMaxDepth = stacktraceMaxDepth;
  }

  public LogstashSocketAppenderFactory() {
    port = SyslogConstants.SYSLOG_PORT;
  }

  @Override
  public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
    final LogstashSocketAppender appender = new LogstashSocketAppender();

    appender.setName("logstash-socket-appender");
    appender.setContext(context);
    appender.setHost(host);
    appender.setPort(port);

    appender.setIncludeCallerData(includeCallerInfo);
    appender.setIncludeMdc(includeMdc);
    appender.setIncludeContext(includeContext);
    appender.setMaxMessageSize(maxMessageSize);
    appender.setShortenedLoggerNameLength(shortenedLoggerNameLength);

    if (useShortenedStacktrace()) {
      ShortenedThrowableConverter shortThrowableConverter = new ShortenedThrowableConverter();
      shortThrowableConverter.setMaxDepthPerThrowable(stacktraceMaxDepth);
      shortThrowableConverter.setMaxLength(stacktraceMaxLength);
      shortThrowableConverter.setRootCauseFirst(stacktraceRootCauseFirst);
      shortThrowableConverter.setExcludes(stacktraceExclude);

      appender.setThrowableConverter(shortThrowableConverter);
    }

    if (customFields != null) {
      try {
        String custom = LogstashAppenderFactoryHelper.getCustomFieldsFromHashMap(customFields);
        appender.setCustomFields(custom);
      } catch (IOException e) {
        System.out.println("unable to parse customFields: "+e.getMessage());
      }
    }

    if (fieldNames != null) {
      appender.setFieldNames(LogstashAppenderFactoryHelper.getFieldNamesFromHashMap(fieldNames));
    }

    addThresholdFilter(appender, threshold);
    appender.start();

    return wrapAsync(appender);
  }
}
