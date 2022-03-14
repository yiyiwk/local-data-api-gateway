<template>
	<div class="warp">
		<div class="dao-lear-box">
			<div class="color-base">
				<RightOutlined  />
				<span class="cursor" @click="backList">数据源列表</span>
				<span class="grhy" v-if="operType!=='列表'">
					<RightOutlined  />
					<span>{{operType+'数据源'}}</span>
				</span>
			</div>
			<div v-if="operType==='列表'">
				<a-button type="primary" @click="addData">
					<template #icon><PlusOutlined /></template>
					添加数据源
				</a-button>
			</div>
		</div>
		
		<div class="show-box">
			<div v-if="operType==='列表'">
				<div class="table">
					<div class="tr">
						<div class="td thbg" style="width:10%">序号</div>
						<div class="td thbg" style="width:20%">名称</div>
						<div class="td thbg" style="width:20%">数据库名称</div>
						<div class="td thbg" style="width:10%">状态</div>
						<div class="td thbg" style="width:40%">操作</div>
					</div>
					<div class="tr" v-for="(item,index) in dataList" :key="index">
						<div class="td" style="width:10%">{{index+1}}</div>
						<div class="td" style="width:20%">{{item.name}}</div>
						<div class="td" style="width:20%">{{item.databaseName}}</div>
						<div class="td" style="width:10%">{{item.state==1?'启用':'禁用'}}</div>
						<div class="td page-around" style="width:40%">						
							<div class="cursor color-base" @click="onOper(index,'modify')"><EditOutlined />修改</div>
							<a-popconfirm title="确定要删除此数据吗？删除后是不可恢复的！" @confirm="delConfirm(index)"  ok-text="确定" cancel-text="取消" placement="bottomRight">
							<div class="cursor color-base"><DeleteOutlined />删除</div>
							</a-popconfirm>
						</div>
					</div>
				</div>
				<DataEmpty v-if="dataList.length==0"></DataEmpty>
			</div>
			
			<div class="oper-box" >
				<keep-alive>
					<DataAdd :editObj="tempEditObj" @comfire="addOrModify" v-if="operType==='添加' || operType==='修改'"></DataAdd>
				</keep-alive>
			</div>
		</div>
		
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { RightOutlined,PlusOutlined,EditOutlined,DeleteOutlined} from '@ant-design/icons-vue';
import { mapState} from 'vuex';
import { message } from 'ant-design-vue';
import DataAdd from './add.vue';
import DataEmpty from '../../components/DataEmpty.vue';

export default defineComponent({
	name: 'DataList',
	components:{
		RightOutlined,
		PlusOutlined,		
		EditOutlined,
		DeleteOutlined,
		DataAdd,
		DataEmpty
		
	},
	data(){
		return {
			dataList:[],
			operType:"列表",//列表 添加  修改 
			tempEditObj:{},
			operIndex:0
		}
	},
	activated(){
		this.getData()
	},
	deactivated(){
		this.backList()
	},
	methods:{
		backList(){
			this.operType="列表"
			this.tempEditObj={}
		},
		delConfirm(index:number){
			this.onOper(index,'del');
		},
		onOper(index:number,type:string){
			let item:any=this.dataList[index]
			this.operIndex=index
			if(type==="modify"){
				
				this.operType="修改"
				item.modify=true
				console.log("item=>",item)
				//this.tempEditObj=item
				for(let key in item){
					this.tempEditObj[key]=item[key]
				}
				return
			}
			if(type==="del"){
				this.delItem(index)
				return
			}
			
		},
		delItem(index:number){
			this.$dsApi({
				url:"Datamart/delete",
				data:{
					id:this.dataList[index].id
				},
				loadmsg:"删除数据源",
				success:()=>{
					message.success("删除成功")
					this.dataList.splice(index,1)
				},
				fail:(err:any)=>{
					message.error("删除失败，"+err.errMsg)
				}					
			})
			
		},
		addData(){
			this.operType="添加"
		},
		addOrModify(e:string){
			if(e){
				let sobj:any=JSON.parse(e);
				console.log("addormodify=>",sobj)
				if(sobj.modify){
					let item=this.dataList[this.operIndex]
					for(let key in item){
						if(sobj[key]){
							item[key]=sobj[key]
						}
					}
				}else{				
					this.dataList.push(sobj)
				}
			}
			
			this.operType="列表"
			this.tempEditObj={}
		},
		//获取列表数据
		getData(){
			this.dataList.length=0
			this.$dsApi({
				url:"Datamart/all",
				data:{},
				loadmsg:"数据源列表",
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
.grhy{
	color:#666
}
</style>
