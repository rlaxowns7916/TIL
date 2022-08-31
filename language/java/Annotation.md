# Annotation

Spring 등에서 많이 쓰인다.

기본적으로는 주석과 동일하다. (더 많은기능을 포함하고있긴 하지만)

Reflection과 같이 이용되는 경우가 많다.

### 기본적인 선언형태

```java
public @interface MyAnnotation {
}
```

### Annotation 선언에 포함 할 수 있는 Annotation

1. @Inherit
- Annotation끼리도 상속이 가능하다.
- SubClass들도 SuperClass가 갖는 Annotation을 상속하게 한다.


2. @Retention
- 에노테이션의 유지시점을 결정 
  - 소스코드,클래스파일,런타임
- 주로 런타임을 선택한다.
  - Reflection을 통한 참조목적

3. @Target
- 에노테이션의 적용대상 결정
  - 필드,메소드,클래스 등등

```java
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String name() default "hello";

    int age() default 10;
}
```

해당 에노테이션의 정보는 subclass에게 유지되며,

type에 선언이 가능하며,

Runtime까지 정보가 유지된다.

Annotation에 지정을 안해도 default값이 지정된다.

### 에노테이션을 이용한 간단한 DI만들기

1. DI를 표현할 에노테이션

    ```java
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.FIELD})
    public @interface MyAutowired {
    }
    ```

2. 주입 받을 객체

    ```java
    public class DiService {
    
        @MyAutowired
        private DiRepository diRepository;
    
        private int id;
        private int password;
    
        public DiRepository getDiRepository() {
            return this.diRepository;
        }
    
    }
    ```

    ```java
    public class DiRepository {
        int a = 10;
    }
    ```

3. DI Container

    ```java
    public class MyDiApplicationContext {
    
        public static <T> T getObject(Class<T> clazz) {
            try {
                T instance = clazz.getConstructor().newInstance();
                Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                    if (field.getAnnotation(MyAutowired.class) != null) {
                        Object newInstance = getObject(field.getType());
                        field.setAccessible(true);
                        try {
                            field.set(instance, newInstance);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
    ```

    1. 우선 필드에서 에노테이션이 붙은걸 찾는다(private인 경우존재 —> getDeclaredFields)
    2. private접근제어자일 수 있기 때문에 setAccessible(true)
    3. 해당 instance에 생성한 객체를 set해준다.

   잘못 사용하면 성능 이슈가 발생할 수 있다.

   (런타임 에러, 자주 사용할 경우)