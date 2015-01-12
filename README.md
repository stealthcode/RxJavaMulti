# RxJavaMulti
Implementation of multi-valued observables supporting more than one value passed to 
subscribers via onNext. This library includes operators and static methods to convert to 
and from the various observable arities including the mono-observable as defined in the 
core RxJava library. 

When a `DyadObservable` is subscribed to by a `DyadSubscriber` the `onNext(T0, T1)` 
method will be called passing a pair of elements.  

## Creating a DyadObservable

### Generate
Emits dyads of an `Integer` and its `String` representation. The DyadObservable's 
behavior upon subscription is to subscribe to the source mono-observable once to 
generate the string representation. 

```java
Observable<Integer> intRange = Observable.range(0,99);
DyadObservable<Integer, String> pairs = DyadObservable.generate(intRange, (Integer i) -> {return i.toString()});
```

### Attach
Emits dyads of an `Integer` and a `java.io.File` where all dyad's second element are 
`==`.

```java
Observable<Integer> intRange = Observable.range(0,99);
DyadObservable<Integer, File> withFile = DyadObservable.attach(intRange, new File("log.txt"));
```

### Product
Emits dyads of a `Movie` and a `Language`. There will be 1 dyad for each movie and 
language combination.

```java
Observable<Movie> movies = movieService.getMovies();
Observable<Language> langs = geo.getAllLanguages();
DyadObservable<Movie, Language> pairs = DyadObservable.product(movies, langs);
```
### Sparse Product
Emits dyads of a `Movie` and a `Language`. Each movie will have one dyad for each 
Language emitted by the `Observable<Language>` returned by the generator function 
provided.

```java
Observable<Movie> movies = movieService.getMovies();
DyadObservable<Movie, Language> pairs = DyadObservable.sparseProduct(movies, (Movie m) -> { 
  List<Languages> langs = subtitleService.getLanguagesForMovie(m);
  return from(langs);
});
```

## Usage

### Filtering
A filter predicate can be written for the first, second, or both elements of a dyad. The 
following example filters all dyads but one based on the second element.

```java
DyadObservable.generate(Observable.range(0,100), (Integer i) -> { return i == 42 ? “yep” : “nope”; })
    .filter2((Boolean isAnswer) -> { return isAnswer.equals(“yep”); });
```

### Mono-Mapping
Dyads can be mapped back to a single valued `Observable` using the `bimap` operator.

```java
Observable<Integer> nums = Observable.range(0,99);
Observable<Integer> factorsOf3 = nums.map((Integer i) -> {return i * 3;});
Observable<Integer> factorsOf5 = DyadObservable.attach(nums, 5).biMap((Integer i, Integer factor) -> {return i * factor;});
```

### Dyadic-Mapping
You can replace a single element of a dyad at a time. The `map1(Func1)` and 
`map1(Func2)` overloads replace the first element of the dyads, while the `map2(Func1)` 
and `map2(Func2)` replace the second.

```java
DyadObservable<String, MyMovieService> pair = DyadObservable.attach(getAllMovies(), MyMovieService())
    .map1((Movie m, MyMovieService service) -> { return service.getSynopsis(m); });
```

### Reducing
The `reduce1` and `reduce2` operators will replace the first 
