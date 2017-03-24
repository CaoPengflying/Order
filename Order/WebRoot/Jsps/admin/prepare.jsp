<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Admin user = (Admin) request.getSession().getAttribute("user");
	int carteenID = user.getCarteenID();
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>备餐</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>

<script type="text/javascript">
var carteenID = <%=carteenID%>;
$(function(){	
	initDate();
	getOrderStatistic();
	getOrders();
});

//将日期初始化为明天
function initDate(){
	var tomorrow = new Date();
	tomorrow.setDate(tomorrow.getDate()+1);
	$("#date").datebox("setValue",  date2String(tomorrow));	//设置当前日期
}
function getOrderStatistic(){
	var date = $('#date').datebox('getValue');	
	$.post("/Order/OrderServlet",
		{
			method:7,  
	       	type:<%=Price.LUNCH%>,
	       	date1:date,
	       	date2:date,
	       	carteenID:carteenID,
			timestamp:(new Date()).valueOf()//加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		$("#lunch").html(data.total);
	  	}
	);
	$.post("/Order/OrderServlet",
		{
			method:7,  
	       	type:<%=Price.DINNER%>,
	       	date1:date,
	       	date2:date,
	       	carteenID:carteenID,
			timestamp:(new Date()).valueOf()//加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		$("#dinner").html(data.total);
	  	}
	);
	$.post("/Order/OrderServlet",
		{
			method:7,  
	       	type:<%=Price.MIDNIGHT%>,
	       	additional:"A",
	       	date1:date,
	       	date2:date,
	       	carteenID:carteenID,
			timestamp:(new Date()).valueOf()//加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		$("#midnightA").html(data.total);
	  	}
	);
	
	$.post("/Order/OrderServlet",
		{
			method:7,  
	       	type:<%=Price.MIDNIGHT%>,
	       	additional:"B",
	       	date1:date,
	       	date2:date,
	       	carteenID:carteenID,
			timestamp:(new Date()).valueOf()//加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		$("#midnightB").html(data.total);
	  	}
	);
	
	$.post("/Order/OrderServlet",
		{
			method:7,  
	       	type:<%=Price.MIDNIGHT%>,
	       	additional:"C",
	       	date1:date,
	       	date2:date,
	       	carteenID:carteenID,
			timestamp:(new Date()).valueOf()//加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		$("#midnightC").html(data.total);
	  	}
	);
}

		
function onDateChange(){
	getOrderStatistic();
	getOrders();
}

function getOrders(){
	var date1 = $('#date').datebox('getValue');
	var date2 = date1;
	var type = 0;
	var additional = "A";
	var v = $("input[name='type']:checked").val();
	switch(Number(v)){
	case 1:
		type = <%=Price.LUNCH%>;
		break;
	case 2:
		type = <%=Price.DINNER%>;
		break;
	case 3:
		type = <%=Price.MIDNIGHT%>;
		additional = "A";
		break;
	case 4:
		type = <%=Price.MIDNIGHT%>;
		additional = "B";
		break;
	case 5:
		type = <%=Price.MIDNIGHT%>;
		additional = "C";
		break;			
	}
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:7,  
	       type:type,
	       additional:additional,
	       date1:date1,
	       date2:date2,
	       	carteenID:carteenID,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 	
}	

//将套餐类别由byte转换为字符串
function typeFormatter(value, rowData, rowIndex) {
	switch (value){
	case <%=Price.LUNCH%>:
		return "中餐";
	case <%=Price.DINNER%>:
		return "晚餐";
	case <%=Price.MIDNIGHT%>:
		return "零点餐["+rowData.additional+"]";
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
		<%@include file="../../inc/menu/menu_admin.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/admin/prepare.inc" %> 
	</div>
	<div data-options="region:'center'" title="备餐统计" >
		<table  class="easyui-datagrid" id="datagrid_orders" data-options="toolbar:'#toolbar_orders',fit:true,striped:true,checkOnSelect:true">
			<thead>
				<tr>					
					<th data-options="field:'eatDate',width:80">用餐日期</th>
					<th data-options="field:'eaterName',width:80">用餐人</th>
					<th data-options="field:'type',width:80,formatter:typeFormatter">套餐类别</th>
					<th data-options="field:'placeName',width:200">送餐点</th>
					<th data-options="field:'ordererName',width:80">订餐人</th>
					<th data-options="field:'orderDate',width:80">订餐日期</th>							
					<th data-options="field:'departmentName',width:200">班组</th>
				</tr>
			</thead>
		</table>
		<div align="center" id="toolbar_orders" style="height:80px;line-height: 40px; overflow:auto; ">
			<label>用餐日期</label>
			<input class="easyui-datebox" id="date" data-options="onSelect:onDateChange,width:100">
			<br/>
			<input type="radio" name="type" value=1 onclick="getOrders()" checked="checked">
			<label style="font-size: 18px;">中餐[<span id="lunch" style="font-size:24px;color: red"></span>份]</label>
			<input type="radio" name="type" value=2 onclick="getOrders()">
			<label style="font-size: 18px;">晚餐【<span id="dinner" style="font-size:24px;color: red"></span>份]</label>
			<input type="radio" name="type" value=3 onclick="getOrders()">
			<label style="font-size: 18px;">零点A餐【<span id="midnightA" style="font-size:24px;color: red"></span>份]</label>
			<input type="radio" name="type" value=4 onclick="getOrders()">
			<label style="font-size: 18px;">零点B餐【<span id="midnightB" style="font-size:24px;color: red"></span>份]</label>
			<input type="radio" name="type" value=5 onclick="getOrders()">
			<label style="font-size: 18px;">零点C餐【<span id="midnightC" style="font-size:24px;color: red"></span>份]</label>
		</div>	
	</div>	
</body>
</html>
