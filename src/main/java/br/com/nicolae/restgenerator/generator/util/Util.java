package br.com.nicolae.restgenerator.generator.util;

public class Util {
	
	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
}
