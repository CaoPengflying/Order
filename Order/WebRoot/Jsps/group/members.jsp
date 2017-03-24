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
	//获取登录职工
	Employee user = (Employee)session.getAttribute("user");
	Connection conn = DButil.getConnection();
	if (conn == null) { 
		return;
	}
	
	Department department = DepartmentDAO.getDepartment(conn,(int)user.getDepartmentID());
	Department superDepartment = null;
	if(department.getWorkshopID() != 0){ 
		superDepartment = DepartmentDAO.getDepartment(conn,(int)department.getWorkshopID());
	}
	String title = "部门："+department.getName()+"&emsp;&emsp;"; 
	if(superDepartment != null){
		title += "所属车间："+superDepartment.getName()+"&emsp;&emsp;";
	}
	title += "电话："+department.getPhone()+"&emsp;&emsp;";
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>班组成员</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script>

var allPlaces; //记录所有的送餐点信息，用于ID到name的转换
//获取班组成员
$(function(){	
	getPlaces();//获取所有送餐点，用于ID至name的转换
	setTimeout("getEmployees()",1000);//延时加载班组成员数据，保证加载之前已经执行getPlaces()来获取送餐点列表
	
});

function getEmployees(){
	$('#datagrid_employees').datagrid({  
	    url:'/Order/EmployeeServlet',  
	    queryParams:{  
	       method:3,  
	       companyID:0,
		   workshopID:0,
	       departmentID:'<%=(int)user.getDepartmentID() %>',
	       idOrName:"",
	       role:0,
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
			url = "&emsp;<a href='Jsps/group/editPlace.jsp?target=24&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
			return allPlaces[index].name + url;
		}
	}
	return "<a href='Jsps/group/editPlace.jsp?target=24&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
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
</script>
</head>

<body class="easyui-layout">
	<div align="center"  data-options="region:'north',collapsible:false" title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc" %> 	
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_group.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/group/members.inc" %> 
	</div>
	<div data-options="region:'center',fit:true" title="<%=title %>" >
		<table  class="easyui-datagrid" id="datagrid_employees" title="班组成员" data-options="striped:true">
			<thead>
				<tr>
					<th data-options="field:'ID',width:50">工号</th>
					<th data-options="field:'name',width:50">姓名</th>
					<th data-options="field:'phone',width:100">电话</th>
					<th data-options="field:'company',width:150">所属公司</th>
					<th data-options="field:'department',width:180">所属班组</th>
					<th data-options="field:'workType',width:100">倒班类型</th>
					<th data-options="field:'placeIDs',width:130,formatter:placeFormatter">送餐点</th>
					<th data-options="field:'lunch',width:60,align:'right'">中餐剩余</th>
					<th data-options="field:'dinner',width:60,align:'right'">晚餐剩余</th>
					<th data-options="field:'midnight',width:70,align:'right'">零点餐剩余</th>
				</tr>
			</thead>
		</table>
	</div>		
			
</body>
</html>
