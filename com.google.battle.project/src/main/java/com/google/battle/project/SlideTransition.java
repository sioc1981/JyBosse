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
		return result;
	};
	
	public void disable() {
		slide1.transitions.remove(this);
        slide2.transitions.remove(this);
	}

	@Override
	public String toString() {
		return "SlideTransition [slide1=" + slide1 + ", slide2=" + slide2 + ", score=" + score + "]";
	}

	
}
