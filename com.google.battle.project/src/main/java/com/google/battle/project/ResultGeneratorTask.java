package com.google.battle.project;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ResultGeneratorTask {
	static final int DUPLICATED_PHOTO_ID = -1;

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
		if(slides.size() % 100 == 0) {
			System.out.println("slides to process: " + initSlides.size());
		}
	}




	protected ResultGeneratorTask compute() {
//		System.out.println("compute");
		Slide sladeA = null;
		SlideTransition startingTransition = null;
    	//if(startSlide != null && ! startSlide.transitions.isEmpty()) {
	    	//startingTransition = startSlide.transitions.pollLast();
	    	//startingTransition = startSlide.transitions.pollLast();
		if(startSlide != null) {
	    	startingTransition = App.findBest(startSlide, startSlide.tags.stream().flatMap(t -> App.tags.get(t).stream()).collect(Collectors.toSet()));
	    	if(startingTransition != null ) {
		    	sladeA = startingTransition.slide2 == startSlide ? startingTransition.slide1 : startingTransition.slide2;
		    	if(sladeA.equals(endSlide)) {
			        if(startingTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || startingTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
			        	System.out.println("" +slides.size()/2+ " start Transition :" + startingTransition);
			        }
	//	    		System.out.println("start slide " + startSlide);
	//	    		System.out.println("conflict with ending slide " + endSlide);
	//	    		System.out.println("bad start transition " + startingTransition);
		    		App.transitions.remove(startingTransition);
					startingTransition.disable();
					sladeA = null;
					
		    		startingTransition = App.findBest(startSlide, startSlide.tags.stream().flatMap(t -> App.tags.get(t).stream()).collect(Collectors.toSet()));
		    		if(startingTransition != null ) {
		    			sladeA = startingTransition.slide2 == startSlide ? startingTransition.slide1 : startingTransition.slide2;
		    		}
		    		System.out.println("new start transition " + startingTransition);
		    	}
	    	}
	    	
	        final int index = startSlide.photo1.index;
			Set<SlideTransition> transitions = App.transitions.stream().filter(t -> t.slide1.photo1.index == index || t.slide2.photo1.index == index).collect(Collectors.toSet());

			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}

		}
    	Slide sladeB = null;
    	SlideTransition endTransition = null;
    	if(endSlide != null ) {
//	        endTransition = endSlide.transitions.pollLast();
	        endTransition = App.findBest(endSlide, endSlide.tags.stream().flatMap(t -> App.tags.get(t).stream()).collect(Collectors.toSet()));
	        if(endTransition != null) {
		        sladeB = endTransition.slide2 == endSlide ? endTransition.slide1 : endTransition.slide2;
		        while (endTransition != null && (sladeB.equals(startSlide) || sladeB.equals(sladeA))) {
	//	        	System.out.println("ending slide " + startSlide);
	//	        	System.out.println("conflict with start slide " + startSlide);
	//	    		System.out.println("bad end transition " + endTransition);
			        if(endTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
			        	System.out.println("" +slides.size()/2+ " end Transition :" + endTransition);
			        	System.out.println("" +slides.size()/2+ " sladeB :" + sladeB);
			        }
	//	        	if(!App.transitions.remove(endTransition)) System.out.println(" cannot remove end transition " + endTransition);
		        	App.transitions.remove(endTransition);
			        if(endTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
			        	System.out.println("" +slides.size()/2+ " Transition removed :" + !App.transitions.contains(endTransition));
			        }
		    		endTransition.disable();
		    		sladeB = null;
		    		endTransition = App.findBest(endSlide, endSlide.tags.stream().flatMap(t -> App.tags.get(t).stream()).collect(Collectors.toSet()));
		    		if(endTransition != null) {
		    			sladeB = endTransition.slide2 == endSlide ? endTransition.slide1 : endTransition.slide2;
		    		}
		    		System.out.println("new end transition " + endTransition);
		    	}
	    	}
	        final int index = endSlide.photo1.index;
			Set<SlideTransition> transitions = App.transitions.stream().filter(t -> t.slide1.photo1.index == index || t.slide2.photo1.index == index).collect(Collectors.toSet());

			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}

    	}

    	if(sladeA != null) {
	        if(startingTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || startingTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
	        	System.out.println("" +slides.size()/2+ " start Transition :" + startingTransition);
	        }
	        startingTransition.disable();
//	    	if(!App.transitions.remove(startingTransition)) System.out.println(" cannot remove starting transition " + startingTransition);
	    	App.transitions.remove(startingTransition);
//	        final int index = sladeA.photo1.index;
//			Set<SlideTransition> transitions = App.transitions.stream().filter(t -> t.slide1.photo1.index == index || t.slide2.photo1.index == index).collect(Collectors.toSet());
//
//			for (SlideTransition transition : transitions) {
//	        	transition.disable();
//	        	App.transitions.remove(transition);
//			}
//			System.out.println("slideA: " + sladeA);
	        slides.add(0,sladeA);
	        initSlides.remove(sladeA);
    	}
    	if(sladeB != null) {
	        if(endTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
	        	System.out.println("" +slides.size()/2+ " end Transition :" + endTransition);
	        	System.out.println("" +slides.size()/2+ " sladeB :" + sladeB);
	        }
	        endTransition.disable();
//	        if(!App.transitions.remove(endTransition)) System.out.println(" cannot remove end transition " + endTransition);
	        App.transitions.remove(endTransition);
	        if(endTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
	        	System.out.println("" +slides.size()/2+ " Transition removed :" + !App.transitions.contains(endTransition));
	        	System.out.println("" +slides.size()/2+ " Transition removed :" + App.transitions.stream().filter(t -> t.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || t.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID).collect(Collectors.toSet()));

	        }
//	        final int index = sladeB.photo1.index;
//			Set<SlideTransition> transitions = App.transitions.stream().filter(t -> t.slide1.photo1.index == index || t.slide2.photo1.index == index).collect(Collectors.toSet());
//			for (SlideTransition transition : transitions) {
//	        	transition.disable();
//	        	App.transitions.remove(transition);
//			}
	        if(endTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID) {
	        	System.out.println("" +slides.size()/2+ " Transition removed :" + !App.transitions.contains(endTransition));
	        	System.out.println("" +slides.size()/2+ " Transition removed :" + App.transitions.stream().filter(t -> t.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || t.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID).collect(Collectors.toSet()));

	        }
//	        System.out.println("sladeB: " + sladeB);
	        slides.add(sladeB);
	        initSlides.remove(sladeB);
//	        if(!initSlides.remove(sladeB)) System.out.println(" cannot remove end slide " + sladeB);
    	}
    	if(sladeA != null || sladeB != null) {
    		return  new ResultGeneratorTask(sladeA,sladeB,slides,initSlides);
    	}
    	return null;
   }



	
}
