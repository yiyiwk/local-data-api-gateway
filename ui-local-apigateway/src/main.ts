import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ds_api from './utils/axios';
import "ant-design-vue/dist/antd.css";

const app=createApp(App);


app.config.globalProperties.$dsApi =ds_api
app.config.globalProperties.$sharkWebPath ='/sharkapiweb/';
//权限检查

	
	if(document.cookie.indexOf("apiUserHasLogin=1")<0){
		localStorage.removeItem('saveLoginInfo')
	}
	
  router.beforeEach((to: any) => {

    const userInfo: any = ( store && store.state && store.state.userInfo ) || {};
	
    if (to.meta && to.meta.requiresAuth && !userInfo.token) {
      // 此路由需要授权，请检查是否已登录
      // 如果没有，则重定向到登录页面
      const path="/login"
      return {
        path,
        // 保存我们所在的位置，以便登录成功后再来
        query: { rdpath: to.path/*,rdquery: JSON.stringify(to.query || {})*/},
      }
    }
  });

import { ConfigProvider,Popover,Select,DatePicker,Input,Button,TimePicker,Popconfirm,Dropdown,Image,TreeSelect,Pagination,Form,Checkbox,Radio,Tooltip} from 'ant-design-vue';
app.use(ConfigProvider);
app.use(Popover);
app.use(Select);
app.use(DatePicker);
app.use(Input);
app.use(Button);
app.use(TimePicker);
app.use(Popconfirm);
app.use(Dropdown);
app.use(Image);
app.use(TreeSelect);
app.use(Pagination);
app.use(Form);
app.use(Checkbox);
app.use(Radio);
app.use(Tooltip);

app.use(store).use(router).mount('#app')
