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
<title>节假日设置</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>

<script type="text/javascript">
var editIndex = undefined;

$(function(){
	loadHolidays();
});	

function loadHolidays(){
	$('#datagrid_holidays').datagrid({  
	    url:'/Order/HolidayServlet',  
	    queryParams:{  
	       method:1, 
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	

function endEditing(){
	if (editIndex == undefined){
		return true;
	}

	if ($('#datagrid_holidays').datagrid('validateRow', editIndex)){
		$('#datagrid_holidays').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function edit(rowIndex,rowData){
	if (editIndex != rowIndex && endEditing()){
		$('#datagrid_holidays').datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
	}
}

function append(){
	if (endEditing()){
		$('#datagrid_holidays').datagrid('insertRow',{index:0,row:{
			date:date2String(new Date()),
			lunch:1,
			dinner:1,
			midnight:1}
		});
		editIndex = 0;
		$('#datagrid_holidays').datagrid('selectRow', editIndex).datagrid('beginEdit', 0);
	}
}
function remove(){
	var rows = $('#datagrid_holidays').datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_holidays').datagrid('getRowIndex',rows[i]);
		$('#datagrid_holidays').datagrid('deleteRow',index);
	}
}

function save(){
	if (!endEditing()){
		return;
	}
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){
		return;
	}
	//获取变更的记录
	var rows_inserted = $('#datagrid_holidays').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_holidays').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_holidays').datagrid('getChanges','updated');
	
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/HolidayServlet",
		{
			method:2,
			inserted:json_inserted,
			deleted:json_deleted,
			updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_holidays').datagrid('acceptChanges');
       		//提示
       		$.messager.show({
				title:'节假日设置',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
	  }
	);	
}
function reject(){
	$('#datagrid_holidays').datagrid('rejectChanges');
	editIndex = undefined;
}

//班组ID至name的格式化
function mealFormatter(value, rowData, rowIndex) {	
	return value==1?"是":"否";
}
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
		<%@include file="../../inc/help/admin/holiday.inc" %>
	</div>
	<div data-options="iconCls:'icon-holiday',region:'center',title:'节假日设置'" >
		<table  class="easyui-datagrid" id="datagrid_holidays" data-options="toolbar:'#toolbar_holiday',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: edit">
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'date',width:100,editor:{type:'datebox'}">日期</th>
					<th data-options="field:'lunch',width:80,align:'right',editor:{type:'checkbox',options:{on:1,off:0}},formatter:mealFormatter">中餐</th>
					<th data-options="field:'dinner',width:80,align:'right',editor:{type:'checkbox',options:{on:1,off:0}},formatter:mealFormatter">晚餐</th>
					<th data-options="field:'midnight',width:80,align:'right',editor:{type:'checkbox',options:{on:1,off:0}},formatter:mealFormatter">零点餐</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_holiday" style="height:50px;line-height: 40px;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="append()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="remove()">删除</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" onclick="reject()">撤销</a>
		</div>
	</div>		
			
</body>
</html>
