package cn.edu.zju.acm.onlinejudge.util;

import java.util.Random;

public class RandomStringGenerator {
	
	private static Random random = new Random(System.currentTimeMillis()); 
	public static String generate(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; ++i) {
			int k = random.nextInt(62);
			if (k < 10) {
				sb.append((char)('0' + k));
			} else if (k < 36) {
				sb.append((char)('A' + k - 10));
			} else {
				sb.append((char)('a' + k - 36));
			}
		}
		
		return sb.toString();
	}
	
	public static String generate() {
		return generate(32);
	}
}
