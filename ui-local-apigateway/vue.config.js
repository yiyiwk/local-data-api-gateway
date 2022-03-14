module.exports = {
	publicPath:'/sharkapiweb/',
	outputDir:"dist",
	assetsDir: "assets",
	productionSourceMap: false,	
	filenameHashing: true,
	configureWebpack:{
		externals:{
			
		}
	}
	// ,devServer:{
	// 	open: false,
	// 	host: 'localhost',
	// 	// 端口
	// 	port: 8080,
	// 	// https
	// 	https: false,
	// 	// 热更新
	// 	hotOnly: false,
	// 	proxy:{
	// 		'/apis': {
	// 			// 目标代理服务器地址
	// 			'target': 'http://127.0.0.1:7999',
	// 			// 开启代理，本地创建一个虚拟服务器 允许跨域
	// 			'changeOrigin': true,
	// 			"secure" : false,
	// 			'pathRewrite' : {
	// 				"^/apis" : ""
	// 			}
	// 		},
	// 	}
	// }
}