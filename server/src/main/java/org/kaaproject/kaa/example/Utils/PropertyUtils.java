package org.kaaproject.kaa.example.Utils;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by pyshankov on 24.02.16.
 */
public class PropertyUtils {

    @Value("${kaa.host}")
    private static String kaaHost;

    @Value("${kaa.port}")
    private static int kaaPort;

    @Value("${kaa.notification.schema.id}")
    private static String schemaId;

    @Value("${kaa.notification.topic.id}")
    private static String topicId;

    @Value("${kaa.app.id}")
    private static String appId;

    @Value("${kaa.appender.user}")
    private static String user;

    @Value("${kaa.appender.password}")
    private static String password;

    public static String getKaaHost() {
        return kaaHost;
    }

    public static int getKaaPort() {
        return kaaPort;
    }

    public static String getSchemaId() {
        return schemaId;
    }

    public static String getTopicId() {
        return topicId;
    }

    public static String getAppId() {
        return appId;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }
}
