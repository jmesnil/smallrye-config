package io.smallrye.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
    private long cacheTime = -1;
    private ChronoUnit cacheUnit;
    private boolean evaluateVariables;

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
    public ConfigAccessorBuilder<T> cacheFor(long value, ChronoUnit timeUnit) {
        this.cacheTime = value;
        this.cacheUnit = timeUnit;
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> evaluateVariables(boolean evaluateVariables) {
        this.evaluateVariables = evaluateVariables;
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> addLookupSuffix(String suffixValue) {
        return this;
    }

    @Override
    public ConfigAccessorBuilder<T> addLookupSuffix(ConfigAccessor<String> suffixAccessor) {
        return this;
    }

    @Override
    public ConfigAccessor<T> build() {
        String resolvedPropertyName = resolvePropertyName();
        T resolvedDefaultValue = resolvedDefaultValue();

        long cacheNanos = -1;
        if (cacheTime != -1) {
            cacheNanos = Duration.of(cacheTime, cacheUnit).toNanos();
        }
        return new SmallryeConfigAccessor(config, type, propertyName, resolvedPropertyName, resolvedDefaultValue, converter, cacheNanos);
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

    private String resolvePropertyName() {
        return propertyName;
    }
}
