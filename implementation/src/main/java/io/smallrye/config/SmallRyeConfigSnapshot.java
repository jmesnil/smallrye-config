package io.smallrye.config;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.config.ConfigAccessor;
import org.eclipse.microprofile.config.ConfigSnapshot;

public class SmallRyeConfigSnapshot implements ConfigSnapshot {
    private final List<ConfigAccessor<?>> configValues;

    public SmallRyeConfigSnapshot(ConfigAccessor<?>... configValues) {
        this.configValues = Arrays.asList(configValues);
    }
}
