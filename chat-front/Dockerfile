# 사용할 Node.js 이미지 지정
FROM node:18-alpine AS build

# 작업 디렉터리 설정
WORKDIR /app

# 패키지 파일 복사 및 설치
COPY package*.json ./
RUN npm install

# 모든 소스 코드 복사
COPY . .

# Vue.js 프로젝트 빌드
RUN npm run build

# Nginx 이미지를 사용하여 빌드된 파일 서빙
FROM nginx:stable-alpine

# Nginx 기본 설정 제거
RUN rm -rf /etc/nginx/conf.d

# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# 빌드된 정적 파일 복사
COPY --from=build /app/dist /usr/share/nginx/html

# 80번 포트 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]