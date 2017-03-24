<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Admin user = (Admin) request.getSession().getAttribute("user");
	int target = Integer.parseInt(request.getParameter("target"));
	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>套餐价格</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>

<script type="text/javascript">
$(function(){
	loadPrices();
});	

function loadPrices(){
	$('#datagrid_prices').datagrid({  
	    url:'/Order/PriceServlet',  
	    queryParams:{  
	       method:2, 
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	

var editIndex = undefined;
function endEditing(){
	if (editIndex == undefined){
		return true;
	}
	if ($('#datagrid_prices').datagrid('validateRow', editIndex)){
		$('#datagrid_prices').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function edit(rowIndex,rowData){
	if (editIndex != rowIndex && endEditing()){
		$('#datagrid_prices').datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
	}
}

function append(){
	if (endEditing()){
		$('#datagrid_prices').datagrid('insertRow',{index:0,row:{
			date_start:date2String(new Date()),
			date_end:date2String(new Date()),
			lunch_normal:10,
			lunch_holiday:10,
			lunch_exception:10,
			dinner_normal:10,
			dinner_holiday:10,
			dinner_exception:10,
			midnight_normal:10,
			midnight_holiday:10,
			midnight_exception:10}});
			editIndex = 0;
		$('#datagrid_prices').datagrid('selectRow', editIndex).datagrid('beginEdit', 0);		
	}
	
}
function remove(){
	var rows = $('#datagrid_prices').datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_prices').datagrid('getRowIndex',rows[i]);
		$('#datagrid_prices').datagrid('deleteRow',index);
	}
}

function save(){
	if (!endEditing()){
		return;
	}
	//获取变更的记录
	var rows_inserted = $('#datagrid_prices').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_prices').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_prices').datagrid('getChanges','updated');
	
	//验证日期的合法性
	for(var i=0; i<rows_inserted.length; i++){
		var str1 = rows_inserted[i].date_start; 
		var str2 = rows_inserted[i].date_end;
		var date_start = new Date(Date.parse(str1.replace(/-/g, "/"))); 
		var date_end = new Date(Date.parse(str2.replace(/-/g, "/")));  
		if(date_start > date_end){//如果日期错误提示并且回到更改之前的状态
			var msg = "日期输入错误：起始日期["+str1+"]大于结束日期["+str2+"]";
			alert(msg);
			return;
		}
	}
	for(var i=0; i<rows_updated.length; i++){
		var str1 = rows_updated[i].date_start;  
		var str2 = rows_updated[i].date_end;
		var date_start = new Date(Date.parse(str1.replace(/-/g, "/")));
		var date_end = new Date(Date.parse(str2.replace(/-/g, "/")));  
		if(date_start > date_end){
			var msg = "日期输入错误：起始日期["+str1+"]大于结束日期["+str2+"]";
			alert(msg);
			return;
		}
	}
	
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){//当选择取消时，恢复到操作之前
			$('#datagrid_prices').datagrid('rejectChanges');
			editIndex = undefined;
		return;
	}
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/PriceServlet",
		{
			method:1,
			inserted:json_inserted,
			deleted:json_deleted,
			updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_prices').datagrid('acceptChanges');
       		//提示
       		$.messager.show({
				title:'编辑价格',
				timeout:3000,
				msg:result,
				width:200,
				showType:'slide'
			});
	  }
	);	
}

function reject(){
	$('#datagrid_prices').datagrid('rejectChanges');
	editIndex = undefined;
}
//自定义验证(扩展easyui验证)
$.extend($.fn.validatebox.defaults.rules, {
 //验证金额
 money:{
          validator: function (value, param) {
           	return (/^(([1-9]\d*)|\d)(\.\d{1,2})?$/).test(value);
           },
           message:'请输入正确的金额'
    }
    });
</script>
		
</head>

<body class="easyui-layout">
	<div align="center"  data-options="region:'north',collapsible:false" title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc" %> 	
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_admin.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/admin/price.inc" %> 
	</div>
	<div data-options="region:'center',title:'套餐价格'" >
		<table  class="easyui-datagrid" id="datagrid_prices" data-options="toolbar:'#toolbar_price',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: edit">
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'date_start',width:100,editor:{type:'datebox',options:{formatter:date2String,parser:string2Date}}">起始日期</th>
					<th data-options="field:'date_end',width:100,editor:{type:'datebox'}">终止日期</th>
					<th data-options="field:'lunch_normal',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">中餐[日常]</th>
					<th data-options="field:'lunch_holiday',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">中餐[假日]</th>
					<th data-options="field:'lunch_exception',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">中餐[例外]</th>					
					<th data-options="field:'dinner_normal',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">晚餐[日常]</th>
					<th data-options="field:'dinner_holiday',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">晚餐[假日]</th>
					<th data-options="field:'dinner_exception',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">晚餐[例外]</th>
					<th data-options="field:'midnight_normal',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">零点餐[日常]</th>
					<th data-options="field:'midnight_holiday',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">零点餐[假日]</th>
					<th data-options="field:'midnight_exception',width:80,align:'right',editor:{type:'numberbox',options:{precision:1,validType:'money'}}">零点餐[例外]</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_price" style="height:50px;line-height: 40px;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="append()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="remove()">删除</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" onclick="reject()">撤销</a>
		</div>
	</div>		
</body>
</html>
