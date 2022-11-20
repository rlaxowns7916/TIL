# Validation
- 올바르지 않은 Data를 검증하기 위한 방법이다.
- 2.3버전 부터는 Spring-Web에 있던 Validation의존성이 분리되었다.
```groovy
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

## @Valid vs @Validated
- @Valid: Java에서 제공해주는 Annotation
- @Validated: Spring에서 제공해주는 Annotation
  - @Valid의 기능을 기본적으로 가지고있다.
  - 유효성을 검증할 그룹을 지정할 수 있다.


## 제공 Annotation
- @Null  // null만 허용
- @NotNull  // null을 허용X "", " "는 허용
- @NotEmpty  // null, ""을 허용하지 않습니다. " "는 허용
- @NotBlank  // null, "", " " 모두 허용X

- @Email  // 이메일 형식 검사.(""의 경우 통과)
- @Pattern(regexp = )  // 정규식을 검사할 때 사용
- @Size(min=, max=)  // 길이를 제한할 때 사용

- @Max(value = )  // value 이하의 값을 받을 때 사용
- @Min(value = )  // value 이상의 값을 받을 때 사용

- @Positive  // 값을 양수로 제한
- @PositiveOrZero  // 값을 양수와 0만 가능하도록 제한

- @Negative  // 값을 음수로 제한
- @NegativeOrZero  // 값을 음수와 0만 가능하도록 제한

- @Future  // 현재보다 미래
- @FutureOrPresent  // 현재이거나 미래
- @Past  // 현재보다 과거
- @PastOrPresent  // 현재거나 과거

- @AssertFalse  // false 여부, null은 체크(X)
- @AssertTrue  // true 여부, null은 체크(X)

## Custom Validation
```java
@Constraint(validatedBy = NoSpecialCharacterValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface CustomValidation {
    String message() default "정책 위반";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

```java
public class CustomValidator implements ConstraintValidator<CustomValidation, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            return false; //false 이면 MethodArgumentNotValidException 발생
        }
        return value.startsWith("Bearer ");
    }
}
```
