package rx.internal.operators.bi;

import rx.BiSubscriber;
import rx.Subscriber;
import rx.functions.Func2;
import rx.operators.BiToSingleOperator;

public class OperatorBiToMonoMap<R, T0, T1> implements BiToSingleOperator<R, T0, T1> {

    private Func2<? super T0, ? super T1, ? extends R> func;

    public OperatorBiToMonoMap(Func2<? super T0, ? super T1, R> func) {
        this.func = func;
    }

    @Override
    public BiSubscriber<? super T0, ? super T1> call(final Subscriber<? super R> child) {
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
