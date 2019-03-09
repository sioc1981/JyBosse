package com.google.battle.project;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class Slide implements Comparable<Slide> {
	
	int interestScore = 0;
	
	Photo photo1;
	Photo photo2;
	
	final Set<String> tags = new TreeSet<String>();
	
	final ConcurrentSkipListSet<SlideTransition> transitions = new ConcurrentSkipListSet<SlideTransition>();
	
	public Slide(Photo photo) {
		photo1 = photo;
		tags.addAll(photo1.tags);
	}

	public Slide(Photo photo1, Photo photo2) {
		this.photo1 = photo1;
		this.photo2 = photo2;
		tags.addAll(photo1.tags);
		tags.addAll(photo2.tags);
	}

	@Override
	public String toString() {
		return "Slide [photo1=" + photo1.index + (photo2 == null ? "" : ", photo2=" + photo2.index) + ", tags=" + tags + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((photo1 == null) ? 0 : photo1.hashCode());
		result = prime * result + ((photo2 == null) ? 0 : photo2.hashCode());
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
		Slide other = (Slide) obj;
		if (photo1 == null) {
			if (other.photo1 != null)
				return false;
		} else if (!photo1.equals(other.photo1))
			return false;
		if (photo2 == null) {
			if (other.photo2 != null)
				return false;
		} else if (!photo2.equals(other.photo2))
			return false;
		return true;
	}

	@Override
	public int compareTo(Slide o) {
		int result = photo1.index - o.photo1.index;
		if(result == 0 && photo2 != null)
			result = photo2.index - o.photo2.index;
		return result;
	}
	
	
	
}
