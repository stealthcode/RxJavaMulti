package rx.internal.operators;

import rx.DualSubscriber;
import rx.BiObservable.DualOperator;

public class OperatorFlip<T0, T1> implements DualOperator<T1, T0, T0, T1> {
    @Override
    public DualSubscriber<T0, T1> wrapDual(final DualSubscriber<? super T1, ? super T0> child) {
        return new DualSubscriber<T0, T1>(child) {
            @Override
            public void onNext(T0 t0, T1 t1) {
                child.onNext(t1, t0);
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override
            public void onComplete() {
                child.onComplete();
            }
        };
    }
}
