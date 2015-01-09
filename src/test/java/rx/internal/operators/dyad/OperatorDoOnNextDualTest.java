package rx.internal.operators.dyad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import rx.DyadSubscriber;
import rx.DyadObservable;
import rx.DyadObservable.DyadOnSubscribe;
import rx.observers.TestDualSubscriber;
import rx.observers.TestEvent;

public class OperatorDoOnNextDualTest {
    final String i0 = "foo";
    final String i1 = "bar";
    
    final DyadObservable<String, String> source = DyadObservable
            .create(new DyadOnSubscribe<String, String>() {
                @Override
                public void call(DyadSubscriber<? super String, ? super String> child) {
                    child.onNext(i0, i1);
                    child.onComplete();
                }
            });
    TestDualSubscriber<String, String> testSubscriber;
    
    @Before
    public void setup() {
        testSubscriber = new TestDualSubscriber<String, String>();
    }
    
    @Test
    public void testDoOnNextPropogatesExactSameItems() {
        source.doOnNext((String t1, String t2) -> {
            
        }).subcribe(testSubscriber);
        final LinkedList<TestEvent<String, String>> items = new LinkedList<TestEvent<String, String>>();
        items.add(new TestEvent<String, String>(i0, i1));
        testSubscriber.assertReceivedOnNext(items);
        testSubscriber.assertTerminalEvent();
        testSubscriber.assertNoErrors();
        final List<TestEvent<String, String>> events = testSubscriber.getOnNextEvents();
        TestEvent<String, String> e = events.iterator().next();
        assertTrue(e.t0 == i0);
        assertTrue(e.t1 == i1);
    }

    @Test
    public void testDoOnNextPropogatesError() {
        source.doOnNext((String t1, String t2) -> {
            throw new RuntimeException();
        }).subcribe(testSubscriber);
        testSubscriber.assertTerminalEvent();
        assertEquals(0, testSubscriber.getOnNextEvents().size());
        final List<Throwable> errors = testSubscriber.getOnErrorEvents();
        assertEquals(1, errors.size());
        final Throwable throwable = errors.get(0);
        assertTrue(throwable instanceof RuntimeException);
        
    }
}
