package rx.internal.operators.bi;

import rx.BiSubscriber;
import rx.functions.Func3;
import rx.operators.BiOperator;

public class OperatorScan<R0, R1, T0, T1> implements BiOperator<R0, R1, T0, T1> {

    /**
     * Creates an OperatorScan instance which will replace the first element of a bivalue with the
     * scan accumulator value.
     * 
     * @param seed
     * @param func
     * @return
     */
    public static <T0, T1, R0> BiOperator<R0, T1, T0, T1> bi1Operator(final R0 seed, final Func3<R0, ? super T0, ? super T1, R0> func) {
        return new OperatorScan<R0, T1, T0, T1>(new BaseBiSubscriber<R0, T1, T0, T1>() {
            R0 accum = seed;

            @Override
            protected void _onNext(T0 t0, T1 t1) {
                accum = func.call(accum, t0, t1);
                childOnNext(accum, t1);
            }
        });
    }

    /**
     * Creates an OperatorScan instance which will replace the first element of a bivalue with the
     * scan accumulator value.
     * 
     * @param func
     * @return
     */
    public static <T0, T1> BiOperator<T0, T1, T0, T1> bi1Operator(final Func3<T0, ? super T0, ? super T1, T0> func) {
        return new OperatorScan<T0, T1, T0, T1>(new BaseBiSubscriber<T0, T1, T0, T1>() {
            T0 accum;
            boolean isFirst = true;

            @Override
            protected void _onNext(T0 t0, T1 t1) {
                accum = !isFirst ? func.call(accum, t0, t1) : t0;
                isFirst = false;
                childOnNext(accum, t1);
            }
        });
    }

    /**
     * Creates an OperatorScan instance which will replace the second element of a bivalue with the
     * scan accumulator value.
     * 
     * @param seed
     * @param func
     * @return
     */
    public static <T0, T1, R1> BiOperator<T0, R1, T0, T1> bi2Operator(final R1 seed, final Func3<R1, ? super T0, ? super T1, R1> func) {
        return new OperatorScan<T0, R1, T0, T1>(new BaseBiSubscriber<T0, R1, T0, T1>() {
            R1 accum = seed;

            @Override
            protected void _onNext(T0 t0, T1 t1) {
                accum = func.call(accum, t0, t1);
                childOnNext(t0, accum);
            }
        });
    }

    /**
     * Creates an OperatorScan instance which will replace the second element of a bivalue with the
     * scan accumulator value.
     * 
     * @param func
     * @return
     */
    public static <T0, T1> BiOperator<T0, T1, T0, T1> bi2Operator(final Func3<T1, ? super T0, ? super T1, T1> func) {
        return new OperatorScan<T0, T1, T0, T1>(new BaseBiSubscriber<T0, T1, T0, T1>() {
            T1 accum;
            boolean isFirst = true;

            @Override
            protected void _onNext(T0 t0, T1 t1) {
                accum = !isFirst ? func.call(accum, t0, t1) : t1;
                isFirst = false;
                childOnNext(t0, accum);
            }
        });
    }

    private BaseBiSubscriber<R0, R1, T0, T1> subscriber;

    public OperatorScan(BaseBiSubscriber<R0, R1, T0, T1> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public BiSubscriber<? super T0, ? super T1> call(BiSubscriber<? super R0, ? super R1> child) {
        subscriber.setChild(child);
        return subscriber;
    }

}
