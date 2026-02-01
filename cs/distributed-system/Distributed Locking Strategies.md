# Distributed Locking Strategies: Redis vs Database-based Locks

## Overview

Distributed locking is a fundamental coordination mechanism in distributed systems where multiple processes or threads must operate on shared resources in a mutually exclusive manner. This document provides a comprehensive comparison of Redis-based and database-based distributed locking approaches, focusing on implementation details, reliability trade-offs, and failure modes based on official documentation.

**Sources:**
- Redis.io: "Distributed Locks with Redis" (Redlock algorithm)
- PostgreSQL.org: Explicit Locking, Advisory Lock Functions (Section 13.3, 9.28.10)
- MySQL dev.mysql.com: Locking Functions, InnoDB Locking (Section 14.14, 17.7)
- MIT/IEEE Academic Papers: CAP Theorem (Brewer, Gilbert & Lynch)

---

## Theoretical Foundation: CAP Theorem

### CAP Properties

According to the CAP theorem formalized by Gilbert and Lynch (MIT, 2002) and Brewer (2000), distributed systems must make fundamental trade-offs between three properties:

1. **Consistency (C):** Every response sent to a client is correct. All nodes see the same data at the same time.
2. **Availability (A):** Every request receives a non-error response without guarantee that it contains the most recent write.
3. **Partition Tolerance (P):** The system continues to operate despite network partitions.

**CAP Theorem:** It is impossible to simultaneously achieve all three properties in an asynchronous distributed system. A system can guarantee at most two of the three properties.

### Implications for Distributed Locking

When network partitions occur (P is present), distributed locking systems must choose between:
- **Consistency + Partition Tolerance (CP):** Sacrifice availability to maintain mutual exclusion guarantees
- **Availability + Partition Tolerance (AP):** Sacrifice consistency, potentially allowing multiple lock holders
- **Consistency + Availability (CA):** Not viable in distributed systems requiring partition tolerance

**Practical Note:** As Brewer and Gilbert note in their analysis, CAP prohibits only a tiny part of design space: perfect availability and consistency in presence of partitions, which are rare. Modern systems often maximize combinations that make sense for specific applications.

---

## Redis-based Distributed Locking

### Single Instance Implementation

The simplest Redis-based distributed lock uses a single Redis instance:

#### Acquiring the Lock

```redis
SET resource_name my_random_value NX PX 30000
```

**Parameters:**
- `resource_name`: Unique identifier for the locked resource
- `my_random_value`: Unique value per lock request (20 bytes from /dev/urandom recommended)
- `NX`: Set only if key does not exist (ensures mutual exclusion)
- `PX`: Set expiration time in milliseconds (prevents deadlocks)

**Implementation Pattern (from Redis.io):**

```python
import uuid
import time
import math

def acquire_lock_with_timeout(conn, lockname, acquire_timeout=10, lock_timeout=10):
    identifier = str(uuid.uuid4())  # 128-bit random identifier
    lock_timeout = int(math.ceil(lock_timeout))
    end = time.time() + acquire_timeout

    while time.time() < end:
        if conn.set(lockname, identifier, nx=True, px=lock_timeout*1000):
            return True, identifier
        time.sleep(0.001)
    return False, None
```

#### Releasing the Lock

To safely release locks and avoid deleting another client's lock, use a Lua script:

```lua
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
```

**Why the Random Value Matters:**
A client may acquire a lock, get blocked performing some operation for longer than the lock validity time, and later remove the lock that was already acquired by some other client. Using just `DEL` is not safe as a client may remove another client's lock. The random value ensures each lock is "signed" and can only be removed by the client that created it.

#### Reliability Concerns with Single Instance

According to Redis.io documentation, the single-instance approach has a critical flaw:

> "This is unfortunately not viable. By doing so we can't implement our safety property of mutual exclusion, because Redis replication is asynchronous."

**Race Condition Scenario:**
1. Client A acquires the lock in master
2. Master crashes before write to key is transmitted to replica
3. Replica gets promoted to master
4. Client B acquires lock to same resource A already holds lock for → **SAFETY VIOLATION**

**When Single Instance Is Acceptable:**
- Applications where race conditions are acceptable during failure scenarios
- Simple deployments where failover-based safety violation is acceptable
- Non-critical resource coordination

---

### Redlock Algorithm (Multi-Instance)

Redis.io provides the **Redlock** algorithm for higher reliability across multiple independent Redis masters.

#### Algorithm Steps

**Assumption:** N independent Redis masters (typically N=5) on different machines to ensure independent failures.

**Acquisition Process:**

1. Get current time in milliseconds
2. Try to acquire lock in all N instances in parallel using:
   - Same key name
   - Same random value
   - Small timeout (5-50ms range) compared to auto-release time
3. Compute elapsed time (current_time - step1_time)
4. Lock considered acquired IF:
   - Client acquired lock in majority of instances (at least N/2 + 1)
   - AND elapsed time < lock validity time
5. If acquisition failed, unlock all instances

**Lock Validity Time Calculation:**
```
validity_time = initial_validity_time - elapsed_time - clock_drift_margin
```

**Release Process:**
Release lock from all instances (whether or not client believes it successfully locked a given instance)

#### Safety and Liveness Guarantees

Redis.io defines three minimum properties for effective distributed locks:

1. **Safety Property (Mutual Exclusion):** At any given moment, only one client can hold a lock.
2. **Liveness Property A (Deadlock Free):** Eventually always possible to acquire a lock, even if client that locked resource crashes or gets partitioned.
3. **Liveness Property B (Fault Tolerance):** As long as majority of Redis nodes are up, clients can acquire and release locks.

**Safety Arguments (from Redis.io):**
- First key to expire in the set will exist for at least: `MIN_VALIDITY = TTL - (T2-T1) - CLOCK_DRIFT`
- While majority of keys are set, another client cannot acquire lock because N/2+1 SET NX operations can't succeed
- Multiple clients cannot simultaneously succeed when time to lock majority was greater than TTL time

**Liveness Arguments:**
- Auto-release of locks (keys expire): eventually keys available again
- Client cooperation in removing locks when not acquired or work terminated
- Random delay on retry to desynchronize competing clients (prevents split-brain conditions)

#### Performance and Failure Modes

**Performance Considerations:**
- **Latency:** Multiplexing connections to all N instances to reduce RTT overhead
- **Throughput:** Parallel lock acquisition across instances
- **Network Partitions:** Availability penalty equal to TTL time when client gets partitioned

**Crash Recovery and Persistence:**

From Redis.io analysis:

| Configuration | Safety on Crash | Availability Impact |
|---------------|------------------|-------------------|
| No persistence | Unsafe if restart during active lock | High availability |
| AOF (fsync=1s) | Generally safe | Moderate availability |
| AOF (fsync=always) | Guaranteed safety | Lower availability (fsync overhead) |

**Delayed Restart Strategy:**
Redis.io notes that safety can be maintained even without persistence:
> "Using delayed restarts it is basically possible to achieve safety even without any kind of Redis persistence available, however note that this may translate into an availability penalty."

Strategy: Make instance unavailable for at least `max_TTL` time after crash before accepting new connections. This ensures all locks that existed when instance crashed become invalid and are automatically released.

#### Failure Modes and Edge Cases

**Clock Drift:**
- Redis does not use monotonic clock for TTL expiration
- Wall-clock shifts may result in lock being acquired by more than one process
- Mitigation: Prevent manual server time changes, use NTP properly

**Extended Locks:**
For long-running operations, Redlock supports lock extension:
- Client extends TTL of key if key exists and value matches original random value
- Must succeed in majority of instances to be considered re-acquired
- Maximum number of extension attempts should be limited to maintain liveness

**Split-Brain Conditions:**
During high contention, multiple clients may lock different subsets of instances:
- Redis.io recommends random delay on retry to make this probabilistically unlikely
- Multiplexing (parallel commands) reduces window for split-brain

---

## PostgreSQL-based Distributed Locking

PostgreSQL offers two primary approaches for distributed locking: **Advisory Locks** and **Row-Level Locks**.

### Advisory Locks

Advisory locks provide application-defined locking semantics. The system does not enforce their use—it is up to the application to use them correctly.

#### Session-Level Advisory Locks

**Functions:**
- `pg_advisory_lock(key bigint)`: Obtain session-level advisory lock
- `pg_try_advisory_lock(key bigint)`: Try to obtain lock (non-blocking)

**Characteristics:**
- Lock held until explicitly released OR session ends
- Do NOT honor transaction semantics:
  - Lock acquired during transaction that is rolled back will still be held
  - Unlock is effective even if calling transaction fails
- Can be acquired multiple times by owning process (counted locks)
- Each lock request must have corresponding unlock request

**Example Usage:**

```sql
-- Acquire advisory lock on application-defined key
SELECT pg_advisory_lock(12345);

-- Check if lock is free (non-blocking)
SELECT pg_try_advisory_lock(12345);

-- Release advisory lock
SELECT pg_advisory_unlock(12345);
```

#### Transaction-Level Advisory Locks

**Function:**
- `pg_advisory_xact_lock(key bigint)`: Obtain transaction-level advisory lock

**Characteristics:**
- Behaves more like regular lock requests
- Automatically released at end of transaction
- No explicit unlock operation needed
- Often more convenient than session-level for short-term usage

**Example Usage:**

```sql
BEGIN;
SELECT pg_advisory_xact_lock(12345);
-- Perform protected operations
COMMIT; -- Lock automatically released
```

#### Advantages of Advisory Locks

According to PostgreSQL.org documentation:
- Faster than flag stored in tables
- Avoid table bloat
- Automatically cleaned up by server at end of session
- Useful for emulating pessimistic locking strategies typical of "flat file" data management systems

#### Limitations and Considerations

**Memory Constraints:**
> "Both advisory locks and regular locks are stored in a shared memory pool whose size is defined by configuration variables max_locks_per_transaction and max_connections."

This imposes an upper limit on number of advisory locks grantable by server, typically in the tens to hundreds of thousands.

**Query Ordering Issues:**
PostgreSQL.org warns about dangerous patterns:

```sql
-- OK: Simple lock acquisition
SELECT pg_advisory_lock(id) FROM foo WHERE id = 12345;

-- DANGER: LIMIT not guaranteed to be applied before locking function
SELECT pg_advisory_lock(id) FROM foo WHERE id > 12345 LIMIT 100;
```

The second form is dangerous because `LIMIT` is not guaranteed to be applied before the locking function is executed. This might cause some locks to be acquired that the application was not expecting, and hence would fail to release.

**Monitoring:**
Use `pg_locks` system view to examine a list of currently outstanding locks in a database server.

---

### Row-Level Locks (SELECT FOR UPDATE)

#### Lock Modes

PostgreSQL provides several row-level locking modes:

| Lock Mode | Conflicts With | Description |
|------------|----------------|-------------|
| FOR UPDATE | UPDATE, DELETE, SELECT FOR UPDATE, SELECT FOR NO KEY UPDATE, SELECT FOR SHARE, SELECT FOR KEY SHARE | Strongest row lock - prevents modifications |
| FOR NO KEY UPDATE | UPDATE (certain columns), SELECT FOR UPDATE, SELECT FOR NO KEY UPDATE | Weaker than FOR UPDATE - allows SELECT FOR KEY SHARE |
| FOR SHARE | UPDATE, DELETE, SELECT FOR UPDATE, SELECT FOR NO KEY UPDATE | Shared lock - blocks writers, allows readers |
| FOR KEY SHARE | DELETE, UPDATE (key changes), SELECT FOR UPDATE | Weakest lock - allows SELECT FOR NO KEY UPDATE |

**Acquisition:**

```sql
-- Acquire exclusive lock on selected rows
SELECT * FROM accounts WHERE acctnum = 11111 FOR UPDATE;

-- Acquire shared lock on selected rows
SELECT * FROM accounts WHERE acctnum = 11111 FOR SHARE;
```

#### Isolation Level Interactions

In **REPEATABLE READ** and **SERIALIZABLE** transactions, `FOR UPDATE` throws an error if row has changed since transaction started. In **READ COMMITTED**, it waits for conflicting transactions to complete.

According to PostgreSQL.org:
> "PostgreSQL doesn't remember any information about modified rows in memory, so there is no limit on number of rows locked at one time. However, locking a row might cause a disk write."

#### Deadlock Handling

PostgreSQL automatically detects and resolves deadlocks by aborting one of the transactions involved.

**Example Deadlock Scenario:**

```sql
-- Transaction 1
UPDATE accounts SET balance = balance + 100.00 WHERE acctnum = 11111;
-- Then tries:
UPDATE accounts SET balance = balance - 100.00 WHERE acctnum = 22222;

-- Transaction 2
UPDATE accounts SET balance = balance + 100.00 WHERE acctnum = 22222;
-- Then tries:
UPDATE accounts SET balance = balance - 100.00 WHERE acctnum = 11111;
-- DEADLOCK: Each waits for the other's lock
```

**Best Practices:**
- Acquire locks on multiple objects in consistent order across all transactions
- Ensure first lock acquired is most restrictive mode needed
- Retry transactions aborted due to deadlocks
- Avoid holding transactions open for long periods (e.g., waiting for user input)

---

## MySQL-based Distributed Locking

MySQL offers two primary distributed locking mechanisms: **Named Locks** (GET_LOCK) and **Row-Level Locks** (SELECT FOR UPDATE).

### Named Locks (GET_LOCK)

#### Functions

| Function | Description | Return Value |
|----------|-------------|-------------|
| GET_LOCK(str, timeout) | Obtain named lock with timeout | 1 (success), 0 (timeout), NULL (error) |
| IS_FREE_LOCK(str) | Check if named lock is free | 1 (free), 0 (not free) |
| IS_USED_LOCK(str) | Check if named lock is in use | Connection ID if true, NULL if false |
| RELEASE_LOCK(str) | Release specific named lock | 1 (released), 0 (not held/doesn't exist), NULL (error) |
| RELEASE_ALL_LOCKS() | Release all current named locks | Number of locks released |

#### Characteristics

**Acquisition:**
```sql
-- Try to acquire lock with 10-second timeout
SELECT GET_LOCK('resource_name', 10);

-- Check if lock is available (non-blocking)
SELECT IS_FREE_LOCK('resource_name');
```

**Behavior:**
- **Exclusive:** Lock is exclusive - only one session can hold lock with given name
- **Session-scoped:** Lock released when session terminates (normally or abnormally)
- **NOT transaction-scoped:** Lock NOT released on COMMIT or ROLLBACK
- **Multiple locks:** Single session can acquire multiple named locks
- **Metadata Locking:** Named locks are part of MySQL's metadata locking system

**From MySQL dev.mysql.com:**
> "A lock obtained with GET_LOCK() is released explicitly by executing RELEASE_LOCK() or implicitly when your session terminates (either normally or abnormally)."

#### Limitations

**Performance:**
Named locks use MySQL's metadata locking subsystem (MDL), which involves some overhead increasing as query volume increases.

**Availability:**
All sessions waiting for a named lock are blocked until the lock is released. No mechanism for lock timeouts or automatic expiration (unlike Redis TTL).

**Scalability:**
While MySQL supports multiple named locks, high contention on specific lock names can lead to significant wait times.

---

### Row-Level Locks (InnoDB)

#### Lock Types

InnoDB implements standard row-level locking:

| Lock Type | Permission | Blocks | Description |
|-----------|-----------|--------|-------------|
| Shared (S) lock | Read row | X lock | Permits reading, allows other S locks |
| Exclusive (X) lock | Update/delete row | S, X locks | Permits modification, blocks all others |

**Locking Modes in SELECT:**

| Option | Lock Type | Conflicts With |
|--------|-----------|----------------|
| FOR UPDATE | X lock | UPDATE, DELETE, SELECT FOR UPDATE, SELECT FOR SHARE |
| FOR SHARE | S lock | UPDATE, DELETE, SELECT FOR UPDATE |
| NOWAIT | Any | Returns immediately if lock unavailable |
| SKIP LOCKED | Any | Skips locked rows instead of waiting |

**From MySQL dev.mysql.com:**
> "InnoDB implements standard row-level locking where there are two types of locks, shared (S) locks and exclusive (X) locks."

**Acquisition:**

```sql
-- Acquire exclusive lock on selected rows
SELECT * FROM accounts WHERE account_id = 123 FOR UPDATE;

-- Acquire shared lock (allow other readers)
SELECT * FROM accounts WHERE account_id = 123 FOR SHARE;

-- Non-blocking attempt
SELECT * FROM accounts WHERE account_id = 123 FOR UPDATE NOWAIT;

-- Skip locked rows
SELECT * FROM accounts WHERE account_id = 123 FOR UPDATE SKIP LOCKED;
```

#### Transaction Isolation Levels

InnoDB offers all four transaction isolation levels from SQL:1992 standard:

| Level | Default | Behavior |
|--------|---------|------------|
| READ UNCOMMITTED | No | Reads uncommitted data (rarely used) |
| READ COMMITTED | No | Reads only committed data |
| REPEATABLE READ | Yes (InnoDB) | Same snapshot throughout transaction |
| SERIALIZABLE | No | Fully isolated, strictest |

#### Lock Wait and Deadlock

**Lock Wait:**
Transactions wait indefinitely for conflicting locks to be released. This means holding transactions open for long periods (e.g., waiting for user input) is a bad practice.

**Deadlock Detection:**
InnoDB automatically detects deadlocks and rolls back one of the transactions involved. The choice of which transaction to abort is difficult to predict.

---

## Comparative Analysis

### Summary Table

| Aspect | Redis (Redlock) | PostgreSQL (Advisory) | MySQL (GET_LOCK) |
|---------|----------------|----------------------|-------------------|
| **Consistency Model** | CP (Consistency + Partition Tolerance) | CP (database ACID) | CP (database ACID) |
| **Mutual Exclusion** | Yes (majority consensus) | Yes (enforced by server) | Yes (enforced by server) |
| **Automatic Release** | Yes (TTL expiration) | Session/transaction end only | Session end only |
| **Failure Recovery** | Delayed restarts or fsync=always | Automatic on session/transaction end | Automatic on session end |
| **Performance** | Fast in-memory, multiplexing | Disk I/O overhead | Disk I/O + metadata locking |
| **Scalability** | Horizontal (add Redis instances) | Vertical (scale database) | Vertical (scale database) |
| **Availability** | Reduced during partitions (TTL penalty) | High (within database) | High (within database) |
| **Clock Dependency** | Yes (TTL expiration) | No | No |
| **Cross-Database** | Independent of application data | Shared with application data | Shared with application data |

### Reliability Trade-offs

**Redis-based Locking:**

**Advantages:**
1. **Performance:** In-memory operations, very low latency
2. **Scalability:** Horizontal scaling by adding Redis instances
3. **Decoupling:** Independent of application database
4. **Auto-recovery:** TTL expiration prevents deadlocks

**Disadvantages:**
1. **Clock dependency:** Sensitive to clock drift and shifts
2. **Network partitions:** Availability penalty during partitions
3. **Persistence trade-off:** High availability vs. safety on crash requires fsync=always (performance cost)
4. **Complexity:** Requires careful implementation of Redlock algorithm or acceptance of single-instance risks

**Database-based Locking:**

**Advantages:**
1. **Strong consistency:** ACID guarantees, no clock dependencies
2. **Simplicity:** Native database functionality, minimal application code
3. **Recovery:** Automatic recovery on transaction/session end
4. **Durability:** Persistent storage guarantees lock state

**Disadvantages:**
1. **Performance:** Disk I/O, database locking overhead
2. **Scalability:** Vertical scaling only (database cluster)
3. **Coupling:** Shares resources with application queries
4. **No auto-release:** Potential for indefinite locks if sessions not properly managed

### Failure Mode Comparison

| Failure Scenario | Redis Response | PostgreSQL Response | MySQL Response |
|----------------|---------------|-------------------|---------------|
| **Client crash holding lock** | TTL auto-releases lock | Session end releases lock | Session end releases lock |
| **Network partition** | Availability penalty (wait TTL) | Depends on HA setup | Depends on HA setup |
| **Redis master crash** | Risk of safety violation (single instance) | N/A | N/A |
| **Database crash** | Locks unavailable during recovery | Locks recovered on restart | Locks recovered on restart |
| **Clock jump** | Potential lock violations | No effect | No effect |
| **Connection loss** | Lock held until TTL expires | Lock released on session end | Lock released on session end |
| **Split-brain (contention)** | Possible (mitigate with retry delay) | Possible (deadlock detection) | Possible (deadlock detection) |

---

## Recommendations

### When to Use Redis-based Locking

Redis-based locking is recommended when:

1. **High-performance requirements:** Low-latency locking is critical
2. **Independent coordination:** Locking unrelated to application data persistence
3. **Horizontal scalability:** Need to scale locking capacity independently
4. **Tolerance for occasional violations:** Application can handle race conditions during failover

**Best Practices:**
- Implement Redlock algorithm (N=5+ instances) for production
- Use unique random values for each lock acquisition
- Implement lock extension for long-running operations
- Use proper fencing tokens for critical resources (as noted in Redis.io)
- Configure fsync=always if crash safety is critical
- Implement delayed restarts for availability during recovery

### When to Use Database-based Locking

Database-based locking is recommended when:

1. **Strong consistency required:** Cannot tolerate race conditions
2. **Simplicity priority:** Prefer native database functionality
3. **Locking tied to data:** Lock coordinates access to same data
4. **Horizontal scaling not needed:** Vertical scaling acceptable

**PostgreSQL Advisory Locks - Use When:**
- Emulating pessimistic locking strategies
- Fast, application-defined locking needed
- Avoid table bloat from lock flags

**MySQL GET_LOCK - Use When:**
- Simple named locking across MySQL servers
- Cross-transaction locking needed
- Lock coordination across databases

**Row-Level Locks (PostgreSQL/MySQL) - Use When:**
- Locking specific rows for update
- Transaction isolation sufficient
- Deadlock handling acceptable

### Hybrid Approaches

Consider combining approaches:

1. **Redis for coordination, database for data:** Use Redis for distributed lock coordination while actual data operations happen in database with row-level locks
2. **Database for critical resources, Redis for performance-critical but tolerant:** Use database locks for critical data consistency, Redis for high-throughput, fault-tolerant operations
3. **Multi-tier locking:** Use Redis for coarse-grained locking (resource-level), database for fine-grained locking (row-level)

---

## Conclusion

The choice between Redis-based and database-based distributed locking depends on application requirements:

- **Redis** offers superior performance and horizontal scalability but requires careful implementation to address clock dependencies, network partitions, and persistence trade-offs. The Redlock algorithm provides stronger safety guarantees at the cost of complexity.

- **Databases** provide strong consistency and simplicity through ACID guarantees but sacrifice performance and scalability. Advisory locks (PostgreSQL) and named locks (MySQL) offer application-defined locking semantics, while row-level locks integrate naturally with transaction workflows.

**Key Decision Factors:**
1. **Consistency requirements:** Can application tolerate occasional race conditions?
2. **Performance needs:** What are acceptable lock acquisition/release latencies?
3. **Scalability requirements:** Will locking demand increase beyond database capacity?
4. **Complexity tolerance:** Can team implement and maintain Redlock or similar algorithms?
5. **Failure handling:** What are acceptable availability penalties during failures?

As the CAP theorem reminds us, perfect consistency, availability, and partition tolerance cannot be simultaneously achieved. The optimal choice depends on which properties matter most for specific applications.

---

## References

1. Redis.io. "Distributed Locks with Redis" (Redlock Algorithm). https://redis.io/docs/latest/develop/clients/patterns/distributed-locks/
2. PostgreSQL.org. "13.3. Explicit Locking". https://www.postgresql.org/docs/current/explicit-locking.html
3. PostgreSQL.org. "9.28. System Administration Functions - Advisory Lock Functions". https://www.postgresql.org/docs/current/functions-admin.html
4. MySQL dev.mysql.com. "14.14 Locking Functions". https://dev.mysql.com/doc/refman/8.4/en/locking-functions.html
5. MySQL dev.mysql.com. "17.7.1 InnoDB Locking". https://dev.mysql.com/doc/refman/en/innodb-locking.html
6. MySQL dev.mysql.com. "17.7.2.4 Locking Reads". https://dev.mysql.com/doc/en/innodb-locking-reads.html
7. Gilbert, S., & Lynch, N. A. (2002). "Brewer's conjecture and the feasibility of consistent, available, partition-tolerant web services". ACM SIGACT News.
8. Lynch, N. A., & Gilbert, S. (2012). "Perspectives on the CAP Theorem". Computer, 45(2): 30–36.
9. Brewer, E. A. (2000). "Towards robust distributed systems". PODC.
