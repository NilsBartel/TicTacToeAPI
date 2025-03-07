package logger;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

public class LoggerConfig {


    public static void disableLoggers() {
        ch.qos.logback.classic.Logger dockerLogger1 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.github.dockerjava");
        dockerLogger1.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger dockerLogger2 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.wire");
        dockerLogger2.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger testContainerLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.testcontainers");
        testContainerLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger testContainerRyukLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("tc.testcontainers/ryuk:0.11.0");
        testContainerRyukLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger postgresLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("tc.postgres:16-alpine");
        postgresLogger.setLevel(Level.ERROR);
    }
}
