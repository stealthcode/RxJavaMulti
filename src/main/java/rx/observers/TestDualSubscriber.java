package rx.observers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.DyadObserver;
import rx.DyadSubscriber;
import rx.Notification;
import rx.Subscriber;

public class TestDualSubscriber<T0, T1> extends DyadSubscriber<T0, T1> {

    private final TestDualObserver<T0, T1> testObserver;
    private final CountDownLatch latch = new CountDownLatch(1);
    private volatile Thread lastSeenThread;

    public TestDualSubscriber(DyadSubscriber<T0, T1> delegate) {
        this.testObserver = new TestDualObserver<T0, T1>(delegate);
    }

    public TestDualSubscriber(DyadObserver<T0, T1> delegate) {
        this.testObserver = new TestDualObserver<T0, T1>(delegate);
    }

    public TestDualSubscriber() {
        this.testObserver = new TestDualObserver<T0, T1>(new DyadObserver<T0, T1>() {

            @Override
            public void onComplete() {
                // do nothing
            }

            @Override
            public void onError(Throwable e) {
                // do nothing
            }

            @Override
            public void onNext(T0 t0, T1 t1) {
                // do nothing
            }

        });
    }

    /**
     * Notifies the Subscriber that the {@code Observable} has finished sending push-based
     * notifications.
     * <p>
     * The {@code Observable} will not call this method if it calls {@link #onError}.
     */
    @Override
    public void onComplete() {
        try {
            lastSeenThread = Thread.currentThread();
            testObserver.onComplete();
        } finally {
            latch.countDown();
        }
    }

    /**
     * Get the {@link Notification}s representing each time this {@link Subscriber} was notified of
     * sequence completion via {@link #onCompleted}, as a {@link List}.
     *
     * @return a list of Notifications representing calls to this Subscriber's {@link #onCompleted}
     *         method
     */
    public List<DualNotification<T0, T1>> getOnCompletedEvents() {
        return testObserver.getOnCompletedEvents();
    }

    /**
     * Notifies the Subscriber that the {@code Observable} has experienced an error condition.
     * <p>
     * If the {@code Observable} calls this method, it will not thereafter call {@link #onNext} or
     * {@link #onCompleted}.
     * 
     * @param e
     *            the exception encountered by the Observable
     */
    @Override
    public void onError(Throwable e) {
        try {
            lastSeenThread = Thread.currentThread();
            testObserver.onError(e);
        } finally {
            latch.countDown();
        }
    }

    /**
     * Get the {@link Throwable}s this {@link Subscriber} was notified of via {@link #onError} as a
     * {@link List}.
     *
     * @return a list of the Throwables that were passed to this Subscriber's {@link #onError}
     *         method
     */
    public List<Throwable> getOnErrorEvents() {
        return testObserver.getOnErrorEvents();
    }

    /**
     * Provides the Subscriber with a new item to observe.
     * <p>
     * The {@code Observable} may call this method 0 or more times.
     * <p>
     * The {@code Observable} will not call this method again after it calls either
     * {@link #onCompleted} or {@link #onError}.
     * 
     * @param t
     *            the item emitted by the Observable
     */
    @Override
    public void onNext(T0 t0, T1 t1) {
        lastSeenThread = Thread.currentThread();
        testObserver.onNext(t0, t1);
    }

    /**
     * Allow calling the protected {@link #request(long)} from unit tests.
     *
     * @param n
     *            the maximum number of items you want the Observable to emit to the Subscriber at
     *            this time, or {@code Long.MAX_VALUE} if you want the Observable to emit items at
     *            its own pace
     */
    public void requestMore(long n) {
        request(n);
    }

    /**
     * Get the sequence of items observed by this {@link Subscriber}, as an ordered {@link List}.
     *
     * @return a list of items observed by this Subscriber, in the order in which they were observed
     */
    public List<TestEvent<T0, T1>> getOnNextEvents() {
        return testObserver.getOnNextEvents();
    }

    /**
     * Assert that a particular sequence of items was received by this {@link Subscriber} in order.
     *
     * @param items
     *            the sequence of items expected to have been observed
     * @throws AssertionError
     *             if the sequence of items observed does not exactly match {@code items}
     */
    public void assertReceivedOnNext(List<TestEvent<T0, T1>> items) {
        testObserver.assertReceivedOnNext(items);
    }

    /**
     * Assert that a single terminal event occurred, either {@link #onCompleted} or {@link #onError}
     * .
     *
     * @throws AssertionError
     *             if not exactly one terminal event notification was received
     */
    public void assertTerminalEvent() {
        testObserver.assertTerminalEvent();
    }

    /**
     * Assert that this {@code Subscriber} is unsubscribed.
     *
     * @throws AssertionError
     *             if this {@code Subscriber} is not unsubscribed
     */
    public void assertUnsubscribed() {
        if (!isUnsubscribed()) {
            throw new AssertionError("Not unsubscribed.");
        }
    }

    /**
     * Assert that this {@code Subscriber} has received no {@code onError} notifications.
     * 
     * @throws AssertionError
     *             if this {@code Subscriber} has received one or more {@code onError} notifications
     */
    public void assertNoErrors() {
        if (getOnErrorEvents().size() > 0) {
            // can't use AssertionError because (message, cause) doesn't exist until Java 7
            throw new RuntimeException("Unexpected onError events: " + getOnErrorEvents()
                    .size(), getOnErrorEvents().get(0));
            // TODO possibly check for Java7+ and then use AssertionError at runtime (since we
            // always compile with 7)
        }
    }

    /**
     * Blocks until this {@link Subscriber} receives a notification that the {@code Observable} is
     * complete (either an {@code onCompleted} or {@code onError} notification).
     *
     * @throws RuntimeException
     *             if the Subscriber is interrupted before the Observable is able to complete
     */
    public void awaitTerminalEvent() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }

    /**
     * Blocks until this {@link Subscriber} receives a notification that the {@code Observable} is
     * complete (either an {@code onCompleted} or {@code onError} notification), or until a timeout
     * expires.
     *
     * @param timeout
     *            the duration of the timeout
     * @param unit
     *            the units in which {@code timeout} is expressed
     * @throws RuntimeException
     *             if the Subscriber is interrupted before the Observable is able to complete
     */
    public void awaitTerminalEvent(long timeout, TimeUnit unit) {
        try {
            latch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }

    /**
     * Blocks until this {@link Subscriber} receives a notification that the {@code Observable} is
     * complete (either an {@code onCompleted} or {@code onError} notification), or until a timeout
     * expires; if the Subscriber is interrupted before either of these events take place, this
     * method unsubscribes the Subscriber from the Observable).
     *
     * @param timeout
     *            the duration of the timeout
     * @param unit
     *            the units in which {@code timeout} is expressed
     */
    public void awaitTerminalEventAndUnsubscribeOnTimeout(long timeout, TimeUnit unit) {
        try {
            awaitTerminalEvent(timeout, unit);
        } catch (RuntimeException e) {
            unsubscribe();
        }
    }

    /**
     * Returns the last thread that was in use when an item or notification was received by this
     * {@link Subscriber}.
     *
     * @return the {@code Thread} on which this Subscriber last received an item or notification
     *         from the Observable it is subscribed to
     */
    public Thread getLastSeenThread() {
        return lastSeenThread;
    }
}
