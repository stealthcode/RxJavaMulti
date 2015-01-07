package rx.internal.operators.dyad;

import rx.DyadSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.operators.DyadOperator;

public class OperatorDoOnNextDual<T0, T1> implements DyadOperator<T0, T1, T0, T1> {

    public static <T0, T1> DyadOperator<T0, T1, T0, T1> singleAction1Operator(Action1<? super T0> action1) {
        return new OperatorDoOnNextDual<T0, T1>(new OnNextSubscriber<T0, T1>() {
            @Override
            public void onNext(T0 t0, T1 t1) {
                try {
                    action1.call(t0);
                    getChild().onNext(t0, t1);
                } catch (Throwable t) {
                    unsubscribe();
                    getChild().onError(t);
                }
            }
        });
    }

    public static <T0, T1> DyadOperator<T0, T1, T0, T1> singleAction2Operator(Action1<? super T1> action2) {
        return new OperatorDoOnNextDual<T0, T1>(new OnNextSubscriber<T0, T1>() {
            @Override
            public void onNext(T0 t0, T1 t1) {
                try {
                    action2.call(t1);
                    getChild().onNext(t0, t1);
                } catch (Throwable t) {
                    unsubscribe();
                    getChild().onError(t);
                }
            }
        });
    }

    public static <T0, T1> DyadOperator<T0, T1, T0, T1> dualActionOperator(Action2<? super T0, ? super T1> action) {
        return new OperatorDoOnNextDual<T0, T1>(new OnNextSubscriber<T0, T1>() {
            @Override
            public void onNext(T0 t0, T1 t1) {
                try {
                    action.call(t0, t1);
                    getChild().onNext(t0, t1);
                } catch (Throwable t) {
                    unsubscribe();
                    getChild().onError(t);
                }
            }
        });
    }

    private static abstract class OnNextSubscriber<T0, T1> extends DyadSubscriber<T0, T1> {
        private DyadSubscriber<? super T0, ? super T1> child;

        public DyadSubscriber<? super T0, ? super T1> getChild() {
            return child;
        }

        public void setChild(DyadSubscriber<? super T0, ? super T1> child) {
            this.child = child;
        }

        @Override
        public void onError(Throwable e) {
            if (!isUnsubscribed())
                child.onError(e);
        }

        @Override
        public void onComplete() {
            if (!isUnsubscribed())
                child.onComplete();
        }
    }

    private OnNextSubscriber<T0, T1> subscriber;

    private OperatorDoOnNextDual(OnNextSubscriber<T0, T1> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public DyadSubscriber<? super T0, ? super T1> call(final DyadSubscriber<? super T0, ? super T1> child) {
        subscriber.setChild(child);
        return subscriber;
    }
}
