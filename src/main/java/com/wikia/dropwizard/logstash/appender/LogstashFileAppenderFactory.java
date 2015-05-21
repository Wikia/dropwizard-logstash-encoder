package com.wikia.dropwizard.logstash.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.validation.ValidationMethod;
import net.logstash.logback.encoder.LogstashEncoder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.TimeZone;

@JsonTypeName("logstash-file")
public class LogstashFileAppenderFactory extends AbstractLogstashAppenderFactory {
    @NotNull
    private String currentLogFilename;

    private boolean archive = true;

    private String archivedLogFilenamePattern;

    @Min(1)
    private int archivedFileCount = 5;

    @JsonProperty
    public String getCurrentLogFilename() {
        return currentLogFilename;
    }

    @JsonProperty
    public void setCurrentLogFilename(String currentLogFilename) {
        this.currentLogFilename = currentLogFilename;
    }

    @JsonProperty
    public boolean isArchive() {
        return archive;
    }

    @JsonProperty
    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    @JsonProperty
    public String getArchivedLogFilenamePattern() {
        return archivedLogFilenamePattern;
    }

    @JsonProperty
    public void setArchivedLogFilenamePattern(String archivedLogFilenamePattern) {
        this.archivedLogFilenamePattern = archivedLogFilenamePattern;
    }

    @JsonProperty
    public int getArchivedFileCount() {
        return archivedFileCount;
    }

    @JsonProperty
    public void setArchivedFileCount(int archivedFileCount) {
        this.archivedFileCount = archivedFileCount;
    }

    @JsonProperty
    public TimeZone getTimeZone() {
        return timeZone;
    }

    @JsonProperty
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @JsonIgnore
    @ValidationMethod(message = "must have archivedLogFilenamePattern if archive is true")
    public boolean isValidArchiveConfiguration() {
        return !archive || (archivedLogFilenamePattern != null);
    }

    @NotNull
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    @Override public Appender<ILoggingEvent> build(final LoggerContext context, final String applicationName, final Layout<ILoggingEvent> layout) {
        final FileAppender<ILoggingEvent> appender = buildAppender(context);

        LogstashEncoder logstashEncoder = getEncoder();

        logstashEncoder.start();

        appender.setName("logstash-file-appender");

        appender.setAppend(true);

        appender.setEncoder(logstashEncoder);

        appender.setContext(context);

        appender.setFile(getCurrentLogFilename());

        appender.setPrudent(false);

        addThresholdFilter(appender, threshold);

        appender.stop();

        appender.start();

        return wrapAsync(appender);
    }

    private LogstashEncoder getEncoder() {
        LogstashEncoder logstashEncoder = new LogstashEncoder();

        logstashEncoder.setIncludeCallerInfo(includeCallerInfo);
        logstashEncoder.setIncludeMdc(includeMdc);
        logstashEncoder.setIncludeContext(includeContext);

        if (customFields != null) {
            try {
                String custom = LogstashAppenderFactoryHelper.getCustomFieldsFromHashMap(customFields);
                logstashEncoder.setCustomFields(custom);
            }
            catch (IOException e) {
                System.out.println("unable to parse customFields: " + e.getMessage());
            }
        }

        if (fieldNames != null) {
            logstashEncoder.setFieldNames(LogstashAppenderFactoryHelper.getFieldNamesFromHashMap(fieldNames));
        }

        return logstashEncoder;
    }


    protected FileAppender<ILoggingEvent> buildAppender(LoggerContext context) {
        if (isArchive()) {
            final RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();

            final DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy =
                    new DefaultTimeBasedFileNamingAndTriggeringPolicy<>();
            triggeringPolicy.setContext(context);

            final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();

            rollingPolicy.setContext(context);

            rollingPolicy.setFileNamePattern(getArchivedLogFilenamePattern());

            rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);

            triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);

            rollingPolicy.setMaxHistory(getArchivedFileCount());

            appender.setRollingPolicy(rollingPolicy);

            appender.setTriggeringPolicy(triggeringPolicy);

            rollingPolicy.setParent(appender);

            rollingPolicy.start();

            return appender;
        }

        return new FileAppender<>();
    }
}
