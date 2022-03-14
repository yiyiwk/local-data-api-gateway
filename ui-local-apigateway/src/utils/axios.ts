import axios from 'axios';
import store from '../store'

// const baseurl="http://192.168.1.132:6888/sharkapiweb/uiservice/"
// const baseurl="http://127.0.0.1:7999/"
const baseurl="/sharkapiweb/uiservice/"
const userInfo: any = ( store && store.state && store.state.userInfo ) || {};

const instance = axios.create({
	method: "post",
	// baseURL:"/apis/sharkapiweb/uiservice/inside/",
	baseURL:baseurl,
	timeout:50000,
	// headers:{'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*','token':'fsdfsdfsd'}
})

function requestCallback(config:any,isSuccess:boolean,promiseCallback:any,outData:any){
	
	if(isSuccess){		
		config.success && config.success(outData);
		console.log(config.loadmsg + '=>success=>', outData);
	}else{		
		config.fail && config.fail(outData);				
		console.log(config.loadmsg + '=>fail=>', outData);
	}
	config.complete && config.complete(outData);
	promiseCallback && promiseCallback(outData);
}

function ds_api(config:any){
	
	const promiseObj=new Promise((resolve,reject)=>{
		
		const errObj:any={
			errMsg:''
		}
		if (!config.url || config.url == '') {
			errObj.errMsg = '参数[url]不能为空';
			requestCallback(config, false, reject, errObj);
			return;
		}

		console.log(config.loadmsg + '=>params=>', config.data);				
		console.log('/'+config.url);
		instance({
			url:config.url,
			headers:{
				'Content-Type': 'application/json',
				'Access-Control-Allow-Origin': '*',
				'token':userInfo.token
			},
			data:config.data || {}
		}).then((res:any)=>{			
			if (res.status != 200) {
				errObj.errMsg += 'request fail';										
				requestCallback(config, false, reject, errObj);
				return
			}
			if(res.data.code==93){
				store.commit("userLogout")
				window.location.reload();
			}
			if (res.data.code!='0') {
				errObj.errData=res.data
				if(res.data.msg!="" && res.data.msg!=null){
					errObj.errMsg = res.data.msg;
				}else if(res.errMsg && res.errMsg!=""){
					errObj.errMsg=res.errMsg
				}else{
					errObj.errMsg= res.data.message
				}
				
				requestCallback(config, false, reject, errObj);
				return;
			}
			requestCallback(config,true,resolve, res);

		}).catch((err:any)=>{
			requestCallback(config, false, reject, {errMsg:err.message,errData:err});
		})


	})
	
	
	return promiseObj.then((res)=>{
		return [null,res]
	}).catch((err) => {
		return [err];
	});
}


export default ds_api



















