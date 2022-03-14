<template>
	<div class="warp">
		<div class="dao-lear-box">
			<div class="color-base">
				<RightOutlined  />
				<span class="cursor">服务迁移说明</span>		
			</div>			
		</div>
		<div class="show-box">
			<div class="serve-tip">
				<div class="model-title">1、API网关文件列表</div>
				<!-- <p>1、API网关文件包含：</p> -->
				<p class="sub-p">主程序文件（jar扩展名）</p>
				<p class="sub-p">db数据目录（首次运行后生成，存储所有配置数据，不能删除）</p>
				<p class="sub-p">jar处理器文件目录（如上传了处理器）</p>
				<p class="sub-p">jdbc数据库驱动目录</p>
				<p class="sub-p">jre11文件目录（Windows、Linux 和MacOS的64位版本）</p>			
				<p class="sub-p">startup.win64.bat：windows系统启动文件</p>
				<p class="sub-p">startup.linux64.sh：linux系统启动文件</p>
				<p class="sub-p">startup.osx64.sh：macos系统启动文件</p>
				<div class="model-title margin-top">2、API网关备份</div>
				<p class="sub-p">关闭API网关，将API网关的主程序文件同级目录下所有文件复制备份（推荐压缩保存）。建议每次配置API网关后手工备份一次，并异机保存。</p>
				<p class="sub-p">特别提醒：如果网关使用docker部署，在备份时切记注意db、jar和jdbc目录的位置（容器内还是外部）并备份好，以免引起原有配置数据丢失。</p>
				<div class="model-title margin-top">3、API网关恢复</div>
				<p class="sub-p">将第2步备份的文件复制到新的服务器，解压后运行对应的启动文件即可。</p>
				<div class="model-title margin-top">4、API网关升级</div>
				<p class="sub-p">登录<a href="https://www.511ds.com/" target="_blank">迪赛智慧数可视化互动平台（https://www.511ds.com/）</a>，进入系统管理，选择数据源中的API，下载最新的“本地数据API网关”程序，在本地解压后将之前的“db数据目录”和“jar处理器文件目录”复制到新解压的目录中，启动API网关即可。</p>
						
			</div>
		</div>
	</div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import {RightOutlined} from '@ant-design/icons-vue';

export default defineComponent({
	name:'ServeMain',
	components:{
		
		RightOutlined
		
	},
	data(){
		return {
			scolor:"blue"
			
		}
	},
	
	activated(){
		
	},
	methods:{

	},
	computed:{
		
	}
	
	
})
	
	
</script>

<style lang="less"  scoped>
p{
	margin:0;
	paddig:0;
}

.warp{
	padding:20px 30px;
}
.show-box{
	height:calc(100vh - 150px);
	overflow-y: auto;
}
.serve-tip{
	padding-top:20px;
	color:#666;
	line-height: 30px;
	padding-left:30px;
	font-size:17px;
}
.sub-p{
	text-indent: 2em;
	line-height: 26px;
	font-size:13px;
}
.dao-lear-box{
	height:50px;
	padding:4px 0 12px;
	display: flex;
	align-items: center;
}
.model-title{
	font-size: 15px;
	border-bottom: 1px solid #d9d7d8;
	line-height: 26px;
	height: 33px;
	text-indent: 10px;
	margin-bottom:6px;
	color:#333;
}
.margin-top{
	margin-top:20px;
}


</style>
