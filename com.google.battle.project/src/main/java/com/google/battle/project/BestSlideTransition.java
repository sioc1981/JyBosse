package com.google.battle.project;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicReference;

public class BestSlideTransition extends RecursiveTask<ConcurrentSkipListSet<SlideTransition>>  {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1265374803087505856L;
//	private Slide slide;
	private Slide newSlide;
	private int start;
	private int limit;
	private Collection<Slide> slides;
	

	public BestSlideTransition( Slide newSlide, Collection<Slide> slides,int start, int limit) {
		super();
		this.slides = slides;
		this.newSlide = newSlide;
		this.start = start;
		this.limit = limit;
		
	}

	protected ConcurrentSkipListSet<SlideTransition> compute() {
		AtomicReference<SlideTransition> best = new AtomicReference<SlideTransition>();
		final ConcurrentSkipListSet<SlideTransition> transitions = new ConcurrentSkipListSet<SlideTransition>();
		slides.stream().skip(start*limit).limit(limit).forEach(slide -> {
			int score = App.getScore(slide, newSlide);
			if(score > 0 ) {
				if (best.get() == null || best.get().score < score) {
					SlideTransition slideTransition = new SlideTransition(slide, newSlide, score);
					transitions.clear();
					transitions.add(slideTransition);
					best.set(slideTransition);
				} else if (best.get().score == score) {
					SlideTransition slideTransition = new SlideTransition(slide, newSlide, score);
					transitions.add(slideTransition);
				}
			} 
		});
		return transitions;
   }

	@Override
	public String toString() {
		return "BestSlideTransition [newSlide=" + newSlide + ", start=" + start + ", limit=" + limit + ", slides="
				+ slides + "]";
	}
	
	

}
