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
	Employee user = (Employee) request.getSession().getAttribute("user");
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订单查询</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript">
var editRow = undefined;
var allPlaces; //记录所有的送餐点信息，用于ID到name的转换
$(function(){
	loadDepartment();	
	loadPlaces();
	initDate();
	loadType();
});

//套餐类别
function loadType() {
	var type = [{'ID':'0','name':'套餐不限'},{'ID':'<%=Price.LUNCH%>','name':'中餐'},{'ID':'<%=Price.DINNER%>','name':'晚餐'},{'ID':'<%=Price.MIDNIGHT%>','name':'零点餐'}];
	
	$('#combo_type').combobox({
			data:type
	});

	$('#combo_type').combobox('select',type[0].ID);
	
}

function loadDepartment(){
	$.post("/Order/DepartmentServlet",
		{
			method:1, 
	       	workshopID:<%=user.getWorkshopID()%>,
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
function initDate() {
    var str = new Date().toDateString();
    $("#date1").datebox("setValue",str);
	$("#date2").datebox("setValue", str);	//设置当前日期
}

//加载食堂送餐点
function loadPlaces()
{ 
       $.post("/Order/PlaceServlet",
		{
	       method:7, 
	       carteenID:0,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result) {
			var data = JSON.parse(result);
			var places = data.rows;
			var empty = new Object;
			empty.ID = 0;
			empty.name="不限";
			places.splice(0,0,empty);
			$('#combo_place').combobox({
				data:places
			});
			$('#combo_place').combobox('select',places[0].ID);
		}
	); 	
}
//获取订单列表
function getOrderOfEmployee(){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	var departmentID =  $("#combo_department").combobox("getValue");
	var idOrName = $("#txt_id_name").textbox("getValue");	
	var type = $("#combo_type").combobox("getValue");
	var placeID = $("#combo_place").combobox("getValue");
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet', 
	    pagination:true,
		pageSize:10,
		pageList:[10,15,20], 
	    queryParams:{  
	       method:18, 
	       date1:date1, 
	       date2:date2,
	       type:type,
	       companyID:0,
	       workshopID:<%=user.getWorkshopID()%>,
	       departmentID:departmentID,
	       idOrName:idOrName,
	       placeID:placeID,
	       carteenID:0,
	       pagination:true,
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
		<%@include file="../../inc/menu/menu_workshop.inc" %> 
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/workshop/orderSearch.inc" %> 
	</div>
	<div data-options="region:'center',title:'订单查询'" >	
		
		<table  class="easyui-datagrid" id="datagrid_orders" height="100%" data-options="toolbar:'#toolbar_employee',fit:true,striped:true,pagination:true">
			<thead>
				<tr>						
					<th data-options="field:'eatDate',width:80">用餐日期</th>
					<th data-options="field:'eaterID',width:80">工号</th>
					<th data-options="field:'eaterName',width:80">用餐人</th>
					<th data-options="field:'type',width:100,formatter:typeFormatter">套餐类别</th>
					<th data-options="field:'placeName',width:150">送餐点</th>
					<th data-options="field:'ordererName',width:80">订餐人</th>
					<th data-options="field:'orderDate',width:80">订餐日期</th>							
					<th data-options="field:'departmentName',width:200">班组</th>
				</tr>
			</thead>
		</table>
		
		<div align="center" id="toolbar_employee" style="height:60px;line-height: 30px;">
			<label>用餐时段</label>
			<input class="easyui-datebox" id="date1" data-options="width:100">-
			<input class="easyui-datebox" id="date2" data-options="width:100">
			&emsp;班组<input class="easyui-combobox" id="combo_department" data-options="valueField:'ID',textField:'name'" />	
			&emsp;送餐点<input class="easyui-combobox" id="combo_place" data-options="valueField:'ID',textField:'name'" />			
			<br/>
			工号或姓名<input class="easyui-textbox" id="txt_id_name">
			&emsp;<input class="easyui-combobox" id="combo_type" data-options="valueField:'ID',textField:'name'" />
			<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="getOrderOfEmployee()">查询</a>		
		</div>
	</div>
			
</body>
</html>
