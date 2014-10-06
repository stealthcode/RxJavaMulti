package rx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable.OnSubscribe;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;

public class BiObservable<T0, T1> {
    private BiOnSubscribe<T0, T1> f;

    public static interface DualOperator<R0, R1, T0, T1> extends Func1<DualSubscriber<? super R0, ? super R1>, DualSubscriber<? super T0, ? super T1>> {
    }

    public static interface BiOperator<R, T0, T1> extends Func1<Subscriber<? super R>, BiSubscriber<? super T0, ? super T1>> {
    }

    public static interface BiOnSubscribe<T0, T1> extends Action1<DualSubscriber<? super T0, ? super T1>> {
    }
    
    private BiObservable(BiOnSubscribe<T0, T1> f) {
        this.f = f;
    }

    public static <T0, T1> BiObservable<T0, T1> create(BiOnSubscribe<T0, T1> f) {
        return new BiObservable<T0, T1>(f);
    }

    public void subcribe(DualSubscriber<T0, T1> subscriber) {
        f.call(subscriber);
    }

    public <R0, R1> BiObservable<R0, R1> lift(final DualOperator<? extends R0, ? extends R1, ? super T0, ? super T1> dualOperator) {
        return BiObservable.create(new BiOnSubscribe<R0, R1>() {
            @Override
            public void call(DualSubscriber<? super R0, ? super R1> child) {
                f.call(dualOperator.call(child));
            }
        });
    }
    
    public <R> Observable<R> lift(final BiOperator<? extends R, ? super T0, ? super T1> biOperator) {
        return Observable.create(new OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> child) {
                f.call(biOperator.call(child));
            }
        });
    }
    
    public static <T0, T1> BiObservable<T0, T1> from(final Observable<? extends T1> ob1, final Func1<? super T1, ? extends T0> f) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                final AtomicBoolean error = new AtomicBoolean();
                ob1.unsafeSubscribe(new Subscriber<T1>() {

                    @Override
                    public void onCompleted() {
                        if (!error.get())
                            subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (error.compareAndSet(false, true))
                            subscriber.onError(e);
                    }

                    @Override
                    public void onNext(T1 t) {
                        if (!error.get())
                            subscriber.onNext(f.call(t), t);
                    }});
            }});
    }

    public static <T0, T1> BiObservable<T0, T1> from(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                final AtomicInteger active = new AtomicInteger(1);
                final AtomicBoolean error = new AtomicBoolean();

                ob0.unsafeSubscribe(new Subscriber<T0>() {
                    @Override
                    public void onCompleted() {
                        if (active.decrementAndGet() == 0)
                            subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        int last;
                        while (!active.compareAndSet(last = active.get(), 0))
                            ;
                        if (last != 0 && error.compareAndSet(false, true)) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onNext(final T0 t0) {
                        if (error.get())
                            return;
                        active.incrementAndGet();
                        ob1.subscribe(new Subscriber<T1>() {
                            @Override
                            public void onCompleted() {
                                if (active.decrementAndGet() == 0)
                                    subscriber.onComplete();
                            }

                            @Override
                            public void onError(Throwable e) {
                                int last;
                                while (!active.compareAndSet(last = active.get(), 0))
                                    ;
                                if (last != 0 && error.compareAndSet(false, true)) {
                                    subscriber.onError(e);
                                }
                            }

                            @Override
                            public void onNext(T1 t1) {
                                if (error.get())
                                    return;
                                subscriber.onNext(t0, t1);
                            }
                        });
                    }
                });
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> just(T0 i0, Observable<? extends T1> ob1) {
        return from(Observable.just(i0), ob1);
    }

    public <R> BiObservable<R, T1> mapFirst(final Func2<? super T0, ? super T1, ? extends R> func) {
        return lift(new DualOperator<R, T1, T0, T1>() {
            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super R, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child) {
                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        child.onNext(func.call(t0, t1), t1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onComplete();
                    }
                };
            }
        });
    }
    
    public <R> Observable<R> biMap(final Func2<? super T0, ? super T1, ? extends R> func) {
        return lift(new BiOperator<R, T0, T1>() {

            @Override
            public BiSubscriber<? super T0, ? super T1> call(final Subscriber<? super R> child) {
                return new BiSubscriber<T0, T1>(child){

                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        child.onNext(func.call(t0, t1));
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onCompleted();
                    }};
            }
        });
    }
    
    public BiObservable<T0, T1> doOnNext(final Action2<T0, T1> action) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child){

                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        action.call(t0, t1);
                        child.onNext(t0, t1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onComplete();
                    }};
            }
        });
    }

    public BiObservable<T0, T1> doOnNextFirst(final Action1<T0> action) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child){

                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        action.call(t0);
                        child.onNext(t0, t1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onComplete();
                    }};
            }
        });
    }

    public BiObservable<T0, T1> doOnNextSecond(final Action1<T1> action) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child){

                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        action.call(t1);
                        child.onNext(t0, t1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onComplete();
                    }};
            }
        });
    }

    public Observable<T0> selectFirst() {
        return Observable.create(new OnSubscribe<T0>() {
            @Override
            public void call(final Subscriber<? super T0> child) {
                DualSubscriber<T0, T1> parent = new DualSubscriber<T0, T1>() {
                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        child.onNext(t0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onCompleted();
                    }
                };
                parent.add(child);
                f.call(parent);
            }
        });
    }

    public <R> BiObservable<T0, R> mapSecond(Func2<? super T0, ? super T1, ? extends R> func) {
        return flip().mapFirst(flip(func)).flip();
    }

    public Observable<T1> selectSecond() {
        return flip().selectFirst();
    }

    private static <T0, T1, R> Func2<T1, T0, R> flip(final Func2<? super T0, ? super T1, ? extends R> func) {
        return new Func2<T1, T0, R>() {
            @Override
            public R call(T1 t1, T0 t0) {
                return func.call(t0, t1);
            }
        };
    }

    public BiObservable<T1, T0> flip() {
        return lift(new DualOperator<T1, T0, T0, T1>() {
            @Override
            public DualSubscriber<T0, T1> call(final DualSubscriber<? super T1, ? super T0> child) {
                return new DualSubscriber<T0, T1>(child) {
                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        child.onNext(t1, t0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        child.onComplete();
                    }
                };
            }
        });
    }
    
}
