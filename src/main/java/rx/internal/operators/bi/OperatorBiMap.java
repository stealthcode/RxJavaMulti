package rx.internal.operators.bi;

import rx.BiSubscriber;
import rx.operators.BiOperator;
import rx.functions.Func1;
import rx.functions.Func2;

public class OperatorBiMap<R0, R1, T0, T1> implements BiOperator<R0, R1, T0, T1> {
    public static <R, T0, T1> BiOperator<R, T1, T0, T1> mono1Operator(final Func1<? super T0, ? extends R> func) {
        return new OperatorBiMap<R, T1, T0, T1>(new BaseBiSubscriber<R, T1, T0, T1>() {
            @Override
            public void _onNext(T0 t0, T1 t1) {
                childOnNext(func.call(t0), t1);
            }
        });
    }

    public static <R, T0, T1> BiOperator<T0, R, T0, T1> mono2Operator(final Func1<? super T1, ? extends R> func) {
        return new OperatorBiMap<T0, R, T0, T1>(new BaseBiSubscriber<T0, R, T0, T1>() {
            @Override
            public void _onNext(T0 t0, T1 t1) {
                getChild().onNext(t0, func.call(t1));
            }
        });
    }

    public static <R, T0, T1> BiOperator<R, T1, T0, T1> bi1Operator(final Func2<? super T0, ? super T1, ? extends R> func) {
        return new OperatorBiMap<R, T1, T0, T1>(new BaseBiSubscriber<R, T1, T0, T1>() {
            @Override
            public void _onNext(T0 t0, T1 t1) {
                getChild().onNext(func.call(t0, t1), t1);
            }
        });
    }

    public static <R, T0, T1> BiOperator<T0, R, T0, T1> bi2Operator(final Func2<? super T0, ? super T1, ? extends R> func) {
        return new OperatorBiMap<T0, R, T0, T1>(new BaseBiSubscriber<T0, R, T0, T1>() {
            @Override
            public void _onNext(T0 t0, T1 t1) {
                getChild().onNext(t0, func.call(t0, t1));
            }
        });
    }

    private BaseBiSubscriber<R0, R1, T0, T1> subscriber;

    public OperatorBiMap(BaseBiSubscriber<R0, R1, T0, T1> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public BiSubscriber<? super T0, ? super T1> call(final BiSubscriber<? super R0, ? super R1> child) {
        subscriber.setChild(child);
        return subscriber;
    }
}
