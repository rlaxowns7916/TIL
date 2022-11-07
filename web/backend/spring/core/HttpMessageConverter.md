# HttpMessageConverter
- Spring에서 제공하는 인터페이스
- HttpRequest 혹은 Http Response 시점의 객체를 직렬화/역직렬화 하는데 사용한다.
- 다양한 MessageConverter가 존재한다.

```java
public interface HttpMessageConverter<T> {

	/**
	 * Indicates whether the given class can be read by this converter.
	 * @param clazz the class to test for readability
	 * @param mediaType the media type to read (can be {@code null} if not specified);
	 * typically the value of a {@code Content-Type} header.
	 * @return {@code true} if readable; {@code false} otherwise
	 */
	boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);

	/**
	 * Indicates whether the given class can be written by this converter.
	 * @param clazz the class to test for writability
	 * @param mediaType the media type to write (can be {@code null} if not specified);
	 * typically the value of an {@code Accept} header.
	 * @return {@code true} if writable; {@code false} otherwise
	 */
	boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

	/**
	 * Return the list of {@link MediaType} objects supported by this converter.
	 * @return the list of supported media types, potentially an immutable copy
	 */
	List<MediaType> getSupportedMediaTypes();

	/**
	 * Read an object of the given type from the given input message, and returns it.
	 * @param clazz the type of object to return. This type must have previously been passed to the
	 * {@link #canRead canRead} method of this interface, which must have returned {@code true}.
	 * @param inputMessage the HTTP input message to read from
	 * @return the converted object
	 * @throws IOException in case of I/O errors
	 * @throws HttpMessageNotReadableException in case of conversion errors
	 */
	T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException;

	/**
	 * Write an given object to the given output message.
	 * @param t the object to write to the output message. The type of this object must have previously been
	 * passed to the {@link #canWrite canWrite} method of this interface, which must have returned {@code true}.
	 * @param contentType the content type to use when writing. May be {@code null} to indicate that the
	 * default content type of the converter must be used. If not {@code null}, this media type must have
	 * previously been passed to the {@link #canWrite canWrite} method of this interface, which must have
	 * returned {@code true}.
	 * @param outputMessage the message to write to
	 * @throws IOException in case of I/O errors
	 * @throws HttpMessageNotWritableException in case of conversion errors
	 */
	void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}

```
- Header의 Content-Type을 통해서 mediaType을 받고 
  해당 Class로 직렬화/역직렬화가 가능한지 canRead, canWrtie 메소드를 통해서 판단한다.
- String 일 경우
  - StringHttpMessageConverter
- Json 일 경우
  - MappingJackson2HttpMessageConverter

## 순서
1. HttpHeader의 Content-Type을 통해서 MediaType을 결정한다.
2. Java객체로 변환 할 수 있는 HttpMessageConverter를 검색하고, 파싱한다.

## 언제 사용되는가?
- @ResponseBody 혹은 @RestController 일 때
  - JSON 혹은 XML 방식을 사용 할 때
  - ViewResolver 대신 HttpMessageConverter를 사용하게 된다.