const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 도커 컨테이너가 실행 중인 포트로 수정
        changeOrigin: true,
        secure: false, // HTTPS를 사용하지 않는 경우 false
        pathRewrite: {
          '^/api': '' // '/api' 경로를 백엔드에서 사용하도록 설정
        }
      }
    }
  }
})
