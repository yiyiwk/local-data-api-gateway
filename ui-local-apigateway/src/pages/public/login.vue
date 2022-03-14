<template>
	<div class="parent">
		<div class="login-header">
			<div><a href="https://www.511ds.com/" target="_blank"><img :src="$sharkWebPath+'ressource/images/logo.png'" class="login-img"><span class="web-title">迪赛智慧数可视化互动平台</span></a></div>
			<div><a href="https://www.511ds.com/" class="guan-wang color-base" target="_blank">访问官网</a></div>
		</div>
		<div class="login-content parent">
			<div class="banner-img-box"><img :src="$sharkWebPath+'ressource/images/loginbg.jpg'"></div>
			<div class="login-box-warp content-center">
				<div class="login-box">
					<div class="login-title">本地数据API网关</div>
					<a-form :model="loginInfo" :rules="rules" @finish="tapUserLogin">
						<a-form-item ref="mobile"  name="mobile">
							<a-input placeholder="请输入用户名"  style="width:336px;height:40px;" :disabled="true" v-model:value="loginInfo.mobile" :allowClear="loginInfo.mobile.length>0">
								<template #prefix>
									<user-outlined type="user" />
								</template>
							</a-input>
						</a-form-item>
						<a-form-item ref="password"  name="password">
							<a-input-password placeholder="请输入密码"  style="width:336px;height:40px;" v-model:value="loginInfo.password">
								<template #prefix>
									<unlock-outlined type="unlock" />
								</template>
							</a-input-password>
						</a-form-item>
						<a-form-item ref="reNewPassword"  name="reNewPassword" v-if="modifyPwd">
							<a-input-password placeholder="请输入密码"  style="width:336px;height:40px;" v-model:value="loginInfo.reNewPassword">
								<template #prefix>
									<unlock-outlined type="unlock" />
								</template>
							</a-input-password>
						</a-form-item>
						<div class="save-pass-font">
							<a-popover v-model:visible="popover.showCheckTips" trigger="contextmenu"  placement="leftTop">
								<template #content>
									请阅读并接受协议内容
								</template>
								<a-checkbox v-model:checked="agreeChecked" @change="()=>agreeChecked?popover.showCheckTips=false:popover.showCheckTips=true">&nbsp;</a-checkbox>
							</a-popover>
							已阅读并同意
							<span class="color-base cursor" @click="goAgreementPage(1)">《用户服务协议》</span>
							和
							<span class="color-base cursor" @click="goAgreementPage(2)">《用户隐私协议》</span>
						</div>
						<div class="pt17">
							<a-popover v-model:visible="popover.visible" trigger="contextmenu" color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
								<template #content>
									{{popover.message}}
								</template>
								<a-button class="login-btn"  size="large" html-type="submit" block :loading="loading">
									{{modifyPwd?"保存":"登录"}}
								</a-button>
							</a-popover>
						</div>
						
						<div class="login-tips color-base" v-if="!modifyPwd">
							说明：1、登录默认用户名dsadmin，密码为空。<br />&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、用户名和密码不同于<a href="https://www.511ds.com/" target="_blank">迪赛智慧数可视化互动平台</a>。<br />

						</div>
					</a-form>
				</div>
				
				
			</div>
		</div>
		<div class="login-footer color-base">
			技术支持：广东迪特赛恩软件技术有限公司     客服电话：400-641-8858
		</div>
	</div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import { mapState,mapMutations} from 'vuex';
import { UserOutlined,UnlockOutlined} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
// import { dateFormat } from '@/utils/tools';


export default defineComponent({
	name:'PublicLogin',
	components:{
		UserOutlined,
		UnlockOutlined,		
		
	},
	data(){
		return {
			loginInfo:{
				mobile:"dsadmin",
				password:"",
				reNewPassword:"",
				firstIn:0
			},
			popover: {visible:false,showCheckTips:false},
			agreeChecked:true,
			loading:false,
			modifyPwd:false
		}
	},
	
	mounted(){
		//this.getData()
	},
	activated(){
		
	},
	methods:{
		...mapMutations(['userLogon']),
		
		tapUserLogin(){
			if(!this.agreeChecked){
				this.popover.showCheckTips=true
				return
			}
			if(this.modifyPwd){
				this.resetPassword()
				return
			}
			let password=this.loginInfo.password
			this.$dsApi({
				url:"User/getUser",
				loadmsg:"登录",
				data:{
					username:"dsadmin",
					password:password					
				},
				success:(res:any)=>{
					let sobj=res.data.data
					if(sobj.firstIn===0 || sobj.firstIn=="0"){
						this.modifyPwd=true
						this.loginInfo.firstIn=0
						this.loginInfo.password=""
						message.error("首次登录，请设置密码")
						return
					}
					
					this.userLogon(sobj);
					localStorage.setItem( "saveLoginInfo", JSON.stringify(sobj));
					document.cookie="apiUserHasLogin=1"
					let topath = "/index";					
					console.log("topath=>",topath)
					this.$router.push({path:topath});
				},
				fail:(error:any)=>{
					this.popover.loading = false;
					this.popover.message = error.errMsg;
					setTimeout(() => {
						this.popover.visible = true;
					}, 100);
				}
			})	
			
		},
		//跳转到服务协议和隐私政策页面。
		goAgreementPage(e:number){
			let url=e===1?'https://www.511ds.com/shark-portal/about_f01.html':'https://www.511ds.com/shark-portal/about_f02.html'
			window.open(url,'_blank');
		},
		resetPassword(){
			let password=this.loginInfo.password;
			this.popover.loading = true;
			this.$dsApi({
				url:"User/passwordModify",
				loadmsg:"保存密码",
				data:{
					username:"dsadmin",
					password:password,
					firstIn:0
				},
				success:()=>{
					this.loginInfo.firstIn=0
					this.modifyPwd=false;
					this.loginInfo.password=""
					this.loginInfo.reNewPassword=""
					message.success("保存成功，请登录")
					this.popover.loading = false;
				},
				fail:(error:any)=>{
					this.popover.loading = false;
					this.popover.message = error?error.errMsg:'找不到页面和资源';
					setTimeout(() => {
						this.popover.visible = true;
					}, 100);
				}
			})
			
			
			
		},
		
		async checkPass(){
			
			if(this.loginInfo.password!=='' && this.loginInfo.reNewPassword!==''){
				if(this.loginInfo.password!==this.loginInfo.reNewPassword){
					return Promise.reject("两次密码不相同");
				}				
			}
			return Promise.resolve();
		}
		
	},
	computed:{
		...mapState(['userInfo']),
		rules(){
			if(this.modifyPwd){
				return {
					
					password:[
						{ required: true, message: "新密码不能为空", trigger: 'blur' },
						{ min: 6, max: 15, message:"新密码不能小于6位或大于15位", trigger: 'blur' },
						
					],
					reNewPassword:[
						{ required: true, message: "确认新密码不能为空", trigger: 'blur' },
						{ min: 6, max: 15, message: "确认新密码不能小于6位或大于15位", trigger: 'blur' },
						{ validator: this.checkPass, trigger: 'blur' },
					],
				}
			}else{
				return {}
			}
			
			
		},
	}
	
	
})
	
	
</script>

<style lang="less" scoped>
@import "../../assets/common.less";
@contentHeight:calc(100vh - 120px);
.login-header{
	.w-h(98%,60px);
	background:#f7f5f6;
	.flex-hor();
	padding:0 1%;
	.flex-ver(center);
}
.login-footer{
	.w-h(100%,60px);
	background:#f7f5f6;
	text-align: center;
	line-height: 60px;
}
.login-content{
	.w-h(100%,@contentHeight);
	background:#2a4a83
}

.login-img{
	width:44px;
	height:44px;
	margin-right:10px;
	vertical-align:middle;
}
.web-title{
	color:#2f4775;
	font-size:22px;
	vertical-align:middle;
}
.guan-wang{
	font-size:16px;	
}
.banner-img-box{
	height:100%;
	width: 100%;	
	background:#2e4978;
	img{
		height:100%;
	}
	
}
.login-box-warp{
	
	position: absolute;
	right:10%;
	top:0;
	height:100%;
	z-index: 99;
}
.login-box{
	padding:30px;
	background: #fff;
	width:400px;
	border-radius: 20px;
}
.login-title{
	text-align: center;
	color:#3b5c91;
	font-size:22px;
	margin-bottom:30px;
}
.save-pass-font{
	font-size:12px;
	color:#a6a8a7;
}
.login-btn{
	background: linear-gradient(to right,#2e4875,#5176b9);
	color:#fff;
}

.pt17{
	padding:17px 0;
}
.login-tips{
	font-size:12px;
	
}


</style>
