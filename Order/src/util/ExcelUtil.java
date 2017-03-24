package util;



import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import jxl.CellType;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.biff.DisplayFormat;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author     : WH
 * @group      : tgb8
 * @Date       : 2014-1-2 下午9:13:21
 * @Comments   : 导入导出Excel工具类
 * @Version    : 1.0.0
 */ 
   
public class ExcelUtil  { 
       
    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param response  使用response可以导出到浏览器
     */ 
    public static  <T>  void   listToExcel ( 
            List<T> list , 
            LinkedHashMap<String,String> fieldMap, 
            int []widths,
            String title,
            String dateRange, 
            String signature,
            HttpServletResponse response  
            ){ 
    	
        response.reset();           
        response.setContentType("application/vnd.ms-excel");//设置response头信息  //改成输出excel文件        
        String filename = String.format("%s(%s).xls", title,dateRange);
		try {
			filename = URLEncoder.encode(filename, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			return;
		}
        response.setHeader("Content-disposition","attachment; filename="+filename); 
          
        OutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e1) {			
			e1.printStackTrace();
			return;
		} 
      //创建工作簿并发送到OutputStream指定的地方 
        try { 
        	WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet=workbook.createSheet(title, 0);
            fillSheet(sheet, list, fieldMap, widths, title,dateRange,signature);
               
            workbook.write(); 
            workbook.close(); 
           
        }catch (Exception e) { 
            e.printStackTrace(); 
        }
    } 
       
    
    private static <T> void fillSheet( 
            WritableSheet sheet, 
            List<T> list, 
            LinkedHashMap<String,String> fieldMap, 
            int []widths,
            String title,
            String dateRange,
            String signature
            )throws Exception{ 
 		SheetSettings setting = sheet.getSettings();
		setting.setOrientation(PageOrientation.LANDSCAPE);
		setting.setPaperSize(PaperSize.A4);
		setting.setFitToPages(true);
		setting.getFooter().getCentre().appendPageNumber() ;    // 为页脚添加页数
        setting.setFooterMargin(0.07); // 设置页脚边距（下）
		
		WritableFont font0 =new WritableFont(WritableFont.createFont("宋体"), 12 ,WritableFont.BOLD);//标题头加粗
		
		WritableFont font1 =new WritableFont(WritableFont.createFont("宋体"), 9,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.WHITE);//列头头不加粗
		
		WritableFont font2 =new WritableFont(WritableFont.createFont("宋体"), 9 ,WritableFont.NO_BOLD);//内容不加粗
		WritableFont font3 =new WritableFont(WritableFont.createFont("宋体"), 9 ,WritableFont.NO_BOLD);//不加粗
		WritableCellFormat wcf1 = new WritableCellFormat(font1);
		WritableCellFormat wcf0 = new WritableCellFormat(font0);
		WritableCellFormat wcf3 = new WritableCellFormat(font3);
		DisplayFormat displayFormat=NumberFormats.TEXT;
		WritableCellFormat wcf2 = new WritableCellFormat(font2,displayFormat);
		wcf0.setVerticalAlignment(VerticalAlignment.CENTRE);  //垂直居中
		wcf0.setAlignment(Alignment.CENTRE);  //
		
		wcf1.setVerticalAlignment(VerticalAlignment.CENTRE);  //垂直居中
		wcf1.setAlignment(Alignment.CENTRE);
		wcf1.setBorder(Border.ALL,BorderLineStyle.THIN);
		wcf1.setBackground(Colour.BLUE_GREY);
		wcf1.setWrap(true);
		
		wcf2.setAlignment(Alignment.LEFT);  //水平居左
		wcf2.setVerticalAlignment(VerticalAlignment.CENTRE);  //垂直居中
		wcf2.setBorder(Border.ALL,BorderLineStyle.THIN);
		wcf3.setAlignment(Alignment.LEFT);  //水平居中
		wcf3.setVerticalAlignment(VerticalAlignment.CENTRE);		
		
		NumberFormat fivedps = new NumberFormat("#0");//一律不保留小数
    	WritableCellFormat wcfNumber = new WritableCellFormat(font2, fivedps);
    	wcfNumber.setAlignment(Alignment.LEFT);  //水平居左
    	wcfNumber.setVerticalAlignment(VerticalAlignment.CENTRE);  //垂直居中
    	wcfNumber.setBorder(Border.ALL,BorderLineStyle.THIN);
    	
        //定义存放英文字段名和中文字段名的数组 
        String[] enFields=new String[fieldMap.size()]; 
        String[] cnFields=new String[fieldMap.size()]; 
           
        //填充数组 
        int count=0; 
        for(Entry<String,String> entry:fieldMap.entrySet()){ 
            enFields[count]=entry.getKey(); 
            cnFields[count]=entry.getValue(); 
            count++; 
        } 
      
        //添加标题行
        Label labelTitle = new Label(0, 0,title,wcf0);
        sheet.mergeCells(0,0, fieldMap.size(),0);//
        sheet.addCell(labelTitle);   	  
        sheet.setRowView(0, 1000);//设置行高
        
        //添加日期范围行
        Label labelDate = new Label(0, 1, "日期："+dateRange, wcf3);
        sheet.mergeCells(0,1, fieldMap.size(),1);//
        sheet.addCell(labelDate);
      
        //填充表头 
       for(int i=0;i<=cnFields.length;i++){
    	   String filedName = i==0?"序号":cnFields[i-1];
    	   Label label=new Label(i,2,filedName,wcf1);
           sheet.addCell(label);    	  
           sheet.setRowView(2, 500);//设置行高
        }
       
      //填充内容
        for(int index=0,rowNo=3;index<list.size();index++,rowNo++){ 
        	//添加序号
        	Label label0 =new Label(0,rowNo,String.valueOf(index+1),wcf2);
	        sheet.addCell(label0);
	        
            //获取每个字段的值
            T item=list.get(index);
            for(int i=1;i<=enFields.length;i++){ 
                Object objValue=getFieldValueByNameSequence(enFields[i-1], item);
                String fieldValue=objValue==null ? "" : objValue.toString();
                
               if(objValue.getClass()==Date.class)
               {
            	   fieldValue=new SimpleDateFormat("yyyy-MM-dd").format(objValue);
            	   Label label =new Label(i,rowNo,fieldValue,wcf2);
                   sheet.addCell(label);
               }
               else if(objValue.getClass()==Float.class||Integer.class==objValue.getClass()||Double.class==objValue.getClass())
               {  
            	   Number number = new Number(i, rowNo, Double.parseDouble(fieldValue),wcfNumber);
            	   sheet.addCell(number); 
               }
               else{
                Label label =new Label(i,rowNo,fieldValue,wcf2);
                sheet.addCell(label);}
            } 
        } 
        
        if(sheet.getCell(4,3).getType() == CellType.NUMBER){
        //添加汇总行
        int row = list.size()+3;
        Label label = new Label(2, row, "合计",wcf3);
        sheet.addCell(label);
        for(int column=4; column<=cnFields.length; column++){
        	double sum = 0;
        	for (int i = 0; i < list.size(); i++) {
				NumberCell cell = (NumberCell) sheet.getCell(column, i+3);
				sum += cell.getValue();		
			}
        	Number number = new Number(column, row, sum,wcf3);
     	   sheet.addCell(number); 
        }
        
        
        //添加签名行
        row += 2;
		label=new Label(0,row,signature,wcf3);
		sheet.addCell(label);	
		sheet.mergeCells(0,row, cnFields.length+1,row);
        } 
		setColumnWidths(sheet,widths);		
    }
       
  
    private static void setColumnWidths(WritableSheet sheet, int[] widths) {

		sheet.setColumnView(0, 5);
		for(int column = 1; column<=widths.length; column++){
			sheet.setColumnView(column, widths[column-1]);
		}
	}


	/*<-------------------------辅助的私有方法----------------------------------------------->*/ 
    /**
     * @MethodName  : getFieldValueByName
     * @Description : 根据字段名获取字段值
     * @param fieldName 字段名
     * @param o 对象
     * @return  字段值
     */ 
    private static  Object getFieldValueByName(String fieldName, Object o) throws Exception{ 
           
        Object value=null; 
        Field field=getFieldByName(fieldName, o.getClass()); 
           
        if(field !=null){ 
            field.setAccessible(true); 
            value=field.get(o); 
        }else{ 
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名"+fieldName); 
        } 
           
        return value;
        
        
        
         
    } 
       
    /**
     * @MethodName  : getFieldByName
     * @Description : 根据字段名获取字段
     * @param fieldName 字段名
     * @param clazz 包含该字段的类
     * @return 字段
     */ 
    private static Field getFieldByName(String fieldName, Class<?>  clazz){ 
        //拿到本类的所有字段 
        Field[] selfFields=clazz.getDeclaredFields(); 
           
        //如果本类中存在该字段，则返回 
        for(Field field : selfFields){ 
            if(field.getName().equals(fieldName)){ 
                return field; 
            } 
        } 
           
        //否则，查看父类中是否存在此字段，如果有则返回 
        Class<?> superClazz=clazz.getSuperclass(); 
        if(superClazz!=null  &&  superClazz !=Object.class){ 
            return getFieldByName(fieldName, superClazz); 
        } 
           
        //如果本类和父类都没有，则返回空 
        return null; 
    } 
       
       
       
    /**
     * @MethodName  : getFieldValueByNameSequence
     * @Description : 
     * 根据带路径或不带路径的属性名获取属性值
     * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
     * 
     * @param fieldNameSequence  带路径的属性名或简单属性名
     * @param o 对象
     * @return  属性值
     * @throws Exception
     */ 
    private static  Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception{ 
           
        Object value=null; 
          
        //将fieldNameSequence进行拆分 
        String[] attributes=fieldNameSequence.split("\\."); 
        if(attributes.length==1){ 
            value=getFieldValueByName(fieldNameSequence, o); 
        }else{ 
            //根据属性名获取属性对象 
            Object fieldObj=getFieldValueByName(attributes[0], o); 
            String subFieldNameSequence=fieldNameSequence.substring(fieldNameSequence.indexOf(".")+1); 
            value=getFieldValueByNameSequence(subFieldNameSequence, fieldObj); 
        } 
        return value;  
           
    }  
       
     
}