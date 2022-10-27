# Stock's talk
+ [스톡스톡 바로가기](https://main.stocks-talk.site)  
+ 각종 주식 정보와 커뮤니티, 모의투자 기능이 있는 주식 웹 서비스

## 1. 프로젝트 소개 🗒
![image](https://user-images.githubusercontent.com/104505378/197696563-9b9a3205-afd7-4df9-87d9-f0f7fef103c9.jpg)
***모의투자를 통해 나만의 주식 경험을 쌓아보세요 !***

***나만의 주식경험을 커뮤니티에 공유해 주세요 !***


### 서비스
+ 모의투자 : 매도매수 기능, 지정가 주문 기능, 수익률 기준 유저별 랭킹 부여
+ 종목정보 : 실시간 주가, 주가 그래프, 뉴스, 재무지표 등 투자 기초 자료 제공
+ 커뮤니티 : 게시글, 댓글, 좋아요, 활동별 뱃지 부여, 실시간 채팅
+ 유저친화 : 다크모드, 이벤트 알람, 사용법 소개 서비스 제공

## 2. 제작기간 && 팀원소개 🏃‍🏃‍♀️💨 
### 2022-09-16 ~ 2022-10-28🔥  
| 이름 | 주특기 | 담당 기능 |
| --- | --- | --- |
| 김학준 | BE | 각종 종목 정보(차트,현재가,기사크롤링,랭킹,종목리스트) 불러오기 및 갱신 스케쥴링 / 모의투자 매매 / 게시글 CRUD / 랭킹 / 깃액션배포 / 구글 로그인 |
| 민지영 | BE | 각종 종목 정보(재무지표,증시인덱스) 불러오기 / 수익률 갱신 / 댓글, 좋아요, 마이페이지, 계좌 CRUD / SSE 알람연동 / 채팅 / 카카오 로그인 |
| 김승원 | FE | 커뮤니티 CRUD / 좋아요 / 관심종목 / 랭킹보드 / 알람 / 유저정보 수정 / 계좌개설 / 업적 |
| 황준수 | FE | 레이아웃 / 댓글 CRUD / 실시간 채팅 / 그래프 / 모의투자 / 주식정보 불러오기 / 게시글 페이지 |
| 홍준형 | FE | 소셜로그인(카카오, 구글) |

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
[API 명세서 바로가기](https://www.notion.so/API-22041a391cbb41919f50574665c7899c)

## 6. ERD 
[ERD 바로가기](https://www.notion.so/ERD-fe370cf911354aea909d03b7cb45cb67)

## 7. Trouble Shooting 
주식 데이터 저장 및 조회
- mySql은 딕셔너리, 이중리스트 등의 데이터를 관리하기엔 유연하지 못함.
- NOSQL 중 가장 익숙하며, RDS와 비슷한 클라우드 환경을 제공하는 mongoDB로 종목정보 데이터를 별도 관리

모의투자 수익률 계산
- 현재가 갱신 시점마다 전 유저의 수익률 계산 및 갱신 시 서버 과부하가 염려
- 전 유저 갱신이 아닌 수익률 조회 요청 건마다 갱신 및 보유종목 테이블에 평균매수가 필드 생성 후 매수 시마다 반영하여 수익률 계산 시 편리하도록 구현

Timestamped 시간 오류
- EC2 배포 후 시간 관련 데이터가 UTC 기준으로 조회됨
- DB에는 한국 시간이 올바르게 저장되나 조회 값이 다름
- RDS, EC2의 timezone을 전부 한국으로 세팅
- main application 클래스에서 @PostConstruct로 타임존을 한국에 맞게 설정하여 해결

