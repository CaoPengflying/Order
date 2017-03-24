<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Admin user = (Admin) request.getSession().getAttribute("user");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>权限管理</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var allCarteens;//所有食堂信息
var editIndex = undefined;//正在编辑的行号

$(function(){	
	loadAllCarteens();
	loadAdmins();
});

function loadAllCarteens()
{ 
 	$.ajaxSetup({ async: false }); 
	$.post("/Order/CarteenServlet",
		{
			method:2,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	allCarteens = data.rows;
	       	var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "无";
	       	allCarteens.splice(0,0,empty);
	  	}
	);
	$.ajaxSetup({ async: true }); 	
}
//加载管理员列表
function loadAdmins(){
	$.post("/Order/AdminServlet",
		{
			method:2, 
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var admins = data.admins;
			$("#datagrid_admins").datagrid('loadData' , admins);
			$("#datagrid_admins").datagrid('selectRow',0);
	  	}
	);
}

//添加管理员
function addAdmin(){
	//如果数据验证不通过则跳过
	if (!endEditing()){
		return;
	}
	
	//在最前面添加管理员，并自动选中
	$('#datagrid_admins').datagrid('insertRow',{index:0,row:{ID:"",name:"",password:"000000",role:<%=Admin.ROLE_ADMIN%>}});
	editIndex = 0;
	$('#datagrid_admins').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);	
	
	//加载食堂选择列表
	var editor = $('#datagrid_admins').datagrid('getEditor',{index:editIndex,field: 'carteenID' });
	$(editor.target).combobox('loadData' , allCarteens);
		
	//清空所有权限
	var checks = $("input[name='power']"); 
	for(var i=0; i<checks.length; i++){
		checks[i].checked = false;
	}
}

//删除管理员
function delAdmin(){
	var table = $('#datagrid_admins');
	var rows = table.datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = table.datagrid('getRowIndex',rows[i]);
		table.datagrid('deleteRow',index);
	}	
}

//编辑管理员名称
function editAdmin(rowIndex, rowData){
	if (editIndex != rowIndex && endEditing()){
		var grid = $("#datagrid_admins");
		grid.datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
		
		var editor = grid.datagrid('getEditor',{index:editIndex,field: 'carteenID' });
		$(editor.target).combobox('loadData' , allCarteens);
	}
}

//保存管理员，包括其中的权限信息
function saveAdmin(){
	var grid = $('#datagrid_admins');
	if (!endEditing()){
		return;
	}
	
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){
		return;
	}
	
	//获取变更的记录
	var rows_inserted = grid.datagrid('getChanges','inserted');
	var rows_deleted = grid.datagrid('getChanges','deleted');
	var rows_updated = grid.datagrid('getChanges','updated');
	
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/AdminServlet",
		{
			method:1,
			json_inserted:json_inserted,
			json_deleted:json_deleted,
			json_updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        grid.datagrid('acceptChanges');
	        
       		//提示
       		$.messager.show({
				title:'编辑管理员信息',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
	  }
	);	
}

function savePermission(){
	var grid = $('#datagrid_admins');
	//获取当前行号
	var row = grid.datagrid('getSelected');
	if(row == null){
		return;
	}	
	
	//计算权限值
	var permission = 0;
	var checks = $("input[name='power']"); 
	for(var i=0; i<checks.length; i++){
		if(checks[i].checked){
			permission += Number(checks[i].value);
		}
	}	
	
	//生成修改管理员的json字符串
	row.permission = permission;
	var updated = [];
	updated.push(row);
	var json_updated = JSON.stringify(updated);
	
	$.post("/Order/AdminServlet",
			{
				method:1,
				json_updated:json_updated,
		       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	       },
	       function(result){
	       		//提示
	       		$.messager.show({
					title:'编辑管理员',
					timeout:3000,
					msg:'修改权限成功',
					width:200,
					showType:'slide'
				});
		  }
		);
}

function onSelectAdmin(rowIndex, rowData){
	//动态修改管理员的权限显示
	var checks = $("input[name='power']"); 
	for(var i=0; i<checks.length; i++){
		var value = Number(checks[i].value);
		checks[i].checked = rowData.permission & value;
	}
	
	 $("#name").html(rowData.name);
}	

function endEditing(){
	if (editIndex == undefined){
		return true;
	}

	if ($('#datagrid_admins').datagrid('validateRow', editIndex)){		
		$('#datagrid_admins').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}



function rejectAdmin(){
	$('#datagrid_admins').datagrid('rejectChanges');
	editIndex = undefined;
}

//送餐点ID至name的格式化
function carteenFormatter(value, rowData, rowIndex) {	
	if(allCarteens == undefined){
		return "";
	}	
	
	if(value == undefined){
		return "";
	}
	
	var ID = Number(value);
	for(var index in allCarteens){
		if(allCarteens[index].ID == ID){
			return allCarteens[index].name;
		}
	}
	return "";
}
</script>
		
</head>

<body class="easyui-layout">
	<div align="center"  data-options="region:'north',collapsible:false" title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc" %> 	
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_super.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/super/admins.inc" %> 	
	</div>
	<div data-options="region:'center',title:'权限管理'" >
		<div style="width: 600px;height:100%;float: left">
			<table  class="easyui-datagrid" id="datagrid_admins" data-options="toolbar:'#toolbar_company',striped:true,onDblClickRow: editAdmin,checkOnSelect:true,selectOnCheck:false,onSelect:onSelectAdmin,singleSelect:true">
				<thead>
					<tr>						
						<th data-options="field:'selector',checkbox:true"></th>
						<th data-options="field:'ID',width:100,editor:{type:'textbox',options:{required:true}}">职工编号</th>
						<th data-options="field:'name',width:100,editor:{type:'textbox',options:{required:true}}">管理员姓名</th>
						<th data-options="field:'password',width:100,editor:{type:'textbox',options:{required:true}}">密码</th>
						<th data-options="field:'phone',width:100,editor:{type:'textbox'}">电话</th>
						<th data-options="field:'carteenID',width:150,formatter:carteenFormatter,editor:{
							type:'combobox',
							options:{
								required:true,
								editable:false,
								valueField:'ID',
								textField:'name',
								data:allCarteens
							}}">所属食堂</th>
					</tr>
				</thead>
			</table>
		</div>
		<div>
			<br/>
			&emsp;&emsp;<label>管理员[<span id="name" style="color:red"></span>]的权限：</label><br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_COMPANY%> >公司管理<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_WORKSHOP%>>车间管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_GROUP%>>班组管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_CARTEEN%>>食堂权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_EMPLOYEE%>>职工管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_WORKTYPE%>>倒班管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_HOLIDAY%>>假日管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_PRICE%>>价格制定权限<br/>
			<!--&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_HISTORY%>>历史订单<br/>
			  -->
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_ORDER%>>代订餐权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_PREPARE%>>备餐权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_DISTRIBUTION%>>送餐权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_ROUTE%>>线路管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_REGULAR%>>订餐规则制定权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_NOTICE%>>发布公告权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_TOTAL%>>统计权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_DATA%>>数据管理权限<br/>
			&emsp;&emsp;<input type="checkbox" name="power" value = <%=Admin.POWER_SEARCH%>>订单查询权限<br/>
	
		<div id="toolbar_company" style="height:50px;line-height: 40px;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addAdmin()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="delAdmin()">删除</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveAdmin()">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" onclick="rejectAdmin()">撤销</a>
			&emsp;&emsp;
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="savePermission()">保存权限</a>
		</div>
	</div>		
	</div>	
</body>
</html>
