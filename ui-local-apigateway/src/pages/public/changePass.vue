<template>
	<div class="warp">
		<div class="oper-menu flex-row">
			<div class="menu-item  cursor" :class="curMenu===index?'action color-base':''" v-for="(item,index) in menuList" :key="index" @click="onMenu(index)">
				<span class="apisfont" :class="item.icon"></span> {{item.text}}
			</div>
		</div>
		<div class="show-box">
			
			
			<!-- <div class="model-title">修改密码</div> -->
			<div class="form-box" v-show="curMenu===0">
				<a-form ref="formRef" :model="changeInfo" :rules="rules" :label-col="{'span': 4}" :wrapper-col="{'span': 22}" @finish="tapUserLogin">
					
					<a-form-item ref="newPass" label="新密码：" name="newPass">
						<a-input-password placeholder="请输入新密码"  style="width:480px;height:40px;" v-model:value="changeInfo.newPass">
							<template #prefix>
								<unlock-outlined type="unlock" />
							</template>
						</a-input-password>
					</a-form-item>
					<a-form-item ref="reNewPass" label="确认新密码：" name="reNewPass" >
						<a-input-password placeholder="请输入新密码"  style="width:480px;height:40px;" v-model:value="changeInfo.reNewPass">
							<template #prefix>
								<unlock-outlined type="unlock" />
							</template>
						</a-input-password>
					</a-form-item>
					<div class="add-btn-box">
						<a-button class="page-btn" @click="onReset">取消</a-button>
						<a-popover v-model:visible="popover.visible" trigger="contextmenu" color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
							<template #content>
								{{popover.message}}
							</template>
							<a-button type="primary" :loading="popover.loading" class="page-btn" html-type="submit" >保存</a-button>
						</a-popover>
						
					</div>
				</a-form>
			</div>
			
			<div class="form-box" v-show="curMenu===2">
				<div class="info-item flex-row">
					<div class="info-item-title">安全码：</div>
					<div class="info-val">
						<a-input v-model:value="tempNameObj.safeCode" placeholder="请输入安全码" style="width:480px;height:40px;"></a-input>						
					</div>
					<div class="mar-left"  @click="modifySafeCode('safe')">
						<EditFilled style="color:#b3bbbe" /><span class="base-color">修改</span>
					</div>
				</div>
				<div class="config-tips">说明：安全码是接口校验的密码，空时不做签名验证。请求时签名JAVA示例代码下载<a :href="$sharkWebPath+'ressource/download/RequestSignature.java'" download="RequestSignature.java">RequestSignature.java</a>，可用于自己业务调用此本地数据API网关服务</div>
			</div>
			
			<div class="form-box" v-show="curMenu===1">
				<div class="info-item flex-row">
					<div class="info-item-title">互联网访问的域名/IP：</div>
					<div class="info-val">
						<a-input v-model:value="tempNameObj.ipName" placeholder="请输入域名/IP地址，如http://demo.cn/" style="width:480px;height:40px;"></a-input>						
					</div>
					<div class="mar-left"  @click="modifySafeCode('ip')">
						<EditFilled style="color:#b3bbbe" /><span class="base-color">修改</span>
					</div>
				</div>
				<div class="config-tips">说明：如果不能将内网服务映射到互联网，可使用第三方内网穿透工具实现服务向互联网开放。</div>
			</div>
			
			<div class="form-box" v-show="curMenu===3">
				<div class="info-item flex-row">
					<div class="info-item-title">时间间隔：</div>
					<div class="info-val">
						<a-select v-model:value="tempNameObj.timeVal" style="width:480px;"  ref="select">
							<a-select-option :value="item.value" v-for="(item,index) in timeList" :key="index">{{item.text}}</a-select-option>
							
						</a-select>
					</div>
					<div class="mar-left"  @click="modifySafeCode('time')">
						<EditFilled style="color:#b3bbbe" /><span class="base-color">修改</span>
					</div>
				</div>
				<div class="config-tips">说明：部署网关服务的服务器时间和互联网当前的时间差</div>
			</div>
		</div>
		
	</div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import { mapState} from 'vuex';
import {EditFilled,UnlockOutlined} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';


export default defineComponent({
	name:'ChangePass',
	components:{						
		UnlockOutlined,
		EditFilled
	},
	data(){
		return {
			changeInfo:{
				mobile:"dsadmin",				
				newPass:"",
				reNewPass:""
			},			
			popover:{
				loading:false,
				visible:false
			},
			tempNameObj:{
				safeCode:"",
				ipName:"",
				timeVal:1				
			},
			timeList:[
				{
					text:"关闭",
					value:1
				},
				{
					text:"1分钟内",
					value:2
				},
				{
					text:"5分钟内",
					value:3
				},
				{
					text:"10分钟内",
					value:4
				},
				{
					text:"30分钟内",
					value:5
				},
				{
					text:"1小时内",
					value:6
				},
				{
					text:"12小时内",
					value:7
				},
				{
					text:"24小时内",
					value:8
				}
			],
			menuList:[
				{
					text:"修改密码",
					icon:"apis-mpass"
				},
				{
					text:"域名/IP设置",
					icon:"apis-ip"
				},
				{
					text:"验证安全码",
					icon:"apis-safe"
				},				
				{
					text:"验证时间戳",
					icon:"apis-time"
				}
				
			],
			curMenu:0
		}
	},
	
	activated(){
		this.getData()
	},
	methods:{
		onMenu(index:number){
			if(this.curMenu===index){
				return
			}
			this.getData()
			this.curMenu=index
		},
		onReset(){
			this.$refs.formRef.resetFields();
		},
		//修改密码
		tapUserLogin(){			
			let newPass=this.changeInfo.newPass			
			this.popover.loading=true
			this.$dsApi({
				url:"User/passwordModify",
				loadmsg:"保存密码",
				data:{
					username:"dsadmin",
					password:newPass,
					firstIn:1
				},
				success:()=>{
					message.success("修改成功，下次请用新密码登录")
					this.popover.loading=false
					this.changeInfo.newPass=""
					this.changeInfo.reNewPass=""
					this.$emit("goback")
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
		
		/* 修改域名/IP、安全码、时间戳 参数
		* type [string] 修改类型 必填 safe-安全码 ip-域名/IP time-时间戳
		*/
		modifySafeCode(type:string){
			console.log(type)
			
			let params:any={}
			if(type==="safe"){
				params.vcode=this.tempNameObj.safeCode
				if(params.vcode.length>0){
					if(!/^[A-Za-z0-9]+$/.test(params.vcode)){
						message.error("安全码只能由大小字母和数字组成")
						return
					}
					if(params.vcode.length<10 || params.vcode.length>20 ){
						message.error("安全码不能小于10位或者大于20位")
						return
					}
				}
				
				
			}else if(type==="ip"){
				params.domainName=this.tempNameObj.ipName;
				if(/.*[\u4e00-\u9fa5]+.*$/.test(params.domainName)){
					return Promise.reject("域名/IP不能包含中文");
				}
				if(params.domainName.length<5 || params.domainName.length>40){
					message.error("域名/IP不能小于5位或者大于40位")
					return
				}
			}else{
				params.ts=this.tempNameObj.timeVal
			}
			
			this.$dsApi({
				url:"Config/update ",
				loadmsg:"保存"+type,
				data:params,
				success:()=>{
				
					message.success("保存成功")
				},
				fail:(err:any)=>{
					message.error("保存失败，"+err.errMsg)
				}
			})
			
		},
		//获取配置数据
		getData(){
			this.$dsApi({
				url:"Config/get ",
				loadmsg:"获取配置",
				data:{},
				success:(res:any)=>{
					if(!res || !res.data){
						return
					}
					let sobj=res.data.data
					this.tempNameObj.safeCode=sobj.vcode
					this.tempNameObj.ipName=sobj.domainName
					this.tempNameObj.timeVal=sobj.ts
				},
				fail:(err:any)=>{
					message.error("获取失败，"+err.errMsg)
				}
			})
		},
		async checkPass(){
			if(this.changeInfo.newPass!=this.changeInfo.reNewPass){
				return Promise.reject("两次输入密码不相同");
			}
			return Promise.resolve()
		}
		
	},
	computed:{
		...mapState(['userInfo']),
		rules(){
			return {
				
				newPass:[
					{ required: true, message: "新密码不能为空", trigger: 'blur' },
					{ min: 6, max: 15, message:"新密码不能小于6位或大于15位", trigger: 'blur' },					
				],
				reNewPass:[
					{ required: true, message: "确认新密码不能为空", trigger: 'blur' },
					{ min: 6, max: 15, message:"确认新密码不能小于6位或大于15位", trigger: 'blur' },
					{ validator: this.checkPass, trigger: 'blur' },					
				]
			}
			
		},
	}
	
	
})
	
	
</script>

<style lang="less" scoped>
	@import "../../assets/common.less";
	@import "../../assets/css/font/iconfont.css";//引入字体图标
	.warp{
		
	}
	.form-box{
		padding-top:40px;
	}
	.add-btn-box{
		margin-top:30px;
		padding-left:94px;
		.page-btn{
			.w-h(98px,42px);
			border-radius: 3px;
			margin-right:30px;
		}
	}
	.model-title{
		font-size: 15px;
		border-bottom: 1px solid #d9d7d8;
		line-height: 26px;
		height: 33px;
		text-indent: 10px;
	}
	.margin-top{
		margin-top:50px;
	}
	.show-box{
		height:calc(100vh - 170px);
		overflow-y: auto;
		padding:20px 30px;
	}
	.form-box{
		.info-item{
			line-height: 26px;
			padding-bottom:6px;
			.flex-ver();
			.info-item-title{
				width:17%;
				color:#333;
				text-align: right;
				margin-right:10px;
			}
			.info-val{
				min-width: 288px;
				
			}
		}
	}
	.config-tips{
		color:#999;
		font-size:13px;
		padding-left:18%;
	}
	.base-color{
		color:#7b88ce;
		margin-left:7px;
		line-height: 26px;
		cursor: pointer;
	}
	.mar-left{
		margin-left:30px;
	}
	.oper-menu{
		height:48px;
		padding:10px 60px 0;
		border-bottom:1px solid #d5d5d5;
		background: #f0efeb;
		.menu-item{
			border:1px solid #f0efeb;
			border-bottom-color:#d5d5d5;
			transition: all .2s ease-out;
			cursor: pointer;
			border-radius: 3px 3px 0 0;
			width:150px;
			height:38px;
			text-align: center;
			line-height: 38px;
			font-size:15px;
			&.action{
				background: #fff;
				border-color:#d5d5d5;
				border-bottom-color:#fff;
			}
		}
	}
</style>
