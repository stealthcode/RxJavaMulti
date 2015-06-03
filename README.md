# RxJavaMulti
Implementation of multi-valued observables supporting more than one value passed to 
subscribers via onNext. This library includes operators and static methods to convert to 
and from the various observable arities including the mono-observable as defined in the 
core RxJava library. 

When a `BiObservable` is subscribed to by a `BiSubscriber` the `onNext(T0, T1)` 
method will be called passing a pair of elements.  

## Creating a BiObservable

### Generate
Emits bi-values of an `Integer` and its `String` representation. The BiObservable's 
behavior upon subscription is to subscribe to the source mono-observable once to 
generate the string representation. 

```java
Observable<Integer> intRange = Observable.range(0,99);
BiObservable<Integer, String> pairs = BiObservable.generate(intRange, (Integer i) -> {return i.toString()});
```

### Attach
Emits bi-values of an `Integer` and a `java.io.File` where all bi-value's second element are 
`==`.

```java
Observable<Integer> intRange = Observable.range(0,99);
BiObservable<Integer, File> withFile = BiObservable.attach(intRange, new File("log.txt"));
```

### Product
Emits bi-values of a `Movie` and a `Language`. There will be 1 bi-value for each movie and 
language combination.

```java
Observable<Movie> movies = movieService.getMovies();
Observable<Language> langs = geo.getAllLanguages();
BiObservable<Movie, Language> pairs = BiObservable.product(movies, langs);
```
### Sparse Product
Emits bi-values of a `Movie` and a `Language`. Each movie will have one bi-value for each 
Language emitted by the `Observable<Language>` returned by the generator function 
provided.

```java
Observable<Movie> movies = movieService.getMovies();
BiObservable<Movie, Language> pairs = BiObservable.sparseProduct(movies, (Movie m) -> { 
  List<Languages> langs = subtitleService.getLanguagesForMovie(m);
  return from(langs);
});
```

## Usage

### Filtering
A filter predicate can be written for the first, second, or both elements of a bi-value. The 
following example filters all bi-values but one based on the second element.

```java
BiObservable.generate(Observable.range(0,100), (Integer i) -> { return i == 42 ? “yep” : “nope”; })
    .filter2((Boolean isAnswer) -> { return isAnswer.equals(“yep”); });
```

### Mono-Mapping
Bi-values can be mapped back to a single valued `Observable` using the `bimap` operator.

```java
Observable<Integer> nums = Observable.range(0,99);
Observable<Integer> factorsOf3 = nums.map((Integer i) -> {return i * 3;});
Observable<Integer> factorsOf5 = BiObservable.attach(nums, 5).biMap((Integer i, Integer factor) -> {return i * factor;});
```

### Bi-Mapping
You can replace a single element of a bi-value at a time. The `map1(Func1)` and 
`map1(Func2)` overloads replace the first element of the bi-values, while the `map2(Func1)` 
and `map2(Func2)` replace the second.

```java
BiObservable<String, MyMovieService> pair = BiObservable.attach(getAllMovies(), MyMovieService())
    .map1((Movie m, MyMovieService service) -> { return service.getSynopsis(m); });
```

### Scanning
The `scan1` and `scan2` operators will replace the first or second element of a bi-value 
respectively with the result of the provided accumulator function. This will preserve 
the second element for each bi-value. Note that scanning over a bi-value cannot emit the 
provided seed value as the pre-computation onNext because there is no second element of 
the bi-value to emit.
