package rx.internal.operators.dyad;

import rx.DyadSubscriber;
import rx.operators.DyadOperator;

public class OperatorTakeLast<T0, T1> implements DyadOperator<T0, T1, T0, T1> {

    @Override
    public DyadSubscriber<? super T0, ? super T1> call(DyadSubscriber<? super T0, ? super T1> child) {
        return new DyadSubscriber<T0, T1>() {
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
