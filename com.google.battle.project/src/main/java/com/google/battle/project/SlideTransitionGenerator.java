package com.google.battle.project;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class SlideTransitionGenerator extends RecursiveAction  {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1265374803087505856L;
//	private Slide slide;
	private Slide newSlide;
	private int start;
	private int limit;
	private List<Slide> slides;
	

	public SlideTransitionGenerator( Slide newSlide, List<Slide> slides,int start, int limit) {
		super();
		this.slides = slides;
		this.newSlide = newSlide;
		this.start = start;
		this.limit = limit;
		
	}

	protected void compute() {
//		System.out.println("compute: start:" + start +" limit: " + limit);
//		System.out.println("compute: " + this);
		AtomicInteger skipCounter = new AtomicInteger();
		slides.stream().skip(start*limit).limit(limit).forEach(slide -> {
			int score = App.getScore(slide, newSlide);
			if(score > 0 ) {
				SlideTransition slideTransition = new SlideTransition(slide, newSlide, score);
				if(!slide.transitions.add(slideTransition)) System.out.println(" cannot add transition in slide " + slide);
				if(!newSlide.transitions.add(slideTransition)) System.out.println(" cannot add transition in newSlide " + newSlide);
				if(!App.transitions.add(slideTransition)) {
					System.out.println("" +App.transitions.contains(slideTransition) + " cannot add " + slideTransition );
					
				}
			} else
				skipCounter.incrementAndGet();
		});
//		System.out.println("skip transition: " + skipCounter.get());
//		System.out.println("End: start:" + start);
   }

	@Override
	public String toString() {
		return "SlideTransitionGenerator [newSlide=" + newSlide + ", start=" + start + ", limit=" + limit + ", slides="
				+ slides + "]";
	}
	
	

}
