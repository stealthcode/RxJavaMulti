package rx.operators;

import rx.DyadSubscriber;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Composes an Rx operator's effect to a subscriber and returns a new subscriber. Takes an Rx
 * operator's single valued subscriber and adapts it to a DualSubscriber that can subscribe to a
 * BiObservable. This allows conversion from a BiObservable to an {@link Observable}.
 *
 * @param <R>
 *            type of a valid downstream {@link Subscriber}
 * @param <T0>
 *            type of first argument
 * @param <T1>
 *            type of second argument
 */
public interface DyadToSingleOperator<R, T0, T1> extends Func1<Subscriber<? super R>, DyadSubscriber<? super T0, ? super T1>> {

}
