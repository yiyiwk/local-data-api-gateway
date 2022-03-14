<template>
	<div>
		<div class="index-head">
			<div>
				<a href="https://www.511ds.com/" target="_blank"><img :src="$sharkWebPath+'ressource/images/logo.png'" class="login-img">
				<span class="web-title">迪赛智慧数可视化互动平台 |</span></a>
				
				<span class="web-subtitle">本地数据API网关</span>
			</div>
			<div class="index-right-head">
				<a-dropdown placement="bottomRight" :trigger="['click']">
					<div class="cursor">
						<img :src="$sharkWebPath+'ressource/images/userimg.png'" class="user-img" />
						<span class="pad10">dsadmin</span>
						<DownOutlined />
					</div>
					<template #overlay>
						<div class="nav-box">							
							<div class="cursor nav-item"  @click="logout">退出</div>
						</div>
					</template>
				</a-dropdown>
			</div>
		</div>
		<div class="index-content">
			<div class="left-nav">
				<div class="left-nav-list" :style="'background:url('+$sharkWebPath+'ressource/images/leftbg.jpg) no-repeat left bottom'">
					<div class="left-nav-item cursor" :class="{'active':navCurrent===index}" v-for="(item,index) in leftNavList" :key="index" @click="onMenuClick(index)">
						<div>
							<img :src="$sharkWebPath+'ressource/images/'+item.icon" class="lnav-img">
							<span>{{item.title}}</span>
						</div>						
						<RightOutlined style="color:#fff" />
					</div>
				</div>
				<div class="complane-tro-box">
					<div class="comp-title">广东迪特赛恩软件技术有限公司</div>
					<div class="complane-content">
						<div class="margin-right">
							<img :src="$sharkWebPath+'ressource/images/wx_01.png'" class="erwei-img">
							<div class="wxkf">微信公众号</div>
						</div>
						<div>
							<p>平台官网：</p>
							<p><a href="https://www.511ds.com/" target="_blank" class="color-white">https://www.511ds.com</a></p>
							<p>客服电话：</p>
							<p>400-641-8858</p>
						</div>
					</div>
				</div>
			</div>
			<div class="index-main">
				<keep-alive>
					<DataList v-if="navCurrent===0"></DataList>
				</keep-alive>
				<keep-alive>
					<ApiList v-if="navCurrent===1"></ApiList>
				</keep-alive>
				<keep-alive>
					<ServeMain v-if="navCurrent===2"></ServeMain>
				</keep-alive>
				<keep-alive>
					<ChangePass @goback="goback" v-if="navCurrent===3"></ChangePass>
				</keep-alive>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import { mapState,mapMutations} from 'vuex';
import { DownOutlined,RightOutlined} from '@ant-design/icons-vue';
import ApiList from '../apiport/apilist.vue';
import DataList from '../dataMate/list.vue';
import ChangePass from '../public/changePass.vue';
import ServeMain from '../serveMove/main.vue';

export default defineComponent({
	name:'IndexIndex',
	components:{
		DownOutlined,
		RightOutlined,
		ApiList,
		DataList,
		ChangePass,
		ServeMain
	},
	data(){
		return {			
			leftNavList:[
				{
					icon:'lnav0.png',
					title:"数据源"					
				},
				{
					icon:'lnav1.png',
					title:"API接口"					
				},
				{
					icon:'lnav2.png',
					title:"服务迁移"					
				},
				{
					icon:'lnav3.png',
					title:"网关配置"					
				},
			],
			navCurrent:0
		}
	},
	
	
	methods:{
		...mapMutations(['userLogout']),
		onMenuClick(index:number){
			if(this.navCurrent===index && (index===0 || index===1)){
				this.navCurrent=9999
				
			}
			setTimeout(()=>{
				this.navCurrent=index
			},0)
			
		},
		logout(){
			this.$dsApi({
				url:"User/goOut",
				data:{},
				loadmsg:"退出",
				success:()=>{}
									
			})
			this.userLogout()
			window.location.reload();
			console.log(window.location.href)
		},
		goback(){
			this.navCurrent=0
		}
		
	},
	computed:{
		...mapState(['userInfo']),
		
	}
	
	
})
	
	
</script>

<style lang="less" scoped>
@import "../../assets/common.less";
@contentHeight:calc(100vh - 60px);
p{
	margin:0;
	padding:0;
}
.index-head{
	.w-h(100%,60px);
	padding:0 1%;
	background: #2d4875;
	.flex-hor();
	.flex-ver(center);
}
.login-img{
	width:44px;
	height:44px;
	margin-right:10px;
	vertical-align:middle;
}
.web-title{
	color:#fff;
	font-size:22px;
	vertical-align:middle;
}
.web-subtitle{
	color:#fff;
	font-size:19px;
	vertical-align:middle;
	margin-left:1em;
}
.index-right-head{
	color:#fff;
}
.pad10{
	padding:0 10px;
}
.nav-box{
	min-width:100px;
	background: #fff;
	.nav-item{
		padding:10px 15px;
		font-size:15px;
		text-align: center;
		&:hover{
			background: #f1f1f1;
		}
	}
}
.index-content{
	.flex-hor();
	
}

.left-nav{
	width:260px;
	height:@contentHeight;
	background: #2e4875;
}
.index-main{
	width:calc(100vw - 260px);
}
.left-nav-list{
	height:calc(100vh - 200px);
}
.left-nav-item{
	height:55px;
	width:260px;
	padding:0 23px 0 37px;
	color:#fff;
	line-height: 55px;
	.flex-hor();
	.flex-ver(center);
	&.active,&:hover{
		background: #5177b9;
	}
}
.lnav-img{
	width:24px;
	height:24px;
	margin-right:10px;
}
.complane-tro-box{
	height:120px;
	padding:10px 25px;
	color:#fff;
}
.comp-title{
	font-size:13px;
	margin-bottom:5px;
}
.complane-content{
	.flex-hor(flex-start);
	font-size:12px;
}
.erwei-img{
	width:72px;
	height:72px;
}
.wxkf{
	font-size:12px;
	text-align: center;
}
.margin-right{
	margin-right:10px;
}
.color-white{
	color:#fff;
}
</style>
