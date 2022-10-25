# Stock's talk
+ [홈페이지]()  
+ 각종 주식 정보와 커뮤니티, 모의투자 기능이 있는 주식 웹 서비스

## 1.  프로젝트 소개 🗒
+ 모의투자 : 매도매수 기능, 지정가 주문 기능, 수익률 기준 유저별 랭킹 부여, 다른 유저의 수익률 열람 기능
+ 종목정보 : 실시간 주가, 주가 그래프, 뉴스, 재무지표 등 투자 기초 자료 제공
+ 커뮤니티 : 게시글, 댓글, 좋아요 활동별 뱃지 부여, 실시간 채팅
+ 유저친화 : 다크모드, 푸시알람 온오프로 유저친화적 서비스 제공

## 2. 팀원소개 🏃‍🏃‍♀️ 

| 이름 | 주특기 | 담당 기능 |
| --- | --- | --- |
| 김학준 | BE |  |
| 민지영 | BE |  |
| 김승원 | FE |  |
| 황준수 | FE |  |
| 홍준형 | FE |  |

## 3. 백엔드 기술 스택 🛠
- Language: **`java`**,**`python`**
- Framework: **`SPRING`, `SPRINGBOOT`,`FLASK`**
- Build Tool: **`Gradle`**
- DB: **`MySQL`**,**`MongoDB`**,**`Redis`**
- Server: **`AWS EC2`**
- Other Tools : **`Git`, `Github`, `swagger`, `AWS S3`, `AWS CodeDeploy`, `WebSocket`, `SSE`, `OAuth`, `notion`, `slack`**

## 4. 아키텍처 📃
![architecture](https://user-images.githubusercontent.com/104505378/197699199-a8e3c943-273f-4a0b-bc06-28424382b127.jpg)


## 5. API 명세서 


## 6. ERD 
![erd](https://user-images.githubusercontent.com/104505378/197696785-ff2ff0d5-f37f-48c1-804a-a8d45889d471.png)

## 7. Trouble Shooting 
Timestamped 시간 오류
- EC2에 배포한 이후 생성일, 수정일이 UTC 기준으로 조회됨
- db에 시간에 맞게 저장은 되지만, 조회만 하면 다르게 출력
- RDS, EC2의 timezone을 다 한국시간에 맞게 설정하여 시도 했지만 실패
- 백의 응답 dto에서 직접 수정해서 보내주거나, 프론트에서 받은 이후 수정하는 옵션
- main application 클래스에서 @PostConstruct을 붙혀 타임존을 한국에 맞게 설정하여 해결

## 8. UI
![image](https://user-images.githubusercontent.com/104505378/197696563-9b9a3205-afd7-4df9-87d9-f0f7fef103c9.jpg)


