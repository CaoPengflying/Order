<%@page import="View.EmployeeView"%>
<%@page import="DAO.EmployeeDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	Employee user = (Employee)session.getAttribute("user");
	Connection conn = DButil.getConnection(); 
	if (conn == null) { 
		return;
	}
	EmployeeView employeeView = EmployeeDAO.getEmployeeView(conn,user.getID());
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>个人基本信息</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>		

<script>
loadPlaces();

//加载职工送餐点列表
function loadPlaces()
{ 
	$.post("/Order/PlaceServlet",
		{
			method:5,  
			employeeID:'<%=user.getID()%>',
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var places = data.places;
       		var str = "";
       		for(var i=0; i<places.length; i++){
       			str += places[i].name;
       			if(i != places.length-1){
       				str += "，";
       			}
       		}
       		str += "【第一个为默认送餐点】";
			$("#places").html(str);
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
		<%@include file="../../inc/menu/menu_common.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>
	<div data-options="region:'center'" title="职工基本信息">
		<div style="width:500px;height:300px;border:2px solid #cccccc;position:absolute;top:50%;left:50%;margin-left:-250px;margin-top:-180px;padding:20px;">
			<table height="100%">
				<tr>
					<td>
						职工工号:
					</td>
					<td>
						<%=employeeView.getID() %>
					</td>
				</tr>
				<tr>
					<td>
						职工姓名:
					</td>
					<td>
						<%=employeeView.getName() %>
					</td>
				</tr>
				<tr>
					<td>
						联系电话:
					</td>
					<td>
						<%=employeeView.getPhone() %>
					</td>
				</tr>
				<tr>
					<td>
						所属班组:
					</td>
					<td>
						<%=employeeView.getDepartment() %>
					</td>
				</tr>
				<tr>
					<td>
						所属公司:
					</td>
					<td>
						<%=employeeView.getCompany() %>
					</td>
				</tr>
				<tr>
					<td>
						倒班类型:
					</td>
					<td>
						<%=employeeView.getWorkType() %>
					</td>
				</tr>
				<tr>
					<td>
						送餐地点:
					</td>
					<td>
						<label id="places"></label>
					</td>
				</tr>
			</table>
		</div>
	</div>
			
</body>
</html>
