<template>
	<div class="com-box">
		<a-form ref="formRef" :model="sqlObj" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol" @finish="saveBtn">
			<a-form-item ref="name" label="名称：" name="name">
				<a-input v-model:value="sqlObj.name" />
			</a-form-item>		   
			<a-form-item ref="subtype" label="类型：" name="type">
				<a-select  v-model:value="selectedVal" ref="select" @change="handleChange">					
					<a-select-option :value="item.type"  v-for="(item,index) in sqlList" :key="index">{{item.name}}</a-select-option>											
				</a-select>
			</a-form-item>
			<a-form-item ref="databaseName" label="类型名称：" name="databaseName" v-if="selectedVal=='9999'">
				<a-input v-model:value="sqlObj.databaseName"   />
			</a-form-item>
			<a-form-item ref="upFiles" label="驱动包上传：" extra="支持上传多个jar包"  name="upFiles" v-if="selectedVal=='9999'">
				<a-button type="primary" style="margin-bottom:5px;" size="large" @click="toUpLoad">
					<template #icon>
						<UploadOutlined />									
					</template>					
					上传					
				</a-button>
				<span class="margin-left-sm" v-if="upFileNameList.length>0">已上传:
					<span class="close-file" v-for="(item,index) in upFileNameList" :key="index">
					{{item}}
					<CloseCircleOutlined style="position: absolute;right:-15px;font-size:14px;top:-10px;z-index: 3;" @click="closeFile(index)" />
					</span>				
				</span>
				<input class="upload-input" id="files" type="file" ref="upFile" @change="fileUp()" accept=".jar" >
			</a-form-item>
			<a-form-item ref="file" label="JAR驱动包名："  name="file">
				<a-input v-model:value="sqlObj.file" :disabled="true"  />
			</a-form-item>
			<a-form-item ref="driverClass" label="驱动类名：" name="driverClass">
				<a-input v-model:value="sqlObj.driverClass" :disabled="selectedVal!='9999'"  />
			</a-form-item>
			<a-form-item ref="url" :label="selectedVal=='9999'?'连接URL：':'URL：'" name="url">   
				<a-textarea v-model:value="sqlObj.url" :auto-size="{ minRows: 7, maxRows: 12 }"   allow-clear />
			</a-form-item>
			
			<a-form-item ref="username" label="用户名：" name="username">
				<a-input v-model:value="sqlObj.username" />
			</a-form-item>
			<a-form-item ref="password" label="密码：" name="password">
				<a-input-password v-model:value="sqlObj.password" />
			</a-form-item>
			<a-form-item ref="state" label="状态："  name="state" >
				<a-radio-group name="radioGroup" v-model:value="sqlObj.state" >					
					<a-radio value="1">开启</a-radio>
					<a-radio value="0">禁用</a-radio>								

				</a-radio-group>
			</a-form-item>
			<div class="add-btn-box">
				<a-popover v-model:visible="popover.visible" trigger="contextmenu" color="pink" placement="bottom" :arrowPointAtCenter="true" @visibleChange="( )=>popover.message?'':popover.visible=false">
					<template #content>
						{{popover.message}}
					</template>
					<a-button type="primary" :loading="loading" class="page-btn" html-type="submit" >保存</a-button>
				</a-popover>
				<a-button class="page-btn" @click="onReset">取消</a-button>
			</div>
		</a-form> 
		
	</div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import { message } from 'ant-design-vue';
import { objDeepClone } from '@/utils/tools';
import { UploadOutlined,CloseCircleOutlined } from '@ant-design/icons-vue';
import { RuleObject } from 'ant-design-vue/es/form/interface';


var datalist:any={
	name:"",
	databaseName:"",
	type:"9999",
	id:9999,
	url:"",					
	username:"",
	password:"",
	modify:false,	
	state:"1",
	file:"",
	driverClass:"",
	custom:"0"
}

export default defineComponent({
	name:"DataAdd",
	components:{
		UploadOutlined,
		CloseCircleOutlined
	},
	props:{
		editObj:{
			type:Object,
			default () {
				return objDeepClone(datalist)
			}
		}
	},
	data(){
		return {
			sqlObj:objDeepClone(datalist),
			sqlList:[
				{
					name:"自定义数据库类型",
					id:9999,
					type:"9999",
					defaultUrl:""
				}
			],
			labelCol: { span: 5},
			wrapperCol: { span: 19 },
			selectedVal:'9999',
			loading:false,
			popover:{visible:false},			
			upFileNameList:[],
			
			
		}
	},
	watch:{
		editObj:{
			handler(newv:any){
				this.upFileNameList.length=0
				this.getSqlList()
				console.log("new=>",newv)
				//如果有值，赋值到sqlObj对象中
				if(Object.keys(newv).length>0){
					for(let key in this.sqlObj){						
						this.sqlObj[key]=newv[key]
					}
					this.sqlObj.state=this.sqlObj.state+''
					this.selectedVal=this.sqlObj.type.toString()

				}else{
					this.sqlObj=objDeepClone(datalist);
					this.selectedVal="9999"
					this.sqlObj.custom='1'
					if(this.sqlList.length>0){
						this.sqlObj.url =this.sqlList[0].defaultUrl
					
					}
					
				}
				this.loading=false	
			},
			deep: true,
			immediate: true
		}
		// editObj(newv){
		// 	console.log("new=>",newv)
		// 	//如果有值，赋值到sqlObj对象中
		// 	if(Object.keys(newv).length>0){
		// 		for(let key in this.sqlObj){					
		// 			this.sqlObj[key]=newv[key]
		// 		}
		// 		this.sqlObj.state=this.sqlObj.state+''
		// 		this.selectedVal=this.sqlObj.type.toString()

		// 	}else{
		// 		this.sqlObj=objDeepClone(datalist);
		// 		this.selectedVal="1"				
		// 	}
		// 	this.loading=false	 
		// }
	},
	mounted(){
		this.upFileNameList.length=0
		this.getSqlList()
		if(Object.keys(this.editObj).length>0){
			for(let key in this.sqlObj){
				this.sqlObj[key]=this.editObj[key]
			}
			this.selectedVal=this.sqlObj.type.toString()
			
		}else{
			
			this.sqlObj=objDeepClone(datalist)
			this.selectedVal="9999"
			this.sqlObj.custom='1'
		}		
		console.log("mounted=>",this.editObj)		
		//this.getSqlList()
	},
	deactivated(){
		this.sqlObj=objDeepClone(datalist)
		this.jarEdit=true
	},
	methods:{
		onReset(){
			this.sqlObj=objDeepClone(datalist)			
			this.upFileNameList.length=0	
			this.$emit("comfire")
		},
		handleChange(value:any){
			console.log("value=>",value)
			this.upFileNameList.length=0
			
			if(value=='9999'){
				console.log("localBd=>",value)
				this.sqlObj.url=""
				this.sqlObj.file=""
				this.sqlObj.driverClass=""
				this.sqlObj.databaseName=""
				this.sqlObj.custom='1'
			}else{
				let sIndex=this.sqlList.findIndex((item:any) => item.id == value);
				this.sqlObj.databaseName=this.sqlList[sIndex].name
				this.sqlObj.url =this.sqlList[sIndex].defaultUrl
				this.sqlObj.file=this.sqlList[sIndex].file
				this.sqlObj.driverClass=this.sqlList[sIndex].driverClass				
				this.sqlObj.custom=this.sqlList[sIndex].custom
			
			}
			
			this.sqlObj.type=value			
			this.selectedVal=value
		},
		closeFile(index:number){
			this.upFileNameList.splice(index,1)
			this.sqlObj.file=this.upFileNameList.join("###")
		},
		toUpLoad(){
			this.$refs.upFile.dispatchEvent(new MouseEvent('click'))
		},
		fileUp(){
			let file = this.$refs.upFile.files[0];
			if(!file){
				return
			}
			message.loading({content:"上传中...",key:"uping"});
			// 拿到上传的文件
			const data = new FormData();
			//把图片或文件添加到data
			data.append("file",file);
			console.log(data.getAll("file")[0])
			let fileobj:any=null
			if(data.getAll("file").length>0){
				fileobj=(data.getAll("file")[0] as any);				
				if(fileobj.size<=0){
					message.error("不能上传空文件")
					this.$refs.upFile.value=null;
					return
				}
			}else{
				return
			}
			const uping=(msg:string)=>{
				message.success({content:msg,key:"uping",duration:2});
			}
			this.$dsApi({
				url:"Datamart/upload",
				data:data,
				params:{
					sharkdata:{}
				},
				loadmsg:"上传",
				onUploadProgress:(e:any)=>{
					console.info( Math.floor(e.loaded/e.total*100))
				},
				success:(res:any)=>{
					this.upFileNameList.push(res.data.data)					
					this.sqlObj.file=this.upFileNameList.join("###")
					
					
					//message.success("上传成功！")
					uping("上传成功！")
				},
				fail:(err:any)=>{
					//message.error(err.errMsg)
					uping(err.errMsg)
				},
				complete:()=>{
					this.$refs.upFile.value=null;
				}
			})
			
		},
		saveBtn(){
			if(this.loading){
				return
			}
			
			let url="Datamart/insert"
			let msgtype="添加"
			let params:any={
				name:this.sqlObj.name,				
				url:this.sqlObj.url,									
				username:this.sqlObj.username,
				password:this.sqlObj.password,							
				state:this.sqlObj.state
				
			}
			if(this.selectedVal=='9999'){
				params.type="0"
				this.sqlObj.custom="1"
			}else{
				params.type=this.sqlObj.type
			}
			if(this.sqlObj.custom==="1"){
				if(this.sqlObj.file.length===0){
					this.popover.message = "需要上传驱动包";
					setTimeout(() => {
						this.popover.visible = true;
					}, 100);
					this.loading=false
					return
				}
				params.file=this.sqlObj.file
				params.class=this.sqlObj.driverClass
				params.dataBaseName=this.sqlObj.databaseName
			}
			
			if(this.sqlObj.modify===true){
				url="Datamart/update"
				params.id=this.sqlObj.id
				msgtype="修改"
			}
			this.loading=true
			this.$dsApi({
				url,
				data:params,
				loadmsg:msgtype+"数据源",
				success:(res:any)=>{
					//console.log(res)
					let sobj:any=res.data.data;
					sobj.modify=this.sqlObj.modify
					sobj.databaseName=this.sqlObj.databaseName
					sobj.custom=this.sqlObj.custom
					this.loading=false					
					this.$emit("comfire",JSON.stringify(sobj))
					this.upIndex=0
				},
				fail:(err:any)=>{
					this.popover.message = err.errMsg;
					setTimeout(() => {
						this.popover.visible = true;
					}, 100);
					this.loading=false
				}					
			})
			
		},
		getSqlList(){
			
			this.$dsApi({
				url:"Datamart/getAllDatabase",
				data:{},
				loadmsg:"驱动列表",
				success:(res:any)=>{
					this.sqlList.length=0
					let slist=res.data.data;
					if(slist.length>0){
						slist.forEach((item:any)=>{
							this.sqlList.push(item)
						})
						if(this.selectedVal=="9999"){
							this.selectedVal="1"
						}
						
					}
					
					this.sqlList.push({
						name:"自定义数据库类型",
						id:9999,
						type:"9999",
						defaultUrl:""
					})
					if(this.selectedVal!="9999"){
						let num=Number(this.selectedVal);
						let sIndex=this.sqlList.findIndex((item:any) => item.id == num);
						if(this.sqlObj.url==""){
							this.sqlObj.url =this.sqlList[sIndex].defaultUrl
						}
						this.sqlObj.type=this.sqlList[sIndex].type+'';
						this.sqlObj.databaseName=this.sqlList[sIndex].name
						this.sqlObj.custom=this.sqlList[sIndex].custom
						this.sqlObj.file = this.sqlList[sIndex].file
						this.sqlObj.driverClass= this.sqlList[sIndex].driverClass
						if(this.sqlObj.custom==="1"){
							if(this.sqlObj.file.indexOf("###")){
								this.upFileNameList=this.sqlObj.file.split("###")
							}else{
								this.upFileNameList.push(this.sqlObj.file)
							}
							
						}
					}

					
					
				},
				fail:(err:any)=>{
					message.error(err.errMsg)
				}
				
			})
			
		},
		checkdbname(rules: RuleObject,databaseName: string){			
			let isAccor:boolean=true;
			this.sqlList.forEach((item:any)=>{
				if(databaseName==item.name){
					isAccor=false
				}
				
			})
			if(!isAccor){
				return Promise.reject("自定义数据库名称不能重复");
			}
			return Promise.resolve();
		}
		
	},
	computed:{
		rules(){
			let jobj:any={
				name: [
					{ required: true, message: '请输入名称', trigger: 'blur' },
					{ min: 1, max: 50, message:"名称不能为空或大于50位", trigger: 'blur' }					
				],
				url: [
					{ required: true, message: '请输入数据库url', trigger: 'blur' },					
				]
				
			}
			if(this.sqlObj.custom==='1'){
				jobj.driverClass=[
					{ required: true, message: '请输入驱动类名', trigger: 'blur' },
					{ min: 1, max: 100, message:"驱动类名不能为空或大于100位", trigger: 'blur' }
				]
				jobj.databaseName=[
					{ required: true, message: '请输入类型名称', trigger: 'blur' },
					{ min: 1, max: 50, message:"类型名称不能为空或大于50位", trigger: 'blur' },
					{ validator: this.checkdbname, trigger: 'blur' }
				]
			}
			return jobj
		}
		
		
	}
		
	
})	
</script>

<style scoped lang="less">

.com-box{
	width:700px;
}
.add-btn-box{
	margin-top:30px;
	padding-left:78px;	
	.page-btn{		
		width:98px;
		height:42px;
		border-radius: 3px;
		margin-right:30px;
	}
	
}	
.upload-input{
	display: none !important;
}
.margin-left-sm{
	margin-left:10px;
}
.close-file{
	position: relative;
	padding:5px 5px;
	border:1px solid #0055AF;
	border-radius: 3px;
	display: inline-block;
	margin-right:20px;
}

</style>
