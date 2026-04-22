module.exports = {
  lintOnSave: false,
  devServer: {
    port: 8083,
    proxy: {
      '/api': {
        target: 'http://localhost:9095',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      },
      '/videos': {
        target: 'http://localhost:9095',
        changeOrigin: true,
        pathRewrite: {
          '^/videos': '/videos'
        }
      },
      '/images': {
        target: 'http://localhost:9095',
        changeOrigin: true,
        pathRewrite: {
          '^/images': '/images'
        }
      }
    }
  }
}