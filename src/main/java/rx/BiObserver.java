package rx;

/**
 * 
 *
 * @param <T0>
 * @param <T1>
 */
public interface BiObserver<T0, T1> {
    /**
     * @param t0
     * @param t1
     */
    public void onNext(T0 t0, T1 t1);

    /**
     * @param e
     */
    public void onError(Throwable e);

    /**
     * 
     */
    public void onComplete();
}