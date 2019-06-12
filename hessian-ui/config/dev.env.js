var merge = require('webpack-merge')
var prodEnv = require('./prod.env')
//设置了开发环境变量。
module.exports = merge(prodEnv, {
  NODE_ENV: '"development"'
})
