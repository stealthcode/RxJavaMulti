package rx.internal.operators.bi;

import rx.BiSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.operators.BiOperator;

public class OperatorDoOnNext<T0, T1> implements BiOperator<T0, T1, T0, T1> {

    public static <T0, T1> BiOperator<T0, T1, T0, T1> mono1Operator(Action1<? super T0> action1) {
        return new OperatorDoOnNext<T0, T1>(new BaseBiSubscriber<T0, T1, T0, T1>() {
            @Override
            protected void _onNext(T0 t0, T1 t1) {
                action1.call(t0);
                childOnNext(t0, t1);
            }
        });
    }

    public static <T0, T1> BiOperator<T0, T1, T0, T1> mono2Operator(Action1<? super T1> action2) {
        return new OperatorDoOnNext<T0, T1>(new BaseBiSubscriber<T0, T1, T0, T1>() {
            @Override
            protected void _onNext(T0 t0, T1 t1) {
                action2.call(t1);
                childOnNext(t0, t1);
            }
        });
    }

    public static <T0, T1> BiOperator<T0, T1, T0, T1> biOperator(Action2<? super T0, ? super T1> action) {
        return new OperatorDoOnNext<T0, T1>(new BaseBiSubscriber<T0, T1, T0, T1>() {
            @Override
            protected void _onNext(T0 t0, T1 t1) {
                action.call(t0, t1);
                childOnNext(t0, t1);
            }
        });
    }

    private BaseBiSubscriber<T0, T1, T0, T1> subscriber;

    private OperatorDoOnNext(BaseBiSubscriber<T0, T1, T0, T1> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public BiSubscriber<? super T0, ? super T1> call(final BiSubscriber<? super T0, ? super T1> child) {
        subscriber.setChild(child);
        return subscriber;
    }
}
