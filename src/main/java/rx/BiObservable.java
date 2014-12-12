package rx;

import rx.Observable.OnSubscribe;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    public void subcribe(DualSubscriber<? super T0, ? super T1> subscriber) {
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

    public <R> Observable<? extends R> lift(final BiOperator<? extends R, ? super T0, ? super T1> biOperator) {
        return Observable.create(new OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> child) {
                f.call(biOperator.call(child));
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> zip(final Observable<? extends T0> ob0, final Func1<? super T0, ? extends T1> f) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(DualSubscriber<? super T0, ? super T1> subscriber) {
                ob0.unsafeSubscribe(new Subscriber<T0>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(T0 t0) {
                        try {
                            T1 t1 = f.call(t0);
                            subscriber.onNext(t0, t1);
                        } catch(Throwable e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> zip(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                subscriber.add(Observable.zip(ob0, ob1, new Func2<T0, T1, Void>() {
                    @Override
                    public Void call(T0 t0, T1 t1) {
                        subscriber.onNext(t0, t1);
                        return null;
                    }
                }).subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Void t) {
                    }
                }));
            }
        });
    }

    public static final <T0, T1> BiObservable<T0, T1> combineLatest(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                subscriber.add(Observable.combineLatest(ob0, ob1, new Func2<T0, T1, Void>() {
                    @Override
                    public Void call(T0 t0, T1 t1) {
                        subscriber.onNext(t0, t1);
                        return null;
                    }
                }).subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Void t) {
                    }
                }));
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> product(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                subscriber.add(ob0.flatMap(new Func1<T0, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(final T0 t0) {
                        return ob1.map(new Func1<T1, Void>() {
                            @Override
                            public Void call(T1 t1) {
                                subscriber.onNext(t0, t1);
                                return null;
                            }
                        });
                    }
                }).subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Void t) {
                    }
                }));
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> sparseProduct(final Observable<? extends T0> ob0, final Func1<? super T0, Observable<T1>> func) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                subscriber.add(ob0.flatMap(new Func1<T0, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(final T0 t0) {
                        return func.call(t0).map(new Func1<T1, Void>() {
                            @Override
                            public Void call(T1 t1) {
                                subscriber.onNext(t0, t1);
                                return null;
                            }
                        });
                    }
                }).subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Void t) {
                    }
                }));
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> product(T0 i0, Observable<? extends T1> ob1) {
        return product(Observable.just(i0), ob1);
    }

    private static class Const1<S0, S1> implements Func2<S0, S1, S0> {
        @Override
        public S0 call(S0 first, S1 second) {
            return first;
        }
    };

    private static class Const2<S0, S1> implements Func2<S0, S1, S1> {
        @Override
        public S1 call(S0 first, S1 second) {
            return second;
        }
    };

    private <R0, R1> BiObservable<R0, R1> transform(final Func2<? super T0, ? super T1, ? extends R0> func0, final Func2<? super T0, ? super T1, ? extends R1> func1) {
        return lift(new DualOperator<R0, R1, T0, T1>() {
            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super R0, ? super R1> child) {
                return new DualSubscriber<T0, T1>(child) {
                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        child.onNext(func0.call(t0, t1), func1.call(t0, t1));
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

    public <R> BiObservable<? extends R, ? extends T1> mapFirst(final Func2<? super T0, ? super T1, ? extends R> func) {
        return transform(func, new Const2<T0, T1>());
    }

    public <R> BiObservable<? extends R, ? extends T1> mapFirst(final Func1<? super T0, ? extends R> func) {
        return transform(new Func2<T0, T1, R>() {
            @Override
            public R call(T0 t0, T1 t1) {
                return func.call(t0);
            }
        }, new Const2<T0, T1>());
    }

    // for TriObservable we'll need many combinations of flatten
    // <a,b,c> -> <r,a>,
    // <a,b,c> -> <r,b>,
    // <a,b,c> -> <r,c>,
    // <a,b,c> -> <r>
    //
    // Quad
    // <a,b,c,d> -> <r,a,b>
    // <a,b,c,d> -> <r,a,c>
    // <a,b,c,d> -> <r,a,d>
    // <a,b,c,d> -> <r,b,c>
    // <a,b,c,d> -> <r,b,d>
    // <a,b,c,d> -> <r,c,d>
    // <a,b,c,d> -> <r,a>
    // <a,b,c,d> -> <r,b>
    // <a,b,c,d> -> <r,c>
    // <a,b,c,d> -> <r,d>
    // <a,b,c,d> -> <r>
    public <R> Observable<? extends R> bimap(final Func2<? super T0, ? super T1, ? extends R> func) {
        return lift(new BiOperator<R, T0, T1>() {

            @Override
            public BiSubscriber<? super T0, ? super T1> call(final Subscriber<? super R> child) {
                return new BiSubscriber<T0, T1>(child) {

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
                    }
                };
            }
        });
    }

    public BiObservable<T0, T1> doOnNext(final Action2<? super T0, ? super T1> action) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child) {

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
                    }
                };
            }
        });
    }

    public BiObservable<T0, T1> doOnNextFirst(final Action1<? super T0> action) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child) {

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
                    }
                };
            }
        });
    }

    public BiObservable<T0, T1> doOnNextSecond(final Action1<? super T1> action) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> child) {
                return new DualSubscriber<T0, T1>(child) {

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
                    }
                };
            }
        });
    }

    public BiObservable<T0, T1> reduceFirst(final Func3<T0, ? super T0, ? super T1, T0> func) {
        return lift(new DualOperator<T0, T1, T0, T1>() {

            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super T0, ? super T1> subscriber) {
                final Map<T1, T0> seeds = new HashMap<T1, T0>();

                return new DualSubscriber<T0, T1>(subscriber) {
                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        T0 seed = seeds.get(t1);
                        seeds.put(t1, (seed == null) ? t0 : func.call(seed, t0, t1));
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        for (Entry<T1, T0> results : seeds.entrySet()) {
                            subscriber.onNext(results.getValue(), results.getKey());
                        }
                    }
                };
            }
        });
    }

    public <R> BiObservable<R, T1> reduceFirst(R seed, final Func3<R, ? super T0, ? super T1, R> func) {
        return lift(new DualOperator<R, T1, T0, T1>() {
            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super R, ? super T1> subscriber) {
                final Map<T1, R> seeds = new HashMap<T1, R>();

                return new DualSubscriber<T0, T1>(subscriber) {
                    @Override
                    public void onNext(T0 t0, T1 t1) {
                        R seed = seeds.get(t1);
                        seeds.put(t1, (seed == null) ? func.call(seed, t0, t1) : func.call(seed, t0, t1));
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        for (Entry<T1, R> results : seeds.entrySet()) {
                            subscriber.onNext(results.getValue(), results.getKey());
                        }
                    }
                };
            }
        });
    }

    /*
    public <R> BiObservable<R, T1> composeFirst(final Func2<Observable<T0>, T1, Observable<R>> func) {
        return lift(new DualOperator<R, T1, T0, T1>() {
            @Override
            public DualSubscriber<? super T0, ? super T1> call(final DualSubscriber<? super R, ? super T1> subscriber) {
                final Map<T1, PublishSubject<T0>> foo = new HashMap<T1, PublishSubject<T0>>();

                return new DualSubscriber<T0, T1>() {
                    @Override
                    public void onNext(T0 t0, final T1 t1) {
                        PublishSubject<T0> subject = foo.get(t1);

                        if (subject == null) {
                            subject = PublishSubject.<T0> create();
                            foo.put(t1, subject);
                            func.call(subject, t1).subscribe(new Subscriber<R>() {
                                @Override
                                public void onCompleted() {
                                    // TODO
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void onNext(R r) {
                                    subscriber.onNext(r, t1);
                                }
                            });
                        }

                        subject.onNext(t0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onComplete() {
                        // TODO Auto-generated method stub

                    }
                };
            }
        });
    }
    */

    public <R> BiObservable<T0, R> mapSecond(Func2<? super T0, ? super T1, ? extends R> func) {
        return transform(new Const1<T0, T1>(), func);
    }

    public <R> BiObservable<T0, R> mapSecond(final Func1<? super T1, ? extends R> func) {
        return transform(new Const1<T0, T1>(), new Func2<T0, T1, R>() {
            @Override
            public R call(T0 t0, T1 t1) {
                return func.call(t1);
            }
        });
    }

    private static <T0, T1, R> Func2<T1, T0, R> flip(final Func2<? super T0, ? super T1, ? extends R> func) {
        return new Func2<T1, T0, R>() {
            @Override
            public R call(T1 t1, T0 t0) {
                return func.call(t0, t1);
            }
        };
    }

    public BiObservable<? extends T1, ? extends T0> flip() {
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
