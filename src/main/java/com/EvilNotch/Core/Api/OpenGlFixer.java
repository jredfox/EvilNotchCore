package com.EvilNotch.Core.Api;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.OpenGlHelper;

public class OpenGlFixer {
	
	
	public static Object[] cacheOpenGlHelper()
	{
		Object[] obj = new Object[30];
		obj[0] = OpenGlHelper.openGL21;
		obj[1] = OpenGlHelper.defaultTexUnit;
		obj[2] = OpenGlHelper.lightmapTexUnit;
		obj[3] = OpenGlHelper.field_153197_d;
		obj[4] = OpenGlHelper.field_153198_e;
		obj[5] = OpenGlHelper.field_153199_f;
		obj[6] = OpenGlHelper.field_153200_g;
		obj[7] = OpenGlHelper.field_153201_h;
		obj[8] = OpenGlHelper.field_153202_i;
		obj[9] = OpenGlHelper.field_153203_j;
		obj[10] = OpenGlHelper.field_153204_k;
		obj[11] = OpenGlHelper.field_153205_l;
		obj[12] = OpenGlHelper.field_153206_m;
		obj[13] = OpenGlHelper.framebufferSupported;
		obj[14] = OpenGlHelper.field_153207_o;
		obj[15] = OpenGlHelper.field_153208_p;
		obj[16] = OpenGlHelper.field_153209_q;
		obj[17] = OpenGlHelper.field_153210_r;
		obj[18] = OpenGlHelper.anisotropicFilteringSupported;
		obj[19] = OpenGlHelper.anisotropicFilteringMax;
		obj[20] = OpenGlHelper.field_153211_u;
		obj[21] = OpenGlHelper.shadersSupported;
		obj[22] = OpenGlHelper.lastBrightnessX;
		obj[23] = OpenGlHelper.lastBrightnessY;
		
		//Private fields
		obj[24] = (Integer) ReflectionUtil.getObject(null, OpenGlHelper.class, MCPMappings.getFeildName("field_153212_w"));
		obj[25] = (Boolean) ReflectionUtil.getObject(null, OpenGlHelper.class, MCPMappings.getFeildName("field_153213_x"));
		obj[26]  = (Boolean) ReflectionUtil.getObject(null, OpenGlHelper.class, MCPMappings.getFeildName("field_153214_y"));
		obj[27]  = (Boolean) ReflectionUtil.getObject(null, OpenGlHelper.class, MCPMappings.getFeildName("field_153215_z"));
		obj[28]  = (Boolean) ReflectionUtil.getObject(null, OpenGlHelper.class, MCPMappings.getFeildName("openGL14"));
		obj[29] = (String) ReflectionUtil.getObject(null, OpenGlHelper.class, MCPMappings.getFeildName("field_153196_B"));
		return obj;
	}
	public static void updateOpenGlHelper(Object[] obj,boolean lightmapUpdate)
	{
		OpenGlHelper.openGL21 = (Boolean) obj[0];
		OpenGlHelper.defaultTexUnit = (Integer) obj[1];
		OpenGlHelper.lightmapTexUnit= (Integer) obj[2];
		OpenGlHelper.field_153197_d= (Boolean) obj[3];
		OpenGlHelper.field_153198_e= (Integer) obj[4];
		OpenGlHelper.field_153199_f= (Integer) obj[5];
		OpenGlHelper.field_153200_g= (Integer) obj[6];
		OpenGlHelper.field_153201_h= (Integer) obj[7];
		OpenGlHelper.field_153202_i= (Integer) obj[8];
		OpenGlHelper.field_153203_j= (Integer) obj[9];
		OpenGlHelper.field_153204_k= (Integer) obj[10];
		OpenGlHelper.field_153205_l= (Integer) obj[11];
		OpenGlHelper.field_153206_m= (Integer) obj[12];
		OpenGlHelper.framebufferSupported= (Boolean) obj[13];
		OpenGlHelper.field_153207_o= (Integer) obj[14];
		OpenGlHelper.field_153208_p= (Integer) obj[15];
		OpenGlHelper.field_153209_q= (Integer) obj[16];
		OpenGlHelper.field_153210_r= (Integer) obj[17];
		OpenGlHelper.anisotropicFilteringSupported= (Boolean) obj[18];
		OpenGlHelper.anisotropicFilteringMax= (Integer) obj[19];
		OpenGlHelper.field_153211_u= (Boolean) obj[20];
		OpenGlHelper.shadersSupported= (Boolean) obj[21];
		if(lightmapUpdate)
		{
			OpenGlHelper.lastBrightnessX= (Float) obj[22];
			OpenGlHelper.lastBrightnessY= (Float) obj[23];
		}
		
		//Private fields
		ReflectionUtil.setObject(null,obj[24],OpenGlHelper.class, MCPMappings.getFeildName("field_153212_w"));
		ReflectionUtil.setObject(null,obj[25], OpenGlHelper.class, MCPMappings.getFeildName("field_153213_x"));
		ReflectionUtil.setObject(null,obj[26], OpenGlHelper.class, MCPMappings.getFeildName("field_153214_y"));
		ReflectionUtil.setObject(null,obj[27], OpenGlHelper.class, MCPMappings.getFeildName("field_153215_z"));
		ReflectionUtil.setObject(null,obj[28], OpenGlHelper.class, MCPMappings.getFeildName("openGL14"));
		ReflectionUtil.setObject(null,obj[29], OpenGlHelper.class, MCPMappings.getFeildName("field_153196_B"));
		if(lightmapUpdate)
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,OpenGlHelper.lastBrightnessX,OpenGlHelper.lastBrightnessY);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);//NEI
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);//NEI
//		GL11.glDisable(GL11.GL_TEXTURE_2D); //NEI had this but, it fucked up my lighting so it's commented out
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);//NEI
	}

}
