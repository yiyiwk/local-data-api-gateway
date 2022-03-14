<template>
	<div>
		<div class="dao-lear-box">
			<div class="color-base">
				<RightOutlined  />
				<span class="cursor" @click="backMain">{{dataObj.name}}</span>
				<span v-if="pageType!=='列表'">
					<RightOutlined  />
					<span class="cursor" @click="backList">入参处理器列表</span>
				</span>
				<span class="grhy">
					<RightOutlined  />
					<span>{{pageType==='列表'?'入参处理器列表':pageType+'入参处理器'}}</span>
				</span>
				
			</div>
			<div v-if="pageType==='列表'">
				
				<a-button type="primary" @click="addSubData">
					<template #icon><PlusOutlined /></template>
					添加入参处理器
				</a-button>
			</div>
		</div>
		
		<div class="show-box">
			<div class="table" v-if="pageType==='列表'">
				<div class="tr">
					<div class="td thbg" style="width:10%">序号</div>
					<div class="td thbg" style="width:25%">名称</div>
					<div class="td thbg" style="width:25%">状态</div>
					<div class="td thbg" style="width:40%">操作</div>
				</div>
				<div class="tr" v-for="(item,index) in dataList" :key="index">
					<div class="td" style="width:10%">{{index+1}}</div>
					<div class="td" style="width:25%">{{item.name}}</div>
					<div class="td" style="width:25%">{{item.state==1?'启用':'禁用'}}</div>
					<div class="td page-around" style="width:40%">
						<div class="cursor color-base" @click="onOper(index,'up')" v-if="index!==0 && dataList.length>1"><ArrowUpOutlined />向上</div>
						<div class="cursor color-base" @click="onOper(index,'down')" v-if="index!==(dataList.length-1) && dataList.length>1"><ArrowDownOutlined />向下</div>
						<div class="cursor color-base" @click="onOper(index,'modify')"><EditOutlined />修改</div>
						<a-popconfirm title="确定要删除此数据吗？删除后是不可恢复的！" @confirm="onOper(index,'del')"  ok-text="确定" cancel-text="取消" placement="bottomRight">
						<div class="cursor color-base" ><DeleteOutlined />删除</div>
						</a-popconfirm>
					</div>
				</div>
				<DataEmpty v-if="dataList.length==0"></DataEmpty>
			</div>
			<!--修改 添加-->
			<div class="oper-box" v-if="pageType==='修改' || pageType==='添加'">
				<a-form ref="formRef" :model="subEditObj" :rules="rules"  :label-col="{'span': 5}" :wrapper-col="{'span': 19}" @finish="saveBtn">
					<a-form-item ref="name" label="名称：" name="name">
						<a-input v-model:value="subEditObj.name" style="height:34px" />
					</a-form-item>
					<a-form-item ref="process" label="处理器：" name="process">
						<div class="up-box">
							<a-popover placement="rightTop" :defaultVisible="true" trigger="contextmenu" v-model:visible="awayShow" @visibleChange="onVisible">
								
								<a-button type="primary" size="large" @click="toUpLoad">
									<template #icon>
										<UploadOutlined />									
									</template>
									
									{{subEditObj.processor.length>0?'替换':'上传'}}
									
								</a-button>
								
								<input class="upload-input" id="files" type="file" ref="file" @change="fileUp()" accept=".jar" >
								<template #content>
									<div class="flex-row">
										<div class="grhy">说明：</div>
										<div class="upload-content grhy">
											<p>1、处理器demo工程</p>
											<p>2、请阅读readme.md文件</p>
											<p>3、实现类编写数据处理代码</p>
											<p>4、maven 打包jar</p>
											<p>5、处理器上传</p>
										</div>
										<div class="down-demo">
											<a :href="$sharkWebPath+'ressource/download/demo.zip'" download="demo"><CloudDownloadOutlined />下载</a>
										</div>
									</div>
								</template>
							</a-popover>
							
						</div>
						<div class="text-grhy" v-if="upFileName.length>0">上传成功：{{upFileName}}</div>
					</a-form-item>					
					<a-form-item ref="state" label="状态："  name="state" >
						<a-radio-group name="radioGroup" v-model:value="subEditObj.state" >					
							<a-radio value="1">开启</a-radio>
							<a-radio value="0">禁用</a-radio>													
						</a-radio-group>
					</a-form-item>
					<div class="add-btn-box">
						<a-button class="page-btn" @click="backList">取消</a-button>						
						<a-popover v-model:visible="popover.visible" trigger="contextmenu" color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
							<template #content>
								{{popover.message}}
							</template>
							<a-button type="primary" :loading="popover.loading" class="page-btn" html-type="submit" >保存</a-button>
						</a-popover>						
					</div>
				</a-form>
				
			</div>
		</div>
		
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { RightOutlined,PlusOutlined,ArrowUpOutlined,ArrowDownOutlined,EditOutlined,DeleteOutlined,UploadOutlined,CloudDownloadOutlined} from '@ant-design/icons-vue';
import { mapState} from 'vuex';
import { message } from 'ant-design-vue';
import { objDeepClone } from '@/utils/tools';
import DataEmpty from '../../components/DataEmpty.vue';

const datalistModel:any={
	name:"",	
	parentId:0,
}
const subEditModel:any={
	modify:false,
	name:"",
	state:"1",
	id:0,
	interfaceId:0,
	sort:1,
	processor:""
	
}

export default defineComponent({
	name: 'ParamsProcessor',
	components:{
		RightOutlined,
		PlusOutlined,
		ArrowUpOutlined,
		ArrowDownOutlined,
		EditOutlined,
		DeleteOutlined,	
		UploadOutlined,
		CloudDownloadOutlined,
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
			dataList:[],
			dataObj:objDeepClone(datalistModel),
			pageType:"列表",//列表  添加 修改
			subEditObj:objDeepClone(subEditModel),
			popover:{visible:false,loading:false},
			awayShow:true,
			upFileName:"",
			operIndex:0
			
		}
	},
	mounted(){		
		for(let key in this.dataObj){
			if(this.editObj[key]){
				this.dataObj[key]=this.editObj[key]
			}			
		}
		if(this.dataObj.parentId>0){
			this.getData()
		}
		
	},
	methods:{
		backMain(){
			this.$emit("hide")
			if(this.pageType==='添加' || this.pageType==='修改'){
				this.$refs.formRef.resetFields();
				this.subEditObj=objDeepClone(subEditModel)
			}
			
		},
		toUpLoad(){
			this.$refs.file.dispatchEvent(new MouseEvent('click'))
		},
		backList(){
			this.pageType="列表"
			this.subEditObj=objDeepClone(subEditModel)
			
		},
		addSubData(){
			this.pageType="添加",
			this.subEditObj=objDeepClone(subEditModel)
			this.upFileName=""
		},
		
		onVisible(bool:boolean){
			if(!bool){
				this.awayShow=true
			}
		},
		selectProcessor(){
			this.repeatedShow=true
		},
		onClose(){
			this.repeatedShow=false
		},
		onOper(index:number,type:string){
			let item:any=this.dataList[index]
			this.operIndex=index
			if(type==="modify"){
				this.pageType="修改";
				for(let key in this.subEditObj){
					if(item[key]){
						this.subEditObj[key]=item[key]
					}					
				}
				this.subEditObj.state=this.subEditObj.state+''
				let namelist=this.subEditObj.processor.split("/");
				this.upFileName=namelist[namelist.length-1]
				
				return
			}
			if(type==="del"){
				this.delItem(index)
				return
			}
			let _len=this.dataList.length
			if(_len===1 && (type==="up" || type==="down")){
				message.error("两条数据以上才可以操作");
				return
			}
			if(index===0 && type==="up"){
				message.error("已是在最上面了")
				return
			}
			if(index===(_len-1) && type==="down"){
				message.error("已是在最下面了")
				return
			}
			this.listSort(type,index)			
			
		},
		listSort(type:string,index:number){
			let urlMethod="ParamHander/sortUp"
			if(type==="down"){
				urlMethod="ParamHander/sortLow"
			}
			let item:any=this.dataList[index]
			//let tempOrder=item.order
			this.$dsApi({
				url:urlMethod,
				data:{
					id:item.id
				},
				loadmsg:"排序"+type,
				success:(res:any)=>{
					this.dataList.length=0
					let slist=res.data.data
					slist.forEach((item:any)=>{
						this.dataList.push(item)
					})					
				},
				fail:(err:any)=>{
					message.error(err)
				}					
			})
		},
		delItem(index:number){
			this.$dsApi({
				url:"ParamHander/delete",
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
		fileUp(){
			let file = this.$refs.file.files[0];
			if(!file){
				return
			}
			// 拿到上传的文件
			const data = new FormData();
			//把图片或文件添加到data
			data.append("file",file);
			console.log(data.getAll("file")[0])
			let fileobj:any=null
			if(data.getAll("file").length>0){
				fileobj=(data.getAll("file")[0] as any);
				if(fileobj.size>31457280){
					message.error("不能上传大于30M的文件")
					this.$refs.file.value=null;
					return
				}
				if(fileobj.size<=0){
					message.error("不能上传空文件")
					this.$refs.file.value=null;
					return
				}
			}else{
				return
			}
			this.$dsApi({
				url:"ParamHander/upload",
				data:data,
				params:{
					sharkdata:{}
				},
				loadmsg:"上传",
				onUploadProgress:(e:any)=>{
					console.info( Math.floor(e.loaded/e.total*100))
				},
				success:(res:any)=>{
					
					this.subEditObj.processor=res.data.data;
					this.upFileName=fileobj.name
					message.success("上传成功！")
				},
				fail:(err:any)=>{
					message.error(err.errMsg)
				},
				complete:()=>{
					this.$refs.file.value=null;
				}
			})
			
		},
		saveBtn(){
			if(this.popover.loading){
				return
			}
			let url="ParamHander/insert"
			let msgtype="添加"
			let params:any={
				name:this.subEditObj.name,
				processor:this.subEditObj.processor,
				interfaceId:this.dataObj.parentId,																
				state:this.subEditObj.state				
			}
			if(this.pageType==="修改"){
				url="ParamHander/update"
				params.id=this.subEditObj.id
				msgtype="修改"
			}
			this.popover.loading=true
			this.$dsApi({
				url,
				data:params,
				loadmsg:msgtype+"处理器",
				success:(res:any)=>{
					let sobj=res.data.data;
					if(this.pageType==="修改"){
						let item=this.dataList[this.operIndex]
						for(let key in item){
							if(this.subEditObj[key]){
								item[key]=this.subEditObj[key]
							}
						}					
					}else{
						this.dataList.push(sobj)
					}										
					
					this.operIndex=0
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
				url:"ParamHander/all",
				data:{
					interfaceId:this.dataObj.parentId
				},
				loadmsg:"接口入参处理器列表",
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
		}
	},
	computed:{
		...mapState(['userInfo']),
		rules(){
			return {
				name: [
					{ required: true, message: '请输入名称', trigger: 'blur' },					
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
	color:#888;
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
.up-box{
	height:224px;
	width:240px;
	line-height: 30px;
}
.upload-content{	
	padding-right:30px;
}
.down-demo{
	
}
.upload-input{
	display: none !important;
}
.text-grhy{
	color:#888;
	font-size:13px;
}
</style>
