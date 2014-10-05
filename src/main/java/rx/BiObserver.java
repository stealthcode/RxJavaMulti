package rx;

public interface BiObserver<T0, T1> {
    public void onNext(T0 t0, T1 t1);

    public void onError(Throwable e);

    public void onComplete();
}