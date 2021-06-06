package com.github.fanzezhen.common.gateway.core.tracing;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.LogData;
import io.jaegertracing.spi.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public class ConsoleReporter implements Reporter {

    private static Logger logger = LoggerFactory.getLogger(ConsoleReporter.class);

    @Override
    public void report(JaegerSpan span) {
	    if (logger.isDebugEnabled()) {
		    String operationName = span.getOperationName();
		    long duration = span.getDuration();
		    List<LogData> logs = span.getLogs();
		    Map<String, Object> tags = span.getTags();
		    String context = span.context().toString();
		    Dto dto = new Dto(context, operationName, duration, tags, logs);
		    logger.info("span {}", dto);
	    }
    }

    @Override
    public void close() {

    }

    public static class Dto {
        String context;
        String operationName;
        long duration;
        Map<String, Object> tags;
        List<LogData> logs;

        public Dto(String context, String operationName, long duration, Map<String, Object> tags, List<LogData> logs) {
            this.context = context;
            this.operationName = operationName;
            this.duration = duration;
            this.tags = tags;
            this.logs = logs;
        }

        @Override
        public String toString() {
            return "Dto{" +
                    "context='" + context + '\'' +
                    ", operationName='" + operationName + '\'' +
                    ", duration=" + (duration/1000) + "ms" +
                    ", tags=" + tags() +
                    ", logs=" + logs() +
                    '}';
        }

        private String tags() {
            if (tags == null) {
                return "{}";
            }
            String ret = "{";
            for (Map.Entry<String, Object> entry : tags.entrySet()) {
                ret += entry.getKey();
                ret += ":";
                ret += entry.getValue();
                ret += ",";
            }
            ret += "}";
            return ret;
        }

        private String logs() {
            if (logs == null) {
                return "{}";
            }
            String ret = "{";
            for (LogData log : logs) {
                Map<String, ?> fields = log.getFields();
                String message = log.getMessage();
                ret += message;
                if(fields != null) {
                    for (Map.Entry<String, ?> entry : fields.entrySet()) {
                        ret += entry.getKey();
                        ret += entry.getValue();
                        ret += ",";
                    }
                }
                ret += ",";
            }
            ret += "}";
            return ret;
        }
    }
}
