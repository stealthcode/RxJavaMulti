package rx.internal.operators.dyad;

import rx.DyadSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.operators.DyadOperator;

public class OperatorDoOnNext<T0, T1> implements DyadOperator<T0, T1, T0, T1> {

    public static <T0, T1> DyadOperator<T0, T1, T0, T1> mono1Operator(Action1<? super T0> action1) {
        return new OperatorDoOnNext<T0, T1>(new BaseDyadSubscriber<T0, T1, T0, T1>() {
            @Override
            protected void _onNext(T0 t0, T1 t1) {
                action1.call(t0);
                childOnNext(t0, t1);
            }
        });
    }

    public static <T0, T1> DyadOperator<T0, T1, T0, T1> mono2Operator(Action1<? super T1> action2) {
        return new OperatorDoOnNext<T0, T1>(new BaseDyadSubscriber<T0, T1, T0, T1>() {
            @Override
            protected void _onNext(T0 t0, T1 t1) {
                action2.call(t1);
                childOnNext(t0, t1);
            }
        });
    }

    public static <T0, T1> DyadOperator<T0, T1, T0, T1> dyadOperator(Action2<? super T0, ? super T1> action) {
        return new OperatorDoOnNext<T0, T1>(new BaseDyadSubscriber<T0, T1, T0, T1>() {
            @Override
            protected void _onNext(T0 t0, T1 t1) {
                action.call(t0, t1);
                childOnNext(t0, t1);
            }
        });
    }

    private BaseDyadSubscriber<T0, T1, T0, T1> subscriber;

    private OperatorDoOnNext(BaseDyadSubscriber<T0, T1, T0, T1> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public DyadSubscriber<? super T0, ? super T1> call(final DyadSubscriber<? super T0, ? super T1> child) {
        subscriber.setChild(child);
        return subscriber;
    }
}
