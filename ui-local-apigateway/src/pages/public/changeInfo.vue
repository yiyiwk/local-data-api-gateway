<template>
	<div>
		<ComHead></ComHead>
		<div class="content-box">
			<div class="page-title">
				<span class="color-base cursor" @click="goBack">工作台</span><RightOutlined style="margin:0 5px" /><span>修改资料</span>
			</div>
			<div class="change-box">
				<div class="box-title">登录用户</div>
				<div class="box-item flex-row">
					<div class="item-title">姓名：</div>
					<a-input v-model:value="adminInfo.userName"  @focus="focus('userName')" @blur="blur('userName')" style="width:276px" />
					<div class="input-tip">{{errorTips.userName}}</div>		
				</div>
				<div class="box-item flex-row">
					<div class="item-title">角色：</div>
					<div class="input-mobile">{{roleName}}</div>					
				</div>
				
				<div class="box-item flex-row">
					<div class="item-title">手机：</div>
					<a-input  v-model:value="adminInfo.userMobile" @focus="focus('userMobile')" @blur="blur('userMobile')" style="width:276px"  />
					<div class="input-tip">{{errorTips.userMobile}}</div>
				</div>
				<div class="box-title two-title-box">接种点</div>
				<div class="box-item flex-row">
					<div class="item-title">名称：</div>
					<a-input v-model:value="adminInfo.orgName" @focus="focus('orgName')" @blur="blur('orgName')" style="width:276px" />
					<div class="input-tip">{{errorTips.orgName}}</div>
				</div>
				<div class="box-item flex-row">
					<div class="item-title">地址：</div>
					<a-input v-model:value="adminInfo.orgAddr" @focus="focus('orgAddr')" @blur="blur('orgAddr')" style="width:276px"  />
					<div class="input-tip">{{errorTips.orgAddr}}</div>
				</div>
				<div class="box-item flex-row">
					<div class="item-title">电话：</div>
					<a-input v-model:value="adminInfo.orgtel" style="width:276px"  />
					
				</div>
				<div class="box-item flex-row">
					<div class="item-title"> </div>
					<div>
						<a-popover v-model:visible="popover.visible" trigger="contextmenu" color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
							<template #content>
								{{popover.message}}
							</template>
							<a-button type="primary" style="margin-right:20px" :loading="popover.loading" @click="submit">确认修改</a-button>
						</a-popover>
						<a-button >取消</a-button>
					</div>					
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import { mapState,mapMutations} from 'vuex';
import ComHead from '../../components/ComHead.vue'; 
import {RightOutlined} from '@ant-design/icons-vue';
import {checkMobile } from '@/utils/tools';
import { message } from 'ant-design-vue';


export default defineComponent({
	name:'PublicChangeInfo',
	components:{
		ComHead,
		RightOutlined
		
	},
	data(){
		return {
			adminInfo:{
				userMobile:"",
				userName:"",
				orgName:"",
				orgAddr:"",
				orgtel:""
			},
			
			popover:{
				loading:false,
				visible:false
			},
			errorTips:{
				userMobile:"",
				userName:"",
				orgName:"",
				orgAddr:""
			}
			
		}
	},
	created (){
		this.getData()
		
	},	
	activated(){
		
	},
	methods:{
		...mapMutations(['userLogout']),
		goBack(){
			this.$router.go(-1)
		},
		blur(e:string){
			if(this.adminInfo[e].length<1){
				this.errorTips[e]="内容不能为空"
			}
			if(e==='userMobile' && !checkMobile(this.adminInfo.userMobile)){
				this.errorTips[e]="手机格式不正确"
			}
		},
		focus(e:string){			
			this.errorTips[e]=""			
		},
		
		submit(){

			let allow=true
			for(let key in this.adminInfo){
				if(this.adminInfo[key]=="" && key!=='orgtel'){
					this.errorTips[key]="内容不能为空"
					allow=false
				}
			}
			if(!allow){
				return
			}
			if(!checkMobile(this.adminInfo.userMobile)){
				this.errorTips.password="手机格式错误"
				return
			}
			
			
			
			this.popover.loading=true
			
			this.$dsapi({
				url:"user/chanage/2/7777/9S8AKhyrt52fP2K9",
				data:this.adminInfo
			}).then((res:any)=>{
				console.log("res=>",res)
				message.success("修改成功！")
				this.userLogout()
				window.location.reload();

			}).catch((error:any)=>{
				this.popover.loading = false;
				this.popover.message = error;
				setTimeout(() => {
					this.popover.visible = true;
				}, 100);
			})
		},
		getData(){
			this.adminInfo.userName=this.userInfo.name
			this.adminInfo.userMobile=this.userInfo.mobile
			this.adminInfo.orgName=this.userInfo.vaccineOrgName
			this.adminInfo.orgAddr=this.userInfo.vaccineOrgAddr
			this.adminInfo.orgtel=this.userInfo.vaccineOrgTel
		}
		
	},
	computed:{
		...mapState(['userInfo']),
		roleName(){
			const roleobj:any={
				"9":"管理员",
				"6":"普通人员",
				"3":"扫码核查员"
			}
			return roleobj[this.userInfo.role+'']
		}
	}
	
	
})
	
	
</script>

<style lang="less" scoped>
	@import "../../assets/common.less";
	
	.content-box{
		padding:0 35px 44px
	}
	.page-title{
		height:60px;
		width:100%;
		line-height:60px;
	}
	.change-box{
		width:700px;
		margin:20px auto 30px;		
	}
	.box-item{
		margin-bottom:15px;
		
		.flex-ver();
		color:#333;
		.item-title{
			width:170px;
			text-align: right;
		}
		.input-mobile{
			width:174px;
			margin-left:10px;
			color:#868686;
		}
		.code-btn{
			width:100px;
			height:32px;
			border:1px solid #cccccc;
			font-weight: 400;
		}
		.input-tip{
			color:#fd5959;
			margin-left:30px;
		}
	}
	.box-title{
		font-size:16px;
		font-weight: bold;
		padding:0 100px 20px;
	}
	.two-title-box{
		margin-top:40px;
		padding-top:30px;
		border-top:1px solid #dfdfdf;
	}
</style>
