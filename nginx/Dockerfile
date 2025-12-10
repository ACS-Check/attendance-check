FROM nginx:alpine

WORKDIR /usr/share/nginx/html

# Copy reverse proxy configuration
COPY ./default.conf /etc/nginx/conf.d/default.conf

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
	chown -R 65532:65532 /var/cache/nginx /var/log/nginx /var/run; \
	chmod -R 755 /var/cache/nginx

EXPOSE 80

# Tomcat 컨테이너와 맞춰서 UID 65532(non-root) 사용
USER 65532

# Nginx 포그라운드 실행
ENTRYPOINT ["/usr/sbin/nginx", "-g", "daemon off;"]
