package rx;

import rx.Observable.OnSubscribe;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.internal.operators.bi.OperatorBiToMonoMap;
import rx.internal.operators.bi.OperatorDoOnNext;
import rx.internal.operators.bi.OperatorFlip;
import rx.internal.operators.bi.OperatorGenerate;
import rx.internal.operators.bi.OperatorBiMap;
import rx.internal.operators.bi.OperatorScan;
import rx.internal.operators.bi.OperatorTakeLast;
import rx.operators.BiOperator;
import rx.operators.BiToSingleOperator;
import rx.operators.SingleToBiOperator;

public class BiObservable<T0, T1> {
    private BiOnSubscribe<T0, T1> onSubscribeFunc;

    /**
     * An action used by a {@link BiObservable} to produce data to be consumed by a downstream
     * {@link BiSubscriber}.
     *
     * @param <T0>
     *            type of first argument
     * @param <T1>
     *            type of second argument
     */
    public static interface BiOnSubscribe<T0, T1> extends Action1<BiSubscriber<? super T0, ? super T1>> {
        @Override
        public void call(BiSubscriber<? super T0, ? super T1> subscriber);
    }

    /**
     * @param onSubscribeFunc
     */
    private BiObservable(BiOnSubscribe<T0, T1> onSubscribeFunc) {
        this.onSubscribeFunc = onSubscribeFunc;
    }

    /**
     * @param onSubscribe
     * @return a {@link BiObservable} wrapping the given {@link BiOnSubscribe} action.
     */
    public static <T0, T1> BiObservable<T0, T1> create(BiOnSubscribe<T0, T1> onSubscribe) {
        return new BiObservable<T0, T1>(onSubscribe);
    }

    /**
     * @param subscriber
     */
    public void subcribe(BiSubscriber<? super T0, ? super T1> subscriber) {
        onSubscribeFunc.call(subscriber);
    }

    /**
     * Create a new DyadObservable that defers the subscription of {@code this} with a
     * {@link BiSubscriber subscriber} that applies the given operator's effect to values produced
     * when subscribed to.
     * 
     * @param biOperator
     *            a function to adapt the types and semantics of the downstream operator.
     * @return a new {@link BiObservable} with a {@link BiOnSubscribe onSubscribeFunc} that
     *         subscribes to {@code this}.
     * @see BiObservable#lift(BiToSingleOperator)
     */
    public <R0, R1> BiObservable<R0, R1> lift(final BiOperator<? extends R0, ? extends R1, ? super T0, ? super T1> biOperator) {
        return new BiObservable<R0, R1>(new BiOnSubscribe<R0, R1>() {
            @Override
            public void call(BiSubscriber<? super R0, ? super R1> child) {
                onSubscribeFunc.call(biOperator.call(child));
            }
        });
    }

    /**
     * Create a new {@link Observable} that defers the subscription of {@code this} with a
     * {@link BiSubscriber subscriber} that applies the given operator's effect to values produced
     * when subscribed to. This overload of {@code lift} converts a DyadObservable to a
     * single-valued Observable.
     * 
     * @param operator
     *            a function to adapt the types and semantics of the downstream operator.
     * @return a new {@link BiObservable} with a {@link BiOnSubscribe onSubscribeFunc} that
     *         subscribes to {@code this}.
     */
    public <R> Observable<R> lift(final BiToSingleOperator<? extends R, ? super T0, ? super T1> operator) {
        return Observable.create(new OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> child) {
                onSubscribeFunc.call(operator.call(child));
            }
        });
    }

    /**
     * Create a new {@link Observable} that defers the subscription of {@code obs} with a
     * {@link Subscriber subscriber} that applies the given operator's effect to values produced
     * when subscribed to. This overload of {@code lift} converts a single-valued Observable to a
     * two-valued {@link BiObservable}.
     * 
     * @param obs
     *            the producer subscribed to.
     * @param op
     *            a function to adapt the types and semantics of the downstream operator to
     *            {@code obs}.
     * @return
     */
    public static <R0, R1, T> BiObservable<R0, R1> lift(Observable<? extends T> obs, SingleToBiOperator<R0, R1, T> op) {
        return new BiObservable<R0, R1>(new BiOnSubscribe<R0, R1>() {
            @Override
            public void call(BiSubscriber<? super R0, ? super R1> subscriber) {
                obs.unsafeSubscribe(op.call(subscriber));
            }
        });
    }

    /**
     * Converts an Observable to a BiObservable by applying a function to generate a second value
     * based on the values produced by the {@code observable}.
     * 
     * @param observable
     *            the producer
     * @param generatorFunc
     *            ran once per call made to onNext to produce the paired BiObservable's second
     *            value
     * @return a BiObservable encapsulating the subscription to the given {@code observable}
     */
    public static <T0, T1> BiObservable<T0, T1> generate(final Observable<? extends T0> observable, final Func1<? super T0, ? extends T1> generatorFunc) {
        return BiObservable.lift(observable, new OperatorGenerate<T0, T1>(generatorFunc));
    }

    /**
     * Creates a BiObservable by zipping two observables. Each value produced by the returned
     * BiObservable is the pair of each value emitted by the given observables.
     * 
     * @param ob0
     *            the first observable
     * @param ob1
     *            the second observable
     * @return a BiObservable encapsulating the subscription to both observables
     */
    public static <T0, T1> BiObservable<T0, T1> zip(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final BiSubscriber<? super T0, ? super T1> child) {
                child.add(Observable.zip(ob0, ob1, new Func2<T0, T1, Void>() {
                    @Override
                    public Void call(T0 t0, T1 t1) {
                        child.onNext(t0, t1);
                        return null;
                    }
                }).subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        child.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onNext(Void t) {
                    }
                }));
            }
        });
    }

    /**
     * @param ob0
     * @param ob1
     * @return
     */
    public static final <T0, T1> BiObservable<T0, T1> combineLatest(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final BiSubscriber<? super T0, ? super T1> child) {
                child.add(Observable
                        .combineLatest(ob0, ob1, new Func2<T0, T1, Void>() {
                            @Override
                            public Void call(T0 t0, T1 t1) {
                                child.onNext(t0, t1);
                                return null;
                            }
                        }).subscribe(new Observer<Void>() {
                            @Override
                            public void onCompleted() {
                                child.onComplete();
                            }

                            @Override
                            public void onError(Throwable e) {
                                child.onError(e);
                            }

                            @Override
                            public void onNext(Void t) {
                            }
                        }));
            }
        });
    }

    /**
     * Creates a BiObservable that emits one pair of elements for each combination of the elements
     * emitted by the observables passed as parameters. This produces the Cartesian product of all
     * emitted elements from two observables.
     * 
     * @param ob0
     * @param ob1
     * @return a BiObservable that produces the Cartesian product of ob0 and ob1
     */
    public static <T0, T1> BiObservable<T0, T1> product(final Observable<? extends T0> ob0, final Observable<? extends T1> ob1) {
        if (ob1 == Observable.empty() || ob0 == Observable.empty())
            return BiObservable.empty();
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final BiSubscriber<? super T0, ? super T1> child) {
                child.add(ob0.flatMap(new Func1<T0, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(final T0 t0) {
                        return ob1.map(new Func1<T1, Void>() {
                            @Override
                            public Void call(T1 t1) {
                                child.onNext(t0, t1);
                                return null;
                            }
                        });
                    }
                }).subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        child.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onNext(Void t) {
                    }
                }));
            }
        });
    }

    public static <T0, T1> BiObservable<T0, T1> empty() {
        return BiObservable.create(new BiOnSubscribe<T0, T1>(){
            @Override
            public void call(BiSubscriber<? super T0, ? super T1> subscriber) {
                subscriber.onComplete();
            }});
    }

    /**
     * Creates a BiObservable from an observable and a non-deterministic-arity generator function.
     * This emits a pair of each generatorFunc's output element with the input it was obtained from.
     * Note that if the generatorFunc produces an "empty" observable then no pairs will be emitted
     * for that input element.
     * 
     * @param ob0
     * @param generatorFunc
     * @return a BiObservable that
     */
    public static <T0, T1> BiObservable<T0, T1> sparseProduct(final Observable<? extends T0> ob0, final Func1<? super T0, Observable<T1>> generatorFunc) {
        return create(new BiOnSubscribe<T0, T1>() {
            @Override
            public void call(final BiSubscriber<? super T0, ? super T1> subscriber) {
                subscriber.add(ob0.flatMap(new Func1<T0, Observable<T1>>() {
                    @Override
                    public Observable<T1> call(final T0 t0) {
                        return generatorFunc.call(t0)
                                .doOnNext(new Action1<T1>() {
                                    @Override
                                    public void call(T1 t1) {
                                        subscriber.onNext(t0, t1);
                                    }
                                });
                    }
                }).subscribe(new Observer<T1>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(T1 t) {
                    }
                }));
            }
        });
    }

    /**
     * Attaches an instance as the paired item to each element emitted from the observable.
     * 
     * @param i0
     * @param ob1
     * @return
     */
    public static <T0, T1> BiObservable<T0, T1> attach(T0 i0, Observable<? extends T1> ob1) {
        return product(Observable.just(i0), ob1);
    }

    /**
     * Attaches an instance as the paired item to each element emitted from the observable.
     * 
     * @param ob0
     * @param i1
     * @return
     */
    public static <T0, T1> BiObservable<T0, T1> attach(Observable<? extends T0> ob0, T1 i1) {
        return product(ob0, Observable.just(i1));
    }

    /**
     * Returns a BiObservable that applies a specified function to each pair of items emitted by
     * the source BiObservable and emits the results of these function applications, replacing the
     * second item with the results. This overload accepts a Func2 that will receive both items
     * emitted by the BiObservable as arguments.
     * 
     * @param func
     *            the function used to produce the new value.
     * @return a BiObservable which transforms the first item emitted using the specified
     *         function.
     */
    public <R> BiObservable<? extends R, ? extends T1> map1(final Func2<? super T0, ? super T1, ? extends R> func) {
        return lift(OperatorBiMap.bi1Operator(func));
    }

    /**
     * Returns a DyadObservable that applies a specified function to each pair of items emitted by
     * the source DyadObservable and emits the results of these function applications, replacing the
     * second item with the results. This overload accepts a Func1 that will receive the first item
     * emitted by the DyadObservable as an argument.
     * 
     * @param func
     *            the function used to produce the new value.
     * @return a DyadObservable which transforms the first item emitted using the specified
     *         function.
     */
    public <R> BiObservable<? extends R, ? extends T1> map1(final Func1<? super T0, ? extends R> func) {
        return lift(OperatorBiMap.mono1Operator(func));
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
    /**
     * Returns a BiObservable that applies a specified function to each pair of items emitted by
     * the source BiObservable and emits the results of these function applications, replacing the
     * emitted values with the result from the specified function.
     * 
     * @param func
     *            the function used to produce the new value.
     * @return an Observable which transforms the pair of items emitted using the specified
     *         function.
     */
    public <R> Observable<R> bimap(final Func2<? super T0, ? super T1, R> func) {
        return lift(new OperatorBiToMonoMap<R, T0, T1>(func));
    }

    /**
     * @param action
     * @return
     */
    public BiObservable<T0, T1> doOnNext(final Action2<? super T0, ? super T1> action) {
        return lift(OperatorDoOnNext.biOperator(action));
    }

    /**
     * @param action
     * @return
     */
    public BiObservable<T0, T1> doOnNext1(final Action1<? super T0> action) {
        return lift(OperatorDoOnNext.mono1Operator(action));
    }

    /**
     * @param action
     * @return
     */
    public BiObservable<T0, T1> doOnNext2(final Action1<? super T1> action) {
        return lift(OperatorDoOnNext.mono2Operator(action));
    }

    /**
     * @param seed
     * @param func
     * @return
     */
    public <R> BiObservable<R, T1> scan1(R seed, final Func3<R, ? super T0, ? super T1, R> func) {
        return lift(OperatorScan.bi1Operator(seed, func));
    }

    /**
     * @param func
     * @return
     */
    public BiObservable<T0, T1> scan1(final Func3<T0, ? super T0, ? super T1, T0> func) {
        return lift(OperatorScan.bi1Operator(func));
    }

    /**
     * @param seed
     * @param func
     * @return
     */
    public <R> BiObservable<T0, R> scan2(R seed, final Func3<R, ? super T0, ? super T1, R> func) {
        return lift(OperatorScan.bi2Operator(seed, func));
    }

    /**
     * @param func
     * @return
     */
    public BiObservable<T0, T1> scan2(final Func3<T1, ? super T0, ? super T1, T1> func) {
        return lift(OperatorScan.bi2Operator(func));
    }

    /**
     * @return
     */
    public BiObservable<T0, T1> takeLast() {
        return lift(new OperatorTakeLast<T0, T1>());
    }

    /**
     * @param seed
     * @param func
     * @return
     */
    public <R> BiObservable<R, T1> reduce1(R seed, final Func3<R, ? super T0, ? super T1, R> func) {
        return scan1(seed, func).takeLast();
    }

    /**
     * @param func
     * @return
     */
    public BiObservable<T0, T1> reduce1(final Func3<T0, ? super T0, ? super T1, T0> func) {
        return scan1(func).takeLast();
    }

    /**
     * @param seed
     * @param func
     * @return
     */
    public <R> BiObservable<T0, R> reduce2(R seed, final Func3<R, ? super T0, ? super T1, R> func) {
        return scan2(seed, func).takeLast();
    }

    /**
     * @param func
     * @return
     */
    public BiObservable<T0, T1> reduce2(final Func3<T1, ? super T0, ? super T1, T1> func) {
        return scan2(func).takeLast();
    }

    /**
     * Returns a BiObservable that applies a specified function to each pair of items emitted by
     * the source BiObservable and emits the results of these function applications, replacing the
     * second item with the results. This overload accepts a Func2 that will receive both items
     * emitted by the BiObservable as arguments.
     * 
     * @param func
     *            the function used to produce the new value.
     * @return a BiObservable which transforms the second item emitted using the specified
     *         function.
     */
    public <R> BiObservable<T0, R> map2(Func2<? super T0, ? super T1, ? extends R> func) {
        return lift(OperatorBiMap.bi2Operator(func));
    }

    /**
     * Returns a BiObservable that applies a specified function to each pair of items emitted by
     * the source BiObservable and emits the results of these function applications, replacing the
     * second item with the results. This overload accepts a Func1 that will receive the second item
     * emitted by the BiObservable as an argument.
     * 
     * @param func
     *            the function used to produce the new value.
     * @return a BiObservable which transforms the second item emitted using the specified
     *         function.
     */
    public <R> BiObservable<T0, R> map2(final Func1<? super T1, ? extends R> func) {
        return lift(OperatorBiMap.mono2Operator(func));
    }

    /**
     * @param func
     * @return
     */
    public static <T0, T1, R> Func2<T1, T0, R> flip(final Func2<? super T0, ? super T1, ? extends R> func) {
        return new Func2<T1, T0, R>() {
            @Override
            public R call(T1 t1, T0 t0) {
                return func.call(t0, t1);
            }
        };
    }

    /**
     * @return
     */
    public BiObservable<? extends T1, ? extends T0> flip() {
        return lift(new OperatorFlip<T0, T1>());
    }

}
