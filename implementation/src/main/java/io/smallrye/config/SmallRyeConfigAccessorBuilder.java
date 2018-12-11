package io.smallrye.config;

import java.time.Duration;

import org.eclipse.microprofile.config.ConfigAccessor;
import org.eclipse.microprofile.config.ConfigAccessorBuilder;
import org.eclipse.microprofile.config.spi.Converter;

public class SmallRyeConfigAccessorBuilder<T> implements ConfigAccessorBuilder<T> {
    private final String propertyName;
    private final Class<T> type;
    private final SmallRyeConfig config;

    private Converter<T> converter;
    private T defaultValue;
    private String defaultStringValue;
    private boolean evaluateVariables;
    private Duration cacheDuration = null;

    public SmallRyeConfigAccessorBuilder(String propertyName, Class<T> type, SmallRyeConfig config) {
        this.propertyName = propertyName;
        this.type = type;
        this.config = config;
   }

    @Override
    public ConfigAccessorBuilder<T> useConverter(Converter<T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> withDefault(T value) {
        this.defaultValue = value;
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> withStringDefault(String value) {
        this.defaultStringValue = value;
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> cacheFor(Duration duration) {
        this.cacheDuration = duration;
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> evaluateVariables(boolean evaluateVariables) {
        this.evaluateVariables = evaluateVariables;
        return this;
    }

    @Override
    public ConfigAccessor<T> build() {
        T resolvedDefaultValue = resolvedDefaultValue();

        long cacheNanos = -1;
        if (cacheDuration != null) {
            cacheNanos = cacheDuration.toNanos();
        }
        return new SmallryeConfigAccessor(config, type, propertyName, resolvedDefaultValue, evaluateVariables, converter, cacheNanos);
    }

    private T resolvedDefaultValue() {
        if (defaultValue != null) {
            return defaultValue;
        } else if (defaultStringValue != null) {
            if (converter != null) {
                return converter.convert(defaultStringValue);
            } else {
                return config.convert(defaultStringValue, type);
            }

        }
        return null;
    }
}
