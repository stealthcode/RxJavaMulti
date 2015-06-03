package rx.operators;

import rx.BiSubscriber;
import rx.functions.Func1;

/**
 * Composes an Rx operator's effect to a to a subscriber and returns a new subscriber. This operator
 * applies its effect to a two valued {@link BiSubscriber subscriber} and returns a new subscriber
 * of the same kind.
 * 
 * @param <R0>
 *            type of the child BiSubscriber's first value
 * @param <R1>
 *            type of the child BiSubscriber's second value
 * @param <T0>
 *            type of the parent BiSubscriber's first value
 * @param <T1>
 *            type of the parent BiSubscriber's second value
 */
public interface BiOperator<R0, R1, T0, T1> extends Func1<BiSubscriber<? super R0, ? super R1>, BiSubscriber<? super T0, ? super T1>> {

}
