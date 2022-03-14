<template>
	<div class="shark-error">
		<div class="header">
      
		</div>
    <DataEmpty text="未找到页面"></DataEmpty>
		
		<div class="buttons">
     <!-- <div v-if="$router" @click="goHomePage">返回上一页</div> -->
      <div @click="goHomePage">返回首页</div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {dateFormat} from '@/utils/tools';
import DataEmpty from '../components/DataEmpty.vue';


export default defineComponent({
  name: 'ErrorPage',
  components:{	
		DataEmpty			
	},
  data(){
    return{
      dateFormat: dateFormat,
      message: "未找到页面"
    }
  },
  mounted(){
    this.buildMessage();
  },
  activated(){
    this.buildMessage();
  },
  methods:{
    buildMessage(){
      if( this.$route.query.message){
        this.message = this.$route.query.message;
      }
      console.info( this.$router)
    },
    // goBackPage(){
    //   this.$router.go( -1);
    // },
    goHomePage(){
      window.location.href= this.$sharkWebPath;
    }
  }
});
</script>

<style scoped lang="less">
.shark-error{
  background: #ffffff;
  
  .header{
    height:50px;
   
    padding:20px 10px;
    font-size:18px;	
    color:#333;
    text-align: center;
    .title{
     
      .logo{
        width:36px;
        height:36px;
       
      }
    }
    .time{
      font-size:14px;	
    }
  }
  .error{
    padding: 20px;
    text-align: center;
    font-size: 14px;
  }
  .buttons{
    display: flex;
    justify-content: center;
    align-items: center;
    div{
      border:1px solid #ccc;
      border-radius: 20px;
      padding:5px 20px;
      margin:0 10px;
    }
  }
}
</style>
