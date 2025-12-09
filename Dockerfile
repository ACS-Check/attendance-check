# -------------------------------
# 공통 ARG: Tomcat / JDK 버전
# -------------------------------
ARG TOMCAT_VERSION=10.1.31
ARG TOMCAT_MAJOR=10
ARG JDK_MAJOR=17

#############################
# Stage 1: Maven 빌드 - WAR 생성
#############################
FROM maven:3.9.9-eclipse-temurin-${JDK_MAJOR} AS build
ARG JDK_MAJOR
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests clean package

#############################
# Stage 1.5: JSP 사전 컴파일 + WAR 슬림화
# - eclipse-temurin JDK 위에서 JSP를 미리 컴파일해서
#   런타임에서 JSP 컴파일 비용/의존성을 제거하는 단계
#############################
FROM eclipse-temurin:${JDK_MAJOR}-jdk AS jspc
ARG JDK_MAJOR
ARG TOMCAT_VERSION
WORKDIR /work

# Stage 1에서 빌드된 WAR 가져오기
COPY --from=build /app/target/attendance.war /work/app.war

# JSP 컴파일에 필요한 Tomcat/Jakarta/ECJ/Ant 의존 jar 수동 다운로드 + JSP precompile + WAR 슬림화
RUN set -eux; \
    mkdir -p /opt/jspc; cd /opt/jspc; \
    curl -fSL -o tomcat-jasper.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-jasper/${TOMCAT_VERSION}/tomcat-jasper-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o tomcat-jasper-el.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-jasper-el/${TOMCAT_VERSION}/tomcat-jasper-el-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o tomcat-juli.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-juli/${TOMCAT_VERSION}/tomcat-juli-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o tomcat-api.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-api/${TOMCAT_VERSION}/tomcat-api-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o tomcat-util.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-util/${TOMCAT_VERSION}/tomcat-util-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o tomcat-util-scan.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-util-scan/${TOMCAT_VERSION}/tomcat-util-scan-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o catalina.jar "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-catalina/${TOMCAT_VERSION}/tomcat-catalina-${TOMCAT_VERSION}.jar"; \
    curl -fSL -o ecj.jar "https://repo1.maven.org/maven2/org/eclipse/jdt/ecj/3.37.0/ecj-3.37.0.jar";\
    curl -fSL -o ant.jar "https://repo1.maven.org/maven2/org/apache/ant/ant/1.10.14/ant-1.10.14.jar"; \
    curl -fSL -o jakarta-el-api.jar "https://repo1.maven.org/maven2/jakarta/el/jakarta.el-api/5.0.0/jakarta.el-api-5.0.0.jar"; \
    curl -fSL -o jsp-api.jar "https://repo1.maven.org/maven2/jakarta/servlet/jsp/jakarta.servlet.jsp-api/3.1.1/jakarta.servlet.jsp-api-3.1.1.jar"; \
    curl -fSL -o servlet-api.jar "https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar"; \
    mkdir -p /work/war; cd /work/war; \
    jar xf /work/app.war; \
    mkdir -p /work/war/WEB-INF/classes; \
    CLASSPATH="/opt/jspc/tomcat-jasper.jar:/opt/jspc/tomcat-jasper-el.jar:/opt/jspc/tomcat-juli.jar:/opt/jspc/tomcat-api.jar:/opt/jspc/tomcat-util.jar:/opt/jspc/tomcat-util-scan.jar:/opt/jspc/catalina.jar:/opt/jspc/jakarta-el-api.jar:/opt/jspc/jsp-api.jar:/opt/jspc/servlet-api.jar:/opt/jspc/ecj.jar:/opt/jspc/ant.jar:/work/war/WEB-INF/lib/*"; \
    # JSP를 미리 class로 컴파일 (JspC 사용)
    java -cp "$CLASSPATH" org.apache.jasper.JspC \
        -uriroot /work/war \
        -d /work/war/WEB-INF/classes \
        -p org.apache.jsp \
        -trimSpaces \
        -compile \
        -webinc /work/jsp-web.xml; \
    # 기존 web.xml 뒤에 JSP 관련 webinc 내용 붙여 넣기
    if [ -f /work/war/WEB-INF/web.xml ]; then \
        sed -i 's@</web-app>@@' /work/war/WEB-INF/web.xml; \
        cat /work/jsp-web.xml >> /work/war/WEB-INF/web.xml; \
        echo '</web-app>' >> /work/war/WEB-INF/web.xml; \
    fi; \
    # JSP, tag 파일 제거 (런타임에 JSP 소스 필요 없음)
    find /work/war -type f \( -name "*.jsp" -o -name "*.tag" -o -name "*.tagx" \) -delete; \
    # WEB-INF/lib 아래 각 jar에서도 메이븐 메타/라이선스/서명 파일 제거해서 용량 줄이기
    for j in /work/war/WEB-INF/lib/*.jar; do \
        mkdir -p /tmp/libwork && cd /tmp/libwork; \
        jar xf "$j"; \
        rm -rf META-INF/maven || true; \
        find META-INF -type f \( -name '*.SF' -o -name '*.DSA' -o -name '*.RSA' -o -iname 'LICENSE*'-o -iname 'NOTICE*' -o -iname 'README*' \) -delete || true; \
        jar cf "$j" .; \
        cd /; rm -rf /tmp/libwork; \
    done; \
    # WAR 재패키징
    cd /work/war; \
    jar cf /work/attendance-precompiled.war .; \
    # 최종 WAR에서 META-INF 불필요 파일 재정리
    mkdir -p /tmp/warwork; cd /tmp/warwork; \
    jar xf /work/attendance-precompiled.war; \
    rm -rf META-INF/maven || true; \
    find META-INF -type f -iname 'license*' -delete || true; \
    find META-INF -type f -iname 'notice*' -delete || true; \
    find META-INF -type f -iname 'readme*' -delete || true; \
    find META-INF -type f \( -name '*.SF' -o -name '*.DSA' -o -name '*.RSA' \) -delete || true; \
    jar cf /work/attendance-precompiled.war .; \
    cd /; rm -rf /tmp/warwork

#############################
# Stage 2: Tomcat 다운로드 + 최소화(prune)
# - Debian 위에서 Tomcat tarball 받아서
#   안 쓰는 webapps / docs / examples / jar 파일 등 제거
#############################
FROM debian:bookworm-slim AS tomcat-builder
ARG TOMCAT_VERSION
ARG TOMCAT_MAJOR

RUN set -eux; \
    apt-get update && apt-get install -y --no-install-recommends curl ca-certificates tar unzip zip;\
    mkdir -p /opt; \
    file="apache-tomcat-${TOMCAT_VERSION}.tar.gz"; \
    url_archive="https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/bin/${file}"; \
    curl -fSL "$url_archive" -o /tmp/tomcat.tar.gz; \
    tar -xzf /tmp/tomcat.tar.gz -C /opt; \
    mv "/opt/apache-tomcat-${TOMCAT_VERSION}" /opt/tomcat; \
    rm /tmp/tomcat.tar.gz; \
    # APR 관련 리스너 제거 (네이티브 의존도 낮추기 목적)
    sed -i '/AprLifecycleListener/d' /opt/tomcat/conf/server.xml; \
    # 기본 webapps, 문서, 예제 제거 (ROOT 포함)
    rm -rf /opt/tomcat/webapps/* /opt/tomcat/webapps.dist /opt/tomcat/docs /opt/tomcat/ROOT /opt/tomcat/examples; \
    # bat, sh 스크립트 제거 (우리 컨테이너에서는 java CMD로 직접 실행)
    rm -rf /opt/tomcat/bin/*.bat /opt/tomcat/bin/*.sh; \
    # 다국어 리소스 제거
    rm -rf /opt/tomcat/lib/tomcat-i18n-*; \
    # txt/md 문서 제거
    find /opt/tomcat -name '*.txt' -delete; \
    find /opt/tomcat -name '*.md' -delete; \
    cd /opt/tomcat/lib; \
    # JSP 컴파일용 ECJ, jasper 계열, 클러스터링/tribes 관련 jar 제거
    rm -f ecj-*.jar; \
    rm -f tomcat-jasper*.jar tomcat-jasper-el*.jar; \
    rm -f catalina-jdbc.jar; \
    rm -f catalina-tribes.jar catalina-ha.jar catalina-storeconfig.jar tomcat-jni.jar; \
    # 남은 jar 들에서도 메타/라이선스/서명 파일 삭제 -> jar 재압축으로 용량 줄이기
    for j in *.jar; do \
        mkdir -p /tmp/jwork && cd /tmp/jwork; \
        unzip -q /opt/tomcat/lib/$j; \
        find . -type f -iname 'license*' -delete; \
        find . -type f -iname 'notice*' -delete; \
        find . -type f -iname 'readme*' -delete; \
        rm -rf META-INF/maven || true; \
        find META-INF -type f \( -name '*.SF' -o -name '*.DSA' -o -name '*.RSA' \) -delete || true; \
        zip -q -9 -r /opt/tomcat/lib/$j .; \
        cd /opt/tomcat/lib; rm -rf /tmp/jwork; \
    done; \
    chmod -R a+rX /opt/tomcat; \
    # 빌드 도구 제거
    apt-get purge -y --auto-remove curl unzip zip; \
    rm -rf /var/lib/apt/lists/*

# JSP 사전 컴파일 완료된 WAR을 ROOT.war로 배치
COPY --from=jspc /work/attendance-precompiled.war /opt/tomcat/webapps/ROOT.war

#############################
# Stage 3: Minimal JRE 생성 (jlink)
# - Alpine + OpenJDK JDK에서 필요한 모듈만 포함한 JRE 생성
# - 여기서 tzdata를 같이 설치해서 타임존 파일 확보
#############################
FROM alpine:3.20 AS jre-builder
ARG JDK_MAJOR
# [시간대 관련] tzdata 추가 설치 (Asia/Seoul 타임존 파일 얻기 위함)
RUN apk add --no-cache openjdk${JDK_MAJOR}-jdk binutils tzdata
ENV JAVA_HOME=/usr/lib/jvm/java-${JDK_MAJOR}-openjdk

RUN set -eux; \
    $JAVA_HOME/bin/jlink \
        --module-path $JAVA_HOME/jmods \
        --add-modules java.base,java.logging,java.naming,java.sql,java.xml,java.management,java.desktop,java.instrument,java.security.jgss,java.security.sasl,jdk.crypto.ec,jdk.unsupported \
        --strip-debug \
        --no-man-pages \
        --no-header-files \
        --compress=2 \
        --output /opt/jre; \
    # JRE 내부 .so 들도 strip해서 약간 더 경량화
    find /opt/jre -type f -name '*.so' -exec strip --strip-unneeded {} + || true

#############################
# Stage 4: 런타임 트리 정리 (권한 + 불필요 파일 제거)
#############################
FROM alpine:3.20 AS runtime-prep
ENV CATALINA_HOME=/opt/tomcat JAVA_HOME=/opt/jre

# jlink로 만든 JRE + 최소화된 Tomcat 복사
COPY --from=jre-builder /opt/jre /opt/jre
COPY --from=tomcat-builder /opt/tomcat /opt/tomcat

RUN set -eux; \
    # Tomcat 로그/워크/temp 디렉터리 비우기
    rm -rf /opt/tomcat/logs/* /opt/tomcat/work/* /opt/tomcat/temp/*; \
    # server.xml 안의 주석 블록 제거 (파일 좀 더 "슬림"하게)
    sed -i '/^[[:space:]]*<!--/,/-->/d' /opt/tomcat/conf/server.xml || true; \
    # JRE 불필요 메타 제거
    rm -rf /opt/jre/legal /opt/jre/release; \
    # temp / work 디렉터리 재생성
    mkdir -p /opt/tomcat/temp /opt/tomcat/work; \
    # non-root 사용자를 위한 소유권 변경 (UID=65532 사용)
    chown -R 65532:65532 /opt/tomcat /opt/jre

#############################
# Stage 5: 최종 런타임 이미지 (scratch)
# - 진짜 실행에 필요한 파일만 넣는 최종 단계
# - 여기서 TZ=Asia/Seoul + /etc/localtime 설정으로 한국 시간대 적용
#############################
FROM scratch

# [시간대 관련] TZ 환경 변수로 기본 타임존 설정
ENV CATALINA_HOME=/opt/tomcat \
    JAVA_HOME=/opt/jre \
    TZ=Asia/Seoul \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -Djava.security.egd=file:/dev/./urandom"

# jlink JRE가 의존하는 기본 라이브러리들 복사
COPY --from=jre-builder /lib/ld-musl-x86_64.so.1 /lib/ld-musl-x86_64.so.1
COPY --from=jre-builder /lib/libz.so.1 /lib/libz.so.1
COPY --from=jre-builder /usr/lib/libgcc_s.so.1 /usr/lib/libgcc_s.so.1
COPY --from=jre-builder /usr/lib/libstdc++.so.6 /usr/lib/libstdc++.so.6

# [시간대 관련] Alpine(tzdata)에서 가져온 Asia/Seoul 타임존 파일을 /etc/localtime으로 복사
#  → OS 레벨 시간도 KST로 인식
COPY --from=jre-builder /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# 준비된 JRE + Tomcat 복사
COPY --from=runtime-prep /opt/jre /opt/jre
COPY --from=runtime-prep /opt/tomcat /opt/tomcat

WORKDIR /opt/tomcat
EXPOSE 8080

# non-root (65532, 예: nobody/nogroup 스타일)로 실행
USER 65532

# Tomcat을 java 명령으로 직접 기동
CMD ["/opt/jre/bin/java","-Dcatalina.home=/opt/tomcat","-Dcatalina.base=/opt/tomcat","-Djava.io.tmpdir=/opt/tomcat/temp","-Djava.util.logging.config.file=/opt/tomcat/conf/logging.properties","-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager","-Djava.awt.headless=true","-cp","/opt/tomcat/bin/*","org.apache.catalina.startup.Bootstrap","start"]
