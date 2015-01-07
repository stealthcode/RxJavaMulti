package rx.operators;

import rx.DyadSubscriber;
import rx.DyadObservable;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Composes an Rx operator's effect to a subscriber and returns a new subscriber. This operator
 * transforms a two valued {@link DyadSubscriber subscriber} and adapts it to a single valued
 * {@link Subscriber} that can subscribe to an Observable. This allows conversion from an
 * {@link Observable observable} to a {@link DyadObservable bi observable}.
 *
 * @param <R0>
 *            type of the first argument
 * @param <R1>
 *            type of the second argument
 * @param <T>
 *            type of the Observable
 * 
 * @see DyadObservable#lift(Observable, SingleToDyadOperator)
 */
public interface SingleToDyadOperator<R0, R1, T> extends Func1<DyadSubscriber<? super R0, ? super R1>, Subscriber<T>> {

}
