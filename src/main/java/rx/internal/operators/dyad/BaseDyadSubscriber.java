package rx.internal.operators.dyad;

import rx.DyadSubscriber;

/**
 * A partial definition of a DyadSubscriber that provides isSubscribed checking on all onError,
 * onComplete, and onNext events then delegates to a sub-class
 * {@link BaseDyadSubscriber#_onNext(Object, Object) _onNext(T0, T1)}. Exceptions thrown from the
 * _onNext will be captured and passed to the child subscription's onError.
 *
 * @param <R0>
 * @param <R1>
 * @param <T0>
 * @param <T1>
 */
public abstract class BaseDyadSubscriber<R0, R1, T0, T1> extends DyadSubscriber<T0, T1> {
    private DyadSubscriber<? super R0, ? super R1> child;

    public DyadSubscriber<? super R0, ? super R1> getChild() {
        return child;
    }

    public void childOnNext(R0 t0, R1 t1) {
        child.onNext(t0, t1);
    }

    public void setChild(DyadSubscriber<? super R0, ? super R1> child) {
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
            getChild().onError(t);
        }
    }

    protected abstract void _onNext(T0 t0, T1 t1);
}
