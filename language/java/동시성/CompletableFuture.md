# CompletableFuture
- java8에 도입
- Future의 단점을 보완하기 위해 도입되었다.
  - nonBlokcing (Future는 get()을 통해 결과를 받았어야 하기 떄문) 
  - Chaining
  - 예외처리
  - 후속 작업 처리
  - 풍부한 조합 메소드
- Callback Hell을 방지하기 위해 도입되었다.
- 기본적으로 ForkjoinPool.commonPool()을 사용한다.
  - 사용자 Custom Pool을 사용할 수 있다.

## 메소드

### [1] supplyAsync
- 작업을 비동기로 실행하고, 결과를 반환
```java
CompletableFuture.supplyAsync(() -> {
  return 1;
}, executor);
```

### [2] thenApply
- map()과 유사하다.
- CompletableFuture의 값을 받아 가공하여, 새로운 CompletableFuture를 반환한다.
  - Immutable
  - Chaining 형태로 작업 가능
```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    return 42; 
});

CompletableFuture<String> resultFuture = future.thenApply(result -> "결과는: " + result);

resultFuture.thenAccept(System.out::println); 
```

### [3] thenAccept
- Consumer를 인자로 받아, 결과를 소비한다.
- 리턴값이 없다.
```java
 CompletableFuture.supplyAsync(() -> {
            return "Hello, World!";
        }).thenAccept(result -> {
            System.out.println("결과: " + result);
        });
```

### [4] exceptionally
- Exception 처리 로직이다.
- 기본 값을 반환하거나, 후처리 작업을 수행한다.
- exceptionally()에서 예외처리가 수행되면, 예외는 그 이후 Chain에 전달되지 않는다.

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    if (true) {
        throw new RuntimeException("예외 발생!");
    }
    return 42;
}).exceptionally(ex -> {
    System.err.println("예외 처리: " + ex.getMessage());
    return 0;
});

Integer result = future.join();
System.out.println("최종 결과: " + result);

// 0
```

### [5] join
- Caller가 Blocking되면서 값을 대기한다.
- UncheckedException을 리턴한다. (get()과의 차이점)

### [6] whenComplete
- 일종의 Consumer
  - 리턴 값이 없다.
  - 결과를 수정하지 않는다.
- 정상적인 결과와 예외 결과를 종합적으로 처리한다. (어쨋든 실행)
- Parameter로 BiConsumer<? super T, ? super Throwable>을 받는다.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello, Async!");

        future.whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                System.out.println("Error: " + throwable.getMessage());
            } else {
                System.out.println("Result: " + result);
            }
        });
```

### [7] whenCompleteAsync
- whenComplete의 후속작업을 다른 ThreadPool에서 실행하게한다.
  - 작업의 성공 실패 여부에 상관없이 수행된다.
- Executor를 인자로 받는다. 
  - 기본인자는 ForkJoinPool.commonPool()

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello, Async!");

        future.whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                System.out.println("Error: " + throwable.getMessage());
            } else {
                System.out.println("Result: " + result);
            }
        }, executor);
```

### [8] handle
- BiFunction<? super T, Throwable, ? extends U> fn 을 인자로 받는다.
- 성공/실패 여부와 관계없이 결과(result) 또는 예외(Throwable)를 처리하여 새로운 결과를 반환한다.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
  if (Math.random() > 0.5) {
    return "Success!";
  } else {
    throw new RuntimeException("Failure!");
  }
});

CompletableFuture<String> handledFuture = future.handle((result, throwable) -> {
  if (throwable != null) {
    return "Fallback Value"; // 실패 시 반환
  } else {
    return result + " Processed"; // 성공 시 결과 변환
  }
});

handledFuture.thenAccept(System.out::println);
```


### [9] allOf
- 모든 CompletableFuture가 완료되면, 결과를 반환한다.
- 결과는 CompletableFuture<Void>로 반환된다.
  - 작업이 완료되었는지에만 관심있기 때문이다.
  - 개별 작업의 결과는 가져오지 않는다
```java
CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);
allFutures.thenRun(() -> System.out.println("All tasks completed!"));
```