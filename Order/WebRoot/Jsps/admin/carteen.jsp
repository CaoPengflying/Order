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
<title>食堂管理</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var editIndex = undefined;

$(function(){
	loadCarteens();
});	

function loadCarteens(){
	$('#datagrid_carteens').datagrid({  
	    url:'/Order/CarteenServlet',  
	    queryParams:{  
	       method:2, 
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	


function endEditing(){
	if (editIndex == undefined){
		return true;
	}

	if ($('#datagrid_carteens').datagrid('validateRow', editIndex)){
		$('#datagrid_carteens').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}

function edit(rowIndex,rowData){
	if (editIndex != rowIndex && endEditing()){
		$('#datagrid_carteens').datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
	}
}

function append(){
	if (endEditing()){
		$('#datagrid_carteens').datagrid('insertRow',{index:0,row:{
			name:""}});
			editIndex = 0;
		$('#datagrid_carteens').datagrid('selectRow', editIndex).datagrid('beginEdit', 0);
	}
}
function remove(){
	var rows = $('#datagrid_carteens').datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_carteens').datagrid('getRowIndex',rows[i]);
		$('#datagrid_carteens').datagrid('deleteRow',index);
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
	var rows_inserted = $('#datagrid_carteens').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_carteens').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_carteens').datagrid('getChanges','updated');
	
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/CarteenServlet",
		{
			method:1,
			json_inserted:json_inserted,
			json_deleted:json_deleted,
			json_updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_carteens').datagrid('acceptChanges');
       		//提示
       		$.messager.show({
				title:'编辑食堂信息',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
	  }
	);	
}
function reject(){
	$('#datagrid_carteens').datagrid('rejectChanges');
	editIndex = undefined;
}

//是否例外bool转字符串
function exceptionFormatter(value, rowData, rowIndex) {
	
	return value?"是":"否";
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
		<%@include file="../../inc/help/admin/carteen.inc" %>
	</div>
	<div data-options="region:'center',title:'食堂管理'" >
		<table  class="easyui-datagrid" id="datagrid_carteens" data-options="toolbar:'#toolbar_carteen',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: edit">
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'ID',width:100">食堂编号</th>
					<th data-options="field:'name',width:200,editor:{type:'textbox',options:{required:true}}">食堂名称</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_carteen" style="height:50px;line-height: 40px;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="append()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="remove()">删除</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" onclick="reject()">撤销</a>
		</div>
	</div>		
			
</body>
</html>
