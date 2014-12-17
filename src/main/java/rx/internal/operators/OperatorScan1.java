package rx.internal.operators;

import rx.BiObservable.DualOperator;
import rx.DualSubscriber;
import rx.functions.Func3;


public class OperatorScan1<T0, T1, R> implements DualOperator<R, T1, T0, T1> {
	private final Func3<R, ? super T0, ? super T1, R> func;
	private final R seed;

	public OperatorScan1(R seed, Func3<R, ? super T0, ? super T1, R> func) {
		this.seed = seed;
		this.func = func;
	}

	@Override
	public DualSubscriber<? super T0, ? super T1> wrapDual(DualSubscriber<? super R, ? super T1> child) {
		return new DualSubscriber<T0, T1>(child) {
			R accum = seed;
			@Override
			public void onNext(T0 t0, T1 t1) {
				accum = func.call(accum, t0, t1);
				child.onNext(accum, t1);
			}

			@Override
			public void onError(Throwable e) {
				child.onError(e);
			}

			@Override
			public void onComplete() {
				child.onComplete();
			}};
	}

}
