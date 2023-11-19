# Reference

## [1] Strong Reference
- ```java
        Object strongRef = new Object();
    ```
- 기본적인 참조 유형
- **객체의 참조가 유지되는 한, GC의 회수 대상이 되지 않는다.**

## [2] Soft Reference
```java
SoftReference<Object> softRef = new SoftReference<>();
```
- GC에 의해서 회수 대상이 되지만, 메모리가 부족하지 않을 경우에는 회수되지 않는다.
  - 캐시를 사용할 때 주로 구현한다.
- 다시 접근 할 때는 null이 발생 할 수 있고, 재생성을 통해서 접근 가능해진다.

## [3] Weak Reference
```java
WeakReference<Object> weakRef = new WeakReference<>();
```
- GC의 다음주기에 회수대상이 될 수 있다.
  - 주로 Listener나 Callback에 사용된다.
- 다시 접근 할 때는 null이 발생 할 수 있고, 재생성을 통해서 접근 가능해진다.

## [4] Phantom Reference
```java
PhantomReference<Object> phantomReference = new PhantomReference<>(new Object(), referenceQueue);
```
- GC가 객체를 회수 하기 전에, 수행할 정리작업을 위해 사용되는 참조이다.
- 직접적인 참조는 제공하지 않으며, ReferenceQueue에서 알림을 받을 수 있다.

