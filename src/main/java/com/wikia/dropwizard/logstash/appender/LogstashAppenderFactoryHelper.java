package com.wikia.dropwizard.logstash.appender;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.fieldnames.LogstashFieldNames;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class LogstashAppenderFactoryHelper {
    
  private static String getOrDefault(Map<String,String> map, String key, String defaultValue) {
    return map.containsKey(key) ? map.get(key) : defaultValue;   
  }
  
  public static LogstashFieldNames getFieldNamesFromHashMap(HashMap<String, String> map) {
    LogstashFieldNames fieldNames = new LogstashFieldNames();

    fieldNames.setTimestamp(getOrDefault(map, "timestamp", "@timestamp"));
    fieldNames.setVersion(getOrDefault(map, "version", "@version"));
    fieldNames.setMessage(getOrDefault(map, "message", "message"));
    fieldNames.setLogger(getOrDefault(map, "logger", "logger_name"));
    fieldNames.setThread(getOrDefault(map, "thread", "thread_name"));
    fieldNames.setLevel(getOrDefault(map, "level", "level"));
    fieldNames.setLevelValue(getOrDefault(map, "levelValue", "level_value"));
    fieldNames.setCaller(getOrDefault(map, "caller", null));
    fieldNames.setCallerClass(getOrDefault(map, "callerClass", "caller_class_name"));
    fieldNames.setCallerMethod(getOrDefault(map, "callerMethod", "caller_method_name"));
    fieldNames.setCallerFile(getOrDefault(map, "callerFile", "caller_file_name"));
    fieldNames.setCallerLine(getOrDefault(map, "callerLine", "caller_line_number"));
    fieldNames.setStackTrace(getOrDefault(map, "stackTrace", "stack_trace"));
    fieldNames.setTags(getOrDefault(map, "tags", "tags"));
    fieldNames.setMdc(getOrDefault(map, "mdc", null));
    fieldNames.setContext(getOrDefault(map, "context", null));

    return fieldNames;
  }

  public static String getCustomFieldsFromHashMap(HashMap<String, String> map) throws IOException {
    StringWriter writer = new StringWriter();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(writer, map);
    return writer.toString();
  }
}
