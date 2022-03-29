# IP Class
- IP 영역을 A~E 클래스로 구성
- 각 클래스별로 IP 할당 범위가 다르다.
- 각 영역은 Network 영역과 Host영역으로 구분된다.
- 적은 IP주소를 적절하고 효율적으로 나누는 것이 목적이다.
  - 모든 IP를 관리하기 힘들기 때문에, Host영역과 IP영역으로 분리하여 라우팅하기 위함이다.
  - **Host영역: 아파트 동, IP 영역: 아파트 호수** 같은 느낌이다.
- 적은 IP주소를 적절하고 효율적으로 나누는 것이 목적이다.
- IPv4 기반이다.
- IPv4 기반이다.

## Class A
- Network가 가질 수 있는 Host가 제일 많다.
- Network(1Byte), Host(3Byte)로 구성된다.
- Network영역의 비트가 0으로 시작한다.

| 클래스       | 앞선 Bit | Network 수 | Network 당 Host 수 |
|-----------|--------|-----------|------------------|
| class A   | 0      | 128       | 16,777,214       |


## Class B
- Network(2Byte), Host(2Byte)로 구성된다.
- Network 영역의 비트가 10으로 시작한다.

| 클래스       | 앞선 Bit | Network 수 | Network 당 Host 수 |
|-----------|------|-----------|------------------|
| class A   | 1    |  16,384      | 65,534       |

## Class C
- Network(3Byte), Host(1Byte)로 구성된다.
- Network 영역의 비트가 110으로 시작한다.

| 클래스       | 앞선 Bit | Network 수 | Network 당 Host 수 |
|-----------|------|-----------|------------------|
| class A   | 11   |  2,097,152      | 254      |

## Class D
- 멀티캐스트 용도로 사용된다.
- 실제 사용되는 경우는 거의 없다.

## Class E
- 연구목적으로 사용되는 Class (Reserved)
- 실제 사용되는 경우는 없다.

![IP Class](https://user-images.githubusercontent.com/57896918/160407150-3e29a844-c563-4723-aebb-787ce9259222.png)



## 문제점
- 영역을 나누어서 효율적으로 관리할 수 있었으나, 각 Class마다 Host개수의 차이가 너무컸다.
- Host영역이 과도하게 남는 경우가 발생하였다.
  - C클래스를 사용하기에는 Host를 다 커버할 수 없고, B클래스를 사용하기에는 너무 많이 남는다.
- CIDR가 나오는 계기가 되었다. (1993년부터 대체되기 시작했다.)
