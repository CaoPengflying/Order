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
<script type="text/javascript">
var editRow = undefined;
var allPlaces; //记录所有的送餐点信息，用于ID到name的转换

$(function(){
	initDate();
	 getOrderTotalOfEmployee();
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

//将送餐点由ID转换为name
function placeFormatter(value, rowData, rowIndex) {
	if(allPlaces == undefined){
		return "";
	}
	var ID = Number(value.split(",")[0]);//获取第一个送餐点编号
	for(var index in allPlaces){
		if(allPlaces[index].ID == ID){
			return allPlaces[index].name;
		}
	}
	return "";
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

//获取职工用餐统计
function getOrderTotalOfEmployee(){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:4,  
	       companyID:0,//不限
	       workshopID:0,//不限
	       departmentID:<%=(int)user.getDepartmentID()%>,
	       date1:date1,
	       date2:date2,
	       carteenID:0,
	       placeID:0,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 	
}

function detailFormatter(index,row){
	return 	"<div style='padding:0px'>"+
				"<table class='datagrid_detail'>"+
				"</table>"+
			"</div>";
}

function onExpandRow(index,row){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	
	var n =$('#datagrid_orders').datagrid('getRows').length;
	for(var i=0; i<n; i++){
		if(index != i){
			$('#datagrid_orders').datagrid('collapseRow',i);
		}
	}
	//获取明细区域
	var datagrid_detail = $('#datagrid_orders').datagrid('getRowDetail',index).find('table.datagrid_detail');
	//获取明细数据
	datagrid_detail.datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:3,  
	       employeeIDs:row.employeeID,
	       type:<%=Price.ALLDAY%>,
	       date1:date1,
	       date2:date2,
	       carteenID:0,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post',
		pagination:true,
		columns:[[
					{field:'eatDate',width:100,title:'用餐日期'},
					{field:'type',width:100,title:'套餐类别',formatter:typeFormatter},
					{field:'eaterName',width:80,title:'用餐人'},
					{field:'placeName',width:160,title:'送餐点'},
					{field:'ordererName',width:80,title:'订餐人'},
					{field:'orderDate',width:100,title:'订餐日期'},
					{field:'departmentName',width:200,title:'班组'}
				]],
				//解决展开是“+”错位问题
		onLoadSuccess:function(){      
           $('#datagrid_orders').datagrid('fixDetailRowHeight',index);      
              setTimeout(function () {  
                  var tr=datagrid_detail.closest('tr');  
                	id = tr.prev().attr('id'); //此子表格父行所在行的id  
                   id = id.replace(/-2-(\d+)$/, '-1-$1'); //detailview没有展开的前部分的id是有规则的  
                   $('#' + id).next().css('height', tr.height());//设置没展开的前部分的高度，由于启用了计时器，会闪一下  
               }, 0);  
           } 

	}); 	
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
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/group/history.inc" %> 
	</div>
	<div data-options="region:'center',title:'历史订单'" >		
		<table  class="easyui-datagrid" id="datagrid_orders" height="100%" data-options="toolbar:'#toolbar_employee',fit:true,striped:true,view: detailview,detailFormatter:detailFormatter,onExpandRow:onExpandRow">
			<thead>
				<tr>					
					<th data-options="field:'employeeID',width:100">工号</th>
					<th data-options="field:'name',width:150,align:'right'">姓名</th>
					<th data-options="field:'lunch',width:200,align:'right'">中餐</th>
					<th data-options="field:'dinner',width:200,align:'right'">晚餐</th>
					<th data-options="field:'midnight',width:200,align:'right'">零点餐</th>
				</tr>
			</thead>
		</table>
		
		<div align="center" id="toolbar_employee" style="height:50px;line-height: 50px;">
			<label>用餐时段</label>
			<input class="easyui-datebox" id="date1" data-options="width:100">-
			<input class="easyui-datebox" id="date2" data-options="width:100">
							
			&emsp;
			<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="getOrderTotalOfEmployee()">查询</a>		
		</div>
	</div>
			
</body>
</html>
