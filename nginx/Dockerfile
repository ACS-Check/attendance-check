# ----------------------------------------
# 공통 ARG: Nginx 버전
#  - 필요하면 build 시 --build-arg NGINX_VERSION=1.xx.x 로 덮어쓰기
# ----------------------------------------
ARG NGINX_VERSION=1.26.2

########################################
# Stage 1: Nginx 소스 빌드 + 타임존 파일 준비
# - Alpine 위에서 Nginx를 직접 컴파일해서
#   필요한 바이너리/라이브러리/타임존만 뽑아내는 단계
########################################
FROM alpine:3.20 AS builder
# 이 스테이지 안에서 NGINX_VERSION 사용 선언
ARG NGINX_VERSION

# 빌드 도구 & 의존성 & 타임존(tzdata) 설치
RUN apk add --no-cache \
    build-base \
    pcre2-dev \
    zlib-dev \
    openssl-dev \
    linux-headers \
    curl \
    tzdata

WORKDIR /tmp

# -------------------------------
# Nginx 소스 다운로드 및 빌드
#  - prefix, 로그 위치, pid 파일 위치 등 최소만 설정
#  - 사용하지 않는 모듈은 과감히 끔
# -------------------------------
RUN set -eux; \
    curl -fSL "http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz" -o nginx.tar.gz; \
    tar xzf nginx.tar.gz; \
    cd nginx-${NGINX_VERSION}; \
    ./configure \
      --prefix=/etc/nginx \
      --sbin-path=/usr/sbin/nginx \
      --conf-path=/etc/nginx/nginx.conf \
      --error-log-path=/var/log/nginx/error.log \
      --http-log-path=/var/log/nginx/access.log \
      --pid-path=/var/run/nginx.pid \
      --with-http_gzip_static_module \
      --with-pcre-jit \
      --without-http_scgi_module \
      --without-http_uwsgi_module \
      --without-http_fastcgi_module \
      --without-http_autoindex_module \
      --without-http_browser_module; \
    make && make install; \
    # 바이너리 스트립해서 용량 더 줄이기
    strip /usr/sbin/nginx; \
    # 빌드 중간 산출물 정리
    rm -rf /tmp/*

# -------------------------------
# Nginx 디렉토리 정리
#  - 기본 HTML 문서 제거
#  - conf.d 디렉토리 생성 (우리 설정용)
#  - 로그/런 디렉토리 생성
# -------------------------------
RUN set -eux; \
    rm -rf /usr/share/nginx/html/* || true; \
    mkdir -p /etc/nginx/conf.d; \
    mkdir -p /var/log/nginx /var/run; \
    # 나중에 scratch에서 UID=65532로 쓸 예정이라 미리 소유권 조정
    chown -R 65532:65532 /etc/nginx /var/log/nginx /var/run

########################################
# Stage 2: 최종 런타임 이미지 (scratch)
# - 진짜 실행에 필요한 파일만 넣는 단계
# - 한국 시간대(Asia/Seoul) 적용
########################################
FROM scratch

# [시간대 관련] 기본 타임존 환경변수 설정
ENV TZ=Asia/Seoul

# -------------------------------
# 바이너리/설정/로그 디렉토리 복사
# -------------------------------
COPY --from=builder /usr/sbin/nginx /usr/sbin/nginx
COPY --from=builder /etc/nginx /etc/nginx
COPY --from=builder /var/log/nginx /var/log/nginx
COPY --from=builder /var/run /var/run

# [시간대 관련]
#  - tzdata에서 가져온 Asia/Seoul 파일을 /etc/localtime 으로 복사
#  - OS 레벨 시간도 KST로 인식하게 됨
COPY --from=builder /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# -------------------------------
# 프로젝트용 Nginx 설정 복사
#  - 빌드 컨텍스트에 default.conf 있어야 함
#  - 예) proxy_pass http://tomcat-service:8080;
# -------------------------------
COPY default.conf /etc/nginx/conf.d/default.conf

# Bring in build context (dist optional)
COPY . /tmp/context
RUN set -eux; \
	rm -rf /usr/share/nginx/html/*; \
	if [ -d /tmp/context/dist ]; then \
		cp -R /tmp/context/dist/. /usr/share/nginx/html/; \
	else \
		printf '<!doctype html>\n<html><head><meta charset="UTF-8"><title>attendance-nginx</title></head><body><h1>nginx proxy ready</h1><p>dist/ 폴더가 비어 있어 기본 페이지를 노출합니다.</p></body></html>\n' > /usr/share/nginx/html/index.html; \
	fi; \
	rm -rf /tmp/context; \
	mkdir -p /var/cache/nginx/client_temp /var/cache/nginx/proxy_temp /var/cache/nginx/fastcgi_temp /var/cache/nginx/uwsgi_temp /var/cache/nginx/scgi_temp; \
	chown -R nginx:nginx /var/cache/nginx /var/log/nginx /etc/nginx/conf.d; \
	chmod -R 755 /var/cache/nginx; \
	touch /var/run/nginx.pid; \
	chown nginx:nginx /var/run/nginx.pid

EXPOSE 80

# Tomcat 컨테이너와 맞춰서 UID 65532(non-root) 사용
USER 65532

# Nginx 포그라운드 실행
ENTRYPOINT ["/usr/sbin/nginx", "-g", "daemon off;"]
