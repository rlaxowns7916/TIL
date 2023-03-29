# Port
- IP와 더불어서 함께 사용된다.
- 일반적으로 Proess를 식별한다고 볼 수 있다. 
- TransportLayer (L4)에서 식별하는데 사용된다.
- 0~66545 (2^16)개를 가질 수 있으며, 0~1023은 WellKnown Port라고하여, 잘 사용하지 않는다.
- Port는 중복되지않는다.


## Port는 어떤 형태를 띄는가
```text
------User-------
Process
[Socket]
------Kernel------
TCP/IP (IP/PORT)
[Driver]
------H/W---------
NIC (Mac)
```
- Process가 TCP/IP에 접근하기 위해서 추상화된 인터페이스가 제공되는데, 이것이 **Socket** 이다.
  - 본질적으로는 File의 형태이다.
  - TCP Socket에는 Port번호가 포함되게 된다.
- Port번호는 16bit정보이다.
  - 그렇기 떄문에 0~66535 사이의 Port번호를 가질 수 있게 된다.