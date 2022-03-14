<template>
	<div id="app">
		<router-view  v-slot="{ Component }">
			<transition name="fade">
				<component :is="Component" />
			</transition>
		</router-view>
	</div>
</template>

<script>


export default {
	name: 'App',
	created(){
		console.log("服务正在启动...")
	}
}
</script>

<style>
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	color: #333;
	
	
}
.fade-enter-active,.fade-leave-active {
	transition: opacity .2s;
}
/*
.fade-leave-active{
	transition: opacity .2s;
}
*/
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
	opacity: 0;
}
::-webkit-scrollbar{
	width: 9px;			
}
::-webkit-scrollbar-thumb {			
	border-radius: 4px;
	box-shadow: inset 0 0 9px rgba(0,0,0,0.3);
	background:rgba(0,0,0,0.3);
}
::-webkit-scrollbar-track {
  box-shadow: inset 0 0 9px rgba(0,0,0,0.1);
  border-radius: 4px;
  background:rgba(0,0,0,0.1);
}
</style>
