<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%@ page import="java.sql.Connection" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Admin user = (Admin) request.getSession().getAttribute("user");
	//原有订餐规则
	Connection conn = DButil.getConnection();
	if (conn == null) { 
		return;
	}
	Regular regular = RegularDAO.getRegular(conn);
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<%
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订餐规则</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">	
	function modifyRegular(){
		var days = $('#days').numberspinner('getValue');
		var lunch = $('#lunch').timespinner('getValue');
		var dinner = $('#dinner').timespinner('getValue');
		var midnight = $('#midnight').timespinner('getValue');
		$.post("/Order/RegularServlet",
		{
			method:1,
			days:days,
			lunch:lunch,
			dinner:dinner,
			midnight:midnight,
	        timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){  		
		    $.messager.show({
				title:'制定订餐规则',
				timeout:3000,
				msg:'制定成功',
				width:200,
				showType:'slide'
			});
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
		<%@include file="../../inc/menu/menu_admin.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/admin/regular.inc" %> 
	</div>
	<div data-options="region:'center'" title='订餐规则'>
		<form id="form_regular" style="border:2px solid #cccccc;position:absolute;top:50%;left:50%;margin-left:-130px;margin-top:-100px">
			<table style="width:360px;height:200px;padding:10px;">
				<tr>
					<td width="180px">最大预订天数：</td>
					<td ><input class="easyui-numberspinner" id="days" value=<%=regular.getDays() %> data-options="required:true" style="width:80px;"></input></td>
				</tr>
				<tr>
					<td>中餐预订截止之间：</td>
					<td><input class="easyui-timespinner" id="lunch" data-options="required:true" value="<%=regular.getLunch()%>" style="width:80px;"></input>(当天)</td>
				</tr>
				<tr>
					<td>晚餐预订截止时间：</td>
					<td><input class="easyui-timespinner"  id="dinner" data-options="required:true" value="<%=regular.getDinner()%>"  style="width:80px;"></input>(提前一天)</td>
				</tr>
				<tr>
					<td>零点餐预订截止时间：</td>
					<td><input class="easyui-timespinner" id="midnight" value="<%=regular.getMidnight()%>" data-options="" style="width:80px;"></input>(提前两天)</td>
				</tr>
				<tr>
					<td colspan="2" align="center"><a href="javascript:void(0)" class="easyui-linkbutton" style="width:200px;" onclick="modifyRegular()">修改订餐规则</a></td>
				</tr>
			</table>
		</form>		
	</div>		
			
</body>
</html>
