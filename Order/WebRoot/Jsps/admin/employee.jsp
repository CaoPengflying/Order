<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="DAO.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	Admin user = (Admin) request.getSession().getAttribute("user");
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>职工管理</title>

<link rel="stylesheet" type="text/css"
	href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var allWorkTypes;
var allPlaces; //记录所有的送餐点信息，用于ID到name的转换
var roles = [{'role':'<%=Employee.ROLE_COMMON%>','name':'普通职工'},{'role':'<%=Employee.ROLE_GROUP%>','name':'班组管理员'},{'role':'<%=Employee.ROLE_WORKSHOP%>','name':'车间管理员'}];
loadAllWorkTypes();
$(function(){
	getPlaces();//获取所有送餐点，用于ID至name的转换	
    loadCompanys();
	loadRoles();
});		

function loadAllWorkTypes()
{ 
 	$.ajaxSetup({ async: false }); 
	$.post("/Order/WorkTypeServlet",
		{
			method:2,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	allWorkTypes = data.workTypes;
	  	}
	);
	$.ajaxSetup({ async: true }); 	
}

//获取所有送餐点
function getPlaces(){
	$.post("/Order/PlaceServlet",
		{
			method:4, 
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		allPlaces = data.rows;
	  	}
	);
}

function loadCompanys(){
	$.post("/Order/CompanyServlet",
		{
			method:1,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var companys = data.rows;
	       	var dstCompanys = companys.slice(0); //复制数组
	       	$('#combo_company_dst').combobox({
	        	data:dstCompanys
	        }); 
	        $('#combo_company_dst').combobox('select',dstCompanys[0].ID);//选中第一个车间 
       		
       		var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	companys.splice(0,0,empty);
	        $('#combo_company').combobox({
	        	data:companys
	        });  
	        $('#combo_company').combobox('select',companys[0].ID);//选中第一个车间     		
	            		
	  	}
	);
}	


function loadRoles(){
	var roleType = [{'ID':'0','name':'不限'},{'ID':'<%=Employee.ROLE_COMMON%>','name':'普通职工'},{'ID':'<%=Employee.ROLE_GROUP%>','name':'班组管理员'},{'ID':'<%=Employee.ROLE_WORKSHOP%>','name':'车间管理员'}];
	
	$('#combo_role').combobox({
			data:roleType
	});

	$('#combo_role').combobox('select',roleType[0].ID);
}


function getEmployees(){
	var companyID = $("#combo_company").combobox("getValue");
	var workshopID = $("#combo_workshop").combobox("getValue");
	var departmentID =  $("#combo_department").combobox("getValue");
	var role = $("#combo_role").combobox("getValue");
	var idOrName = $("#txt_id_name").textbox("getValue");
	//自动选择公司时，班组没有值，所以此时不应加载职工信息
	if(departmentID == ""){
		return;
	}
	
	$('#datagrid_employees').datagrid({  
	    url:'/Order/EmployeeServlet', 
		pagination:true,
		pageSize:13,
		pageList:[10,13,20], 
	    queryParams:{  
	       method:3, 
	       companyID:companyID,
	       workshopID:workshopID,
	       departmentID:departmentID,
	       role:role,
	       idOrName:idOrName,
		   pagination:true,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	

//当选择车间加载车间所属班组
function onSelectWorkshop(record){
	$.post("/Order/DepartmentServlet",
		{
			method:1, 
	       	workshopID:record.ID,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var departments = data.rows;	     	
	       	var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	departments.splice(0,0,empty);
	        $('#combo_department').combobox({
	        	data:departments
	        });  
	        
	        $('#combo_department').combobox('select',departments[0].ID);//选中第一个车间     		
	  	}
	);
}
//调岗当选择车间加载车间所属班组
function onSelectDstWorkshop(record){
	$.post("/Order/DepartmentServlet",
		{
			method:1, 
	       	workshopID:record.ID,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var departments = data.rows;
	        $('#combo_department_dst').combobox({
	        	data:departments
	        });  
	        
	        $('#combo_department_dst').combobox('select',departments[0].ID);//选中第一个车间     		
	  	}
	);
}
//当选择公司 加载公司所属车间
function onSelectCompany(record) {
	$.post("/Order/WorkshopServlet",
		{
			method:1,
			companyID:record.ID,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var workshops = data.rows;
	               	
	       	var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	workshops.splice(0,0,empty);
	       	
	        $('#combo_workshop').combobox({
	        	data:workshops
	        });  
	        
	        $('#combo_workshop').combobox('select',workshops[0].ID);//选中第一个车间     	
	  	}
	);
}
//调岗当选择公司加载公司所属车间
function onSelectDstCompany(record){
	$.post("/Order/WorkshopServlet",
		{
			method:1,
			companyID:record.ID,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var workshops = data.rows;     
	       	
	        $('#combo_workshop_dst').combobox({
	        	data:workshops
	        });  
	        
	        $('#combo_workshop_dst').combobox('select',workshops[0].ID);//选中第一个车间     	
	  	}
	);
}
var editIndex = undefined;
function endEditing(){
	if (editIndex == undefined){
		return true;
	}

	if ($('#datagrid_employees').datagrid('validateRow', editIndex)){
		$('#datagrid_employees').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function edit(rowIndex,rowData){
	if (editIndex != rowIndex && endEditing()){
		$('#datagrid_employees').datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
	}
}

function append(){
	if (endEditing()){
		var companyID = $("#combo_company").combobox("getValue");
		var workshopID = $("#combo_workshop").combobox("getValue");
		var departmentID = $("#combo_department").combobox("getValue");
		var company = $("#combo_company").combobox("getText");
		var workshop = $("#combo_workshop").combobox("getText");
		var department = $("#combo_department").combobox("getText");
		if(companyID == 0 || departmentID==0){
			alert("请选择公司和班组");
			return;
		}
		
		$('#datagrid_employees').datagrid('insertRow',{index:0,row:{
			name:"",
			phone:"",
			password:"000000",
			role:<%=Employee.ROLE_COMMON%>,
			workTypeID:3,
			companyID:companyID,
			company:company,
			workshopID:workshopID,
			workshop:workshop,
			departmentID:departmentID,
			department:department
		}});
		editIndex = 0;
		$('#datagrid_employees').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
		var editor_place = $("#datagrid_employees").datagrid('getEditor',{index:editIndex,field: 'placeIDs' });
		$(editor_place.target).combobox('loadData' , allPlaces); 
		$(editor_place.target).combobox('setValue' , allPlaces[0].ID); 
		var editor_worktype = $("#datagrid_employees").datagrid('getEditor',{index:editIndex,field: 'workTypeID' });
		$(editor_worktype.target).combobox('setValue' , allWorkTypes[0].ID); 
	}
}
function remove(){
	var rows = $('#datagrid_employees').datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_employees').datagrid('getRowIndex',rows[i]);
		$('#datagrid_employees').datagrid('deleteRow',index);
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
	var rows_inserted = $('#datagrid_employees').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_employees').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_employees').datagrid('getChanges','updated');
	
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/EmployeeServlet",
		{
			method:1,
			json_inserted:json_inserted,
			json_deleted:json_deleted,
			json_updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_employees').datagrid('acceptChanges');
	        
       		//提示
       		if(result == null || result.length == 0){
       			msg = "保存成功";
       		}else{
       			msg = "部分保存成功</br>"+result;
       		}
       		
       		$.messager.show({
				title:'编辑职工信息',
				timeout:3000,
				msg:msg,
				width:200,
				showType:'slide'
			});
	  }
	);	
}
function reject(){
	$('#datagrid_employees').datagrid('rejectChanges');
	editIndex = undefined;
}

//调岗
function changeDepartment(){
	var rows = $('#datagrid_employees').datagrid('getChecked');
	if(rows == null || rows.length == 0){
		return;
	}
	var companyID = $("#combo_company_dst").combobox("getValue");
	var companyName = $("#combo_company_dst").combobox("getText");
	var workshopID = $("#combo_workshop_dst").combobox("getValue");
	var workshopName = $("#combo_workshop_dst").combobox("getText");	
	var departmentID = $("#combo_department_dst").combobox("getValue");
	var departmentName = $("#combo_department_dst").combobox("getText");	
	var msg = "确认将选中的"+rows.length+"个职工调岗至"+companyName+"-"+workshopName+"-"+departmentName+"?";
	if(confirm(msg) == false){
		return;
	}
	
	//修改所属班组，生成相应JSON字符串
	for(var i=0; i<rows.length; i++){
		rows[i].companyID = companyID;
		rows[i].workshopID = workshopID;
		rows[i].departmentID = departmentID;
	}
	var json_updated = JSON.stringify(rows);
	
	//提交后台执行变更
	$.post("/Order/EmployeeServlet",
		{
			method:1,
			json_updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//重新加载职工信息  
	        getEmployees();
       		//提示
       		$.messager.show({
				title:'职工管理',
				timeout:3000,
				msg:'调岗成功',
				width:200,
				showType:'slide'
			});
	  }
	);	
}

//将送餐点由ID转换为name
function placeFormatter(value, rowData, rowIndex) {
	if(allPlaces == undefined){
		return "";
	}	
	
	if(value == undefined){
		return "";
	}
	
	var ID = Number(value.split(",")[0]);//获取第一个送餐点编号
	for(var index in allPlaces){
		if(allPlaces[index].ID == ID){
			url = "&emsp;<a href='Jsps/admin/editPlace.jsp?target=4&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
			return allPlaces[index].name + url;
		}
	}
	return "<a href='Jsps/admin/editPlace.jsp?target=4&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
}

//送餐点ID至name的格式化
function worktypeFormatter(value, rowData, rowIndex) {	
	for(var i=0; i<allWorkTypes.length; i++){
		if(value == allWorkTypes[i].ID){
			return allWorkTypes[i].name;
		}
	}
	return "";
}

//角色ID至name的格式化
function roleFormatter(value, rowData, rowIndex) {
	switch(Number(value)){
		case <%=Employee.ROLE_WORKSHOP%>:
			return "车间管理员";
		case <%=Employee.ROLE_COMMON%>:
			return "普通职工";
		case <%=Employee.ROLE_GROUP%>:
			return "班组管理员";
	}	
}

//boolean转换为中文
function lockFormatter(value, rowData, rowIndex) {	
	return value==1?"是":"否";
}

$.extend($.fn.validatebox.defaults.rules, {
 //移动手机号码验证  
    mobile: {//value值为文本框中的值  
        validator: function (value) {  
            //var reg = /^1[3|4|5|8|9]\d{9}$/;  
            return /^((\d2,3)|(\d{3}\-))?(0\d2,3|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value) || /^(13|15|18)\d{9}$/i.test(value); 
        },  
        message: '输入电话或手机号码格式不准确.'  
    }
    });
</script>

</head>

<body class="easyui-layout">
	<div align="center" data-options="region:'north',collapsible:false"
		title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc"%>
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_admin.inc"%>
	</div>
	<div data-options="region:'south'"
		style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc"%>
	</div>
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250"
		style="padding:10px;">
		<%@include file="../../inc/help/admin/employee.inc"%>
	</div>
	<div data-options="region:'center',title:'职工管理'">
		<table class="easyui-datagrid" id="datagrid_employees"
			data-options="toolbar:'#toolbar_employee',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: edit">
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'ID',width:50,halign:'center',editor:{type:'textbox',options:{required:true}}">工号</th>
					<th data-options="field:'name',width:60,halign:'center',editor:{type:'textbox',options:{required:true}}">姓名</th>
					<th data-options="field:'password',width:80,halign:'center',editor:{type:'textbox'}">密码</th>
					<th data-options="field:'phone',width:80,halign:'center',editor:{type:'textbox',options:{validType:'mobile'}}">电话</th>
					<th data-options="field:'company',width:120,halign:'center'">所属公司</th>
					<th data-options="field:'workshop',width:200,halign:'center'">所属车间</th>
					<th data-options="field:'department',width:200,halign:'center'">所属班组</th>
					<th data-options="field:'placeIDs',width:130,halign:'center',formatter:placeFormatter">送餐点</th>
					<th data-options="field:'role',width:70,halign:'center',formatter:roleFormatter,editor:{
							type:'combobox',
							options:{
								required:true,
								editable:false,
								valueField:'role',
								textField:'name',
								data:roles
							}}">角色</th>
					<th data-options="field:'workTypeID',width:70,halign:'center',formatter:worktypeFormatter,editor:{
							type:'combobox',
							options:{
								required:true,
								editable:false,
								valueField:'ID',
								textField:'name',
								data:allWorkTypes
							}}">倒班类别</th>
					<th
						data-options="field:'lock',width:70,halign:'center',editor:{type:'checkbox',options:{on:1,off:0}},formatter:lockFormatter">是否锁定</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_employee"
			style="height:60px;line-height: 30px; overflow:auto;">
			<label>过滤条件：</label> &emsp;
			公司<input class="easyui-combobox"id="combo_company" data-options="valueField:'ID',textField:'name', onSelect:onSelectCompany" />
			&emsp;车间<input class="easyui-combobox" id="combo_workshop"data-options="valueField:'ID',textField:'name',onSelect:onSelectWorkshop" />
			&emsp;班组<input class="easyui-combobox" id="combo_department"data-options="valueField:'ID',textField:'name'" /> &emsp;
			&emsp;角色<input class="easyui-combobox" id="combo_role"data-options="valueField:'ID',textField:'name'" />
			&emsp;&emsp;&emsp;工号或姓名 <input class="easyui-textbox"id="txt_id_name" style="width:100px">
				 <a class="easyui-linkbutton" data-options="iconCls:'icon-search'"onclick="getEmployees()">查找</a>
				 <!-- <a class="easyui-linkbutton" data-options="iconCls:'icon-add'"	onclick="append()">添加</a> 
				 <a class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="remove()">删除</a>  -->
				<a href="javascript:void(0)"class="easyui-linkbutton" data-options="iconCls:'icon-save'"onclick="save()">保存</a> 
				<a href="javascript:void(0)"class="easyui-linkbutton" data-options="iconCls:'icon-undo'"onclick="reject()">撤销</a> 
				&emsp; <a href="javascript:void(0)"class="easyui-linkbutton" data-options="iconCls:'icon-exchange'"onclick="changeDepartment()">调岗至</a>
				<input class="easyui-combobox"id="combo_company_dst"data-options="valueField:'ID',textField:'name',onSelect:onSelectDstCompany" />
				<input class="easyui-combobox"id="combo_workshop_dst"data-options="valueField:'ID',textField:'name',onSelect:onSelectDstWorkshop" />
				<input class="easyui-combobox" id="combo_department_dst"data-options="valueField:'ID',textField:'name'" /> &emsp;
		</div>
	</div>

</body>
</html>
