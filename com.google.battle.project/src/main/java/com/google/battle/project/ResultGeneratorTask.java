package com.google.battle.project;

import java.util.List;
import java.util.TreeSet;

public class ResultGeneratorTask {
	private static final int DUPLICATED_PHOTO_ID = -1;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1265374803087505856L;

	private Slide startSlide;
	private Slide endSlide;
	private List<Slide> initSlides;
	private List<Slide> slides;
	

	public ResultGeneratorTask(Slide startSlide, Slide endSlide, List<Slide> slides, List<Slide> initSlides) {
		super();
		this.startSlide = startSlide;
		this.endSlide = endSlide;
		this.initSlides = initSlides;
		this.slides = slides;
	}




	protected ResultGeneratorTask compute() {
		Slide sladeA = null;
		SlideTransition startingTransition = null;
    	if(startSlide != null && ! startSlide.transitions.isEmpty()) {
	    	startingTransition = startSlide.transitions.pollLast();
	    	sladeA = startingTransition.slide2 == startSlide ? startingTransition.slide1 : startingTransition.slide2;
	    	if(sladeA.equals(endSlide)) {
//	    		System.out.println("start slide " + startSlide);
//	    		System.out.println("conflict with ending slide " + endSlide);
//	    		System.out.println("bad start transition " + startingTransition);
	    		App.transitions.remove(startingTransition);
				startingTransition.disable();
				
	    		startingTransition = startSlide.transitions.pollLast();
		    	sladeA = startingTransition.slide2 == startSlide ? startingTransition.slide1 : startingTransition.slide2;
//		    	System.out.println("new start transition " + startingTransition);
	    	}
    	}
    	Slide sladeB = null;
    	SlideTransition endTransition = null;
    	if(endSlide != null && ! endSlide.transitions.isEmpty()) {
	        endTransition = endSlide.transitions.pollLast();
	        sladeB = endTransition.slide2 == endSlide ? endTransition.slide1 : endTransition.slide2;
	        while ( sladeB.equals(startSlide) || sladeB.equals(sladeA)) {
//	        	System.out.println("ending slide " + startSlide);
//	        	System.out.println("conflict with start slide " + startSlide);
//	    		System.out.println("bad end transition " + endTransition);
	    		App.transitions.remove(endTransition);
	    		endTransition.disable();
				
	    		endTransition = endSlide.transitions.pollLast();
	    		sladeB = endTransition.slide2 == endSlide ? endTransition.slide1 : endTransition.slide2;
//	    		System.out.println("new end transition " + endTransition);
	    	}
    	}

    	if(sladeA != null) {
	        if(startingTransition.slide1.photo1.index == DUPLICATED_PHOTO_ID || startingTransition.slide2.photo1.index == DUPLICATED_PHOTO_ID) {
	        	System.out.println("" +slides.size()/2+ " start Transition :" + startingTransition);
	        }
	    	App.transitions.remove(startingTransition);
			startingTransition.disable();
			TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>(startSlide.transitions);
			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}
//			System.out.println("slideA: " + sladeA);
	        slides.add(0,sladeA);
	        initSlides.remove(sladeA);
    	}
    	if(sladeB != null) {
	        if(endTransition.slide1.photo1.index == DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == DUPLICATED_PHOTO_ID) {
	        	System.out.println("" +slides.size()/2+ " end Transition :" + endTransition);
	        	System.out.println("" +slides.size()/2+ " sladeB :" + sladeB);
	        }
	        App.transitions.remove(endTransition);
	        endTransition.disable();
			TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>(endSlide.transitions);
			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}
//	        System.out.println("sladeB: " + sladeB);
	        slides.add(sladeB);
	        initSlides.remove(sladeB);
    	}
    	if(sladeA != null || sladeB != null) {
    		return  new ResultGeneratorTask(sladeA,sladeB,slides,initSlides);
    	}
    	return null;
   }

	
}
