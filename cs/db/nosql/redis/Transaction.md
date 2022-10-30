# Redis Transaction

- Redis에서도 트랜잭션을 보장 할 수 있다.
    - RDB의 트랜잭션과 다르다.
    - **Rollback 을 보장하지 않는다.**
        - Redis의 단순성과 성능에 큰 영향을 끼치기 때문에 지원하지않는다.
    - 중간에 오류가 발생해도, **해당 명령어를 제외하고 다 실행된다.**
      - Transaction Queue에 쌓여있고, EXEC 시에 순서대로 실행된다.
- **MULTI, EXEC, DISCARD, WATCH**를 통해서 실행 가능하다.
- 트랜잭션의 모든 명령은 직렬화되며, 순차적으로 실행된다.
    - 트랜잭션이 실행되는 동안, 다른 명령어들은 실행되지 않는다. (격리성 보장)
    - 트랜잭션을 통해 명령어의 실행을 하나의 단위로 묶는 것이다.

## MULTI & EXEC

```shell
> MULTI
OK

> INCR foo
QUEUED

> INCR bar
QUEUED

> EXEC

1) (integer) 1
2) (integer) 1
```

- MULTI를 통해서 여러개의 명령어를 입력한다. (Queue에 대기중 상태로 쌓인다.)
- EXEC 전까지 Transaction Queue에 쌓였던 명령어를 하나의 단위로 실행한다.
- Transaction 단위로 묶인 명령어들은 Atomic하게 실행된다.

## DISCARD

```shell
> SET foo 1
OK

> MULTI
OK

> INCR foo
QUEUED

> DISCARD
OK

> GET foo
"1"

```

- Transaction Queue를 비울 때 사용한다.
- Transaction 진행 과정을 버릴 때 사용한다.
- **아무런 명령을 실행하지 않으며, 다시 일반적인 모드로 전환된다.**

## WATCH

```shell
> WATCH foo # Transaction용 WATCH의 시작
OK

> incrby foo 3 # Transaction 외부에서 WATCH 대상의 value 변경
(integer) 3

> MULTI # Transaction 시작
OK

> incrby foo 100 #Transaction 내부 명령어
QUEUED

> EXEC # 실행
(nli) # Transaction 실패 (watch를 시작한 시점 이후에 해당 Key에 해당하는 Value가 변경되었기 때문이다)

```

- CAS (Compare And Set)을 통한 **Optimistic Lock**을 제공한다.
- WATCH 명령어에 대한 변경이 감지되면, 전체 Transaction이 중단된다.
- NULL을 리턴함으로 하여 Transaction 실패를 알린다.
- **WATCH로 지정되었던 Key의 Transaction이 EXEC 될 경우 UNWATCH 상태로 변경된다.**
- 결국 될 때 까지 돌리는 방법밖에 없다.
