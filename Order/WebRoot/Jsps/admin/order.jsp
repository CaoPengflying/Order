<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="View.*" %>
<%@ page import="DAO.*" %>
<%@ page import="java.sql.Connection" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<%	
	//获取登录职工
	Admin user = (Admin) request.getSession().getAttribute("user");
	Connection conn = DButil.getConnection();
	if (conn == null) { 
		return;
	}
	
	//获取可预订天数
	int days = RegularDAO.getRegular(conn).getDays();
	int target = Integer.parseInt(request.getParameter("target"));
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订餐</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var editIndex = undefined;
var label = 0;//判断是否正在订餐或者退订
var allPlaces; //记录所有的送餐点信息，用于ID到name的转换

getPlaces();//获取所有送餐点，用于ID至name的转换
//loadDepartments(workshopID);

$(function(){
	//限制日期选择范围
	$('#date').datebox().datebox('calendar').calendar({
		validator: function(date){
			var now = new Date();
			var d1 = new Date(now.getFullYear(), now.getMonth(), now.getDate());
			var d2 = new Date(now.getFullYear(), now.getMonth(), now.getDate()+<%=days%>);
			return d1<=date && date<=d2;
		}
	});
	
	$("#date").datebox("setValue", new Date().toDateString());	//设置当前日期
		
	//getOrders();
	loadCompanys();
});

function loadCompanys(){
	$.post("/Order/CompanyServlet",
		{
			method:1,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var companys = data.rows;
	       	$('#combo_company_dst').combobox({
	        	data:companys
	        }); 
	        $('#combo_company_dst').combobox('select',companys[0].ID);//选中第一个车间 
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

function onSelectWorkshop(record){
	loadDepartments(record.ID);
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
			url = "&emsp;<a href='Jsps/admin/editPlace.jsp?target=10&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
			return allPlaces[index].name + url;
		}
	}
	return "<a href='Jsps/admin/editPlace.jsp?target=10&employeeID="+rowData.ID+"&name="+encodeURI(encodeURI(rowData.name))+"'>编辑</a>";
}

//获取所有送餐点
function getPlaces(){
	$.ajax({
		type: "post",
		url:"/Order/PlaceServlet",
		data:{method:4,timestamp:(new Date()).valueOf()},
		async:false,
        success: function(result){
        	var data = JSON.parse(result);
       		var places = data.rows;
       		allPlaces=places;
      }});
}

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
function getEmployees(){
	var workshopID = $("#combo_workshop").combobox("getValue");
	var departmentID =  $("#combo_department").combobox("getValue");
	var idOrName = $("#txt_id_name").textbox("getValue");
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
	       workshopID:workshopID,
	       departmentID:departmentID,
	       idOrName:idOrName,
	       role:0,
		   pagination:true,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	

function onAfterEdit(rowIndex, rowData, changes) {
	editIndex = undefined;
}

//获取订单列表
function getOrders(){
	var date = $('#date').datebox('getValue');
	var type = $("input[name='type']:checked").val();
	var rows=$("#datagrid_employees").datagrid('getRows');
	if(rows.length < 1){
		return;
	}
	var eaterIDs = "";
	for(var i=0;i<rows.length;i++)
	{
		eaterIDs += rows[i].ID;
		if(i < rows.length-1){
			eaterIDs += ",";
		}
	}
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:3,  
	       employeeIDs:eaterIDs,
	       type:type,
	       date1:date,
	       date2:date,
	       carteenID:0,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 	
}


function addOrder(){
	var eaterIDs = "";
	var eatDate = $('#date').datebox('getValue');
	var type = $("input[name='type']:checked").val();
	var additional = $('#select_additional').combobox('getValue');
	var placeIDs = "";
	var rows=$("#datagrid_employees").datagrid('getChecked');
	if(rows.length == 0){
		alert("请选择用餐人");
		return;
	}
	
	for(var i=0;i<rows.length;i++)
	{
		//判断职工有无送餐点
		if(rows[i].placeIDs.length == 0) {
			alert(rows[i].name+"没有指定送餐点,不能订餐！");
			return  "";
		}
		if(rows[i].lock == 1) {
			alert(rows[i].name+"已经被锁定，不能订餐！");
			return "";
		}
		//判断职工的剩余餐补数
		switch(parseInt(type)) {
			case <%=Price.LUNCH %>:
			case <%=Price.DINNER %>:
			if((rows[i].lunch + rows[i].dinner) <= 0) {
				alert(rows[i].name + "的剩余套餐数不足");
				return;
			}
			break;
			case <%=Price.MIDNIGHT %>:
			if(rows[i].midnight <= 0) {
				alert(rows[i].name + "的剩余套餐数不足");
				return;
			}
		}
		
		eaterIDs += rows[i].ID;
		placeIDs += rows[i].placeIDs.split(",")[0];
		if(i < rows.length-1){
			eaterIDs += ",";
			placeIDs += ",";
		}
	}
	if(parseInt(label) == 0) {
		var textNode = document.createTextNode("正在提交订餐请求，请耐心等待");
            //获取div对象
      	var divNode = document.getElementById("toolbar_order");
            //给div添加文本元素
        divNode.appendChild(textNode);
		label = 1;
		$.post("/Order/OrderServlet",
			{
				method:1,
				eaterIDs:eaterIDs,
				ordererID:'<%=user.getID() %>',
				type:type,
				additional:additional,
		     	placeIDs:placeIDs,
		     	placeID:0,
		        eatDate:eatDate,
		        timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	       },
	       function(result){  
	       		//刷新订单
	       		getOrders();
	       		//刷新职工餐补等信息
	       		getEmployees();		
			    $.messager.show({
					title:'订餐结果',
					timeout:10000,
					msg:result,
					width:400,
					showType:'slide'
				});
				label = 0;
				divNode.removeChild(textNode);
		  }
		);	
		
	}
}

function delOrder(){
	//获取选中的订单，如果没有选中则不作任何处理
	var rows_deleted = $('#datagrid_orders').datagrid('getChecked');
	if(rows_deleted.length < 1){
		return;
	}
	
	var json_deleted = JSON.stringify(rows_deleted);
	if(parseInt(label) == 0) {
		var textNode = document.createTextNode("正在提交退订请求，请耐心等待");
            //获取div对象
      	var divNode = document.getElementById("toolbar_order");
            //给div添加文本元素
        divNode.appendChild(textNode);
		label = 1;
		$.post("/Order/OrderServlet",
			{
				method:2,
				json_deleted:json_deleted,
				operator:'<%=user.getID() %>',
		        timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	       },
	       function(result){  
	       		//刷新订单
	       		getOrders();
	       		//刷新职工餐补等信息
	       		getEmployees();	
	       		//退订按钮文字提示还原
	       		$('#btn_cancel').linkbutton({
			    	text: "退订"
				});
	       		//$('#datagrid_orders').datagrid('reload');     		
			    $.messager.show({
					title:'退订结果',
					timeout:10000,
					msg:result,
					width:400,
					showType:'slide'
				});
				label = 0;
				divNode.removeChild(textNode);
			}
		);	
		
		  
	}
}

function SelectOrder(rowIndex, rowData){
	var count = $('#datagrid_orders').datagrid('getChecked').length;
	$('#btn_cancel').linkbutton({
    	text: "退订【"+count+"】"
	});
}

</script>
		
</head>
	<h2>Multiple Accordion Panels</h2>
	<p>Enable 'multiple' mode to expand multiple panels at one time.</p>
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
		<%@include file="../../inc/help/admin/order.inc" %> 	
	</div>
	<div data-options="region:'center'" >
		<div class="easyui-accordion" >
			<div  title="预订套餐" data-options="selected:false,collapsed:true,collapsible:true,fit:true,iconCls:'icon-order'" style="height:398px">
				<table  class="easyui-datagrid" id="datagrid_orders" data-options="toolbar:'#toolbar_order',striped:true,fit:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onCheck:SelectOrder,onUncheck:SelectOrder">
					<thead>
						<tr>
							<th data-options="field:'selector',checkbox:true"></th>
							<th data-options="field:'eatDate',width:80">用餐日期</th>
							<th data-options="field:'eaterName',width:80">用餐人</th>
							<th data-options="field:'type',width:80,formatter:typeFormatter">套餐类别</th>
							<th data-options="field:'placeName',width:150">送餐点</th>
							<th data-options="field:'ordererName',width:80">订餐人</th>
							<th data-options="field:'orderDate',width:80">订餐日期</th>							
							<th data-options="field:'departmentName',width:200">班组</th>
						</tr>
					</thead>
				</table>
			</div>
			<div title="订餐人员" data-options="selected:true,collapsed:false,collapsible:true,fit:true,iconCls:'icon-group'" style="height:398px;">
				<table  class="easyui-datagrid" id="datagrid_employees" data-options="toolbar:'#toolbar_employee',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true">
					<thead>
						<tr>
							<th data-options="field:'selector',checkbox:true"></th>
							<th data-options="field:'ID',width:50">工号</th>
							<th data-options="field:'name',width:50,align:'right'">姓名</th>
							<th data-options="field:'workshop',width:150">所属车间</th>
							<th data-options="field:'department',width:150">所属班组</th>
							<th data-options="field:'placeIDs',width:140,align:'center',formatter:placeFormatter,editor:{ type:'combobox',options:{required:true,editable:true,valueField:'ID',textField:'name'}}">送餐点</th>
							<th data-options="field:'lunch',width:100,align:'right'">中餐剩余数</th>
							<th data-options="field:'dinner',width:100,align:'right'">晚餐剩余数</th>
							<th data-options="field:'midnight',width:100,align:'right'">零点餐剩余数</th>
						</tr>
					</thead>
				</table>
			</div>	
		</div>			
		<div id="toolbar_order" style="height:40px;line-height: 40px;">
			<input class="easyui-datebox" id="date" data-options="onSelect:getOrders,width:90">
			<input type="radio" name="type" value="<%=Price.LUNCH%>" checked="checked" onclick="getOrders()">中餐
			<input type="radio" name="type" value="<%=Price.DINNER%>" onclick="getOrders()">晚餐
			<input type="radio" name="type" value="<%=Price.MIDNIGHT%>" onclick="getOrders()">零点餐
			<select class="easyui-combobox" id="select_additional">
				<option value="A">A餐</option>
				<option value="B">B餐</option>
				<option value="C">C餐</option>
			</select>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addOrder()">订餐</a>
			<a id="btn_cancel" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="delOrder()">退订</a>
						
		</div>
					
		<div id="toolbar_employee" style="height:30px;line-height: 30px; overflow:auto;">
			&emsp;公司<input class="easyui-combobox"id="combo_company" data-options="valueField:'ID',textField:'name', onSelect:onSelectCompany" />
			&emsp;车间<input class="easyui-combobox" id="combo_workshop" data-options="valueField:'ID',textField:'name',onSelect:onSelectWorkshop" />
			&emsp;班组<input class="easyui-combobox" id="combo_department" data-options="valueField:'ID',textField:'name'" />
			&emsp;&emsp;&emsp;&emsp;按工号或姓名查找
			<input class="easyui-textbox" id="txt_id_name">
			<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="getEmployees()">查找</a>
		</div>
	</div>		
			
</body>
</html>
