# RxJavaMulti
Implementation of multi-valued observables supporting more than one value passed to 
subscribers via onNext. This library includes operators and static methods to 
convert to and from the various observable arities including the mono-observable as 
defined in the core RxJava library. 

When a `DyadObservable` is subscribed to by a `DyadSubscriber` the `onNext(T0, T1)` 
method will be called passing a pair of elements.  

## Creating a DyadObservable

### Generate
Emmits dyads of an `Integer` and its `String` representation. The DyadObservable's 
behavior upon subscription is to subscribe to the source mono-observable once to 
generate the string representation. 

```java
Observable<Integer> intRange = Observable.range(0,99);
DyadObservable<Integer, String> pairs = DyadObservable.generate(intRange, (Integer i) -> {return i.toString()});
```

### Attach
Emmits dyads of an `Integer` and a `java.io.File` where all dyad's second element 
are `==`.

```java
Observable<Integer> intRange = Observable.range(0,99);
DyadObservable<Integer, File> withFile = DyadObservable.attach(intRange, new File("log.txt"));
```

### Product
Emmits dyads of a `Movie` and a `Language`. There will be 1 dyad for each movie 
and language combination.

```java
Observable<Movie> movies = movieService.getMovies();
Observable<Language> langs = geo.getAllLanguages();
DyadObservable<Movie, Language> pairs = DyadObservable.product(movies, langs);
```
### Sparse Product
Emmits dyads of a `Movie` and a `Language`. Each movie will have one dyad for each 
Language emmitted by the `Observable<Language>` returned by the generator function 
provided.

```java
Observable<Movie> movies = movieService.getMovies();
DyadObservable<Movie, Language> pairs = DyadObservable.sparseProduct(movies, (Movie m) -> { 
  List<Languages> langs = subtitleService.getLanguagesForMovie(m);
  return from(langs);
});
```
