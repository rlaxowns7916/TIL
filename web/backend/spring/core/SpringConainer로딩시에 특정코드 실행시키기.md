# ApplicationRunner
- Spring에서 정의해준 Model을 인자로 받는다.
- Argument의 Key, Value에 접근 가능하다.
```java
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationRunner가 실행됩니다.");

        for (String name : args.getOptionNames()) {
            System.out.println("arg name: " + name);
            System.out.println("arg value: " + args.getOptionValues(name));
        }
    }
}
```

# CommandLineRunner
- Spring Conainer 로딩 이후 실행 할 작업들을 정의한다.
- Spring 실행시, Argument로 필요한 인자를 받을 때 사용한다.
```java
@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunner가 실행됩니다.");
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}
```

## 복수개를 정의한다면?
- 실행순서가 보장되지 않는다.
- 실행순서를 보장하려면 @Order 에노테이션을 사용해야 한다.

