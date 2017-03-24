<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>权限错误</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<style type="text/css">
	#bigBox{
	margin-left:200px;
	margin:150px 400px;
		
	}
	#midBox{
		position:absolute;
	}
	#smallBox{
		position:relative;
		float:right;
		
	}
</style>
</head>

<body>
	<div id="bigBox" >
		<div id="midBox">
			<div id="smallBox">
				<span>权限错误，请与超级管理员确认您的权限！</span>
			</div>
		</div>
	</div>
</body>
</html>
