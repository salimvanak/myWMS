package uk.ltd.mediamagic.mywms.common;

import java.util.Comparator;

public class PositionComparator implements Comparator<String> {
	
	public static String[] splitOrderPosNumber(String s) {
		int i = s.lastIndexOf('-')+1;
		if (i < 0 || i >= s.length()) {
			return new String[] {s, "0"};
		}
		else {
			return  new String[] {s.substring(0, i), s.substring(i)};
		}
	}

	@Override
	public int compare(String p1, String p2) {
		String[] s1 = splitOrderPosNumber(p1);
		String[] s2 = splitOrderPosNumber(p2);

		int x = s1[0].compareTo(s2[0]);
		if (x != 0) return x;
		try {
			return Integer.compare(Integer.parseInt(s1[1]), Integer.parseInt(s2[1]));
		}
		catch (NumberFormatException e) {
			return p1.compareTo(p1);
		}
	}
	
}
