# 시스템 콜(SystemCall)
- UserMode에서 KernelMode의 기능에 접근 가능하게 한다.
```text
시스템 호출(system call)은 운영 체제의 커널이 제공하는 서비스에 대해, 
응용 프로그램의 요청에 따라 커널에 접근하기 위한 인터페이스이다. 
보통 C나 C++과 같은 고급 언어로 작성된 프로그램들은 
직접 시스템 호출을 사용할 수 없기 때문에 
고급 API를 통해 시스템 호출에 접근하게 하는 방법이다.
```

OS Kernel은 **KernelMode**와 **UserMode**로 나뉘어진다.<br>
중요자원에 대한 보호차원에서 커널의 모드를 2가지로 나눈 것이다.<br>

사용자 응용프로그램은 Kernel차원에서 접근해야 하는 리소스에 접근하지 못하기 때문에,
SystemCall을 통해서 요청을 한다.<br>

SystmCall에 의해 요청받은 작업은 KernelMode에서 처리 후,<br>
그 결과를 사용자 프로그램에게 리턴한다.

### 시스템 콜의 역할

프로세스 제어(Process Control)
- fork()
- exit()
- wait()

파일 조작(File Manipulation)
- open()
- read()
- write()
- close()

장지 조작(Device Manipulation)
- ioctl()
- read()
- write()

정보 유지(Information Maintenance)
- getpid()
- alarm()
- sleep()

통신(Commnication)
- pipe()
- shm_open()
- mmap()

보호(Protection)
- chmod()
- umask()
- chown()