package com.EvilNotch.Core.Util.Java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.Core.MainCommonMod;
import com.EvilNotch.Core.Util.Line.LineBase;
import com.EvilNotch.Core.Util.Line.LineDynamicLogic;
import com.EvilNotch.Core.Util.Line.LineItemStack;
import com.EvilNotch.Core.Util.Line.LineItemStackBase;

public class JavaUtil {
	
	public static String getStaticArrayStringWithLiteral(int[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "i" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayString(boolean[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(byte[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "b" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(short[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "s" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(long[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "l" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(float[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "f" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(double[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "d" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(String[] obj)
	{
		String str = "";
		for(int i=0;i<obj.length;i++)
		{
			if(i != 0)
				str += " ";
			str += "\"" + obj[i].toString() + "\",";
		}
		if(str.equals(""))
			return null;
		
		return str.substring(0,str.length()-1);
	}
	
	public static List<Integer> staticToArray(int[] values) 
	{
		ArrayList<Integer> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	public static List<Boolean> staticToArray(boolean[] values) 
	{
		ArrayList<Boolean> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	public static List<Byte> staticToArray(byte[] values) 
	{
		ArrayList<Byte> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	public static List<Short> staticToArray(short[] values) 
	{
		ArrayList<Short> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	public static List<Long> staticToArray(long[] values) 
	{
		ArrayList<Long> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	public static List<Float> staticToArray(float[] values) 
	{
		ArrayList<Float> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	public static List<Double> staticToArray(double[] values) 
	{
		ArrayList<Double> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	/**
	 * Supports most used line only
	 * @param init_list
	 * @return
	 */
	public static ArrayList<LineBase> staticToLineArray(String[] init_list)
	{
		ArrayList<String> strlist = (ArrayList<String>) JavaUtil.staticToArray(init_list);
		ArrayList<LineBase> list = new ArrayList();
		for(String s : strlist)
			list.add(new LineItemStackBase(s));
		return list;
	}
	
	public static HashMap sortByValues(HashMap map) 
	{ 
	     List list = new LinkedList(map.entrySet());
	     // Defined Custom Comparator here
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	             return ((Comparable) ((Map.Entry) (o1)).getValue())
	                .compareTo(((Map.Entry) (o2)).getValue());
	          }
	     });

	     // Here I am copying the sorted list in HashMap
	     // using LinkedHashMap to preserve the insertion order
	     HashMap sortedHashMap = new LinkedHashMap();
	     for (Iterator it = list.iterator(); it.hasNext();) {
	            Map.Entry entry = (Map.Entry) it.next();
	            sortedHashMap.put(entry.getKey(), entry.getValue());
	     } 
	     return sortedHashMap;
	}
	
	public static String toFileCharacters(String s)
	{ 
		String invalid = ".,[]{}()!;\"'*?<>|/\\||?*^!@#$%&<>:=";
		String resault = "";
		String sub = "";
		for (int i=0;i<s.length();i++)
		{
			sub = s.substring(i, i+1);
			if (!invalid.contains(sub) && resault.length() < 240)
				resault = resault + sub;
		}
		if (resault.toUpperCase().equals("CON") || resault.toUpperCase().equals("PRN") || resault.toUpperCase().equals("AUX") || resault.toUpperCase().equals("NUL"))
		{
			return resault + " null";
		}
		for (int j=0;j<10;j++)
		{
			String com = "COM" + String.valueOf(j);
			String lpt = "LPT" + String.valueOf(j);
			if (resault.toUpperCase().equals(com) || resault.toUpperCase().equals(lpt))
			{
				return resault + " null";
			}
		}
		if(resault.equals(""))
			resault = "failedModName";
		
		return resault;
	}
	
	public static void printMap(Map map)
	{
		Iterator it = map.entrySet().iterator();
		int index = 0;
		System.out.print("[");
		while(it.hasNext())
		{
			Map.Entry pair = (Entry) it.next();
			System.out.print(" Key" + index + ":" + pair.getKey() + " Value"  + index + ":" + pair.getValue());
			index++;
		}
		System.out.print("]\n");
	}
	
	
	public static List staticToArray(Object[] str)
	{
	    List list = new ArrayList();
	    for(int i=0;i<str.length;i++)
	    	list.add(str[i]);
	    return list;
	}
	public static Object[] arrayToStatic(List list)
	{
		Object[] strstatic = new String[list.size()];
		for(int i=0;i<list.size();i++)
			strstatic[i] = list.get(i);
		return strstatic;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setList(List filelist,List list,int index)
	{
		for(int i=0;i<list.size();i++)
		{
			Object entry = list.get(i);
			boolean flag = false;
			if(index + i < filelist.size() && !flag)
				filelist.set(index+i,entry);
			else{
				filelist.add(entry);
				flag = true;
			}
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void deleteListRange(List filelist,int index1,int index2)
	{
		List<String> sub = filelist.subList(index1, index2);
		sub.clear();
	}
	public static void moveFileFromJar(Class clazz,String input,File output) {
		try {
			InputStream inputstream =  clazz.getResourceAsStream(input);
			FileOutputStream outputstream = new FileOutputStream(output);
			output.createNewFile();
			IOUtils.copy(inputstream,outputstream);
			inputstream.close();
			outputstream.close();
		} catch (IOException io) {io.printStackTrace();}
		
	}
	public static boolean ArrayhasEqualString(String[] list, String strhead) 
	{
		for(int i=0;i<list.length;i++)
		{
			String s = list[i];
			if(s == null)
				continue;//Static arrays are known for nulls
			if(strhead.equals(s))
				return true;
		}
		return false;
	}
	public static int findLastChar(String str, String character) 
	{
		int index = 0;
		for(int i=0;i<str.length();i++)
		{
			String s = str.substring(i, i+1);
			if(s.equals(character))
				index = i;
		}
		return index;
	}
	public static String[] splitStringAtIndex(int index,String tosplit) 
	{
		String[] list = new String[2];
		String first = "";
		String second = "";
		for(int i=0;i<tosplit.length();i++)
		{
			if(i < index)
				first += tosplit.substring(i, i+1);
			if(i > index)
				second += tosplit.substring(i, i+1);
		}
		list[0] = first;
		list[1] = second;
		return list;
	}

}
