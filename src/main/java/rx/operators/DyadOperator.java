package rx.operators;

import rx.DyadSubscriber;
import rx.functions.Func1;

/**
 * Composes an Rx operator's effect to a to a subscriber and returns a new subscriber. This operator
 * applies its effect to a two valued {@link DyadSubscriber subscriber} and returns a new subscriber
 * of the same kind.
 * 
 * @param <R0>
 *            first downstream type (to consumer)
 * @param <R1>
 *            second downstream type
 * @param <T0>
 *            first upstream type (from producer)
 * @param <T1>
 *            second downstream type
 */
public interface DyadOperator<R0, R1, T0, T1> extends Func1<DyadSubscriber<? super R0, ? super R1>, DyadSubscriber<? super T0, ? super T1>> {

}
