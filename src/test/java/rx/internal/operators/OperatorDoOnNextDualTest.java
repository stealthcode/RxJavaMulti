package rx.internal.operators;

import org.junit.Test;

import rx.BiObservable;
import rx.BiObservable.BiOnSubscribe;
import rx.DualSubscriber;
import rx.functions.Action2;

public class OperatorDoOnNextDualTest {
	@Test
	public void test() {
		final String t0 = "foo";
		final String t1 = "bar";
		final BiObservable<String, String> source = BiObservable.create(new BiOnSubscribe<String, String>() {
			@Override
			public void call(DualSubscriber<? super String, ? super String> child) {
				child.onNext(t0, t1);
				child.onComplete();
			}});
		source.doOnNext(new Action2<String, String>() {
			@Override
			public void call(String t1, String t2) {
				
			}})
			.subcribe(new DualSubscriber<String, String>() {

				@Override
				public void onNext(String t0, String t1) {
					
				}

				@Override
				public void onError(Throwable e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					
				}});
	}
}
