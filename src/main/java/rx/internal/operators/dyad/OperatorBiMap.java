package rx.internal.operators.dyad;

import rx.DyadSubscriber;
import rx.Subscriber;
import rx.functions.Func2;
import rx.operators.DyadToSingleOperator;

public class OperatorBiMap<R, T0, T1> implements DyadToSingleOperator<R, T0, T1> {

    private Func2<? super T0, ? super T1, ? extends R> func;

    public OperatorBiMap(Func2<? super T0, ? super T1, ? extends R> func) {
        this.func = func;
    }

    @Override
    public DyadSubscriber<? super T0, ? super T1> call(final Subscriber<? super R> child) {
        return new DyadSubscriber<T0, T1>(child) {

            @Override
            public void onNext(T0 t0, T1 t1) {
                child.onNext(func.call(t0, t1));
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override
            public void onComplete() {
                child.onCompleted();
            }
        };
    }
}
