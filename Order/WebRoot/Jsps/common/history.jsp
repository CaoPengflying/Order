<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
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
	
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>历史订单</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>
		
<script type="text/javascript" >
$(function(){
	initDate();
	getOrders();
});

//设置日期范围为该月的第一天和最后一天
function initDate(){
    var firstDay = new Date();
    firstDay.setDate(1);
    var str1 = date2String(firstDay);
    
    var lastDay = new Date();
    lastDay.setMonth(lastDay.getMonth()+1, 0);
    var str2 = date2String(lastDay);

	$("#date1").datebox("setValue", str1);
	$("#date2").datebox("setValue", str2);
}
function getOrders(){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	var type = $("input[name='type']:checked").val();
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:3,  
	       employeeIDs:'<%=user.getID() %>',
	       type:type,
	       date1:date1,
	       date2:date2,
	       carteenID:0,
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
		<%@include file="../../inc/menu/menu_common.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/common/history.inc" %> 	
	</div>
	<div data-options="region:'center'" >
		<table  class="easyui-datagrid" id="datagrid_orders" title="历史订单" data-options="fit:true,iconCls:'icon-history',toolbar:'#toolbar_order',striped:true,checkOnSelect:true">
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
		<div align="center" id="toolbar_order" style="height:50px;line-height: 50px;">
			<label>用餐时段</label>
			<input class="easyui-datebox" id="date1" data-options="width:100">-
			<input class="easyui-datebox" id="date2" data-options="width:100">
			&emsp;
			<input type="radio" name="type" value="<%=Price.LUNCH%>" checked="checked" >中餐
			<input type="radio" name="type" value="<%=Price.DINNER%>">晚餐
			<input type="radio" name="type" value="<%=Price.MIDNIGHT%>">零点餐				
			&emsp;
			<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="getOrders()">查询</a>
			
		</div>	
	</div>
</body>
</html>
