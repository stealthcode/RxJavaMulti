package rx.internal.operators.bi;

import rx.BiSubscriber;
import rx.operators.BiOperator;

public class OperatorFlip<T0, T1> implements BiOperator<T1, T0, T0, T1> {
    @Override
    public BiSubscriber<T0, T1> call(final BiSubscriber<? super T1, ? super T0> child) {
        return new BiSubscriber<T0, T1>(child) {
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
