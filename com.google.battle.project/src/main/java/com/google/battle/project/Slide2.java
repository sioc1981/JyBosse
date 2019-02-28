package com.google.battle.project;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;

public class Slide2 implements Serializable{

	public Slide2 preced;
	public Slide2 suiv;
	
	
	
	Transition interestTransition;
	int bestScore = 0;
	
	public boolean horizontal = true;
	
	Photo photo1;
	Photo photo2;
	
	
	Set<String> tags = new HashSet<String>();
	
	public Slide2(Photo photo) {
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
	
	public void setBestScored(Slide2 slide) {
//		Set<String> commontags = new HashSet<>();
//				Collections.copy(commontags, transition.slide1.tags);
		Slide2 predSlide = (Slide2) SerializationUtils.clone(slide.preced);
		Slide2 nextSlide = (Slide2) SerializationUtils.clone(slide.suiv);
		
		int score1 = getScore(predSlide);
		if(score1 > bestScore) {
			bestScore = score1;
			suiv = slide.preced;
		}
		
		int score2 = getScore(nextSlide);
		if(score2 > bestScore) {
			bestScore = score2;
			preced = slide.suiv;
		}
	}


	private int getScore(Slide2 slide) {
		Slide2 slideCopyCommon = (Slide2) SerializationUtils.clone(slide);
		Slide2 slideCopy = (Slide2) SerializationUtils.clone(slide);
		Slide2 thisCopy = (Slide2) SerializationUtils.clone(this);
		
		slideCopyCommon.tags.retainAll(tags);
		int common = slideCopyCommon.tags.size();
		
		slideCopy.tags.removeAll(slideCopyCommon.tags);
		int unique = slideCopy.tags.size();
		
		thisCopy.tags.removeAll(slideCopyCommon.tags);
		int thisUnique = thisCopy.tags.size();
		
		
		
		return Math.min(Math.min(common, unique), thisUnique);
	}
}
