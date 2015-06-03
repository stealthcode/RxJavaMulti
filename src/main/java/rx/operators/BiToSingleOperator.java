package rx.operators;

import rx.BiSubscriber;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Composes an Rx operator's effect to a subscriber and returns a new subscriber. Takes an Rx
 * operator's single valued subscriber and adapts it to a DualSubscriber that can subscribe to a
 * DyadObservable. This allows conversion from a DyadObservable to an {@link Observable}.
 *
 * @param <R>
 *            type of a valid downstream {@link Subscriber}
 * @param <T0>
 *            type of the parent BiSubscriber's first value
 * @param <T1>
 *            type of the parent BiSubscriber's second value
 */
public interface BiToSingleOperator<R, T0, T1> extends Func1<Subscriber<? super R>, BiSubscriber<? super T0, ? super T1>> {

}
