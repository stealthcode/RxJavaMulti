package rx.internal.operators.bi;

import rx.BiSubscriber;
import rx.operators.BiOperator;

public class OperatorTakeLast<T0, T1> implements BiOperator<T0, T1, T0, T1> {

    @Override
    public BiSubscriber<? super T0, ? super T1> call(BiSubscriber<? super T0, ? super T1> child) {
        return new BiSubscriber<T0, T1>() {
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
