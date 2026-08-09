package br.com.nicolae.restgenerator.generator.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.Strings;

public class EnvUtil {
	
	public static String getText(String text) {
		if(Strings.CS.startsWith(text, "[") && Strings.CS.endsWith(text, "]")) {
			String[] randomText = text.replace("[", "").replace("]", "").split(",");
			
			text = randomText[RandomUtils.insecure().randomInt(0, randomText.length - 1)];
		}
		
		return text;
	}
	
}
