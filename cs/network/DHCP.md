# DHCP (Dynamic Host Configuration Protocol)
- **TCP/IP에 필요한 자동설정을 제공해주는 프로토콜이다.**
  - 같은 BroadCast 도메인 내에서만 수행된다.
  - Network 관리자는 수동으로 IP를 관리할 필요가 없으며, IP충돌도 방지된다.
- **복잡한 인터넷 설정을 제공하는 Server와 할당 받으려는 Client로 구분된다.**
- OSI 7 Layer에서 **애플리케이션 계층(Layer 7)**에서 동작하지만, 동작 자체는 데이터 링크 계층(Layer 2) 내의 브로드캐스트 도메인에서 수행된다.
  - 네트워크 설정 자체를 자동화 하는 것이기 떄문에 분류는 Layer7으로 된다.
- IP할당의 경우 영구 할당이 아닌 **임대(Lease)** 이다.

## 장점
- 자동으로 설정하기 떄문에 일일이 관리할 필요가 없어진다.
- 자동으로 설정되기 떄문에 IP 충돌 등을 방지 할 수 있다.

## 단점
- DHCP에 전적으로 의존하고 있다.
  - DHCP서버가 다운되면, 새로운 Client는 IP를 할당받을 수 없다.
  - 자체적인 보안 프롵토콜이 존재하지 않는다.
  - 고정IP장치와의 혼합 시 복잡하다. (DHCP Reservation을 통해서 해결 가능)

# 주요 개념
## 1. DHCP Server
- Client에게 IP주소와 Network 설정을 제공해주는 Server이다.
- Network관리자가 설정한 IP Pool 내에서 동작한다.
- ex) 가정에서는 공유기가 DHCP Server로 동작한다.

## 2. DHCP Client
- DHCP Server로 부터 IP주소를 할당받는 Client이다. (Host)

## 3. IP Pool
- DHCP Server가 Client에게 할당해줄 IP주소의 범위이다.

## 4. Lease
- IP주소를 할당받은 Client가 사용할 수 있는 기간이다.
- Lease가 만료되면 Client는 새롭게 IP주소를 갱신하거나 반환해야한다.

# 제공해주는 것들
- IP주소
- SubnetMask
- Gateway IP주소 (L3Switch, Router, ...)
- DNS 주소


## 과정 (할당)
1. Discover
   - 클라이언트가 네트워크에 연결되면 DHCP Discover 브로드캐스트 메시지를 보내 DHCP 서버를 찾는다.
   - 이 메시지는 L2 브로드캐스트(255.255.255.255)로 같은 브로드캐스트 도메인 내 모든 장치에게 전송된다.
2. Offer
   - DHCP 서버는 클라이언트의 요청을 받고, 사용 가능한 IP 주소와 설정 정보를 포함한 DHCP Offer 메시지를 브로드캐스트로 전송한다.
   - Unicast 일 수도 있고, BroadCast일 수도 있다.
     - Discover 메세지의 BroadCastFlag의 값에 따라 달라진다. (1 = BroadCast, 0 = Unicast)
3. Request
   - 클라이언트는 제공받은 IP 주소 중 하나를 선택하고, DHCP Request 메시지를 보내 요청을 확인한다.
   - 클라이언트는 기존에 사용했던 IP 주소가 있다면 이를 요청할 수도 있다.
4. Acknowledge
   - DHCP 서버는 요청을 확인하고, 클라이언트에게 IP 주소와 설정 정보를 확정하는 DHCP Acknowledge 메시지를 전송한다. (Unicast)
   - 클라이언트는 이 정보를 저장하고 IP 주소를 사용하기 시작한다.

## 과정 (갱신)
1. Request
   - 기존에 사용하던 IP를 계속 사용할 수 있도록 요청한다.
2. Acknowledge
    - 요청을 확인하고, IP주소를 계속 사용할 수 있도록 응답한다.