function date2String(aDate){  
	var year=aDate.getFullYear();  
	var month=aDate.getMonth();  
	month++;  
	var mydate=aDate.getDate();  
	
	return year+"-"+(month<10?"0":"")+month+"-"+(mydate<10?"0":"")+mydate; 
}  

function string2Date(s){	
	if (!s) return new Date();  
	var y = s.substring(0,4);  
	var m =s.substring(5,7);  
	var d = s.substring(8,10);
	if (!isNaN(y) && !isNaN(m) && !isNaN(d) ){  
	    return new Date(y,m-1,d);  
	} else {  
	    return new Date();  
	}	
}
