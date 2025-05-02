# ☁️ AWS 기반 프로젝트 배포 및 설정 정리
## 🔹 12-1. EC2 (Elastic Compute Cloud)

* **설정 요약**

    * EC2 인스턴스 타입: `t2.medium`
    * 플랫폼: Amazon Linux 2
    * 탄력적 IP 할당 완료
    * 인바운드 보안 그룹: 8080 포트 오픈 (애플리케이션 접근용)

* **어플리케이션 실행**

  ```bash
  nohup java -jar your-app.jar > app.log 2>&1 &
  ```

* **Health Check API**

    * URL: `http://<Elastic-IP>:8080/health`
    * 응답 예시:

      ```json
      {
        "status": "OK"
      }
      ```
    * 누구나 접근 가능하며, 서버 상태 점검에 사용됩니다.

![EC2 설정.png](photo/EC2%20%EC%84%A4%EC%A0%95.png)
![헬스체크 응답 성공.png](photo/%ED%97%AC%EC%8A%A4%EC%B2%B4%ED%81%AC%20%EC%9D%91%EB%8B%B5%20%EC%84%B1%EA%B3%B5.png)
---

## 🔹 12-2. RDS (Relational Database Service)

* **설정 요약**

    * 엔진: MySQL 8.0
    * DB 이름: `delivery`
    * 사용자: `root`
    * 보안 그룹: EC2의 IP만 허용

* **연결 확인**

    * EC2에서 MySQL CLI 접속

      ```bash
      mysql -h <RDS-endpoint> -u root -p
      ```

![RDS 설정.png](photo/RDS%20%EC%84%A4%EC%A0%95.png)

---

## 🔹 12-3. S3 (Simple Storage Service)

* **기능**

    * 유저 프로필 이미지 업로드/삭제 API 구현
    * 파일 이름은 UUID 기반으로 생성
    * 이미지 MIME 타입 검사 및 필터링
    * URL은 S3 public URL 포맷으로 반환

* **API 요약**

    * `POST /api/v1/users/profile-image`

        * 요청: 멀티파트 이미지 파일
        * 응답: 이미지 URL
    * `DELETE /api/v1/users/profile-image`

        * 요청: 없음 (로그인 유저 기준 삭제)

![S3 설정.png](photo/S3%20%EC%84%A4%EC%A0%95.png)

---
## 🔹 13. 대용량 데이터 처리

* **데이터 생성**

    * 유저 데이터 100만 건을 테스트 코드로 삽입
    * 닉네임은 UUID 기반으로 랜덤하게 생성하여 중복 최소화


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generateMillionUsers() {
        int total = 1_000_000;
        int batchSize = 2000;
        List<User> users = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            String email = "test" + i + "@example.com";
            String nickname = "user_" + UUID.randomUUID().toString().substring(0, 8);
            String rawPassword = "Test1234!";
            String encodedPassword = "encoded-pass";

            users.add(User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .build());

            if (users.size() == batchSize) {
                userRepository.saveAll(users);
                users.clear();
                System.out.println((i + 1) + " users inserted...");
            }
        }

        if (!users.isEmpty()) {
            userRepository.saveAll(users);
        }

        System.out.println("총 " + total + "명의 유저 삽입 완료");
    }

* **검색 API**

    * API: GET /users/list?nickname=user_7102abad
    * 조건: 닉네임 정확히 일치해야 함
    * 인덱싱 : CREATE INDEX idx_nickname ON users(nickname);
    * DB 쿼리: SELECT * FROM users WHERE nickname = ?


* **인덱싱 전**
*![인덱싱 전 결과.png](photo/%EC%9D%B8%EB%8D%B1%EC%8B%B1%20%EC%A0%84%20%EA%B2%B0%EA%B3%BC.png) 


* **인덱싱 후**
* ![인덱싱 후 결과.png](photo/%EC%9D%B8%EB%8D%B1%EC%8B%B1%20%ED%9B%84%20%EA%B2%B0%EA%B3%BC.png)

* **복잡한 쿼리에서 조회 성능 개선**
* https://sukh115.tistory.com/100