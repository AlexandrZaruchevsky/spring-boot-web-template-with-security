package ru.az.sandbox.security.utils;

public enum StrUtil {

	;
	
	public static String[] getFioFromStr(String str) {
		String[] split = str.split("\\s+");
		String[] fio = {"", "", ""};
		if (split.length > 0) {
			fio[0] = split[0];
		}
		if (split.length > 1) {
			fio[1] = split[1];
		}
		if (split.length > 2) {
			fio[2] = split[2];
		}
		return fio;
	}
	
}
