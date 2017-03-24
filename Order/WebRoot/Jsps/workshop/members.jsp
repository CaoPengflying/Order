<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%@ page import="View.*" %>
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
	//获取登录职工
	Employee user = (Employee)session.getAttribute("user");
	Connection conn = DButil.getConnection();
	if (conn == null) { 
		return;
	}
	WorkshopView workshop = WorkshopDAO.getWorkshop(conn,user.getWorkshopID());
	String title = String.format("车间：%s  电话：%s",workshop.getName(),workshop.getPhone());
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>车间成员</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script>
var allPlaces; //记录所有的送餐点信息，用于ID到name的转换
var workshopID = <%=(int)user.getWorkshopID()%>;
var roles = [{'role':'<%=Employee.ROLE_COMMON %>','name':'普通职工'},{'role':'<%=Employee.ROLE_GROUP %>','name':'班组管理员'},{'role':'<%=Employee.ROLE_WORKSHOP %>','name':'车间管理员'}];
getPlaces();//获取所有送餐点，用于ID至name的转换
loadDepartments(workshopID);
loadDstDepartments(workshopID);
function loadDepartments(workshopID){
	$.post("/Order/DepartmentServlet",
		{
			method:1, 
	       	workshopID:workshopID,
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
	        
	        $('#combo_department').combobox('select',departments[0].ID);//选中第一个班组 		
	        
	  	}
	);
}
function loadDstDepartments(workshopID){
	$.post("/Order/DepartmentServlet",
		{
			method:1, 
	       	workshopID:workshopID,
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

function getEmployees(){
	var departmentID =  $("#combo_department").combobox("getValue");
	//自动选择公司时，班组没有值，所以此时不应加载职工信息
	if(departmentID == ""){
		return;
	}
	
	$('#datagrid_employees').datagrid({  
	    url:'/Order/EmployeeServlet', 
		pagination:true,
		pageSize:15,
		pageList:[15,20], 
	    queryParams:{  
	       method:3, 
	       companyID:0,
	       workshopID:<%=(int)user.getWorkshopID()%>,
	       departmentID:departmentID,
	       idOrName:"",
	       role:0,
		   pagination:true,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	

//将送餐点由ID转换为name
function placeFormatter(value, rowData, rowIndex) {
	if(allPlaces == undefined){
		return "";
	}
	var ID = Number(value.split(",")[0]);//获取第一个送餐点编号
	for(var index in allPlaces){
		if(allPlaces[index].ID == ID){
			url = "&emsp;<a href='Jsps/workshop/editPlace.jsp?target=42&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
			return allPlaces[index].name + url;
		}
	}
	return "<a href='Jsps/workshop/editPlace.jsp?target=42&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";;
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

function setGroup(){
	//获取变更的记录
	var rows_updated = $('#datagrid_employees').datagrid('getChecked');
	if(rows_updated == null || rows_updated.length == 0){
		return;
	}
	var msg = "确定要将"+rows_updated.length+"个员工设为管理员？";
	if(confirm(msg) == false){
		return;
	}
	//转换为json字符串
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/EmployeeServlet",
		{
			method:16,
			json_updated:json_updated,
			role:<%=Employee.ROLE_GROUP%>,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_employees').datagrid('reload');    
       		//提示
   			$.messager.show({
				title:'编辑员工角色',
				timeout:3000,
				msg:'设置成功',
				width:200,
				showType:'slide'
			});
 
	  }
	);	
}
//设为普通员工
function setCommon(){
	//获取变更的记录
	var rows_updated = $('#datagrid_employees').datagrid('getChecked');
	if(rows_updated == null || rows_updated.length == 0){
		return;
	}
	var msg = "确定要将"+rows_updated.length+"个员工设为普通员工？";
	if(confirm(msg) == false){
		return;
	}
	//转换为json字符串
	var json_updated = JSON.stringify(rows_updated);
	//提交后台执行变更
	$.post("/Order/EmployeeServlet",
		{
			method:16,
			json_updated:json_updated,
			role:<%=Employee.ROLE_COMMON%>,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_employees').datagrid('reload');    
       		//提示
   			$.messager.show({
				title:'编辑员工角色',
				timeout:3000,
				msg:'设置成功',
				width:200,
				showType:'slide'
			});
 
	  }
	);	
}
//调岗
function changeDepartment(){
	var rows = $('#datagrid_employees').datagrid('getChecked');
	if(rows == null || rows.length == 0){
		return;
	}	
	var departmentID = $("#combo_department_dst").combobox("getValue");
	var departmentName = $("#combo_department_dst").combobox("getText");	
	var msg = "确认将选中的"+rows.length+"个职工调岗至"+departmentName+"?";
	if(confirm(msg) == false){
		return;
	}
	
	//修改所属班组，生成相应JSON字符串
	for(var i=0; i<rows.length; i++){
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

//班组ID至name的格式化
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
</script>
</head>

<body class="easyui-layout">
	<div align="center"  data-options="region:'north',collapsible:false" title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc" %> 	
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_workshop.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/workshop/members.inc" %> 
	</div>
	<div data-options="region:'center'" title="<%=title %>" >
		<table  class="easyui-datagrid" id="datagrid_employees" data-options="toolbar:'#toolbar_employee',striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,fit:true">
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'ID',width:50,halign:'center'">工号</th>
					<th data-options="field:'name',width:50,halign:'center'">姓名</th>
					<th data-options="field:'phone',width:100,halign:'center'">电话</th>
					<th data-options="field:'company',width:150,halign:'center'">所属公司</th>
					<th data-options="field:'department',width:180,halign:'center'">所属班组</th>
					<th data-options="field:'role',width:70,halign:'center',formatter:roleFormatter">角色</th>	
					<th data-options="field:'workType',width:100">倒班类型</th>
					<th data-options="field:'placeIDs',width:130,formatter:placeFormatter">送餐点</th>
					<th data-options="field:'lunch',width:60,align:'right'">中餐剩余</th>
					<th data-options="field:'dinner',width:60,align:'right'">晚餐剩余</th>
					<th data-options="field:'midnight',width:70,align:'right'">零点餐剩余</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_employee" style="height:30px;line-height:30px; overflow:auto;" >
			班组
			<input class="easyui-combobox" id="combo_department" data-options="valueField:'ID',textField:'name',onSelect:getEmployees" style="width:240px;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" onclick="setGroup()">设为班组管理员</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" onclick="setCommon()">设为普通职工</a>			
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-exchange'" onclick="changeDepartment()">调岗至</a>	
			<input class="easyui-combobox" id="combo_department_dst" data-options="valueField:'ID',textField:'name'"/>	
		</div>
	</div>		
			
</body>
</html>
