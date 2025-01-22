# SubNet
- 말 그대로, Network를 다시 쪼개는 행위
- CIDR의 개념과 연동된다. (IP주소를 효율적으로 사용하기 위해)
- Class 개념을 그대로 사용하는 것이 아닌, Host영역을 Network영역에 포함시켜서 사용한다.
- Subnet을 나누면 나눌수록 사용할 수 있는 갯수가 줄어든다.
  - Host영역에서 사용 가능한 주소의 갯수는 2^(Host영역의 bit 자리) - 2 이다.
  - Host영역이 모두 0(Network 주소) 일때, 모두 1일 때 (Broadcast 주소)는 주소로 사용하지 못하기 떄문이다
  - **즉 subnetting을 통해 만들어지는 범위마다 2개씩 사용이 불가능해진다.**
- SubnetMask를 사용하여, Network영역의 bit수를 표현한다. 192.168.32.254/25 (32bit 중 25bit가 network 영역)

## SubnetMask
- IP영역에서의 Network영역과 Host영역을 구분짓는 역할을 하는 32bit의 이진수
- Network영역의 bit를 1로, Host영역의 bit를 0으로 표현한다.

| CIDR      | SubnetMask |
|-----------|----------|
| /8   | 255.0.0.0    |
| /16   |255.255.0.0     |
| /24   |255.255.255.0     |
| /30   |255.255.255.252     |
- **IP주소와 SubnetMask를 AND연산을 하면 Network영역을 알 수 있다.(어떤 Subnet에 속하는지)***
  - And연산을 한다는 것은, Host영역은 제거하고 Network영역만 남기는 행위이다.
  - AND 연산 결과로 나오는 네트워크 주소는 해당 IP가 속한 서브넷(Subnet)의 Network Address이다.
- ex)
  - IP: 192.168.1.10
  - SubnetMask: 255.255.255.0
  - Network Address: 192.168.1.0


## 예제
```text
192.168.32.0/24: 기본적인 C Class (서브넷이 발생하지 않음)
192.168.32.0/25: 위의 것에서 network 영역이 2배로 늘어남


192.168.32.0/25 : 서브넷1의 Network Address  (all 0)
192.168.32.1~192.168.32.126 : 서브넷1의 host 할당 가능한 부분
192.168.32.127 : 서브넷1의 Broadcast Address (all 1)


192.168.32.128 : 서브넷2의 NetworkAddress (all 0)
192.168.32.129~192.168.32.254 : 서브넷2의 host 할당 가능한 부분
192.168.32.255 : 서브넷2의 Broadcast Address (all 1)
```


