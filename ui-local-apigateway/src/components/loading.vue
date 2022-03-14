<template>	
	<div class="loading-box" v-show="showModal">
		<div class="box">
			<div class="circle"></div>
			<div class="circle"></div>
		</div>
		<div class="loading-text">Loading...</div>
	</div>	
</template>

<script lang="ts">
import { defineComponent} from 'vue';

export default defineComponent({
	name: 'DesignerLoading',	
	data(){
		return {
			showModal:false			
		}
	},	
	methods:{
		show(){
			this.showModal=true
		},
		hide(){
			this.showModal=false
		}
	}
		
})	
	
</script>

<style lang="less">

/***加载特效***/

.loading-box{
	display:flex;
	justify-content: center;
	align-items: center;
	flex-direction: column;
	width:100%;
	height:100%;
	min-height: 400px;
}
.loading-box .box{
	position: relative;
	width:70px;
	height:70px;
}
.loading-box .box .circle{
	position: absolute;
	top:0;
	left:0;
	width:100%;
	height:100%;
	background:#38c1ff;
	border-radius: 50%;
	animation:animate 5s linear infinite
}

.loading-box .box .circle:nth-child(2){
	background: #ff3378;
	animation-delay:-2.5s
}

@keyframes animate{
	0%{
		transform:scale(1);
		transform-origin:left;
	}
	50%{
		transform:scale(0);
		transform-origin:left;
	}
	50.01%{
		transform:scale(0);
		transform-origin:right;
	}
	100%{
		transform:scale(1);
		transform-origin:right;
	}
}

.loading-text{
	margin-top:7px;
	font-weight:400;
	font-size:20px;
	letter-spacing: 4px;
	color:#333;
}


</style>
