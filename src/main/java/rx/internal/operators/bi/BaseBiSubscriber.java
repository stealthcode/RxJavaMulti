package rx.internal.operators.bi;

import rx.BiSubscriber;

/**
 * A partial definition of a BiSubscriber that provides isSubscribed checking on all onError,
 * onComplete, and onNext events then delegates to a sub-class
 * {@link BaseBiSubscriber#_onNext(Object, Object) _onNext(T0, T1)}. Exceptions thrown from the
 * _onNext will be captured and passed to the child subscription's onError.
 *
 * @param <R0> child BiSubscriber's first value type
 * @param <R1> child BiSubscriber's second value tye
 * @param <T0> parent BiSubscriber's first value type
 * @param <T1> parent BiSubscriber's second value type
 */
public abstract class BaseBiSubscriber<R0, R1, T0, T1> extends BiSubscriber<T0, T1> {
    private BiSubscriber<? super R0, ? super R1> child;

    public BiSubscriber<? super R0, ? super R1> getChild() {
        return child;
    }

    public void childOnNext(R0 t0, R1 t1) {
        child.onNext(t0, t1);
    }

    public void setChild(BiSubscriber<? super R0, ? super R1> child) {
        this.child = child;
    }

    @Override
    public void onError(Throwable e) {
        if (!isUnsubscribed())
            child.onError(e);
    }

    @Override
    public void onComplete() {
        if (!isUnsubscribed())
            child.onComplete();
    }

    @Override
    public void onNext(T0 t0, T1 t1) {
        try {
            _onNext(t0, t1);
        } catch (Throwable t) {
            unsubscribe();
            child.onError(t);
        }
    }

    protected abstract void _onNext(T0 t0, T1 t1);
}
