# Chunk
- 여러 Item을 묶은 하나의 덩어리
- 한번에 하나의 Item을 입력받아, Chunk단위의 덩어리를 만든 후, **Chunk단위로 트랜잭션 처리를 한다.**
  - **Chunk 단위의 Trasaction Commit/Rollback 이 수행된다.**
- 대용량 데이터를 한번에 처리하는 것이 아닌, Chunk단위로 쪼개어서 입출력하며, 더이상 쪼갤 수 없을 때 까지 반복된다.
  - 읽을 Item이 더 존재하면 CONTINUABLE, 없다면 null || FINISHED
- InputChunk, OutputChunk가 존재하며, Chunk<I,O> 형태로 표현된다.
  - Chunk<I>는 ItemReader로부터 읽은 하나의 Item을 의미한다.
  - Chunk<O>는 Chunk<I>를 ItemProcessor가 적절하게 가공 필터링 한 다음, ItemWriter에 전달되는 타입을 의미한다.
- ChunkContext에 Chunk단위의 Item들을 저장하고 있따. (캐싱목적)
- ItemReader와 ItemProcssor,  ItemWriter는 Chunk에 담겨있는 개별 Item들을 처리한다.
```text
Source -> ItemReader -> Chunk<I> -> ItemProcessor -> Chunk<O> -> ItemWriter
```

# Chunk Architecture
1. ItemReader가 Source로부터 Read한다.
2. 내부적으로 List를 가지고 있으며, Chunk<I>형태로 읽고, ChunkSize 까지 저장한다. 
3. ItemProcessor에 Size만큼 채워진 Chunk<I> List를 전달한다.
4. ItemProcessor는 Chunk<I>를 가공하고, Chunk<O>를 반환한다.
   - ItemProcessor가 없을 경우에는, Chunk<I>와 Chunk<O>는 같다.
5. ItemWriter는 Chunk<O>를 받아서 DB나 File에 Write한다.

# ChunkOrientedTasklet  
- SpringBatch에서 제공하는 Tasklet의 구현체
  - Tasklet Step에 의해서 반복적으로 실행되며, 실행될 때 마다, 매번 새로운 Transaction이 생성된다. (Chunk 단위의 트랜잭션이기 때문)
  - Chunk에서 Exception이 발생할 경우, 해당 Chunk는 Rollback되며, 이전에 완료된 Chunk는 그대로 Commit된다,
  - ```text
        Tasklet (fun execute(StepContribution, ChunkContext) : RepeatStatus) 
           ^
           |
           |
      ChunkOrientedTasklet (val ChunkProvider, val ChunkProcessor)
    
    ```
- Chunk 지향 프로세싱을 담당하는 도메인 객체이다.
  - 내부적으로 ItemReader를 핸들링하는 ChunkProvider, ItemProcessor와 ItemWirter를 핸들링하는 ChunkProcessor타입의 구현체를 가진다. o
```kotlin
fun chunkStep(): Step {
    return stepBuilderFactory.get("chunkStep")
        .<I,O>chunk(chunkSize) // chunkSize설정, CommitInterval을 의미한다.
        .<I,O>chunk(completionPolicy()) // chunkProcess를 읽기위한 정책 설정 Class
        .reader(reader()) // ItemReader
        .processor(processor()) // ItemProcessor
        .writer(writer()) // ItemWriter
        .stream(itemStream()) // 재시작 데이터를 관리하는 Callback에 대한 Stream (이전 데이터를 DB에 저장하고 있기 떄문에 실패 지점부터 읽기 가능)
        .readerIsTransactionalQueue() // Item이 JMS, MessageQueueServer와 같은 트랜잭션 외부에서 읽혀지고, 캐시할 것인지 여부 (default: false)
        .listener(chunkListener()) // Chunk 프로세스가 진행되는 특정시점에 Callback을 받을 Listener
        .build() 
}
```

# ItemReader
- Required (Chunk-Oriented Tasklet 설정 시)
- 다양한 형태의 Source를 읽을 수 있게 하는 Interface
  - Flat파일 (csv, txt 등, 고정 위치로 정의 된 데이터 필드, 특수문자로 구별된 데이터 행)
    - FlatFileItemReader
  - XML, JSON
    - StaxEventItemReader
    - JsonItemReader
    - MultiResourceItemReader
    - ...
  - DB
    - JdbcCursorItemReader
    - JpaCursorItemReader
    - JdbcPagingItemReader
    - ItemReaderAdapter
    - ...
  - JMS, MQ 
  - Custom Reader (구현 시, Thread-Safe하게 구현해야 함)
    - SynchronizedItemStreamReader
    - CustomItemReader
    - ...
- ```kotlin
    interface ItemReader<T> {
        @Throws(Exception::class, UnexpectedInputException::class, ParseException::class, NonTransientResourceException::class)
        fun read() : T {
        } 
    }
  ```
- Item 하나를 읽으며, 읽을게 없다면 null을 리턴한다.
  - Item하나는 DB Row 한개 혹은 XML 파일의 하나의 Element가 될 수 있다.
- **더 이상 처리해야 할 Item이 없을 때, Exception이 던저지는 것이 아닌 ItemProcessor로 처리가 넘어간다.**
- **다수의 구현체들이 ItemReader, ItemStream 인터페이스를 동시에 구현하고 있다.**
  - 파일의 Stream을 열거나 종료, DB커넥션을 열거나 종료하는 초기화 작업을 수행한다.
  - ExecutionContext에 read관련 여러가지 상태정보를 저장해 재시작 시, 참조 가능하도록 지원한다.

# ItemProcessor
- Optional
- Read 후 Write 하기 이전, 데이터를 가공하고 필터링 하는 역할이다.
  - 하나의 Item씩 처리한다.
  - **ItemReader와 ItemWriter와 분리되어, 비즈니스 로직을 구현 할 수 있다.**
  - ClassifierCompositeItemProcessor (분류에 따른 선택적 처리)
  - CompositeItemProcessor (ItemProcessor를 연결해서 처리)
  - CustomItemProcessor (Customize)
- null을 반환하면 Chunk<O>에 전달 되지 않기 떄문에, ItemWriter의 대상이 되지 않는다.
- ```kotlin
    interface ItemProcessor<I,O> {
        @Throws(Exception::class)
        fun process(item: I) : O {
        }
    }
  ```
# ItemWriter
- Required (ChunkOrientedTasklet 구현시)
  - Chunk단위의 List를 받는다.
  - Write가 완료되면 Transaction을 종료하고, 다음 Chunk로 이동한다.
- Chunk단위의 데이터를 받아, 일괄 Write하기 위한 인터페이스
  - Flat파일 (csv, txt, ...)
    - FlatFileItemWriter
  - XML, JSON
    - StaxEventItemWriter
    - JsonItemReader
    - MultiResourceItemReader
  - DB
    - JdbcBatchItemWriter
    - JpaItemWriter
    - ItemWriterAdapter
    - ...
  - JMS, MQ
  - Mail Service
  - Custom Writer
- ```kotlin
    interface ItemWriter<T> {
        @Throws(Exception::class)
        fun write(items: List<T>)
    }
  ```
- **다수의 구현체들이 ItemWriter, ItemStream 인터페이스를 동시에 구현하고 있다.**
  - 파일의 Stream을 열거나 종료, DB커넥션을 열거나 종료하는 초기화 작업을 수행한다.
  - 보통 구현체들은 Reader 구현체와 1:1로 대응되어 있다.