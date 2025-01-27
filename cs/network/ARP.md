# ARP (Address Resolution Protocol)
- Ipv4에서 MAC주소를 알아내기 위해서 사용하는 프로토콜이다.
- 같은 BroadcastDomain안에서만 동작한다.
- **IP주소로 MAC주소를 알아내려 할 때 사용한다.**
- **최초 PC 부팅 시, Gateway의 MAC주소를 알아내기 위해서, ARP가 발생한다.**
  - **그럼 부팅 시, Gateway의 IP는 어떻게 알고있지? ==> DHCP**

## 과정
1. BroadCast를 통해서, ARP Request가 발생한다. (Broadcast)
2. Gateway는 자신을 찾는 요청이라는 것을 인지하고 Response를 보낸다. (Unicast)
3. 이후 모든 요청의 Packet Destination MAC주소에는 Gateway의 MAC주소가 찍힌다. (해당 목적지의 서버 MAC이 찍히는게 아니다.)

## 명령어
```shell
# IP주소와 MAC주소를 연결한 ARP 캐시테이블이 나온다.
## windows
$ arp -a

## Linux / Mac
$ ip neigh show
$ arp -n
```