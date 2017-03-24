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
	
	Employee employee = EmployeeDAO.getEmployee(conn, user.getID());//重新获取职工信息，因送餐点可能已经变化了
	//获取可选送餐点
	List<Place> places = PlaceDAO.getPlaces(conn,employee.getPlaceIDs());
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订餐</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
		
<script type="text/javascript" >

$(function(){
	getFutureOrders();
	getRest();//显示剩余餐补数量	
});

function getFutureOrders(){
	var table = $("#panel_order")[0];
	$.post("/Order/OrderServlet",
		{
			method:13,
			employeeID:"<%=user.getID()%>",
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
			table.innerHTML = result;
	  }
	);	
}

function addOrder(eatDate,type,additional){	
	var placeIDs = $('#select_place').combobox('getValue');
	if(placeIDs.length == 0){
		alert("请编辑您的送餐点");
		return;
	}
	
	$.post("/Order/OrderServlet",
		{
			method:1,
			eaterIDs:'<%=user.getID() %>',
			ordererID:'<%=user.getID() %>',
			type:type,
			additional:additional,
	     	placeIDs:placeIDs,
	     	placeID:0,
	        eatDate:eatDate,
	        timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
       		//更新订餐页面
       		getFutureOrders();
       		//显示剩余餐补数量
       		getRest();
       		//$('#datagrid_orders').datagrid('reload');     		
		    $.messager.show({
				title:'订餐结果',
				timeout:10000,
				msg:result,
				width:400,
				showType:'slide'
			});
	  }
	);	
}

function delOrder(orderID){	
	$.post("/Order/OrderServlet",
		{
			method:14,
			orderID:orderID,
			operator:'<%=user.getID()%>',
	        timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){  
       		//更新订餐页面
       		getFutureOrders();
       		//显示剩余餐补数量
       		getRest();    		
		    $.messager.show({
				title:'退订结果',
				timeout:10000,
				msg:result,
				width:400,
				showType:'slide'
			});
	  }
	);	
}

//显示剩余餐补数量
function getRest(){
	$.post("/Order/EmployeeServlet",
		{
			method:7,
			employeeID:'<%=user.getID() %>',
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){  
       		var data = JSON.parse(result);
       		var employee = data.employee;
       		var title = "订餐&emsp;&emsp;&emsp;剩余套餐数量：中餐【"+employee.lunch+"】,晚餐【"+employee.dinner+"】,零点餐【"+employee.midnight+"】";       		
	 		$("#div_order").panel("setTitle",title);
	  }
	);	
}
</script>
		
<style>
	.table_order{border-collapse:collapse;border-spacing:0;border-left:1px solid #888;border-top:1px solid #888;}
	.table_order td{border-right:1px solid #888;border-bottom:1px solid #888;padding:5px 10px;}
	.table_order th{border-right:1px solid #888;border-bottom:1px solid #888;padding:5px 10px;background-color:#888;font-size:18px;}
	.table_order tr{height:35px;}
</style>
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
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/common/order.inc" %> 	
	</div>
	<div id="div_order" data-options="region:'center',tools:'#tt'"  title="订餐" >		
		<div id="panel_order">
			
		</div>
	</div>		
	<div id="tt">
		<label style="font-size:11px;">选择送餐点</label>
		<select class="easyui-combobox" id="select_place">
			<% for(Place place:places){ %>
				<option value=<%=(int)place.getID() %>><%=place.getName() %></option>
			<%} %>
		</select>
	</div>
</body>
</html>
