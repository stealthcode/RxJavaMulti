package rx.internal.operators;

import rx.DualSubscriber;
import rx.BiObservable.DualOperator;

public class OperatorTakeLast2<T0, T1> implements DualOperator<T0, T1, T0, T1> {

    @Override
    public DualSubscriber<? super T0, ? super T1> wrapDual(DualSubscriber<? super T0, ? super T1> child) {
        return new DualSubscriber<T0, T1>() {
            T0 lastT0;
            T1 lastT1;
            private boolean haveLast;

            @Override
            public void onNext(T0 t0, T1 t1) {
                lastT0 = t0;
                lastT1 = t1;
                haveLast = true;
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override
            public void onComplete() {
                if (haveLast)
                    child.onNext(lastT0, lastT1);
                child.onComplete();
            }
        };
    }
}
