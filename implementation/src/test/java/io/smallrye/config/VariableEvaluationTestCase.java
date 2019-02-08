package io.smallrye.config;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.eclipse.microprofile.config.Config;
import org.junit.Test;

public class VariableEvaluationTestCase {

    @Test
    public void testVariableEvaluationEnabled() {
        Config config = buildConfig(
                "server.url", "http://${server.host}:${server.port}",
                "server.host", "example.org",
                "server.port", "8080");
        String serverUrl = config.getValue("server.url", String.class);
        assertEquals("http://example.org:8080", serverUrl);
    }

    @Test(expected = NoSuchElementException.class)
    public void testVariableEvaluationEnabled_2() {
        Config config = buildConfig(
                "server.url", "http://${server.host}:${server.port}",
                "server.host", "example.org");
        // server.port is not configured, getValue must throw a NoSuchElementException
        String serverUrl = config.getValue("server.url", String.class);
        assertEquals("http://example.org:${server.port}", serverUrl);
    }

    @Test
    public void testVariableEvaluationEnabled_3() {
        Config config = buildConfig(
                "server.url", "http://${server.host:example.org}:${server.port:8080}");
        String serverUrl = config.getValue("server.url", String.class);
        assertEquals("http://example.org:8080", serverUrl);
    }

    private static Config buildConfig(String... keyValues) {
        return SmallRyeConfigProviderResolver.INSTANCE.getBuilder()
                .addDefaultSources()
                .withSources(KeyValuesConfigSource.config(keyValues))
                .build();
    }

}
