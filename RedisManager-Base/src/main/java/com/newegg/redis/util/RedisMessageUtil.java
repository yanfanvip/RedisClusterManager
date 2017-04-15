package com.newegg.redis.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.newegg.redis.model.M_clusterInfo;

public class RedisMessageUtil {
	
	public static <T> T convert(String message, Class<T> clazz) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String[] lines = message.split("\n");
		Field[] fields= clazz.getDeclaredFields();
		for (Field field : fields) {
			map.put(field.getName(), null);
		}
		for (String line : lines) {
			String[] tmp = line.split(":");
			if(map.containsKey(tmp[0].trim())){
				map.put(tmp[0].trim(), tmp[1].trim());
			}
		}
		return convert(map, clazz);
	}
	
	public static <T> T convert(Map<String,String> map, Class<T> clazz) throws Exception{
		T t = clazz.newInstance();
		for (String key : map.keySet()) {
			String value = map.get(key);
			if(value != null){
				setProperty(t, key, value);
			}
		}
		return t;
	}
	
    public static <T> PropertyDescriptor getPropertyDescriptor(Class<T> clazz, String propertyName) throws Exception {  
        StringBuffer sb = new StringBuffer();//构建一个可变字符串用来构建方法名称  
        Method setMethod = null;  
        Method getMethod = null;  
        PropertyDescriptor pd = null;  
        Field f = clazz.getDeclaredField(propertyName);//根据字段名来获取字段  
        if (f!= null) {  
            //构建方法的后缀  
           String methodEnd = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);  
           sb.append("set" + methodEnd);//构建set方法  
           setMethod = clazz.getDeclaredMethod(sb.toString(), new Class[]{ f.getType() });  
           sb.delete(0, sb.length());//清空整个可变字符串  
           sb.append("get" + methodEnd);//构建get方法  
           //构建get 方法  
           getMethod = clazz.getDeclaredMethod(sb.toString(), new Class[]{ });  
           //构建一个属性描述器 把对应属性 propertyName 的 get 和 set 方法保存到属性描述器中  
           pd = new PropertyDescriptor(propertyName, getMethod, setMethod);  
        }
        return pd;  
    }  
      
    public static void setProperty(Object obj,String propertyName,String value) throws Exception{  
        Class<?> clazz = obj.getClass();//获取对象的类型  
        PropertyDescriptor pd = getPropertyDescriptor(clazz,propertyName);//获取 clazz 类型中的 propertyName 的属性描述器  
        Method setMethod = pd.getWriteMethod();//从属性描述器中获取 set 方法 
        Class<?> setClazz = setMethod.getParameterTypes()[0];
        Object newData = value;
        if(setClazz != String.class){
        	Method convertData = setClazz.getDeclaredMethod("valueOf", new Class[]{ String.class});
        	newData = convertData.invoke(null, new Object[]{value});
        }
        setMethod.invoke(obj, new Object[]{newData});//调用 set 方法将传入的value值保存属性中去  
    }
      
    public static Object getProperty(Object obj, String propertyName) throws Exception{  
       Class<?> clazz = obj.getClass();//获取对象的类型  
       PropertyDescriptor pd = getPropertyDescriptor(clazz,propertyName);//获取 clazz 类型中的 propertyName 的属性描述器  
       Method getMethod = pd.getReadMethod();//从属性描述器中获取 get 方法  
       return getMethod.invoke(clazz, new Object[]{});//调用方法获取方法的返回值  
    }
	
	public static void main(String[] args) throws Exception {
		String message = "cluster_state:fail"
				+ "cluster_slots_assigned:0"
				+ "cluster_slots_ok:0"
				+ "cluster_slots_pfail:0"
				+ "cluster_slots_fail:0"
				+ "cluster_known_nodes:1"
				+ "cluster_size:0"
				+ "cluster_current_epoch:0"
				+ "cluster_my_epoch:0"
				+ "cluster_stats_messages_sent:0"
				+ "cluster_stats_messages_received:0";
		M_clusterInfo clusterInfo = convert(message, M_clusterInfo.class);
		System.out.println(clusterInfo);
	}
}
