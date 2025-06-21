# PVM (Python Virtual Machine)
- **Python 바이트코드를 실행하는 가상 머신**
- JVM처럼 중간 바이트코드를 실행하는 런타임 엔진
- 다양한 구현체가 존재
  - CPython (Default)
  - pypy (JIT 컴파일러)
  - MicroPython (임베디드 시스템용)
  - Jython (Java 플랫폼용)
  - ...

## .pyc 파일
- Python 소스코드가 컴파일되어 생성되는 바이트코드 + 메타 데이터 파일
  - Magic Number(Python 버전 정보)
  - Hash/Size OR Timestamp (소스코드의 수정 시간)
  - ...
- Python 인터프리터(PVM)가 읽고 실행하기 위해 만들어진 중간 코드 형태
- **생성 되는 시점**
  - import 구문으로 모듈을 불러올 때 
  - python -m py_compile <파일명.py> 명령어 실행 시
  - __main__이 아닌 모듈로 실행될 때
- 매번 컴파일하지 않고, 캐시된 바이트코드를 사용하여 성능 향상


## 과정
1. SourceCode -> AST(Abstract Syntax Tree) 생성
2. AST -> ByteCode 변환
3. ByteCode 실행

## 실행환경

### [1] Module 시스템
- Python 프로세스 단위로 격리된다.
  - 프로세스 별로 `sys.modules`를 가진다.
- 모든 Python 구현체는 `sys.modules` 딕셔너리를 통해서 이미 로드된 module들을 관리
  - `sys.modules`은 Python 프로세스 전체에서 공유되는 전역 모듈 캐시
- 동일한 Module의 load를 방지한다.

### [2] Import 매커니즘
- sys.path에 정의된 경로에서 모듈을 검색
- **"First Found Wins" 원칙**: 첫 번째로 발견된 모듈 사용, 이후 동일명 모듈 무시
- **검색 우선순위**: sys.modules 캐시 → .pyc 파일 → .py 파일
- .pyc 파일 유효성 검사 (타임스탬프/해시 기반)
- **캐시 무효화**: 소스 파일 수정 시 .pyc 재생성

## VS JVM

| 구분 | PVM | JVM |
|---|---|---|
| **바이트코드 형식** | Python 바이트코드 (.pyc) | Java 바이트코드 (.class) |
| **컴파일 시점** | 런타임 (import 시) | 명시적 컴파일 (javac) |
| **바이트코드 저장** | `__pycache__` (선택적) | `.class` 파일 (필수) |
| **실행 엔진** | 스택 기반 인터프리터 | 스택 기반 + JIT 컴파일러 |
| **메모리 관리** | 레퍼런스 카운팅 + GC | Generational GC |
| **최적화** | 제한적 (PEP 659 등) | 적극적 JIT 최적화 |
| **플랫폼 독립성** | 부분적 (C 확장 의존) | 완전한 독립성 |