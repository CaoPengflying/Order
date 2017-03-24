<%@page import="bean.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>九江石化食堂订餐系统</title>
<link href="../css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script src="../js/cookie.js"></script>
<script src="../js/jquery.js"></script>
<script src="../js/bgstretcher.js"></script>
<script type="text/javascript" charset="utf-8">

function keyDown(e){
	var ev= window.event||e;
	//13是键盘上面固定的回车键
	if (ev.keyCode == 13) {
	//你要执行的方法
		document.form1.submit();
	}
}

$(document).ready(function(){	
	var role= getCookie('role');
	if(role == 1){
		//$("input[id='admin']").checked = true;
		document.getElementById('admin').checked = true;
	}else{
		document.getElementById('emploee').checked = true;
	}
	$("#USER_LOGINNAME").val(getCookie('user_loginName'));	
	//$("#USER_PASSWD").val(getCookie('user_password'));
	//$("input[name='role']:checked").val(getCookie('role'));	
	$("#USER_LOGINNAME").focus();
	  
        //  Initialize Backgound Stretcher	   
		$('.login_bgr').bgStretcher({
			images: ['../images/01.jpg', '../images/02.jpg', '../images/03.jpg', '../images/04.jpg', '../images/05.jpg'],
			imageWidth: 1024, 
			imageHeight: 400, 
			slideDirection: 'N',
			slideShowSpeed: 2000,
			transitionEffect: 'fade',
			sequenceMode: 'normal',
			buttonPrev: '',
			buttonNext: '',
			pagination: '',
			anchoring: 'left center',
			anchoringImg: 'left center'
		});
		
	});	
	function login(){
		var userId = $("#USER_LOGINNAME").val();
		var password = $("#USER_PASSWD").val();
		var role = $("input[name='role']:checked").val();
		$.post("/Order/LoginServlet",
		{
			userId:userId,
			password:password,
			role:role
		},function(result){
			var data = JSON.parse(result); 
			var success = data.success;
			if(!success){
				alert(data.msg);
				return;
			}
			if(document.getElementById('check').checked == true){
			//if($("input[name='check']:checked").val() == 1){
				setCookie('user_loginName', userId);
				//setCookie('user_password', password);
				setCookie('role',role);
			}else{
			delCookie('user_loginName');
			//delCookie('user_password');
			delCookie('role');
			//setCookie('role',$("input[name='role']:checked").attr("value"));				
			}
			if(role == 0){
			    switch(Number(data.role)){
		    		case <%=Employee.ROLE_COMMON%>:
			    		window.self.location.href = "./common/index.jsp?target=0";
			    		break;  
		    		case <%=Employee.ROLE_GROUP%>:
			    		window.self.location.href = "./group/index.jsp?target=0";
			    		break;  
		    		case <%=Employee.ROLE_WORKSHOP%>:
			    		window.self.location.href = "./workshop/index.jsp?target=0";
			    		break;
			    	}
			    }else{			
			    	switch(Number(data.role)){    	 			    		  
		    		case <%=Admin.ROLE_ADMIN%>:
			    		window.self.location.href = "./admin/index.jsp?target=0";
			    		break;    
		    		case <%=Admin.ROLE_SUPERADMIN%>:
			    		window.self.location.href = "./super/admins.jsp";
			    		break;  
			    		}
			    }
	    });
	}
	
	
</script>
</head>

<body>
<div class="login_up">
    <img src="../images/login_logo.png" />
</div>
<div class="login_box_top"></div>
<div class="login">
  <form > 
  <div class="login_enter">
	  <div class="login_d">
	   <div class="login-box">
         <div class="mt">
             <img src="../images/login_p.png" />
         </div>
         <div class="mc">
         <div class="form">
         <div class="item_item-fore1">
              <div class="login-label1"></div>
              <input id="USER_LOGINNAME" name="eID" type="text" placeholder="用户名" Required=true class="idxt"/>
              <span id="show_text1"></span>
         </div>
         <div class="item_item-fore1">
              <div for="loginname" class="login-label2"></div>
              <input id="USER_PASSWD" name="ePassWord" type="password" placeholder="密码" Required=true class="idxt" onkeydown="keyDown(event)"/>
              <span id="show_text2"></span> 
         </div>
		 <div class="item_item-fore1">
		 	 &emsp;&emsp;
              <input id="emploee" type="radio" name="role" value="0" checked >职工
              <input id="admin" type="radio" name="role" value="1" >管理员
              <input id="check"type="checkbox" name="check" value="1" checked>记住帐号
         </div>
         <div class="item item-fore3" style="padding-left:30px;">
              <div class="safe">
              	  
              <div class="login-btn">					
			  <input id="btnLoginOk" type="button" class="button" onclick="login();" value="">
				 	 
              </div>

              </div>
           </div>
   		 </div>
       </div>
	   </div>
	 </div>
  </div>
  </form>
</div>
<div class="login_bgr"></div>
<div class="login_footer"><span class="c_l">2016  九江石化食堂订餐系统</span><span class="c_l">技术支持 <a href="http://www.gtkj.com.cn/">江西省国泰科技有限公司</a></span><span class="c_r">建议您使用IE8+、Google Chrome，分辨率1280*800及以上浏览本系统，获得更好用户体验。</span></div>
</body>
</html>