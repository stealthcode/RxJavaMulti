package rx.operators;

import rx.BiSubscriber;
import rx.BiObservable;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Composes an Rx operator's effect to a subscriber and returns a new subscriber. This operator
 * transforms a two valued {@link BiSubscriber subscriber} and adapts it to a single valued
 * {@link Subscriber} that can subscribe to an Observable. This allows conversion from an
 * {@link Observable observable} to a {@link BiObservable bi observable}.
 *
 * @param <R0>
 *            type of the child BiSubscriber first value
 * @param <R1>
 *            type of the child BiSubscriber second value 
 * @param <T>
 *            type of the parent Subscriber
 * 
 * @see BiObservable#lift(Observable, SingleToBiOperator)
 */
public interface SingleToBiOperator<R0, R1, T> extends Func1<BiSubscriber<? super R0, ? super R1>, Subscriber<T>> {

}
