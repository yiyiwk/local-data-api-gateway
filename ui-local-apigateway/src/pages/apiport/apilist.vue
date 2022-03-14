<template>
	<div class="warp">
		<div v-if="subPage==='main' || subPage==='add' || subPage==='modify'">
			<!--导航栏-->
			<div class="dao-lear-box">
				<div class="color-base">
					<RightOutlined  />
					<span class="cursor" @click="backApi">API接口列表</span>
					<span class="grhy" v-if="subPage==='add' || subPage==='modify'">
						<RightOutlined  />
						<span>{{subPage==='add'?'添加API接口':'修改API接口'}}</span>
					</span>
				</div>
				<div v-if="subPage==='main'">
					<a-button type="primary" @click="addApi">
						<template #icon><PlusOutlined /></template>
						添加API接口
					</a-button>
				</div>
			</div>
			
			<div class="show-box">
				<div class="table" v-if="subPage==='main'">
					<div class="tr">
						<div class="td thbg" style="width:7%">序号</div>
						<div class="td thbg" style="width:10%">名称</div>
						<div class="td thbg" style="width:25%">访问路径</div>
						<div class="td thbg" style="width:10%">状态</div>
						<div class="td thbg" style="width:10%">数据项</div>
						<div class="td thbg" style="width:10%">入参处理器</div>
						<div class="td thbg" style="width:10%">结果处理器</div>
						
						<div class="td thbg" style="width:18%">操作</div>
					</div>
					<div class="tr" v-for="(item,index) in apiList" :key="index">
						<div class="td" style="width:7%">{{index+1}}</div>
						<div class="td" style="width:10%">{{item.name}}</div>
						<div class="td" style="width:25%">
							<div>POST请求：{{item.fullUrl}} 
								<a-tooltip title="复制成功" trigger="click" :visible="item.showToop">
								<span style="font-size:15px;margin-left:15px;" @click="copyUrl(item)"><CopyOutlined  title="复制" /></span>
								</a-tooltip>
							</div>
						</div>
						<div class="td" style="width:10%">{{item.state=='1'?'启用':'禁用'}}</div>
						<div class="td" style="width:10%">
							<span class="color-base cursor" @click="goSubPage(item,'data')">列表({{item.dataitemNum}})</span>
						</div>
						<div class="td" style="width:10%">
							<span class="color-base cursor" @click="goSubPage(item,'paramsprocessor')">列表({{item.paramHandlerNum}})</span>
						</div>
						<div class="td" style="width:10%">
							<span class="color-base cursor" @click="goSubPage(item,'processor')">列表({{item.handlerNum}})</span>
						</div>
						
						<div class="td page-around" style="width:18%">							
							<div class="cursor color-base" @click="onOper(index)"><EditOutlined />修改</div>
							<a-popconfirm title="确定要删除此数据吗？删除后是不可恢复的！" @confirm="delItem(index)"  ok-text="确定" cancel-text="取消" placement="bottomRight">
							<div class="cursor color-base"><DeleteOutlined />删除</div>
							</a-popconfirm>
						</div>
					</div>
					<DataEmpty v-if="apiList.length==0"></DataEmpty>
				</div>
				<!--api修改添加-->
				<div class="oper-box" v-if="subPage==='add' || subPage==='modify'">
					<a-form ref="apiFormRef" :model="apiEditObj" :rules="rules"  :label-col="{'span': 5}" :wrapper-col="{'span': 19}" @finish="apiSaveBtn">
						
						<a-form-item ref="name" label="名称：" name="name">
							<a-input v-model:value="apiEditObj.name" style="height:34px" />
						</a-form-item>
						<a-form-item ref="path" label="访问路径：" name="path">
							<a-input v-model:value="apiEditObj.path" @blur="blur" style="height:34px" />
							<template #extra>
								<div v-if="extraUrl">
									{{"(全路径POST请求："+extraUrl+")"}} 
									<a-tooltip title="复制成功" trigger="click" >
									<span style="font-size:15px;margin-left:15px; cursor: pointer;" @click="copyUrl({fullUrl:extraUrl,detail:true})"><CopyOutlined  title="复制" /></span>
									</a-tooltip>
								</div>
							</template>
						</a-form-item>
						<a-form-item ref="represent"  label="描述：" name="represent">
							<a-textarea v-model:value="apiEditObj.represent" :auto-size="{ minRows: 6, maxRows: 9 }"   allow-clear />
						</a-form-item>
						<a-form-item ref="state" label="状态："  name="state" >
							<a-radio-group name="radioGroup" v-model:value="apiEditObj.state" >					
								<a-radio value="1">开启</a-radio>
								<a-radio value="0">禁用</a-radio>								
						
							</a-radio-group>
						</a-form-item>
						<div class="add-btn-box">
							<a-button class="page-btn" @click="backApi">取消</a-button>							
							<a-popover v-model:visible="popover.visible" trigger="contextmenu" color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
								<template #content>
									{{popover.message}}
								</template>
								<a-button type="primary" :loading="popover.loading"  class="page-btn" html-type="submit" >保存</a-button>
							</a-popover>
							
						</div>
					</a-form>
				</div>
			</div>
		</div>
		<!--子项目列表组件-->
		<div class="sub-box">
			<DataList :editObj="tempEditObj" @hide="closeData" v-if="subPage==='data'"></DataList>
			<ProcessorList :editObj="tempEditObj" @hide="closeData" v-if="subPage==='processor'"></ProcessorList>
			<ParamsProcessor :editObj="tempEditObj" @hide="closeData" v-if="subPage==='paramsprocessor'"></ParamsProcessor>
		</div>
		
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { RightOutlined,PlusOutlined,EditOutlined,DeleteOutlined,CopyOutlined} from '@ant-design/icons-vue';
import { mapState} from 'vuex';
import { message } from 'ant-design-vue';
import DataList from './datalist.vue';
import ProcessorList from './processorlist.vue';
import ParamsProcessor from './paramsprocessor.vue';
import { objDeepClone } from '@/utils/tools';
import DataEmpty from '../../components/DataEmpty.vue';

const apiEditModal:any={
	path:"",
	fullUrl:"",
	name:"",
	id:0,
	represent:"",
	modify:false,
	state:"1",
	dataitemNum:0,
	handlerNum:0
}

export default defineComponent({
	name: 'ApiList',
	components:{
		RightOutlined,
		PlusOutlined,		
		EditOutlined,
		DeleteOutlined,
		DataList,
		ProcessorList,
		CopyOutlined,
		DataEmpty,
		ParamsProcessor
		
	},
	data(){
		return {
			apiList:[],
			subPage:'main',//main-主页列表 data-数据项列表 ,processor-结果处理器列表  add-主页添加，modify-主页修改 paramsProcessor-入参处理器列表
			tempEditObj:{},
			apiEditObj:objDeepClone(apiEditModal),
			popover:{visible:false,loading:false},
			operIndex:0,
			extraUrl:"",
			
		}
	},
	activated(){
		this.getApiList()
	},
	methods:{
		closeData(){
			this.subPage="main"
			this.getApiList()
		},
		addApi(){
			this.subPage="add"
			this.extraUrl=""
		},
		backApi(){
			this.subPage="main"
			this.apiEditObj=objDeepClone(apiEditModal);
		},
		//失焦后获取全路径
		blur(){
			if(this.apiEditObj.path=="" || !this.apiEditObj.path){
				return
			}
			this.$dsApi({
				url:"Interface/path",
				data:{
					path:this.apiEditObj.path
				},
				loadmsg:"获取全路径",
				success:(res:any)=>{
					let sobj=res.data.data
					this.extraUrl=sobj
					this.apiEditObj.fullUrl=sobj
				}
									
			})
		},
		copyUrl(item:any){
			let oInput = document.createElement('input');
			oInput.value = item.fullUrl;
			document.body.appendChild(oInput);
			oInput.select(); // 选择对象
			document.execCommand("Copy"); // 执行浏览器复制命令
			oInput.className = 'oInput';
			oInput.style.display='none';
			if(!item.detail){
				item.showToop=true
				setTimeout(()=>{
					item.showToop=false
				},2500)
			}
			
		},
		onOper(index:number){
			let item:any=this.apiList[index]
			this.operIndex=index
			this.subPage="modify"
			for(let key in item){				
				this.apiEditObj[key]=item[key]				
			}
			this.extraUrl=this.apiEditObj.fullUrl
			this.apiEditObj.state=this.apiEditObj.state+''		
		},
		delItem(index:number){			
			this.$dsApi({
				url:"Interface/delete",
				data:{
					interfaceId:this.apiList[index].id
				},
				loadmsg:"删除API",
				success:()=>{
					message.success("删除成功")
					this.apiList.splice(index,1)
				},
				fail:(err:any)=>{
					message.error("删除失败，"+err.errMsg)
				}					
			})
		},
		//跳转到子项目列表 data-数据项列表 ,processor-结果处理器列表  paramsProcessor-入参处理器列表
		goSubPage(item:any,type:string){
			//console.log("processor=>",item)
			this.subPage=type			
			for(let key in item){
				this.tempEditObj[key]=item[key]
			}
			this.tempEditObj.parentId=item.id
		},
		//添加修改api
		apiSaveBtn(){
			if(this.popover.loading){
				return
			}
			if(this.extraUrl==""){
				message.error("访问路径发生未知错误，无法保存")
				return
			}
			let url="Interface/insert"
			let msgtype="添加"
			let params:any={
				name:this.apiEditObj.name,
				represent:this.apiEditObj.represent,
				path:this.apiEditObj.path,																
				state:this.apiEditObj.state				
			}
			if(this.subPage==="modify"){
				url="Interface/update"
				params.id=this.apiEditObj.id
				msgtype="修改"
			}
			this.popover.loading=true
			this.$dsApi({
				url,
				data:params,
				loadmsg:msgtype+"API",
				success:(res:any)=>{
					let sobj=res.data.data;
					sobj.handlerNum=0;
					sobj.dataitemNum=0
					if(this.subPage==="modify"){
						let item=this.apiList[this.operIndex]
						for(let key in item){
							if(this.apiEditObj[key]){
								item[key]=this.apiEditObj[key]
							}
						}
						this.operIndex=0
					}else{
						this.apiList.push(sobj)
					}										
					
					
					this.subPage="main"
					this.apiEditObj={}
								
				},
				fail:(err:any)=>{
					this.popover.message = err.errMsg;
					setTimeout(() => {
						this.popover.visible = true;
					}, 100);					
				},
				complete:()=>{
					this.popover.loading=false
				}
			})
			
		},
		//获取aip列表数据
		getApiList(){
			this.apiList.length=0
			this.$dsApi({
				url:"Interface/all",
				data:{},
				loadmsg:"API列表",
				success:(res:any)=>{
					let slist=res.data.data;
					slist.forEach((item:any)=>{
						this.apiList.push(item)
					})
			
				},
				fail:(err:any)=>{
					message.error(err.errMsg)
				}					
			})
		}
	},
	computed:{
		...mapState(['userInfo']),
		rules(){
			return {
				name: [
					{ required: true, message: '请输入名称', trigger: 'blur' },					
				],
				
				path: [
					{ required: true, message: '请输入访问路径', trigger: 'blur' },
					
				]
				
			}
		}
	},
	deactivated(){
		this.subPage="main"
	}
		
});
</script>

<style lang="less" scoped>
@import "../../assets/common.less";
.warp{
	padding:20px 30px;
}
.show-box{
	height:calc(100vh - 170px);
	overflow-y: auto;
}
.thbg{
	background: #f0efeb;
}
.page-around{
	justify-content: space-around !important;
}
.oper-box{
	width:700px;
}
.add-btn-box{
	margin-top:30px;
	padding-left:143px;
	.page-btn{
		.w-h(98px,42px);
		border-radius: 3px;
		margin-right:30px;
	}
	
}
.copy{
	width:100px;
	height:30px;
	border:1px solid #888;
	border-radius: 3px;
	
	margin-left:5px;
}
</style>
