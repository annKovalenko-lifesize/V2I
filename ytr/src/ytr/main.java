package ytr;

import java.util.ArrayList;
import java.util.Arrays;

public class main {
	
	public static void main(String[] args) {
		
		int[] s = {1,3,5,4,7,10,6};
		
		System.out.println(localMaxima(s));
		
		
	}
	
	//1.4

	public static String replace(String s) {
		
		char[] str = s.toCharArray();
		int index = 0;
		ArrayList<Character> newArr = new ArrayList<>();
		for(int i = 0; i< s.length(); i++) {
			
			if((int)str[i] !=32) {
				newArr.add(index, str[i]);
				index++;
			}
			else {
				newArr.add(index, '%');
				newArr.add((index+1), '2');
				newArr.add((index+2), '0');
				index += 3;
			}
			
		}
		String finStr = "";
		for(int i = 0; i<newArr.size(); i++) {
			finStr += (newArr.get(i).charValue());
		}
		
		
		return finStr;
	}
	
	//1.5
	
	public static String compress(String s) {
		
		int count = 1;
		StringBuffer st = new StringBuffer();
		for(int i = 1; i < s.length(); i++) {
			char next = s.charAt(i);
			char current = s.charAt(i-1);
			if(current == next) {
				count++;
			}
			else {
				st.append(current);
				st.append(count);
				count = 1;
			}	
		}
		if(s == null || s.isEmpty() || s.length() <= st.length()) {
			return s;
		}
		else {
			return st.toString();
		}
	}
	
	public static ArrayList<Integer> localMaxima(int[] arr) {
		
		ArrayList<Integer> maximas = new ArrayList<>();
		if(arr[0] > arr[1]) {
			maximas.add(arr[0]);
		}
		
		int prev = 0;
		int next = 0;
		int curr = 0;
		for(int i = 1; i < arr.length-1; i++ ) {
			prev = arr[i-1];
			next = arr[i+1];
			curr = arr[i];
			if(prev < curr && next < curr) {
				maximas.add(curr);
			}
		}
		if(arr[arr.length-1] > arr[arr.length - 2]) {
			maximas.add(arr[arr.length-1]);
		}
		
		return maximas;
	}
	
}
