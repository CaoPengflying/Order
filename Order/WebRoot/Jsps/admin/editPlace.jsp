													<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%@ page import="java.sql.Connection" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<%
	Admin user = (Admin) request.getSession().getAttribute("user");
	
	//获取需要编辑送餐点的职工号和姓名
	String employeeID = request.getParameter("employeeID");
	String name = java.net.URLDecoder.decode(request.getParameter("name"),"utf-8");
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>送餐点编辑</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script>
$(function(){	
	loadPlaces();
	loadAllPlaces();
});

//加载职工送餐点列表
function loadPlaces()
{ 
	$.post("/Order/PlaceServlet",
		{
			method:5,  
			employeeID:'<%=employeeID%>',
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var places = data.places;
			$("#datagrid_places_employee").datagrid('loadData' , places);
	  }
	);	
} 

//加载所有送餐点
function loadAllPlaces()
{ 
	$('#datagrid_place_all').datagrid({  
	    url:'/Order/PlaceServlet', 
	    pagination:true,
		pageSize:14,
		pageList:[10,14,20],
	    queryParams:{  
	       method:4, 
	       pagination:true,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 	
}

//添加送餐点至职工送餐列表中
function addPlaceToEmployee(){
	//获取现有的送餐点
	var rows = $("#datagrid_places_employee").datagrid('getRows');
	
	//获取选中待添加的送餐点
	var rows_appending = $("#datagrid_place_all").datagrid('getChecked');
	if(rows_appending.lengh < 1){
		return;
	}
	
	//不存在则添加，避免重复
	for(var i=0; i<rows_appending.length; i++){
		var exist = false;
		for(var j=0; j<rows.length; j++){
			if(rows[j].ID == rows_appending[i].ID){
				exist = true;
				break;
			}
		}
		if(!exist){
			$("#datagrid_places_employee").datagrid('appendRow',rows_appending[i]);
		}		
	}
}
function removePlaceFromEmployee(){
	//获取选中的送餐点
	var rows = $("#datagrid_places_employee").datagrid('getChecked');
	if(rows.lengh < 1){
		return;
	}
	
	//逐行删除，注意逆序删除
	for(var i=rows.length-1;i>=0; i--){
		var index = $("#datagrid_places_employee").datagrid('getRowIndex',rows[i]);
		$("#datagrid_places_employee").datagrid('deleteRow',index);
	}
}

function savePlace(){
	var msg = "确定要保存修改吗？";

	//获取职工送餐点
	var rows = $("#datagrid_places_employee").datagrid('getRows');
	if(rows.length > 13){
		msg = "每个职工最多只能有13个送餐点，请重新选择";
		alert(msg);
		return;
	}
	
	if(confirm(msg) == false){
		return;
	}
	
	//生成送餐点ID序列，以逗号隔开
	var placeIDs = "";
	for(var i=0; i<rows.length; i++){
		placeIDs += rows[i].ID;
		if(i < rows.length-1){
			placeIDs += ",";
		}
	}
	
	$.post("/Order/EmployeeServlet",
		{
			method:6, 
			employeeID:'<%=employeeID%>',
			placeIDs:placeIDs,
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		$.messager.show({
				title:'编辑职工送餐点',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
	  }
	);
	
}

//上移
function moveup() {
    var row = $("#datagrid_places_employee").datagrid('getSelected');
    if(row == null)
    	return;
    var index = $("#datagrid_places_employee").datagrid('getRowIndex', row);
    resort(index, true);
     
}
//下移
function movedown() {
    var row = $("#datagrid_places_employee").datagrid('getSelected');
    if(row == null)
    	return;
    var index = $("#datagrid_places_employee").datagrid('getRowIndex', row);
    resort(index, false);	     
}
	
function resort(index, direction) {
	if (direction == true) {	//上移
		if (index <= 0) {
       		return;
     	}
		var current = $("#datagrid_places_employee").datagrid('getData').rows[index];
		var previous = $("#datagrid_places_employee").datagrid('getData').rows[index - 1];
		$("#datagrid_places_employee").datagrid('getData').rows[index] = previous;
		$("#datagrid_places_employee").datagrid('getData').rows[index - 1] = current;
		$("#datagrid_places_employee").datagrid('refreshRow', index);
		$("#datagrid_places_employee").datagrid('refreshRow', index - 1);
		$("#datagrid_places_employee").datagrid('unselectAll');
		$("#datagrid_places_employee").datagrid('selectRow', index - 1);           
	}else{
       	var rows = $("#datagrid_places_employee").datagrid('getRows').length;
       	if (index >= rows - 1) {
			return;
		}
		var current = $("#datagrid_places_employee").datagrid('getData').rows[index];
		var next = $("#datagrid_places_employee").datagrid('getData').rows[index + 1];
		$("#datagrid_places_employee").datagrid('getData').rows[index + 1] = current;
		$("#datagrid_places_employee").datagrid('getData').rows[index] = next;
		$("#datagrid_places_employee").datagrid('refreshRow', index);
		$("#datagrid_places_employee").datagrid('refreshRow', index + 1);
		$("#datagrid_places_employee").datagrid('unselectAll');
		$("#datagrid_places_employee").datagrid('selectRow', index + 1);           
	}
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
		<%@include file="../../inc/help/group/editPlace.inc" %> 
	</div>
	<div data-options="region:'center'" >
		<div style="width: 400px;float: left">
			<table  class="easyui-datagrid" id="datagrid_places_employee" title="<%=name %>的送餐点" data-options="toolbar:'#toolbar_employee',striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true"style="height:500px;">
				<thead>
					<tr>
						<th data-options="field:'selector',checkbox:true"></th>
						<th data-options="field:'name',width:150">送餐点</th>
					</tr>
				</thead>
			</table>
		</div>
		<div style="width: 400px;float: left">
			<table  class="easyui-datagrid" id="datagrid_place_all" title="所有送餐点" data-options="toolbar:'#toolbar_all',striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,pagination:true"style="height:500px;">
				<thead>
					<tr>
						<th data-options="field:'selector',checkbox:true"></th>
						<th data-options="field:'name',width:150">送餐点</th>
					</tr>
				</thead>
			</table>
		</div>
					
		<div id="toolbar_employee" >			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-move_up'" onclick="moveup()">上移</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-move_down'" onclick="movedown()">下移</a>			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="removePlaceFromEmployee()">删除</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="savePlace()">保存</a>					
		</div>
		
		<div id="toolbar_all" >			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-back'" onclick="addPlaceToEmployee()">添加</a>					
		</div>
	</div>		
			
</body>
</html>
