package com.google.battle.project;

import java.util.List;
import java.util.TreeSet;

public class ResultGeneratorTask {
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
    	if(startSlide != null && ! startSlide.transitions.isEmpty()) {
	    	SlideTransition startingTransition = startSlide.transitions.pollLast();
	    	App.transitions.remove(startingTransition);
			startingTransition.disable();
			TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>(startSlide.transitions);
			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}
			sladeA = startingTransition.slide2 == startSlide ? startingTransition.slide1 : startingTransition.slide2;
//			System.out.println("slideA: " + sladeA);
	        slides.add(0,sladeA);
	        initSlides.remove(sladeA);
    	}
    	Slide sladeB = null;
    	if(endSlide != null && ! endSlide.transitions.isEmpty()) {
	        SlideTransition endTransition = endSlide.transitions.pollLast();
	        App.transitions.remove(endTransition);
	        endTransition.disable();
			TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>(endSlide.transitions);
			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}
	        sladeB = endTransition.slide2 == endSlide ? endTransition.slide1 : endTransition.slide2;
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
