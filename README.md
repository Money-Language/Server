<img width="200" alt="스크린샷 2022-11-23 오후 3 30 16" src="https://user-images.githubusercontent.com/97823928/203484758-128c85c6-8367-444c-9897-7a417c70fa0e.png">
<br></br>

<p align="center"><img width="700" alt="스크린샷 2022-11-23 오후 3 33 39" src="https://user-images.githubusercontent.com/97823928/203485168-7b49ba2b-0634-4d80-a226-760b17623bd9.png">

## Project
#### 앱테크 기능을 통해 밀레니얼 세대의 문해력을 향상시키는 서비스, MOGE
> 🏆 Make Us CMC CX 2기 최우수상 수상  </br>
> 🕓 프로젝트 기간: 2022.10.4 ~ 2022.11.20 </br>


## Record
* [MOGE 팀의 개발 일지](https://www.notion.so/makeus-challenge/Money-Language-MOGE-c6267d09e23643099aae9dd94ad9bf03)  
👉 [나의 Notion](https://www.notion.so/MOGE-2f90d6e321bb4afc8fed50a6ffc055b1)  
👉 [API 명세서](https://www.notion.so/API-6e702cab8b5340e7b0eb9783f7ca1f6a)  

## Architecture


## ERD 

<p align="center"><img width="800" alt="스크린샷 2022-11-23 오후 3 46 38" src="https://user-images.githubusercontent.com/97823928/203486709-af899c44-8ecc-414c-9d8e-6bfc13eb0bb4.png">

## Git Convention

```
- feat     : 새로운 기능 / 특징
- fix      : 버그를 고침
- refactor : 리팩토링
- chore    : 프러덕션 코드가 바뀌지 않는 기타 변경 사항
- style    : 코드 형식, 세미코론 추가 등 비즈니스 로직에 변경 없는 사항
- docs     : 문서화 업데이트
- test     : 테스트 코드
- deps     : dependency 관련
```

## Foldering

```
main
├─ 📦 global
│   ├─ common
│   ├─ config
│   │  ├─ Awsconfig.java
│   │  ├─ MailConfig.java
│   │  ├─ secret
│   │  │  ├─ Secret.java
│   │  ├─ security
│   │  │  ├─ JwtService.java
│   │  │  ├─ SHA256.java
│   │  │  └─ WebSecurityConfig.java
│   ├─ exception
│   │  ├─ BaseException.java
│   │  └─ BaseResonseStatus.java
│   └─ util
│      └─ ValidationRegex.java
├─ 📦 domain
│   ├─ board 
│   │  ├─ controller
│   │  ├─ dao
│   │  ├─ service
│   │  └─ model
│   ├─ fcm 
│   ├─ s3
│   ├─ social
│   ├─ user
└─  └─ quiz
```

## Issues

* [💡 반복적인 validation 처리를 위한 ValidateUtils 생성 ]()  
* [💡 소셜로그인 시 중복된 이메일 가입을 허용하는 문제 ]() 

## App Description

![badge](https://img.shields.io/badge/Part-Back--end-brightgreen) 
![react](https://img.shields.io/badge/Tech--stack-Spring-orange) 

 ```스플래시 →  회원가입 →  카테고리 설정```
<img width="900" alt="스크린샷 2022-11-23 오후 4 55 07" src="https://user-images.githubusercontent.com/97823928/203495983-6543ba02-4f99-478e-8445-2d43e408062b.png">

* 스플래쉬가 끝나면 로그인 화면이 나온다.
* 회원가입 시 서비스 이용 약관, 개인정보 수집 및 이용 등의 권한을 요청한다.
* 회원가입 시 이메일, 패스워드, 닉네임을 입력해야하며, 이메일과 닉네임은 중복검사를 수행한다.
* 사용자는 관심 카테고리 3개를 설정하게 되며, 설정된 카테고리에 따라 메인화면에서 문제를 추천 받는다.
 
```퀴즈 풀기```  
<img width="590" alt="스크린샷 2022-11-23 오후 5 07 56" src="https://user-images.githubusercontent.com/97823928/203497732-d54b3fcc-9f1f-4c08-a6db-f4072f2b42f7.png">

* 사용자가 선택한 카테고리에 맞게 퀴즈를 풀 수 있다
* 정답을 맞힌 경우 10 포인트를 획득한다.
* 하루에 풀 수 있는 문제 수는 제한되어 있지 않으나 포인트는 150으로 제한한다.

```퀴즈 등록 ```    
<img width="889" alt="스크린샷 2022-11-23 오후 6 04 05" src="https://user-images.githubusercontent.com/97823928/203507278-f4f59a9e-035c-4888-84ce-fe4f938370b3.png">

* 사용자는 객관식, 주관식의 퀴즈를 낼 수 있으며, 하나의 문제 당 10포인트 획득하게 된다.
* 출제할 수 있는 문제는 10개로 제한한다.

```오답 복습 ```      
<img width="309" alt="스크린샷 2022-11-23 오후 5 03 30" src="https://user-images.githubusercontent.com/97823928/203497138-95403a02-67d5-494f-a716-2ff00d272285.png">
* 당일에 틀린 오답은 그 다음날 풀 수 있다.
* 문제를 다 풀었을 경우 맞춘 개수 여부와 상관없이 10포인트를 획득하게 된다.
