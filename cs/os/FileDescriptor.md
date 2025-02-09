# File Descriptor
- OS가 File을 추상화하여 관리하기 위한 Integer 값
- File뿐만 아니라 Socket, Pipe, 장치 등등도 FD로 관리된다. (Unix 계열에선 모든걸 File로 관리하긴하 때문)
- **FileDescriptor는 Process가 File을 다루기위한 OS가 제공하는 Handle이라고 볼 수 있다.**



## 개념
- 운영체제가 파일을 식별하는 정수형 ID
- 모든 I/O 자원을 관리하는 데 사용됨
- Process 별로 File Descriptor Table을 가짐
- File을 Open하면 새로운 File Descriptor가 할당되고, Close하면 해제됨
  - 기본적으로 3개가 열려있다.
    - 0: stdin
    - 1: stdout
    - 2: stderr
  - close()하지 않으면, Leak이 발생 할 수 있다.

## 예시
```c
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

int main() {
    int fd = open("example.txt", O_RDONLY);  // 파일 열기 (읽기 전용)
    
    if (fd == -1) {
        perror("파일 열기 실패");
        return 1;
    }
    
    printf("할당된 파일 디스크립터: %d\n", fd);
    close(fd);  // 파일 닫기
    return 0;
}
```