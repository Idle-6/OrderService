# java 애플리케이션 실행을 위한 jdk17 버전의 베이스 이미지 구성
FROM meddream/jdk17:latest

# ./build/libs/ 디렉터리의 모든 *SNAPSHOT.jar 파일을 Docker 이미지 내의 cicdtest.jar 이름으로 복사
COPY ./build/libs/*SNAPSHOT.jar cicdtest.jar

# 컨테이너가 실행될 때 기본적으로 실행될 명령을 정의 java -jar cicdtest.jar
ENTRYPOINT ["java", "-jar", "cicdtest.jar"]

# ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "cicdtest.jar"] 
# 다음과 같이 실행 옵션을 추가로 줄 수도 있습니다