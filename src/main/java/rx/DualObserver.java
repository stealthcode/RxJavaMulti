package rx;

public interface DualObserver<T0, T1> {
    public void onNext(T0 t0, T1 t1);

    public void onError(Throwable e);

    public void onComplete();
}