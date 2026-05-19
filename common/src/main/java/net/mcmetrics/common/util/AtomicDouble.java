package net.mcmetrics.common.util;

import java.util.concurrent.atomic.AtomicLong;

// Reference: https://stackoverflow.com/questions/5505460/java-is-there-no-atomicfloat-or-atomicdouble
public class AtomicDouble extends Number {

    private final AtomicLong bits;

    public AtomicDouble() {
        this(0.0);
    }

    public AtomicDouble(final double initialValue) {
        this.bits = new AtomicLong(Double.doubleToLongBits(initialValue));
    }

    @Override
    public int intValue() {
        return (int) get();
    }

    @Override
    public long longValue() {
        return (long) get();
    }

    @Override
    public float floatValue() {
        return (float) get();
    }

    @Override
    public double doubleValue() {
        return get();
    }

    public double get() {
        return Double.longBitsToDouble(bits.get());
    }

    public double getAndSet(final double newValue) {
        return Double.longBitsToDouble(bits.getAndSet(Double.doubleToLongBits(newValue)));
    }

    public final boolean compareAndSet(final double expect, final double update) {
        return bits.compareAndSet(Double.doubleToLongBits(expect), Double.doubleToLongBits(update));
    }

    public final boolean weakCompareAndSet(final double expect, final double update) {
        return bits.weakCompareAndSetPlain(Double.doubleToLongBits(expect), Double.doubleToLongBits(update));
    }
}
