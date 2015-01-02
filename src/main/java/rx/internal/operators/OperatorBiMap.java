package rx.internal.operators;

import rx.BiSubscriber;
import rx.Subscriber;
import rx.BiObservable.BiOperator;
import rx.functions.Func2;

public class OperatorBiMap<R, T0, T1> implements BiOperator<R, T0, T1> {

    private Func2<? super T0, ? super T1, ? extends R> func;

    public OperatorBiMap(Func2<? super T0, ? super T1, ? extends R> func) {
        this.func = func;
    }

    @Override
    public BiSubscriber<? super T0, ? super T1> wrapDualToSingle(final Subscriber<? super R> child) {
        return new BiSubscriber<T0, T1>(child) {

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
