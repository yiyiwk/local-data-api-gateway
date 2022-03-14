
/**
 *对Date的扩展，将 Date 转化为指定格式的String
 *月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 *年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 *例子：
 *(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 *(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
 */
function dateFormat( time: any, fmt: string){
	let d = time;
	if( typeof time == "number") d = new Date( time);
	if( typeof d != "object") return "";
	const o: any = {
		"M+": d.getMonth() + 1, //月份
		"d+": d.getDate(), //日
		"h+": d.getHours(), //小时
		"m+": d.getMinutes(), //分
		"s+": d.getSeconds(), //秒
		"q+": Math.floor((d.getMonth() + 3) / 3), //季度
		"S": d.getMilliseconds() //毫秒
	};
	if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (d.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (const k in o)
		if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

//对象转字串，解决function的丢失问题
const functionNodeKey: string = "sharkFunction()=>";
function objConvertString( json: any, convertFunction = true, space=0){
	if( !convertFunction) return JSON.stringify(json, null, space);
	return JSON.stringify(json, function(key, val) {
		if (typeof val === 'function') {
			return functionNodeKey+val;
		}
		return val;
	},space);
}

//字串转对象，解决有function的问题
function objFromString( json: string, convertFunction = true){
	if( !convertFunction) return JSON.parse(json);
	return JSON.parse( json,function(k,v){
		//以function打头才转换
		if( v && typeof v =="string" && v.indexOf(functionNodeKey)>-1){
			//return new Function( "return "+v)();
			return eval("("+v.substring( functionNodeKey.length)+")")
		}
		return v;
	});
}

//合并前两个方法
function objDeepClone( json: any, convertFunction = true){
	return objFromString( objConvertString( json, convertFunction), convertFunction);
}

function checkMobile(value:string) {
	if (!/(^1[3|4|5|6|7|8|9][0-9]{9}$)/.test(value)) {		
		return false;
	}
	return true;
}
//日期转换为时间戳
function dateToStamp(value:string){
	let valueStandart=""
	if(value.indexOf("-")>-1){
		valueStandart=value.replace(/-/g, '/')
	}
	return new Date(valueStandart).getTime()

}
//字符串格式化 strFormat("a={0},b={1}",1,2)或strFormat("a={name},b={sex}",{name:"123",sex:1})
function strFormat(str: string, ...values: any) {
	if( !str) return "";
	const result = str;
	if( !values || values.length<1) return result;
	if( typeof values[0] == "object"){
		return str.replace(/\{(.+?)\}/g, function (match: any, param: string) {
			if (values[0][param]) {
				return values[0][param];
			} else {
				return "";
			}
		});
	}else{
		return str.replace(/\{(\d+)\}/g, function (match: any, index: number) {
			if (values.length > index) {
				return values[index];
			} else {
				return "";
			}
		});
	}
}

export {
	dateFormat,	
	objFromString,
	objConvertString,
	objDeepClone,
	checkMobile,
	dateToStamp,
	strFormat
}