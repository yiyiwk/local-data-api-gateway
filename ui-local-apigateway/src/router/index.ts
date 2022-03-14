import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'



import ErrorPage from "../pages/ErrorPage.vue"


//配置路由
const routes:Array<RouteRecordRaw> = [
	{
		path: '/:pathMatch(.*)',
		redirect: '/error',
	},
	{
		path:'/',
		redirect: '/index',
	},
	{
		path:'/login',
		name:'PublicLogin',
		component: () => import('../pages/public/login.vue')
	},
	{
		path:  '/index',
		name: 'IndexIndex',
		component: () => import('../pages/index/index.vue'),
		meta: { requiresAuth: true }
	},
	{
		// 匹配所有路径  vue2使用*   vue3使用/:pathMatch(.*)*或/:pathMatch(.*)或/:catchAll(.*)
		path: "/error",
		name: "ErrorPage",
		component: ErrorPage
		
	}

	
]

const router = createRouter({
	history:createWebHistory(process.env.BASE_URL),
	routes
})

export default router






