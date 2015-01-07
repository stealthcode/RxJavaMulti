package rx.internal.operators.dyad;

import rx.DyadSubscriber;
import rx.operators.DyadOperator;

public class OperatorFlip<T0, T1> implements DyadOperator<T1, T0, T0, T1> {
    @Override
    public DyadSubscriber<T0, T1> call(final DyadSubscriber<? super T1, ? super T0> child) {
        return new DyadSubscriber<T0, T1>(child) {
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
