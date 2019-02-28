package com.google.battle.project;

import java.util.HashSet;
import java.util.Set;

public class Slide {

	public boolean horizontal = true;
	
	Photo photo1;
	Photo photo2;
	
	
	Set<String> tags = new HashSet<String>();
	
	public Slide(Photo photo) {
		photo1 = photo;
		horizontal = photo.horizontal;
	}

	public boolean isFull() {
		return photo1 != null && photo2 != null;
	}
	
	public Set<String> getTags() {
		if(photo1 != null)
			tags.addAll(photo1.tags);
		
		if(photo1 != null)
			tags.addAll(photo2.tags);

			return tags;
		}

	public void addSecondPhoto(Photo photo) {
		photo2 = photo;
	}
}
