package rx.observers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.DyadObserver;
import rx.Notification;

public class TestDualObserver<T0, T1> implements DyadObserver<T0, T1> {

    private final DyadObserver<T0, T1> delegate;
    private final ArrayList<TestEvent<T0, T1>> onNextEvents = new ArrayList<TestEvent<T0, T1>>();
    private final ArrayList<Throwable> onErrorEvents = new ArrayList<Throwable>();
    private final ArrayList<DualNotification<T0, T1>> onCompletedEvents = new ArrayList<DualNotification<T0, T1>>();

    public TestDualObserver(DyadObserver<T0, T1> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public TestDualObserver() {
        this.delegate = (DyadObserver<T0, T1>) INERT;
    }

    @Override
    public void onComplete() {
        onCompletedEvents.add(DualNotification.<T0, T1>createOnCompleted());
        delegate.onComplete();
    }

    /**
     * Get the {@link Notification}s representing each time this observer was notified of sequence
     * completion via {@link #onCompleted}, as a {@link List}.
     *
     * @return a list of Notifications representing calls to this observer's {@link #onCompleted}
     *         method
     */
    public List<DualNotification<T0, T1>> getOnCompletedEvents() {
        return Collections.unmodifiableList(onCompletedEvents);
    }

    @Override
    public void onError(Throwable e) {
        onErrorEvents.add(e);
        delegate.onError(e);
    }

    /**
     * Get the {@link Throwable}s this observer was notified of via {@link #onError} as a
     * {@link List}.
     *
     * @return a list of Throwables passed to this observer's {@link #onError} method
     */
    public List<Throwable> getOnErrorEvents() {
        return Collections.unmodifiableList(onErrorEvents);
    }

    @Override
    public void onNext(T0 t0, T1 t1) {
        onNextEvents.add(new TestEvent<T0, T1>(t0, t1));
        delegate.onNext(t0, t1);
    }

    /**
     * Get the sequence of items observed by this observer, as an ordered {@link List}.
     *
     * @return a list of items observed by this observer, in the order in which they were observed
     */
    public List<TestEvent<T0, T1>> getOnNextEvents() {
        return Collections.unmodifiableList(onNextEvents);
    }

    /**
     * Get a list containing all of the items and notifications received by this observer, where the
     * items will be given as-is, any error notifications will be represented by their
     * {@code Throwable}s, and any sequence-complete notifications will be represented by their
     * {@code Notification} objects.
     *
     * @return a {@link List} containing one item for each item or notification received by this
     *         observer, in the order in which they were observed or received
     */
    public List<Object> getEvents() {
        ArrayList<Object> events = new ArrayList<Object>();
        events.add(onNextEvents);
        events.add(onErrorEvents);
        events.add(onCompletedEvents);
        return Collections.unmodifiableList(events);
    }

    /**
     * Assert that a particular sequence of items was received in order.
     *
     * @param items
     *            the sequence of items expected to have been observed
     * @throws AssertionError
     *             if the sequence of items observed does not exactly match {@code items}
     */
    public void assertReceivedOnNext(List<TestEvent<T0, T1>> items) {
        if (onNextEvents.size() != items.size()) {
            throw new AssertionError("Number of items does not match. Provided: " + items.size() + "  Actual: " + onNextEvents.size());
        }

        for (int i = 0; i < items.size(); i++) {
            final TestEvent<T0, T1> assertedTestEvent = items.get(i);
            if (assertedTestEvent == null) {
                // check for null equality
                if (onNextEvents.get(i) != null) {
                    throw new AssertionError("Value at index: " + i + " expected to be [null] but was: [" + onNextEvents.get(i) + "]");
                }
            } else if (!assertedTestEvent.t0.equals(onNextEvents.get(i).t0) || !(assertedTestEvent.t1.equals(onNextEvents.get(i).t1))) {
                throw new AssertionError("Value at index: " + i + " expected to be [" + assertedTestEvent + "] (" + assertedTestEvent.getClass().getSimpleName() + ") but was: [" + onNextEvents.get(i) + "] (" + onNextEvents
                        .get(i).getClass().getSimpleName() + ")");

            }
        }

    }

    /**
     * Assert that a single terminal event occurred, either {@link #onCompleted} or {@link #onError}
     * .
     *
     * @throws AssertionError
     *             if not exactly one terminal event notification was received
     */
    public void assertTerminalEvent() {
        if (onErrorEvents.size() > 1) {
            throw new AssertionError("Too many onError events: " + onErrorEvents.size());
        }

        if (onCompletedEvents.size() > 1) {
            throw new AssertionError("Too many onCompleted events: " + onCompletedEvents.size());
        }

        if (onCompletedEvents.size() == 1 && onErrorEvents.size() == 1) {
            throw new AssertionError("Received both an onError and onCompleted. Should be one or the other.");
        }

        if (onCompletedEvents.size() == 0 && onErrorEvents.size() == 0) {
            throw new AssertionError("No terminal events received.");
        }
    }

    // do nothing ... including swallowing errors
    private static DyadObserver<Object, Object> INERT = new DyadObserver<Object, Object>() {

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Object t0, Object t1) {

        }

    };
}
