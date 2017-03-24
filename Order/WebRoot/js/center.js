//增加和移除编辑器 
	   $.extend($.fn.datagrid.methods, {
		addEditor : function(jq, param) {
			if (param instanceof Array) {
				$.each(param, function(index, item) {
					var e = $(jq).datagrid('getColumnOption', item.field);
					e.editor = item.editor;
				});
			} else {
				var e = $(jq).datagrid('getColumnOption', param.field);
				e.editor = param.editor;
			}
		},
		removeEditor : function(jq, param) {
			if (param instanceof Array) {
				$.each(param, function(index, item) {
					var e = $(jq).datagrid('getColumnOption', item);
					e.editor = {};
				});
			} else {
				var e = $(jq).datagrid('getColumnOption', param);
				e.editor = {};
			}
		}
	});

//得到明天时间
function gettoday(){
				var date = new Date();
				var ctime = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+(date.getDate());
				return ctime;
			}
//由当前日期得到之后i天的日期
function  getMoreDate(i){
	if(i<0)return "";
	else{
			var date=new Date();
			 var fro = date.getTime();
				var to = fro+i * 24 * 60 * 60 * 1000;
				to = new Date(to);
				to = to.getFullYear()+'-'+(to.getMonth()+1)+'-'+to.getDate();
				return to;
	}
}
	//由当前日期得到之后i天的日期
	function  getMoreDateYMD(i){
		if(i<0)return "";
		else{
				var date=new Date();
				 var fro = date.getTime();
					var to = fro+i * 24 * 60 * 60 * 1000;
					to = new Date(to);
					to = to.getFullYear()+'年'+(to.getMonth()+1)+'月'+to.getDate()+'日';
					return to;
		}
	}
	 
	
	//得到当前月的第一天
	function getFirstDayInMonth(){
		var  day = new Date();
        var   firstdate = day.getFullYear() + '-' +(day.getMonth()+1)+ '-01'; 
        return firstdate;  
     }
	//得到当前月的最后一天
	function getLastDayInMonth(){ 
		var  today = new Date();
		var year=today.getFullYear();
		var month=(today.getMonth()+1)<10?"0"+(today.getMonth()+1):today.getMonth()+1;
		var  day = new Date(year,month,0);   
        var lastdate = year + '-' + month + '-' + day.getDate();//获取当月最后一天日期    
        return lastdate;  
     }
	 function oTypeformatter(value,row,index){
						switch(value){
							case "1": return "中餐";
							case "2": return "晚餐";
							case "3": return "零点A餐";
							case "4": return "零点B餐";
							case "5": return "零点C餐";
						}
		}
	 
	 function loadhtml_financeManager(titles,html,uID){
			if($('#zw').tabs('exists',titles)){
				$('#zw').tabs('select',titles);//$('#zw').tabs('getTab',titles).panel('refresh', html);
			}else{
				$('#zw').tabs('add',{
					title: titles,
					closable:true,
					selected: true,
					href: "Jsps/financeManager/"+html+"&uID="+uID,
					tools: [{
			iconCls: 'icon-mini-refresh',
			handler: function() {
			var tab = $('#zw').tabs('getSelected'); 
				tab.panel('refresh', "Jsps/financeManager/"+html);
					}}],
				});
			}
		}
	
	//string：字符串表达式包含要替代的子字符串。
	//reallyDo：被搜索的子字符串。
	//replaceWith：用于替换的子字符串。
	String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) {  
	    if (!RegExp.prototype.isPrototypeOf(reallyDo)) {  
	        return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith);  
	    } else {  
	        return this.replace(reallyDo, replaceWith);  
	    }  
	}
	
//easyui 的扩展
	  
	
