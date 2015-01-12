package rx.internal.operators.dyad;

import rx.DyadSubscriber;
import rx.functions.Func3;
import rx.operators.DyadOperator;

public class OperatorScan<R0, R1, T0, T1> implements DyadOperator<R0, R1, T0, T1> {

    /**
     * Creates an OperatorScan instance which will replace the first element of a dyad with the
     * scan accumulator value.
     * 
     * @param seed
     * @param func
     * @return
     */
    public static <T0, T1, R0> DyadOperator<R0, T1, T0, T1> dyad1Operator(final R0 seed, final Func3<R0, ? super T0, ? super T1, R0> func) {
        return new OperatorScan<R0, T1, T0, T1>(new BaseDyadSubscriber<R0, T1, T0, T1>() {
            R0 accum = seed;

            @Override
            protected void _onNext(T0 t0, T1 t1) {
                accum = func.call(accum, t0, t1);
                childOnNext(accum, t1);
            }
        });
    }

    /**
     * Creates an OperatorScan instance which will replace the first element of a dyad with the
     * scan accumulator value.
     * 
     * @param func
     * @return
     */
    public static <T0, T1> DyadOperator<T0, T1, T0, T1> dyad1Operator(final Func3<T0, ? super T0, ? super T1, T0> func) {
        return new OperatorScan<T0, T1, T0, T1>(new BaseDyadSubscriber<T0, T1, T0, T1>() {
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
     * Creates an OperatorScan instance which will replace the second element of a dyad with the
     * scan accumulator value.
     * 
     * @param seed
     * @param func
     * @return
     */
    public static <T0, T1, R1> DyadOperator<T0, R1, T0, T1> dyad2Operator(final R1 seed, final Func3<R1, ? super T0, ? super T1, R1> func) {
        return new OperatorScan<T0, R1, T0, T1>(new BaseDyadSubscriber<T0, R1, T0, T1>() {
            R1 accum = seed;

            @Override
            protected void _onNext(T0 t0, T1 t1) {
                accum = func.call(accum, t0, t1);
                childOnNext(t0, accum);
            }
        });
    }

    /**
     * Creates an OperatorScan instance which will replace the second element of a dyad with the
     * scan accumulator value.
     * 
     * @param func
     * @return
     */
    public static <T0, T1> DyadOperator<T0, T1, T0, T1> dyad2Operator(final Func3<T1, ? super T0, ? super T1, T1> func) {
        return new OperatorScan<T0, T1, T0, T1>(new BaseDyadSubscriber<T0, T1, T0, T1>() {
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

    private BaseDyadSubscriber<R0, R1, T0, T1> subscriber;

    public OperatorScan(BaseDyadSubscriber<R0, R1, T0, T1> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public DyadSubscriber<? super T0, ? super T1> call(DyadSubscriber<? super R0, ? super R1> child) {
        subscriber.setChild(child);
        return subscriber;
    }

}
