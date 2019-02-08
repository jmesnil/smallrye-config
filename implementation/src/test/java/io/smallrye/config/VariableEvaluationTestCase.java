package io.smallrye.config;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.eclipse.microprofile.config.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VariableEvaluationTestCase {

    @Before
    public void setUp() {
        System.setProperty("server.url", "http://${server.host}:${server.port}");
    }

    @After
    public void tearDown() {
        System.clearProperty("server.url");
    }

    @Test
    public void testDefaultVariableEvaluation() {
        Config config = SmallRyeConfigProviderResolver.INSTANCE.getConfig();
        String serverUrl = config.getValue("server.url", String.class);
        assertEquals("http://${server.host}:${server.port}", serverUrl);
    }

    @Test
    public void testVariableEvaluationEnabled() {
        try {
            System.setProperty("smallrye.config.evaluate-variables", "true");

            Config config = SmallRyeConfigProviderResolver.INSTANCE.getBuilder()
                    .addDefaultSources()
                    .withSources(new PropertiesConfigSource(new Properties() {{
                        put("server.host", "example.org");
                        put("server.port", "8080");
                    }}, "props"))
                    .build();
            String serverUrl = config.getValue("server.url", String.class);
            assertEquals("http://example.org:8080", serverUrl);
        } finally {
            System.clearProperty("smallrye.config.evaluate-variables");
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testVariableEvaluationEnabled_2() {
        try {
            System.setProperty("smallrye.config.evaluate-variables", "true");

            Config config = SmallRyeConfigProviderResolver.INSTANCE.getBuilder()
                    .addDefaultSources()
                    .withSources(new PropertiesConfigSource(new Properties() {{
                        put("server.host", "example.org");
                    }}, "props"))
                    .build();
            // server.port is not configured, getValue must throw a NoSuchElementException
            String serverUrl = config.getValue("server.url", String.class);
            assertEquals("http://example.org:${server.port}", serverUrl);
        } finally {
            System.clearProperty("smallrye.config.evaluate-variables");
        }
    }

    @Test
    public void testVariableEvaluationEnabled_3() {
        try {
            System.setProperty("smallrye.config.evaluate-variables", "true");

            Config config = SmallRyeConfigProviderResolver.INSTANCE.getBuilder()
                    .addDefaultSources()
                    .withSources(new PropertiesConfigSource(new HashMap<String, String>() {{
                        put("server.url", "http://${server.host:example.org}:${server.port:8080}");
                    }}, "props", 500))
                    .build();
            // server.port is not configured, getValue must throw a NoSuchElementException
            String serverUrl = config.getValue("server.url", String.class);
            assertEquals("http://example.org:8080", serverUrl);
        } finally {
            System.clearProperty("smallrye.config.evaluate-variables");
        }
    }

}
