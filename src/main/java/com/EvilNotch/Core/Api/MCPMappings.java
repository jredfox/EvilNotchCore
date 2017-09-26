package com.EvilNotch.Core.Api;

import com.EvilNotch.Core.MainCommonMod;

public class MCPMappings {
	
	/**
	 * Returns name mapping based on environment 
	 * @param isEclipse
	 * @param de_obfuscated
	 * @return
	 */
	public static String getFeildName(boolean isEclipse, String de_obfuscated)
	{
		if(isEclipse || !FieldAcess.fieldToOb.containsKey(de_obfuscated))
			return de_obfuscated;
		
		return FieldAcess.fieldToOb.get(de_obfuscated);
	}

	public static String getMethodName(boolean isEclipse, String de_obfuscated)
	{
		if(isEclipse || !FieldAcess.methodToOb.containsKey(de_obfuscated))
			return de_obfuscated;
		
		return FieldAcess.methodToOb.get(de_obfuscated);
	}
	
	public static String getParameterName(boolean isEclipse, String de_obfuscated)
	{
		if(isEclipse ||  !FieldAcess.paramToOb.containsKey(de_obfuscated))
			return de_obfuscated;
		
		return FieldAcess.paramToOb.get(de_obfuscated);
	}
	
	public static String getFeildName(String de_obfuscated)
	{
		if(MainCommonMod.isEclipse || !FieldAcess.fieldToOb.containsKey(de_obfuscated))
			return de_obfuscated;
		
		return FieldAcess.fieldToOb.get(de_obfuscated);
	}

	public static String getMethodName(String de_obfuscated)
	{
		if(MainCommonMod.isEclipse || !FieldAcess.methodToOb.containsKey(de_obfuscated))
			return de_obfuscated;
		
		return FieldAcess.methodToOb.get(de_obfuscated);
	}
	
	public static String getParameterName(String de_obfuscated)
	{
		if(MainCommonMod.isEclipse || !FieldAcess.paramToOb.containsKey(de_obfuscated))
			return de_obfuscated;
		
		return FieldAcess.paramToOb.get(de_obfuscated);
	}

}
