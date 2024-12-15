# EnableAnnotation
- 단순히 선언하는 것 만으로도, 자동 설정을 하게해주는 Annotation들
  - @EnableWebMvc
  - @EnableFeignClients
  - @EnableJpaRepositories
  - ...
```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({DelegatingWebMvcConfiguration.class})
public @interface EnableWebMvc {
}
```


## @Import
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
  /**
   * @Configuration, ImportSelector, ImportBeanDefinitionRegistrar, or regular component classes to import.
   * @return
   */
  Class<?>[] value();
}
```
- @Import에 해당하는 Class를 설정에 이용하겠다는 의미이다.
- value로 넘어온 Class들을 ApplicationContext에 등록하게 하는 역할을한다.
  - @EnableWebMvc의 경우 DelegatingWebMvcConfiguration.class를 Import하게 되어, WebMvcConfigurationSupport를 상속받는다.
- @Configuration Class와 같이 있어야한다.
  - @Configuration이 @Import에 있는 Class들을 해석하여 추가적으로 등록하게 하는 트리거 역할을 한다.

### 일반 Class
- @Configuration으로 취급
- 해당 설정 내부에 있는 Bean들이 등록된다.

### ImportBeanDefinitionRegistrar
- 동적으로 Bean을 직접 등록한다. (registerBeanDefinitions())
```java
public interface ImportBeanDefinitionRegistrar {

	/**
	 * Register bean definitions as necessary based on the given annotation metadata of
	 * the importing {@code @Configuration} class.
	 * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
	 * registered here, due to lifecycle constraints related to {@code @Configuration}
	 * class processing.
	 * <p>The default implementation delegates to
	 * {@link #registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)}.
	 * @param importingClassMetadata annotation metadata of the importing class
	 * @param registry current bean definition registry
	 * @param importBeanNameGenerator the bean name generator strategy for imported beans:
	 * {@link ConfigurationClassPostProcessor#IMPORT_BEAN_NAME_GENERATOR} by default, or a
	 * user-provided one if {@link ConfigurationClassPostProcessor#setBeanNameGenerator}
	 * has been set. In the latter case, the passed-in strategy will be the same used for
	 * component scanning in the containing application context (otherwise, the default
	 * component-scan naming strategy is {@link AnnotationBeanNameGenerator#INSTANCE}).
	 * @since 5.2
	 * @see ConfigurationClassPostProcessor#IMPORT_BEAN_NAME_GENERATOR
	 * @see ConfigurationClassPostProcessor#setBeanNameGenerator
	 */
	default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
			BeanNameGenerator importBeanNameGenerator) {

		registerBeanDefinitions(importingClassMetadata, registry);
	}

	/**
	 * Register bean definitions as necessary based on the given annotation metadata of
	 * the importing {@code @Configuration} class.
	 * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
	 * registered here, due to lifecycle constraints related to {@code @Configuration}
	 * class processing.
	 * <p>The default implementation is empty.
	 * @param importingClassMetadata annotation metadata of the importing class
	 * @param registry current bean definition registry
	 */
	default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
	}
```

### ImportSelector
- Bean으로 등록될 Class의 이름을 리턴한다. (selectImports 메소드)
```java
public interface ImportSelector {

	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 * @return the class names, or an empty array if none
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

	/**
	 * Return a predicate for excluding classes from the import candidates, to be
	 * transitively applied to all classes found through this selector's imports.
	 * <p>If this predicate returns {@code true} for a given fully-qualified
	 * class name, said class will not be considered as an imported configuration
	 * class, bypassing class file loading as well as metadata introspection.
	 * @return the filter predicate for fully-qualified candidate class names
	 * of transitively imported configuration classes, or {@code null} if none
	 * @since 5.2.4
	 */
	@Nullable
	default Predicate<String> getExclusionFilter() {
		return null;
	}
}
```

## 누가 @Import를 읽는가?
1. SpringContext 초기화 시, BeanFactoryPostProcessor들을 실행한다.
2. BeanFactoryPostProcessor 중 ConfigurationClassPostProcessor를 실행한다.
3. ConfigurationClassPostProcessor의 postProcessBeanDefinitionRegistry()메소드가 수행
   - ConfigurationClassPostProcessor < BeanDefinitionRegistryPostProcessor < BeanFactoryPostProcessor
4. ConfigurationClassParser를 통해 @Configuration, @Import, @ComponentScan을 읽는다.

