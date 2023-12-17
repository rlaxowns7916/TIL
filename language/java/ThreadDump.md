# ThreadDump
- JVM에서 실행중인 모든 Thread의 상세정보를 포착하는 Snapshot이다.
- Thread간의 경합, 성능문제 진단, 버그 추적, 메모리 누수분석 등등에서 수행된다.

## 방법

### [1] PID 확인
```shell
$ jps -v

> Process ID| Main Class or JAR	| JVM Arguments and Properties

```
- jps명령을 통해서 JavaApplication의 여러가지 정보를 가져온다.
- -v 옵션은 JVM관련 파라미터까지 보여준다.

### [2] ThreadDump 수행
```shell
# jstack 사용
$ jstack [PID]

# kill 사용
$ kill -3 [PID]
```
- jstack
  - JDK에 포함된 도구중 하나이다.
  - Console에 출력하거나 Redirect도 가능하다.
  - Java프로세스에 대한 더 상세하고 구조화된 정보를 제공한다.
- kill
  - System단에서 제공한다.
  - jstack보다는 간략한 정보를 제공한다.
  - **SIGQUIT(3)은 종료하라는 신호이지만, JVM에는 무시하고 ThreadDump를 생성한다.**

### [3] ThreadDump 결과
```text
"http-nio-8080-exec-1" #178 daemon prio=5 os_prio=31 cpu=0.05ms elapsed=939.31s tid=0x0000000118b81e00 nid=0x1002b waiting on condition  
   java.lang.Thread.State: WAITING (parking)
	at jdk.internal.misc.Unsafe.park(java.base@17.0.7/Native Method)
	- parking to wait for  <0x0000000721231708> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(java.base@17.0.7/LockSupport.java:341)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionNode.block(java.base@17.0.7/AbstractQueuedSynchronizer.java:506)
	at java.util.concurrent.ForkJoinPool.unmanagedBlock(java.base@17.0.7/ForkJoinPool.java:3463)
	at java.util.concurrent.ForkJoinPool.managedBlock(java.base@17.0.7/ForkJoinPool.java:3434)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(java.base@17.0.7/AbstractQueuedSynchronizer.java:1623)
	at java.util.concurrent.LinkedBlockingQueue.take(java.base@17.0.7/LinkedBlockingQueue.java:435)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:117)
	at org.apache.tomcat.util.threads.TaskQueue.take(TaskQueue.java:33)
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1114)
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1176)
	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(java.base@17.0.7/Thread.java:833)
```
| 값                  | 설명                                            |
|:-------------------|:----------------------------------------------|
| ThreadName         | Thread 이름                                     
| SequenceNum        | JVM에서 보다 쉽게 식별하기 위해서 Thread에게 할당한 SequenceNum 
| Thread Priority    | Java프로세스에서의 Thread 우선순위                       
| OS Thread Priority | Os에서의 Thread 우선순위                             
| ExecutedTime       | Thread가 CPU에서 실행된 시간                          
| ElapsedTime        | Thread가 시작된 후 지난 총 시간                         
| ThreadId           | JVM에서 Thread에게 할당한 식별자                        |
| NativeId           | OS가 Thread에 할당한 식별자                           |
| Thread State       | Thread의 현재 상태를 나타낸다.                          |
| Call Stack         | Thread의 CallStack 정보를 나타낸다.                   |