package com.google.battle.project;

public class SlideTransition implements Comparable<SlideTransition> {
	
	final Slide slide1, slide2;
	
	final int score;

	public SlideTransition(Slide slide1, Slide slide2, int score) {
		super();
		this.slide1 = slide1;
		this.slide2 = slide2;
		this.score = score;
	}

	@Override
	public int compareTo(SlideTransition o) {
		int result = this.score - o.score;
		if(result == 0)
			result = slide1.photo1.index - o.slide1.photo1.index;
		if(result == 0)
			result = slide2.photo1.index - o.slide2.photo1.index;
		if(result == 0 && slide1.photo2 != null)
			result = slide1.photo2.index - o.slide1.photo2.index;
		if(result == 0 && slide2.photo2 != null)
			result = slide2.photo2.index - o.slide2.photo2.index;
		return result;
	};
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + score;
		result = prime * result + ((slide1 == null) ? 0 : slide1.hashCode());
		result = prime * result + ((slide2 == null) ? 0 : slide2.hashCode());
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
		SlideTransition other = (SlideTransition) obj;
		if (score != other.score)
			return false;
		if (slide1 == null) {
			if (other.slide1 != null)
				return false;
		} else if (!slide1.equals(other.slide1))
			return false;
		if (slide2 == null) {
			if (other.slide2 != null)
				return false;
		} else if (!slide2.equals(other.slide2))
			return false;
		return true;
	}

	public void disable() {
		slide1.transitions.remove(this);
        slide2.transitions.remove(this);
        slide1.photo1.tags.forEach(t -> App.tags.get(t).remove(slide1));
        if(slide1.photo2 != null)
        	slide1.photo2.tags.forEach(t -> App.tags.get(t).remove(slide1));
        slide2.photo1.tags.forEach(t -> App.tags.get(t).remove(slide2));
        if(slide2.photo2 != null)
        	slide2.photo2.tags.forEach(t -> App.tags.get(t).remove(slide2));
	}

	@Override
	public String toString() {
		return "SlideTransition [slide1=" + slide1 + ", slide2=" + slide2 + ", score=" + score + "]";
	}

	
}
