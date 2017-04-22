package cn.jiguang.hivehfile.util;

import cn.jiguang.hivehfile.exception.ColumnNumMismatchException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by jiguang
 * Date: 2017/4/19
 */
public class StructConstructor {
    /**
     *  根据指定的对象类型，自动将传入的字符串封装成所需Struct对象
     * @param line  待解析的字符串
     * @param classPath 返回的对象全路径
     * @param columnMap Hive表的字段序列
     * @return
     */
    public static Object parse(String line, String classPath, List<Map<String, String>> columnMap)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, ColumnNumMismatchException {
        Map<String,Object> data  = preParse(line,columnMap);
        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classPath);
        Field[] fields = clazz.getDeclaredFields();
        if(fields.length!=columnMap.size()) throw new ColumnNumMismatchException("字段序列参数与对象属性不匹配");
        Object obj = clazz.newInstance();
        for(Field field:fields){
            if(isContains(columnMap,field.getName())){
                invokeSet(obj,field.getName(),data.get(field.getName()));
            }
        }
        return obj;
    }

    /**
     * 字符串拆分成哈希表
     * @param line
     * @param columnMap
     * @return
     */
    private static Map<String,Object> preParse(String line, List<Map<String, String>> columnMap){
        int i = 0;
        Map<String,Object> map = new HashMap<String, Object>();
        String[] values = line.split("\\x01");
        for(Map<String,String> $itera:columnMap){
            for(String name:$itera.keySet()){
                String type = $itera.get(name);
                Object value = null;
                if(type.equals("string")){
                    value = values[i];
                }else if(type.equals("bigint")){
                    value = Long.parseLong(values[i]);
                } else if(type.equals("array")){
                    String[] subString = values[i].split("\\x02");
                    ArrayList<String> arr = new ArrayList<String>();
                    for(String $s:subString){
                        if( !$s.equals("") ){
                            arr.add($s);
                        }
                    }
                    value = arr;
                }
                map.put(name,value);
                i++;
            }
        }
        return map;
    }

    /**
     * 判断字符串数组中是否含有特定的子字符串
     * @param columnMap
     * @param column
     * @return
     */
    private static Boolean isContains(List<Map<String,String>> columnMap,String column){
        Boolean result = false;
        for(Map<String,String> map:columnMap){
            if(map.containsKey(column)){
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * java反射bean的set方法
     *
     * @param objectClass
     * @param fieldName
     * @return
     */
     public static Method getSetMethod(Class objectClass, String fieldName) {
        try {
            Class[] parameterTypes = new Class[1];
            Field field = objectClass.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            StringBuffer sb = new StringBuffer();
            sb.append("set");
            sb.append(fieldName.substring(0, 1).toUpperCase());
            sb.append(fieldName.substring(1));
            Method method = objectClass.getMethod(sb.toString(), parameterTypes);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * java反射bean的get方法
     *
     * @param objectClass
     * @param fieldName
     * @return
     */
    public static Method getGetMethod(Class objectClass, String fieldName) {
        try {
            Class[] parameterTypes = new Class[1];
            Field field = objectClass.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            StringBuffer sb = new StringBuffer();
            sb.append("get");
            sb.append(fieldName.substring(0, 1).toUpperCase());
            sb.append(fieldName.substring(1));
            Method method = objectClass.getDeclaredMethod(sb.toString(), new Class[]{});
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行set方法
     *
     * @param o 执行对象
     * @param fieldName 属性
     * @param value 值
     */
    private static void invokeSet(Object o, String fieldName, Object value) {
        Method method = getSetMethod(o.getClass(), fieldName);
        try {
            method.invoke(o,  value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行get方法
     * @param o 执行对象
     * @param fieldName 属性
     * @return get方法执行结果
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeGet(Object o,String fieldName) throws InvocationTargetException, IllegalAccessException {
        Method method = getGetMethod(o.getClass(), fieldName);
        Object result = method.invoke(o, new Object[]{});
        return result;
    }

    /**
     * 根据输入的字段和类型，自动生成字段类型列表
     * @param input 以逗号作为不同字段的分隔符，以冒号作为字段名和字段类型的分隔符
     *      e.g.
     *          imei:string,city_list:array,country_list:array
     * @return
     */
    public static List<Map<String,String>> assemblyColumnList(String input){
        String[] columnsAndType  =  input.split(",");
        List<Map<String,String>> columnList = new ArrayList<Map<String, String>>();
        for(String $itera : columnsAndType){
            String columnName = $itera.split(":")[0];
            String columnType = $itera.split(":")[1];
            Map<String,String> map = new HashMap<String, String>();
            map.put(columnName,columnType);
            columnList.add(map);
        }
        return columnList;
    }

    /**
     * 通过反射获取封装对象的字段列表
     * @param className  类的全权限路径
     * @return
     * @throws ClassNotFoundException
     */
    public static List<String> getStructFields(String className) throws ClassNotFoundException {
        List<String> fields = new ArrayList<String>();
        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        for( Field field : clazz.getDeclaredFields()){
            fields.add(field.getName());
        }
        return  fields;
    }

    /**
     * 通过反射获取指定类的Class
     * @param className 类的全限定路径
     * @return
     * @throws ClassNotFoundException
     */
    public static Class getStructClass(String className) throws ClassNotFoundException {
        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        return clazz;
    }
}
