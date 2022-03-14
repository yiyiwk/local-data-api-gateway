<template>
	<div>
		<div class="dao-lear-box">
			<div class="color-base">
				<RightOutlined  />
				<span class="cursor" @click="backMain">{{dataObj.name}}</span>
				<span v-if="pageType!=='列表'">
					<RightOutlined  />
					<span class="cursor" @click="backList">数据项列表</span>
				</span>
				<span class="grhy">
					<RightOutlined  />
					<span>{{pageType==='列表'?'数据项列表':pageType+'数据项'}}</span>
				</span>
				
			</div>
			<div v-if="pageType==='列表'">
				<a-button type="primary" @click="addSubData">
					<template #icon><PlusOutlined /></template>
					添加数据项
				</a-button>
			</div>
		</div>
		
		<div class="show-box">
			<div class="table" v-if="pageType==='列表'">
				<div class="tr">
					<div class="td thbg" style="width:10%">序号</div>
					<div class="td thbg" style="width:20%">数据项名称</div>
					<div class="td thbg" style="width:15%">数据项节点名</div>
					<div class="td thbg" style="width:25%">数据源名称</div>
					<div class="td thbg" style="width:10%">状态</div>
					<div class="td thbg" style="width:20%">操作</div>
				</div>
				<div class="tr" v-for="(item,index) in dataList" :key="index">
					<div class="td" style="width:10%">{{index+1}}</div>
					<div class="td" style="width:20%">{{item.name}}</div>
					<div class="td" style="width:15%">{{item.nodename}}</div>
					<div class="td" style="width:25%">{{item.datamartName}}</div>
					<div class="td" style="width:10%">{{item.state=='1'?'启用':'禁用'}}</div>
					<div class="td page-around" style="width:20%">
						<div class="cursor color-base" @click="onOper(index)"><EditOutlined />修改</div>
						<a-popconfirm title="确定要删除此数据吗？删除后是不可恢复的！" @confirm="delItem(index)"  ok-text="确定" cancel-text="取消" placement="bottomRight">
						<div class="cursor color-base"><DeleteOutlined />删除</div>
						</a-popconfirm>
					</div>
				</div>
				<DataEmpty v-if="dataList.length==0"></DataEmpty>
			</div>
			<div class="oper-box" v-if="pageType==='修改' || pageType==='添加'">
				<a-form ref="formRef" :model="subEditObj" :rules="rules"  :label-col="{'span': 5}" :wrapper-col="{'span': 19}" @finish="saveBtn">
					<a-form-item ref="name" label="数据项名称：" name="name">
						<a-input v-model:value="subEditObj.name" style="height:34px" />
					</a-form-item>
					<a-form-item ref="nodename" label="数据项节点名：" extra="封装查询数据的节点值" name="nodename">
						<a-input v-model:value="subEditObj.nodename" style="height:34px" />
					</a-form-item>
					
					<a-form-item ref="source" label="数据源名称：" name="source">
						<!-- <a-input v-model:value="subEditObj.datamartName" style="height:34px" /> -->
						
						<a-select label-in-value  v-model:value="selectedVal" ref="select" @change="handleChange">
							
							<a-select-option :value="item.id"  v-for="(item,index) in yuanList" :key="index">{{item.name}}</a-select-option>											
						</a-select>
					</a-form-item>
					<a-form-item ref="querysql" extra="" label="查询语句：" name="querysql">
						<a-textarea v-model:value="subEditObj.querysql" :auto-size="{ minRows: 4, maxRows: 9 }"   allow-clear />
						<div class="sql-tips">
							1.查询语句可动态传值，使用 {#参数:默认值#} 引用参数<br />
							2.接口使用json传值，更改 {#参数:默认值#} 其中参数值 <br />
						</div>
						
					</a-form-item>
					<a-form-item ref="state" label="状态："  name="state" >
						<a-radio-group name="radioGroup" v-model:value="subEditObj.state" @change="radioChange">					
							<a-radio value="1">开启</a-radio>
							<a-radio value="0">禁用</a-radio>								
					
						</a-radio-group>
					</a-form-item>
					<div class="add-btn-box">
						<a-button class="page-btn" @click="backList">取消</a-button>
						<a-button class="page-btn" type="primary" @click="onView">预览</a-button>
						<a-popover v-model:visible="popover.visible"  color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
							<template #content>
								{{popover.message}}
							</template>
							<a-button type="primary" :loading="popover.loading" :disabled="!subEditObj.checkSql" class="page-btn" html-type="submit" >保存</a-button>
						</a-popover>
						
					</div>
				</a-form>
				
			</div>
			<div class="add-btn-box" v-if="pageType==='修改' || pageType==='添加'">
				<div class="show-tips">
					<div class="text-wrap">{{sqlBack}}</div>
				</div>
			</div>
		</div>
		
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { RightOutlined,PlusOutlined,EditOutlined,DeleteOutlined} from '@ant-design/icons-vue';
import { mapState} from 'vuex';
import { message } from 'ant-design-vue';
import { objDeepClone,strFormat} from '@/utils/tools';
import DataEmpty from '../../components/DataEmpty.vue';

const datalistModel:any={
	name:"",	
	parentId:0,
}
const subEditModel:any={
	modify:true,
	checkSql:false,
	name:"",
	state:"1",
	datamartId:0,
	interfaceId:0,
	querysql:"",
	nodename:"",
	id:0,
	datamartName:""
}

export default defineComponent({
	name: 'DataList',
	components:{
		RightOutlined,
		PlusOutlined,		
		EditOutlined,
		DeleteOutlined,		
		DataEmpty
	},
	props:{
		editObj:{
			type:Object,
			default () {
				return objDeepClone(datalistModel)
				
			}
		}
	},	
	data(){
		return {
			dataList:[
				{
					name:"上传公司数据",
					state:1,
					datamartId:1,
					interfaceId:1,
					querysql:"1323432",
					nodename:"234",
					id:0,
					datamartName:""
				},
				
			],
			yuanList:[],
			dataObj:objDeepClone(datalistModel),
			pageType:"列表",//列表  添加 修改
			subEditObj:objDeepClone(subEditModel),
			popover:{visible:false,loading:false,message:"请先[预览]，查询数据无误后才能保存"},
			operIndex:0,
			selectedVal:{value:0},
			sqlBack:""
		}
	},
	watch:{
		'subEditObj.checkSql'(nval){
			if(nval){
				this.popover.message="可以保存"
			}else{
				this.popover.message="请先[预览]，查询数据无误后才能保存"
			}
		}
	},
	mounted(){		
		for(let key in this.dataObj){
			if(this.editObj[key]){
				this.dataObj[key]=this.editObj[key]
			}			
		}		
		this.getYuan()
		if(this.dataObj.parentId>0){
			this.getData()
		}		
	},
	methods:{
		backMain(){
			this.$emit("hide")
			if(this.pageType==='添加' || this.pageType==='添加'){
				this.$refs.formRef.resetFields();
				this.subEditObj=objDeepClone(subEditModel)
			}
			
		},
		backList(){
			this.pageType="列表"
			this.subEditObj=objDeepClone(subEditModel)
			
		},
		addSubData(){
			this.pageType="添加"
			this.sqlBack=""
			if(this.subEditObj.datamartId===0 && this.yuanList.length>0){
				this.selectedVal.value=this.yuanList[0].id
				this.subEditObj.datamartId=this.yuanList[0].id
				this.subEditObj.datamartName=this.yuanList[0].name
				this.subEditObj.interfaceId=this.dataObj.parentId
			}
		},
		radioChange(e:any){
			console.log("radioe=>",e.target)
			if(e.target.value==='0'){
				this.subEditObj.checkSql=true
			}else{
				this.subEditObj.checkSql=false
			}
		},
		onView(){
			if(this.selectedVal.value=="0"){
				message.error("请先配置数据源")
				return
			}
			if(this.subEditObj.querysql==""){
				message.error("语句校验不能为空")
				return
			}
			this.$dsApi({
				url:"Dataitem/checkQuery",
				data:{
					datamartId:this.subEditObj.datamartId,
					querysql:this.subEditObj.querysql,
					nodename:this.subEditObj.nodename
				},
				loadmsg:"语句校验",
				success:(res:any)=>{
					// console.log(res)
					let sobj=JSON.stringify(res.data.data,null,2);
					this.sqlBack=strFormat(sobj)
					this.subEditObj.checkSql=true
				},
				fail:(err:any)=>{
					message.error(err.errMsg)
				}					
			})
			
		},
		onOper(index:number){
			let item:any=this.dataList[index]
			this.operIndex=index
			this.sqlBack=""
			this.pageType="修改";
			for(let key in this.subEditObj){
				if(item[key]){
					this.subEditObj[key]=item[key]
				}					
			}
			this.selectedVal={value:this.subEditObj.datamartId}
			this.subEditObj.state=this.subEditObj.state+''
	
		},
		delItem(index:number){
			this.$dsApi({
				url:"Dataitem/delete",
				data:{
					id:this.dataList[index].id
				},
				loadmsg:"删除数据项",
				success:()=>{
					message.success("删除成功")
					this.dataList.splice(index,1)
				},
				fail:(err:any)=>{
					message.error("删除失败，"+err.errMsg)
				}					
			})
		},
		handleChange(value:any){
			console.log("value=>",value)
			this.selectedVal.value=value.value		
			this.subEditObj.datamartId=value.value
			this.subEditObj.datamartName=value.label.children
		},
		saveBtn(){
			if(this.popover.loading){
				return
			}
			
			let url="Dataitem/insert"
			let msgtype="添加"
			let params:any={
				name:this.subEditObj.name,
				nodename:this.subEditObj.nodename,
				datamartId:this.subEditObj.datamartId,																
				querysql:this.subEditObj.querysql,
				state:this.subEditObj.state,
				interfaceId:this.subEditObj.interfaceId
			}
			if(this.pageType==="修改"){
				url="Dataitem/update"
				params.id=this.subEditObj.id
				msgtype="修改"
			}
			this.popover.loading=true
			this.$dsApi({
				url,
				data:params,
				loadmsg:msgtype+"API",
				success:(res:any)=>{
					let sobj=res.data.data;
					sobj.datamartName=this.subEditObj.datamartName
					if(this.pageType==="修改"){
						let item=this.dataList[this.operIndex]
						for(let key in item){
							if(this.subEditObj[key]){
								item[key]=this.subEditObj[key]
							}
						}
						this.operIndex=0
					}else{
						this.dataList.push(sobj)
					}										
					this.pageType="列表"
					this.subEditObj=objDeepClone(subEditModel)
								
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
		getData(){
			this.dataList.length=0
			this.$dsApi({
				url:"Dataitem/all",
				data:{
					interfaceId:this.dataObj.parentId
				},
				loadmsg:"数据项列表",
				success:(res:any)=>{
					let slist=res.data.data;
					slist.forEach((item:any)=>{
						this.dataList.push(item)
					})
		
				},
				fail:(err:any)=>{
					message.error(err)
				}					
			})
			
		},
		getYuan(){
			this.yuanList.length=0
			this.$dsApi({
				url:"Datamart/all",
				data:{},
				loadmsg:"数据源列表",
				success:(res:any)=>{
					let slist=res.data.data;
					slist.forEach((item:any)=>{
						if(item.state===1 || item.state=="1"){
							this.yuanList.push(item)
						}
						
					})
					if(this.yuanList.length===0){
						this.yuanList.push({
							id:0,
							name:"请先配置数据源"
						})
					}
					if(this.subEditObj.datamartId===0 && this.yuanList.length>0){
						this.selectedVal.value=this.yuanList[0].id
						this.subEditObj.datamartId=this.yuanList[0].id
						this.subEditObj.datamartName=this.yuanList[0].name
						this.subEditObj.interfaceId=this.dataObj.parentId
					}
					
				},
				fail:(err:any)=>{
					message.error(err)
				}					
			})
			
		},
		async checkNodename(){
			
			if(/.*[\u4e00-\u9fa5]+.*$/.test(this.subEditObj.nodename)){
				return Promise.reject("数据节点名不能包含中文");
			}
			return Promise.resolve();
		}
	},
	computed:{
		...mapState(['userInfo']),
		rules(){
			return {
				name: [
					{ required: true, message: '请输入数据名称', trigger: 'blur' },					
				],
				nodename: [
					{ required: true, message: '请输入数据节点名', trigger: 'blur' },
					{ validator: this.checkNodename, trigger: 'blur' },
				],
				datamartName: [
					{ required: true, message: '请输入数据源名称', trigger: 'blur' },
					
				],
				querysql: [
					{ required: true, message: '请输入查询语句', trigger: 'blur' },
					
				],
			}
		}
	}
		
});
</script>

<style lang="less" scoped>
@import "../../assets/common.less";

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
.grhy{
	color:#666;
}
.sql-tips{
	font-size:13px;
	line-height: 1.7em;
	color:#999;
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
.show-tips{
	width:97%;
	min-height:142px;
	max-height:318px;
	border:1px solid #d9d9d9;
	border-radius:2px;
	background: #f5f5f5;
	overflow-y: auto;
}
:deep(.ant-form-item-extra){
	font-size:13px;
}
.text-wrap{
	word-break:break-all;
	word-wrap:break-word;
	white-space: pre-wrap;
}
</style>
