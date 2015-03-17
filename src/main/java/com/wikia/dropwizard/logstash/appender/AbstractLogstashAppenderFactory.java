package com.wikia.dropwizard.logstash.appender;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import io.dropwizard.logging.AbstractAppenderFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;

abstract class AbstractLogstashAppenderFactory extends AbstractAppenderFactory {
  @NotNull
  protected String host;

  @Min(1)
  @Max(65535)
  protected int port;

  protected boolean includeCallerInfo = false;

  protected boolean includeContext = true;

  protected boolean includeMdc = true;

  protected HashMap<String, String> customFields = Maps.newHashMap();

  protected HashMap<String, String> fieldNames;

  @JsonProperty
  public void setHost(String host) {
    this.host = host;
  }

  @JsonProperty
  public String getHost() {
    return host;
  }

  @JsonProperty
  public void setPort(int port) {
    this.port = port;
  }

  @JsonProperty
  public int getPort() {
    return port;
  }

  @JsonProperty
  public boolean getIncludeCallerInfo() {
    return includeCallerInfo;
  }

  @JsonProperty
  public void setIncludeCallerInfo(boolean includeCallerInfo) {
    this.includeCallerInfo = includeCallerInfo;
  }

  @JsonProperty
  public boolean getIncludeContext() {
    return includeContext;
  }

  @JsonProperty
  public void setIncludeContext(boolean includeContext) {
    this.includeContext = includeContext;
  }

  @JsonProperty
  public boolean getIncludeMdc() {
    return includeMdc;
  }

  @JsonProperty
  public void setIncludeMdc(boolean includeMdc) {
    this.includeMdc = includeMdc;
  }

  @JsonProperty
  public HashMap<String, String> getCustomFields() {
    return customFields;
  }

  @JsonProperty
  public void setCustomFields(HashMap<String, String> customFields) {
    this.customFields = customFields;
  }

  @JsonProperty
  public HashMap<String, String> getFieldNames() {
    return fieldNames;
  }

  @JsonProperty
  public void setFieldNames(HashMap<String, String> fieldNames) {
    this.fieldNames = fieldNames;
  }

  protected String renderCustomFields(String applicationName) {
      // always set the application name except when overridden
      customFields.putIfAbsent("applicationName", applicationName);

      try {
          return LogstashAppenderFactoryHelper.getCustomFieldsFromHashMap(customFields);
      } catch (IOException e) {
          System.out.println("unable to parse customFields: " + e.getMessage());
          return "{}";
      }
    }
}
