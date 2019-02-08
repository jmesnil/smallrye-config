package io.smallrye.config;

import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigAccessor;
import org.eclipse.microprofile.config.ConfigSnapshot;
import org.eclipse.microprofile.config.spi.Converter;

public class SmallryeConfigAccessor<T> implements ConfigAccessor<T> {

    private final Config config;
    private final  Class<T> type;
    private final String propertyName;
    private final T defaultValue;
    private boolean evaluateVariables;
    private final Converter<T> converter;
    private long cacheNanos;

    private T cachedValue;
    private long cachedTime = System.nanoTime();

    SmallryeConfigAccessor(Config config, Class<T> type, String propertyName, T defaultValue, boolean evaluateVariables, Converter<T> converter, long cacheNanos) {
        this.config = config;
        this.type = type;
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.evaluateVariables = evaluateVariables;
        this.converter = converter;
        this.cacheNanos = cacheNanos;
    }

    @Override
    public T getValue() {
        if (cacheNanos != -1) {
            if (cachedValue != null && System.nanoTime() < (cachedTime + cacheNanos)) {
                return cachedValue;
            }
        }

        T value = null;
        Optional<T> optionalValue = config.getOptionalValue(propertyName, type);
        if (optionalValue.isPresent()) {
            value = optionalValue.get();
        } else {
            if (defaultValue != null) {
                value = defaultValue;
            }
        }

        if (cacheNanos != -1) {
            cachedTime = System.nanoTime();
            cachedValue = value;
        }
        return value;
    }

    @Override
    public T getValue(ConfigSnapshot configSnapshot) {
        return null;
    }

    @Override
    public Optional<T> getOptionalValue(ConfigSnapshot configSnapshot) {
        return Optional.empty();
    }

    @Override
    public Optional<T> getOptionalValue() {
        return Optional.empty();
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
}
