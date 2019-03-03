package com.google.battle.project;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Photo {

    public Photo(final String line, final int index) {
        final String[] items = line.split(" ");
        final List<String> list = Arrays.asList(items);
        horizontal = "H".equals(list.get(0));
        //int tagsCount = Integer.valueOf(list.remove(0));
        this.index = index;
        tags  = list.stream().skip(2).collect(Collectors.toCollection(TreeSet<String>::new));
    }

    // H or V
    public boolean horizontal;
    public int index;
    public TreeSet<String> tags;
    @Override
    public String toString() {
        return "Photo [horizontal=" + horizontal + ", index=" + index + ", tags=" + tags + "]";
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
		if (index != other.index)
			return false;
		return true;
	}

    
}
