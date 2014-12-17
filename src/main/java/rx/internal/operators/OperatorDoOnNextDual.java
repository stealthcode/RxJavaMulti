package rx.internal.operators;

import rx.DualSubscriber;
import rx.BiObservable.DualOperator;
import rx.functions.Action1;
import rx.functions.Action2;

public class OperatorDoOnNextDual<T0, T1> implements DualOperator<T0, T1, T0, T1> {
	
	public static <T0, T1> DualOperator<T0, T1, T0, T1> singleAction1Operator(Action1<? super T0> action1) {
		return new OperatorDoOnNextDual<T0, T1>(new OnNextSubscriber<T0, T1>() {
			@Override
			public void onNext(T0 t0, T1 t1) {
				action1.call(t0);
				getChild().onNext(t0, t1);
			}
		});
	}
	
	public static <T0, T1> DualOperator<T0, T1, T0, T1> singleAction2Operator(Action1<? super T1> action2) {
		return new OperatorDoOnNextDual<T0, T1>(new OnNextSubscriber<T0, T1>() {
			@Override
			public void onNext(T0 t0, T1 t1) {
				action2.call(t1);
				getChild().onNext(t0, t1);
			}
		});
	}
	
	public static <T0, T1> DualOperator<T0, T1, T0, T1> dualActionOperator(Action2<? super T0, ? super T1> action) {
		return new OperatorDoOnNextDual<T0, T1>(new OnNextSubscriber<T0, T1>() {
			@Override
			public void onNext(T0 t0, T1 t1) {
				action.call(t0, t1);
				getChild().onNext(t0, t1);
			}
		});
	}
	
	private static abstract class OnNextSubscriber<T0, T1> extends DualSubscriber<T0, T1> {
		private DualSubscriber<? super T0, ? super T1> child;
		
		public DualSubscriber<? super T0, ? super T1> getChild() {
			return child;
		}
		
		public void setChild(DualSubscriber<? super T0, ? super T1> child) {
			this.child = child;
		}

        @Override
        public void onError(Throwable e) {
            child.onError(e);
        }

        @Override
        public void onComplete() {
            child.onComplete();
        }
	}
	
	private OnNextSubscriber<T0, T1> subscriber;

	private OperatorDoOnNextDual(OnNextSubscriber<T0, T1> subscriber) {
		this.subscriber = subscriber;
	}

	@Override
    public DualSubscriber<? super T0, ? super T1> wrapDual(final DualSubscriber<? super T0, ? super T1> child) {
		subscriber.setChild(child);
		return subscriber;
    }
}
