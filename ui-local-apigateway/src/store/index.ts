import { createStore } from 'vuex'
import {objDeepClone } from '@/utils/tools';
//用户初始化数据状态
const userEmpty: any = {
	firstIn: 0,
	token: "",	
	username: "",
	
};



const store=createStore({
	state:{
		userInfo:JSON.parse((localStorage.getItem("saveLoginInfo") as string)) || objDeepClone(userEmpty),
		
	},
	getters:{},
	mutations:{
		userLogon( state: any, val: any){
			//修改用户信息
			for(const key in state.userInfo){
				if(val[key]){
					state.userInfo[key]=val[key]
				}
			}
			
		},
		userLogout( state: any){
			//修改用户信息
			state.userInfo = objDeepClone( userEmpty);
			localStorage.removeItem('saveLoginInfo')
		},
		
		
	},
	actions:{},
  
})

export default store