/*
文件说明：页面加载时Loading JS
文件描述：解决IE或FF下，初始化加载时，页面布局乱掉的问题
*/
var width = $(window).width();
var height = $(window).height();

var html = "<div id='loading' style='position:absolute;left:0;width:100%;height:" + height + "px;top:0;background:#E0ECFF;opacity:1;filter:alpha(opacity=100);'>";
html += "<div style='position:absolute;cursor1:wait;left:" + ((width / 2) - 75) + "px;top:200px;width:150px;height:16px;padding:12px 5px 10px 30px;";
html += "background:#fff url(easyUI/themes/default/images/loading.gif) no-repeat scroll 5px 10px;border:2px solid #ccc;color:#000;'>";
html += "正在加载，请等待...";
html += "</div>";
html += "</div>";
alert(html)
window.oncomplete = function () {
    var mask = document.getElementById('loading');
    mask.parentNode.removeChild(mask);
};
document.write(html);
 