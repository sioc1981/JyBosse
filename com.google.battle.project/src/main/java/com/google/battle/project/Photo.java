package com.google.battle.project;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Photo {

	public Photo(String line, int index) {
		String[] items = line.split(" ");
		List<String> list = Arrays.asList(items);
		horizontal = "H".equals(list.get(0));
		//int tagsCount = Integer.valueOf(list.remove(0));
		this.index = index;
		tags  = list.stream().skip(1).collect(Collectors.toList());
	}
	
	// H or V
	public boolean horizontal;
	public int index;
	public List<String> tags;
	public boolean used = false;
}
