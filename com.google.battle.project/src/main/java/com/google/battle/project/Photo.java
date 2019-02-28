package com.google.battle.project;

import java.util.List;

public class Photo {
	public enum Format {
		H,V;
	}
	
	final int id;
	
	final Format format;
	
	final List<String> tags;

	public Photo(int id, Format format, List<String> tags) {
		super();
		this.id = id;
		this.format = format;
		this.tags = tags;
	}

	public int getId() {
		return id;
	}

	public Format getFormat() {
		return format;
	}

	public List<String> getTags() {
		return tags;
	}
	
	
	
}
