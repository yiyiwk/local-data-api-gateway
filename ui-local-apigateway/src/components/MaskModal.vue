<template>
	<transition name="fade">
		<div class="cu-modal" :class="[showModal?'show':'']" @tap="close">
			<div class="cu-dialog">
				<slot></slot>
			</div>
		</div>
	</transition>
</template>

<script lang="ts">
import { defineComponent } from 'vue';


export default defineComponent({
	name: 'MaskModal',
	data(){
		return {
			showModal: false
		}
	},
	methods:{
		close() {
			this.showModal = false;
			this.$emit('hide');
		},
		confirm() {
			this.showModal = false;
			this.$emit('confirm');
		},
		show() {
			this.showModal = true;
		}
	}
	
		
});
</script>

<style lang="less" scoped>
	.cu-modal {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		z-index: 1110;
		opacity: 0;
		outline: 0;
		-ms-transform: scale(1.185);
		transform: scale(1.185);
		backface-visibility: hidden;
		perspective: 2000px;
		background: rgba(0, 0, 0, 0.6);
		transition: all 0.3s ease-in-out 0s;
		pointer-events: none;
		
	}
	.cu-modal.show {
		opacity: 1;
		transition-duration: 0.3s;
		-ms-transform: scale(1);
		transform: scale(1);
		overflow-x: hidden;
		overflow-y: auto;
		pointer-events: auto;
	}
	.cu-dialog {
		position: fixed;			
		margin: auto;
		left: 0;
		right: 0;
		top:0;
		bottom:0;
		width: 800px;
		z-index: 10;
		overflow: hidden;	
		height:max-content;
	}

</style>
