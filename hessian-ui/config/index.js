// see http://vuejs-templates.github.io/webpack for documentation.
var path = require('path')
//描述了开发和构建两种环境下的配置，前面的build文件夹下也有不少文件引用了index.js里面的配置。
module.exports = {
  build: {
    //  // 构建产品时使用的配置
    env: require('./prod.env'),
    index: path.resolve(__dirname, '../src/main/resources/static/index.html'),    // html入口文件
    assetsRoot: path.resolve(__dirname, '../src/main/resources/static/'),    // 产品文件的存放路径
    assetsSubDirectory: 'static',    // 二级目录，存放静态资源文件的目录，位于dist文件夹下
    // 发布路径，如果构建后的产品文件有用于发布CDN或者放到其他域名的服务器，可以在这里进行设置
    // 设置之后构建的产品文件在注入到index.html中的时候就会带上这里的发布路径
    assetsPublicPath: '/',
    productionSourceMap: true,    // 是否使用source-map
    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,    // 是否开启gzip压缩
    productionGzipExtensions: ['js', 'css'],    // gzip模式下需要压缩的文件的扩展名，设置js、css之后就只会对js和css文件进行压缩
    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report    // 是否展示webpack构建打包之后的分析报告
  },
  // 开发过程中使用的配置
  dev: {
    env: require('./dev.env'),    // 环境变量
    port: 3000,    // dev-server监听的端口
    autoOpenBrowser: false,    // 是否自动打开浏览器
    assetsSubDirectory: 'static',    // 静态资源文件夹
    assetsPublicPath: '/',    // 发布路径
    // 代理配置表，在这里可以配置特定的请求代理到对应的API接口
    // 例如将‘localhost:8080/api/xxx‘代理到‘www.example.com/api/xxx‘
    proxyTable: {},
    // CSS Sourcemaps off by default because relative paths are "buggy"
    // with this option, according to the CSS-Loader README
    // (https://github.com/webpack/css-loader#sourcemaps)
    // In our experience, they generally work as expected,
    // just be aware of this issue when enabling this option.
    cssSourceMap: false    // 是否开启 cssSourceMap
  }
}
