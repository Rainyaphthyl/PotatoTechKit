package io.github.rainyaphthyl.potteckit.config.option;

import fi.dy.masa.malilib.config.option.IntegerConfig;

/**
 * Displays FPS(TPS) and also stores NSPF(NSPT)
 */
public class InvIntegerConfig extends IntegerConfig {
    protected double product;
    protected double inverseValue;

    @SuppressWarnings("unused")
    public InvIntegerConfig(String name, int defaultValue) {
        this(name, defaultValue, 1.0);
    }

    public InvIntegerConfig(String name, int defaultValue, double product) {
        this(name, product, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, name);
    }

    @SuppressWarnings("unused")
    public InvIntegerConfig(String name, int defaultValue, double product, String commentTranslationKey) {
        this(name, product, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, commentTranslationKey);
    }

    @SuppressWarnings("unused")
    public InvIntegerConfig(String name, double product, int defaultValue, int minValue, int maxValue) {
        this(name, product, defaultValue, minValue, maxValue, name);
    }

    public InvIntegerConfig(String name, double product, int defaultValue, int minValue, int maxValue, String commentTranslationKey, Object... commentArgs) {
        this(name, product, defaultValue, minValue, maxValue, false, commentTranslationKey, commentArgs);
    }

    public InvIntegerConfig(String name, double product, int defaultValue, int minValue, int maxValue, boolean sliderActive, String commentTranslationKey, Object... commentArgs) {
        super(name, defaultValue, minValue, maxValue, sliderActive, commentTranslationKey, commentArgs);
        this.product = product;
        updateInverseValue();
    }

    public double getProduct() {
        return product;
    }

    private void updateInverseValue() {
        inverseValue = product / effectiveIntegerValue;
    }

    public double getInverseValue() {
        return inverseValue;
    }

    @Override
    public boolean setValue(Integer newValue) {
        boolean flag = super.setValue(newValue);
        if (flag) {
            updateInverseValue();
        }
        return flag;
    }

    @Override
    public boolean setIntegerValue(int newValue) {
        boolean flag = super.setIntegerValue(newValue);
        if (flag) {
            updateInverseValue();
        }
        return flag;
    }

    @Override
    protected void updateEffectiveValue() {
        super.updateEffectiveValue();
        updateInverseValue();
    }

    @Override
    public void setValueFromString(String value) {
        super.setValueFromString(value);
        updateInverseValue();
    }
}
