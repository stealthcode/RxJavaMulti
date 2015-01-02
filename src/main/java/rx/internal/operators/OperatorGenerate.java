package rx.internal.operators;

import rx.DualSubscriber;
import rx.Subscriber;
import rx.BiObservable.SingleToDualOperator;
import rx.functions.Func1;

public class OperatorGenerate<T0, T1> implements SingleToDualOperator<T0, T1, T0> {

    private Func1<? super T0, ? extends T1> generatorFunc;

    public OperatorGenerate(Func1<? super T0, ? extends T1> generatorFunc) {
        this.generatorFunc = generatorFunc;
    }

    @Override
    public Subscriber<T0> wrapSingleToDual(final DualSubscriber<? super T0, ? super T1> child) {
        return new Subscriber<T0>() {
            @Override
            public void onCompleted() {
                child.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override
            public void onNext(T0 t0) {
                try {
                    T1 t1 = generatorFunc.call(t0);
                    child.onNext(t0, t1);
                } catch (Throwable e) {
                    child.onError(e);
                }
            }
        };
    }

}
