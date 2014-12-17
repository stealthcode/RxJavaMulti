package rx.internal.types;

import rx.functions.Func2;

/**
 * 
 *
 * @param <S0>
 * @param <S1>
 */
public class Const2<S0, S1> implements Func2<S0, S1, S1> {
    @Override
    public S1 call(S0 first, S1 second) {
        return second;
    }
}
