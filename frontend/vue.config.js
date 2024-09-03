const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        liveReload: true,
        proxy: {
          "/api": {
            "target": "http://localhost:8989",
            "secure": false,
            "pathRewrite": {
              "^/api": ""
            },
            "changeOrigin": true
          }
        },
    }
})
