<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	Admin user = (Admin) request.getSession().getAttribute("user");
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>送餐路线</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript">

function closes(){
	$("#Loading").fadeOut("normal",function(){
		$(this).remove();
	});
}
var pc;
$.parser.onComplete = function(){
	if(pc) clearTimeout(pc);
	pc = setTimeout(closes, 1000);
};

var allCarteens;//所有食堂信息
var editIndex = undefined;//正在编辑的行号

$(function(){	
	loadRoutes();
	loadAllPlaces();		
	loadAllCarteens();
});

//加载线路列表
function loadRoutes(){
	$.post("/Order/RouteServlet",
		{
			method:4, 
			carteenID:<%=user.getCarteenID()%>, 
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var routes = data.routes;
			$("#datagrid_route").datagrid('loadData' , routes);
			$("#datagrid_route").datagrid('selectRow',0);
	  	}
	);
}

function loadAllCarteens()
{ 
 	$.ajaxSetup({ async: false }); 
	$.post("/Order/CarteenServlet",
		{
			method:2,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	allCarteens = data.rows;
	  	}
	);
	$.ajaxSetup({ async: true }); 	
}
//加载线路的送餐点
function loadRoutePlaces(routeID)
{ 
	$.post("/Order/PlaceServlet",
		{
			method:6,  
			routeID:routeID,
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var places = data.places;
			$("#datagrid_places_route").datagrid('loadData' , places);
	  }
	);	
} 

//添加路线
function addRoute(){
	if (!endEditingRoute()){
		return;
	}
	$('#datagrid_route').datagrid('insertRow',{index:0,row:{name:"",carteenID:2}});
	editIndexRoute = 0;
	$('#datagrid_route').datagrid('selectRow', editIndexRoute).datagrid('beginEdit', editIndexRoute);
	
	var editor = $('#datagrid_route').datagrid('getEditor',{index:editIndexRoute,field: 'carteenID' });
	$(editor.target).combobox('loadData' , allCarteens);
	
	//清空线路送餐点
	var rows = $("#datagrid_place_route").datagrid('getRows');		
	for(var i=rows.length-1; i>=0; i--){
		$("#datagrid_place_route").datagrid('deleteRow',i);
	}
}

//删除路线
function delRoute(){
	var row = $('#datagrid_route').datagrid('getSelected');
	var msg = "确定要删除线路【"+row.name+"】";
	if(confirm(msg) == false){
		return;
	}
	
	var route = JSON.stringify(row);
	
	//提交后台执行变更
	$.post("/Order/RouteServlet",
		{
			method:2,
			route:route,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	       	var index = $('#datagrid_route').datagrid('getRowIndex',row);
			$('#datagrid_route').datagrid('deleteRow',index);
	        $('#datagrid_route').datagrid('acceptChanges');
       		//提示
       		$.messager.show({
				title:'编辑线路',
				timeout:3000,
				msg:'删除成功',
				width:200,
				showType:'slide'
			});
	  }
	);		
}

//编辑线路名称
function editRoute(rowIndex, rowData){
	if (editIndexRoute != rowIndex && endEditingRoute()){
		$('#datagrid_route').datagrid('beginEdit', rowIndex);
		editIndexRoute = rowIndex;
	}
	
	
}

//保存线路，包括其中的送餐点信息
function saveRoute(){
	if (!endEditingRoute()){
		return;
	}
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){
		return;
	}
	var row = $("#datagrid_route").datagrid('getSelected');
	if(row == null){
		return;
	}
	
	//生成送餐点ID序列，以逗号隔开
	var rows = $("#datagrid_place_route").datagrid('getRows');
	var placeIDs = "";
	for(var i=0; i<rows.length; i++){
		placeIDs += rows[i].ID;
		if(i < rows.length-1){
			placeIDs += ",";
		}
	}
	var index =  $('#datagrid_route').datagrid('getRowIndex', row);
	$('#datagrid_route').datagrid('updateRow',{index:index,row:{ID:row.ID,name:row.name,placeIDs:placeIDs}});	
	
	//获取变更的记录(注意插入、更新不能批量完成，因为线路送餐点只有一个线路的)
	var rows_inserted = $('#datagrid_route').datagrid('getChanges','inserted');
	//var rows_deleted = $('#datagrid_route').datagrid('getChanges','deleted');
	//var rows_updated = $('#datagrid_route').datagrid('getChanges','updated');
	var route = JSON.stringify(row);
	if(rows_inserted.length > 1){
		alert("一次只能新增一条线");
		return;
	}else if(rows_inserted.length == 1){
		//提交后台执行添加
		$.post("/Order/RouteServlet",
			{
				method:1,
				route:route,
		       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	       },
	       function(result){
	       		loadRoutes();//重新加载线路，以获得新添加的线路ID
		       	//提交变更数据  
		        $('#datagrid_route').datagrid('acceptChanges');
	       		//提示
	       		$.messager.show({
					title:'编辑线路',
					timeout:3000,
					msg:'添加成功',
					width:200,
					showType:'slide'
				});
		  }
		);	
	}else{
		//提交后台执行修改
		$.post("/Order/RouteServlet",
			{
				method:3,
				route:route,
		       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	       },
	       function(result){
		       	//提交变更数据  
		        $('#datagrid_route').datagrid('acceptChanges');
	       		//提示
	       		$.messager.show({
					title:'编辑线路',
					timeout:3000,
					msg:'修改成功',
					width:200,
					showType:'slide'
				});
		  }
		);	
	}	
}

function onSelectRoute(rowIndex, rowData){
	if(rowData.ID != null){
		//动态修改线路送餐点表格的标题
		var title = "线路【"+rowData.name+"】送餐点列表";
		var panel = $("#datagrid_place_route").datagrid("getPanel");
		panel.panel("setTitle",title);
		
		$.post("/Order/PlaceServlet",
			{
				method:6, 
				routeID:rowData.ID,
				timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
		    },
			function(result){  
	       		var data = JSON.parse(result);
	       		var places = data.places;
				$("#datagrid_place_route").datagrid('loadData' , places);
		  }
		);
	}
}	

//移除线路送餐点
function removePlaceFromRoute(){
	//获取选中的送餐点
	var rows = $("#datagrid_place_route").datagrid('getChecked');
	if(rows.lengh < 1){
		return;
	}
	
	for(var i=rows.length-1;i>=0; i--){
		var index = $("#datagrid_place_route").datagrid('getRowIndex',rows[i]);
		$("#datagrid_place_route").datagrid('deleteRow',index);
	}
}

//上移
function moveup() {
    var row = $("#datagrid_place_route").datagrid('getSelected');
    if(row == null)
    	return;
    var index = $("#datagrid_place_route").datagrid('getRowIndex', row);
    resort(index, true);
     
}
//下移
function movedown() {
    var row = $("#datagrid_place_route").datagrid('getSelected');
    if(row == null)
    	return;
    var index = $("#datagrid_place_route").datagrid('getRowIndex', row);
    resort(index, false);	     
}
	
function resort(index, direction) {
	if (direction == true) {	//上移
		if (index <= 0) {
       		return;
     	}
		var current = $("#datagrid_place_route").datagrid('getData').rows[index];
		var previous = $("#datagrid_place_route").datagrid('getData').rows[index - 1];
		$("#datagrid_place_route").datagrid('getData').rows[index] = previous;
		$("#datagrid_place_route").datagrid('getData').rows[index - 1] = current;
		$("#datagrid_place_route").datagrid('refreshRow', index);
		$("#datagrid_place_route").datagrid('refreshRow', index - 1);
		$("#datagrid_place_route").datagrid('unselectAll');
		$("#datagrid_place_route").datagrid('selectRow', index - 1);           
	}else{
       	var rows = $("#datagrid_place_route").datagrid('getRows').length;
       	if (index >= rows - 1) {
			return;
		}
		var current = $("#datagrid_place_route").datagrid('getData').rows[index];
		var next = $("#datagrid_place_route").datagrid('getData').rows[index + 1];
		$("#datagrid_place_route").datagrid('getData').rows[index + 1] = current;
		$("#datagrid_place_route").datagrid('getData').rows[index] = next;
		$("#datagrid_place_route").datagrid('refreshRow', index);
		$("#datagrid_place_route").datagrid('refreshRow', index + 1);
		$("#datagrid_place_route").datagrid('unselectAll');
		$("#datagrid_place_route").datagrid('selectRow', index + 1);           
	}
}

//加载所有送餐点
function loadAllPlaces()
{ 
	$('#datagrid_place_all').datagrid({  
	    url:'/Order/PlaceServlet', 
	    pagination:true,
		pageSize:10,
		pageList:[10,15,20],
	    queryParams:{  
	       method:4, 
	       pagination:true,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 	
}

//添加送餐点
function addPlace(){
	if (endEditingPlace()){
		$('#datagrid_place_all').datagrid('insertRow',{index:0,row:{name:""}});
		editIndexPlace = 0;
		$('#datagrid_place_all').datagrid('selectRow', editIndexPlace).datagrid('beginEdit', editIndexPlace);
		var editor_carteen = $("#datagrid_place_all").datagrid('getEditor',{index:editIndexPlace,field: 'carteenID' });
		$(editor_carteen.target).combobox('loadData' , allCarteens); 
		$(editor_carteen.target).combobox('setValue' , allCarteens[0].ID); 
		}
}

//删除送餐点
function delPlace(){
	var rows = $('#datagrid_place_all').datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_place_all').datagrid('getRowIndex',rows[i]);
		$('#datagrid_place_all').datagrid('deleteRow',index);
	}
}
	


function savePlaces(){
	if (!endEditingPlace()){
		return;
	}
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){
		return;
	}
	//获取变更的记录
	var rows_inserted = $('#datagrid_place_all').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_place_all').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_place_all').datagrid('getChanges','updated');
	console.info(rows_inserted);
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	//提交后台执行变更
	$.post("/Order/PlaceServlet",
		{
			method:1,
			inserted:json_inserted,
			deleted:json_deleted,
			updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_place_all').datagrid('acceptChanges');
	        $('#datagrid_place_all').datagrid('reload');
       		//提示
       		$.messager.show({
				title:'编辑送餐点',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
	  }
	);	
	
}

	
//添加送餐点至线路
function addPlaceToRoute(){
	//获取现有的线路送餐点
	var rows = $("#datagrid_place_route").datagrid('getRows');
	
	//获取选中待添加的送餐点
	var rows_appending = $("#datagrid_place_all").datagrid('getChecked');
	if(rows_appending.lengh < 1){
		return;
	}
	
	//不存在则添加，避免重复
	for(var i=0; i<rows_appending.length; i++){
		var exist = false;
		for(var j=0; j<rows.length; j++){
			if(rows[j].ID == rows_appending[i].ID){
				exist = true;
				break;
			}
		}
		if(!exist){
			$("#datagrid_place_route").datagrid('appendRow',rows_appending[i]);
		}		
	}
}

var editIndexPlace;
var editIndexRoute;
function endEditingPlace(){
	if (editIndexPlace == undefined){
		return true;
	}

	if ($('#datagrid_place_all').datagrid('validateRow', editIndexPlace)){		
		$('#datagrid_place_all').datagrid('endEdit', editIndexPlace);
		editIndexPlace = undefined;
		return true;
	} else {
		return false;
	}
}

function endEditingRoute(){
	if (editIndexRoute == undefined){
		return true;
	}

	if ($('#datagrid_route').datagrid('validateRow', editIndexRoute)){		
		$('#datagrid_route').datagrid('endEdit', editIndexRoute);
		editIndexRoute = undefined;
		return true;
	} else {
		return false;
	}
}
	
function rejectPlaces(){
	$('#datagrid_place_all').datagrid('rejectChanges');
	editIndexPlace = undefined;
}

//撤销路线送餐点的变更
function rejectRoutePlaces(){
	$('#datagrid_place_route').datagrid('rejectChanges');
}

//撤销路线的变更
function rejectRoute(){
	$('#datagrid_route').datagrid('rejectChanges');
	editIndexRoute = undefined;
	$('#datagrid_place_route').datagrid('rejectChanges');
}

//编辑送餐点
function editPlace(rowIndex, rowData){
	if (editIndexPlace != rowIndex && endEditingPlace()){
		var grid = $("#datagrid_place_all");
		grid.datagrid('beginEdit', rowIndex);
		editIndexPlace = rowIndex;
		
		var editor = grid.datagrid('getEditor',{index:editIndexPlace,field: 'carteenID' });
		$(editor.target).combobox('loadData' , allCarteens);
	}
}
//送餐点ID至name的格式化
function carteenFormatter(value, rowData, rowIndex) {	
	if(allCarteens == undefined){
		return "";
	}	
	
	if(value == undefined){
		return "";
	}
	
	var ID = Number(value);
	for(var index in allCarteens){
		if(allCarteens[index].ID == ID){
			return allCarteens[index].name;
		}
	}
	return "";
}
</script>
		
</head>

<body class="easyui-layout">
	<div id='Loading' style="position:absolute;z-index:1000;top:0px;left:0px;width:100%;height:100%;background:#DDDDDB url('style/images/bodybg.jpg');text-align:center;padding-top: 20%;"><h1><img src='style/images/loading.gif'/><font color="#15428B">加载中···</font></h1></div>
	
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
		<%@include file="../../inc/help/admin/route.inc" %> 
	</div>
	<div data-options="region:'center'" style="overflow:auto;" >
		<div style="width: 300px;float: left">
			<table  class="easyui-datagrid" id="datagrid_route" title="线路列表" data-options="toolbar:'#toolbar_route',striped:true,onSelect:onSelectRoute,onDblClickRow:editRoute,checkOnSelect:true,selectOnCheck:false,singleSelect:true" style="height:400px;">
				<thead>
					<tr>
						<th data-options="field:'selector',checkbox:true"></th>
						<th data-options="field:'name',width:150,editor:{type:'textbox',options:{required:true}}">线路</th>
						<th data-options="field:'carteenID',width:120,formatter:carteenFormatter,editor:{
							type:'combobox',
							options:{
								required:true,
								editable:false,
								valueField:'ID',
								textField:'name',
								data:allCarteens
							}}">所属食堂</th>
					</tr>
				</thead>
			</table>
		</div>
		<div style="width: 300px;float: left">
			<table  class="easyui-datagrid" id="datagrid_place_route" title="送餐点列表" data-options="toolbar:'#toolbar_place',striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true" style="height:400px;">
				<thead>
					<tr>
						<th data-options="field:'selector',checkbox:true"></th>
						<th data-options="field:'name',width:150,">送餐点</th>
					</tr>
				</thead>
			</table>
		</div>
		<div style="float: left">
			<table  class="easyui-datagrid" id="datagrid_place_all" title="备选送餐点" data-options="toolbar:'#toolbar_place_all',striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: editPlace" style="height:400px;">
				<thead>
					<tr>
						<th data-options="field:'selector',checkbox:true"></th>
						<th data-options="field:'name',width:150,editor:{type:'textbox',options:{required:true,}}">送餐点</th>
						<th data-options="field:'carteenID',width:150,formatter:carteenFormatter,editor:{
							type:'combobox',
							options:{
								required:true,
								editable:false,
								valueField:'ID',
								textField:'name',
								data:allCarteens
							}}">所属食堂</th>
					</tr>
				</thead>
			</table>
		</div>
					
		<div id="toolbar_route" >			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addRoute()">添加</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="delRoute()">删除</a>			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveRoute()">保存</a>	
			<a class="easyui-linkbutton" data-options="iconCls:'icon-redo'" onclick="rejectRoute()">撤销</a>							
		</div>
		
		<div id="toolbar_place" >			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-move_up'" onclick="moveup()">上移</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-move_down'" onclick="movedown()">下移</a>			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="removePlaceFromRoute()">删除</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-redo'" onclick="rejectRoutePlaces()">撤销</a>			
		</div>
		
		<div id="toolbar_place_all" >			
			<a class="easyui-linkbutton" data-options="iconCls:'icon-back'" onclick="addPlaceToRoute()">添加到线路</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addPlace()">添加</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="delPlace()">删除</a>					
			<a class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="savePlaces()">保存</a>	
			<a class="easyui-linkbutton" data-options="iconCls:'icon-redo'" onclick="rejectPlaces()">撤销</a>						
		</div>
	</div>		
			
</body>
</html>