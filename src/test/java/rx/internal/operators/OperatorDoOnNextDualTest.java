package rx.internal.operators;

import org.junit.Test;

import rx.BiObservable;
import rx.BiObservable.BiOnSubscribe;
import rx.DualSubscriber;

public class OperatorDoOnNextDualTest {
    @Test
    public void test() {
        final String i0 = "foo";
        final String i1 = "bar";
        final BiObservable<String, String> source = BiObservable
                .create(new BiOnSubscribe<String, String>() {
                    @Override
                    public void call(DualSubscriber<? super String, ? super String> child) {
                        child.onNext(i0, i1);
                        child.onComplete();
                    }
                });
        source.doOnNext((String t1, String t2) -> {
            
        })
        .subcribe(new DualSubscriber<String, String>() {
            @Override
            public void onNext(String t0, String t1) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
