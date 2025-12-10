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
	rm -rf /tmp/context

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
