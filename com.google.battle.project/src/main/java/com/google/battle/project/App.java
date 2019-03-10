package com.google.battle.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String[] fileList = new String [] {
//            "a_example.txt"
//            ,
//            "b_lovely_landscapes.txt"
//            ,
//            "c_memorable_moments.txt"
//            ,
            "d_pet_pictures.txt"
//            ,
//            "e_shiny_selfies.txt"
    };
    
    public static int maxPhoto = 0;

    private static int totalScore = 0;
    
//    static TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>();
    static ConcurrentSkipListSet<SlideTransition> transitions = new ConcurrentSkipListSet<SlideTransition>();

    static HashMap<String,ConcurrentSkipListSet<Slide>> tags = new HashMap<>();
    
    static AtomicReference<SlideTransition> best = new AtomicReference<SlideTransition>();
    
    private static ExecutorService executor = Executors.newWorkStealingPool();
    
    public static void main( final String[] args )
    {
        for (final String fileIn : App.fileList) {
            final ClassLoader classLoader = App.class.getClassLoader();
            final File file = new File(classLoader.getResource(fileIn).getFile());
            try {
                final List<Photo> photos = loadPhotos(file);

                final List<Slide> slides = createSlides(photos);
                write(slides, fileIn);
            } catch (final IOException e) {
                System.out.println(e);
            }
        }
        System.out.println("Total score: " + App.totalScore);
        executor.shutdownNow();

    }
    private static List<Slide> createSlides(final List<Photo> myPhotos) {
    	transitions.clear();
    	List<Photo> photos = myPhotos;
        List<Slide> initSlides = new ArrayList<>();
        Slide slide = null;
//        do {
//            slide = findNextSlide(photos);
//            if(slide != null) {
//                addSlide(initSlides, slide);
//                //				  slides.add(slide);
//            }
//        } while (slide != null);

        do {
        	slide = findNextHorizontalSlide(photos);
        	if(slide != null) {
        		addSlide(initSlides, slide);
        		//				  slides.add(slide);
        	}
        } while (slide != null);
        
        photos = sortVerticalPhotos(photos);
        do {
        	slide = findNextVerticalSlide(photos);
        	if(slide != null) {
        		addSlide(initSlides, slide);
        	}
        } while (slide != null);
        System.out.println("best slide : " + best.get());
//        App.interests.keySet().forEach(k -> App.interests.get(k).set(App.interests.get(k).get()/2));
//        System.out.println("interests: " + App.interests);
//        StreamSupport.stream(Spliterators.spliterator(slides, Spliterator.SIZED), true).forEach(p -> p.interestScore = (int) p.tags.stream().mapToInt(t -> App.interests.get(t).get()).average().orElseThrow());
//
//        
//        slides.sort( (p1,p2) -> p1.interestScore - p2.interestScore);
//        
//        slides = parabolaSort(slides);
        List<Slide> slides = new ArrayList<>();
        while(best.get() != null) {
        	System.out.println("start transitions : " + transitions.size() + " at score " + best.get().score);
	        while(!transitions.isEmpty()) {
//	        	System.out.println(" Transition removed :" + App.transitions.stream().noneMatch(endTransition -> endTransition.slide1.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID || endTransition.slide2.photo1.index == ResultGeneratorTask.DUPLICATED_PHOTO_ID));
	        	final List<Slide> tmpSlides = new ArrayList<>();
		        SlideTransition startingTransition = transitions.pollLast();
		        System.out.println("init slide : " +startingTransition);
	//	        if(startingTransition.slide1.photo1.index == 226 || startingTransition.slide2.photo1.index == 226) {
	//	        	System.out.println("startingTransition :" + startingTransition);
	//	        }
				Slide startSlide = startingTransition.slide1;
				Slide endSlide = startingTransition.slide2;
				startingTransition.disable();
				tmpSlides.add(0,startSlide);
		        initSlides.remove(startSlide);
		        final int startIndex = startSlide.photo1.index;
				Set<SlideTransition> transitions = App.transitions.stream().filter(t -> t.slide1.photo1.index == startIndex || t.slide2.photo1.index == startIndex).collect(Collectors.toSet());
				for (SlideTransition transition : transitions) {
		        	transition.disable();
		        	App.transitions.remove(transition);
				}
		        tmpSlides.add(endSlide);
		        initSlides.remove(endSlide);
		        final int endIndex = endSlide.photo1.index;
				transitions = App.transitions.stream().filter(t -> t.slide1.photo1.index == endIndex || t.slide2.photo1.index == endIndex).collect(Collectors.toSet());
				for (SlideTransition transition : transitions) {
		        	transition.disable();
		        	App.transitions.remove(transition);
				}

		        ResultGeneratorTask task = new ResultGeneratorTask(startSlide, endSlide, tmpSlides, initSlides);
	//	        task.compute();
		        while (task != null) {
		        	task = task.compute();
	//	        	System.out.println(" while transitions : " + transitions.size());
		        }
		        slides.addAll(tmpSlides);

	        }
	        best.set(null);
	        System.out.println("remaning slides: " + initSlides.size());
        	final List<Slide> remainSlides = new ArrayList<>();
	        initSlides.forEach(s -> addSlide(remainSlides, s));
        }
        System.out.println("remaning slides: " + initSlides.size());
        slides.addAll(initSlides);
        int score = computeScore(slides);
        System.out.println("Score: " + score);
//        initSlides.forEach(s -> System.out.println(s));
        App.totalScore += score;
        return slides;
    }

    private static List<Photo> sortVerticalPhotos(List<Photo> photos) {
		return photos;
	}
	private static int computeScore(List<Slide> slides) {
    	int score = 0;
    	 Slide previousSlide = null;
    	 for (Slide slide : slides) {
    		 if(previousSlide != null) {
                 score += getScore(previousSlide, slide);
             }
    		 previousSlide = slide;
		}
		return score;
	}
	private static void addSlide(final List<Slide> slides, final Slide newSlide) {
		if(slides.size() % 100 == 0) {
			System.out.println("slides " + slides.size());
		}
		newSlide.tags.forEach(t -> tags.computeIfAbsent(t, a -> new ConcurrentSkipListSet<Slide>()).add(newSlide));
		
//		ForkJoinPool pool = ForkJoinPool.commonPool();
//		int limit = 499;
//		ArrayList<BestSlideTransition> tasks = new ArrayList<BestSlideTransition>();
//		for (int i = 0; i*limit  < slides.size(); i++) {
//			BestSlideTransition task = new BestSlideTransition(newSlide, slides, i, limit);
//			pool.submit(task);
//			tasks.add(task);
//		}
//		
//		tasks.forEach(t -> {
//			try {
//				SlideTransition s = t.get();
//				if(s != null && (best.get() == null || best.get().score < s.score) ){
//					best.set(s);
//				}
//			} catch (InterruptedException | ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		});
//		pool.shutdownNow();
		findBestAndInit(newSlide, slides);
//		
//		slides.stream().filter(s -> getScore(s, newSlide) > 0).forEach(slide -> {
//			SlideTransition slideTransition = new SlideTransition(slide, newSlide, getScore(slide, newSlide));
//			slide.transitions.add(slideTransition);
//			newSlide.transitions.add(slideTransition);
//			transitions.add(slideTransition);
//
//		});
        slides.add(newSlide);
    }
	
//	private static Future<Void> createTransition(final Slide slide, final Slide newSlide ) {
//		return (Future<Void>) executor.submit(() -> {
//	//		System.out.println("slide " + slide);
//			int score = getScore(slide, newSlide);
//			if(score > 0 ) {
//				SlideTransition slideTransition = new SlideTransition(slide, newSlide, score);
//	//			System.out.println("slideTransition " + slideTransition);
//				if(!slide.transitions.add(slideTransition)) System.out.println(" cannot add transition in slide " + slide);
//				if(!newSlide.transitions.add(slideTransition)) System.out.println(" cannot add transition in newSlide " + newSlide);
////				newSlide.transitions.add(slideTransition);
//				transitions.add(slideTransition);
//	//			System.out.println(transitions);
//			}
//		});
//	}
//	

    private static Slide findNextSlide(final List<Photo> photos) {
        Photo photoV = null;
        Slide slide2 = null;
        for (final Photo photo : photos) {
            if (photo.horizontal) {
                slide2 = new Slide(photo);
                break;
            } else {
                if (photoV == null) {
                    photoV = photo;
                } else {
                    slide2 =  new Slide(photoV, photo);
                    photoV = null;
                    break;
                }
            }
        }
        if (slide2 != null) {
            photos.remove(slide2.photo1);
            if (slide2.photo2 != null) {
                photos.remove(slide2.photo2);
            }
        }
        return slide2;
    }
    private static Slide findNextHorizontalSlide(final List<Photo> photos) {
        Slide slide2 = null;
        for (final Photo photo : photos) {
            if (photo.horizontal) {
                slide2 = new Slide(photo);
                break;
            }
        }
        if (slide2 != null) {
            photos.remove(slide2.photo1);
        }
    	return slide2;
    }
    private static Slide findNextVerticalSlide(final List<Photo> photos) {
    	Photo photoV = null;
    	Slide slide2 = null;
    	for (final Photo photo : photos) {
			if (photoV == null) {
				photoV = photo;
			} else {
				slide2 =  new Slide(photoV, photo);
				photoV = null;
				break;
			}
    	}
    	if (slide2 != null) {
    		photos.remove(slide2.photo1);
    		if (slide2.photo2 != null) {
    			photos.remove(slide2.photo2);
    		}
    		//slide2.interestScore = slide2.tags.stream().mapToInt(t -> App.interests.get(t).get()).sum();
    	}
    	return slide2;
    }
    
    
    private static List<Photo> loadPhotos(final File file) throws IOException {
        App.tags.clear();
        final AtomicInteger index = new AtomicInteger();
        final Scanner scanner = new Scanner(file);
        App.maxPhoto = Integer.valueOf(scanner.nextLine());
        
        List<Photo> result = new ArrayList<>();
        while(scanner.hasNextLine()) {
            result.add( new Photo(scanner.nextLine(),index.getAndIncrement()));
        }
//        result.stream().map(p->p.tags).forEach(s -> s.forEach(t -> App.interests.computeIfAbsent(t, a -> new AtomicInteger()).getAndIncrement()));
//        System.out.println("interests: " + App.interests);
//        //StreamSupport.stream(Spliterators.spliterator(result, Spliterator.SIZED), true).forEach(p -> p.interestScore = p.tags.stream().mapToInt(t -> App.interests.get(t).get()).sum());
//        // StreamSupport.stream(Spliterators.spliterator(result, Spliterator.SIZED), true).forEach(p -> p.interestScore
//        // = p.tags.size()/2);
//        System.out.println("Sorting: ");
//        //result.sort( (p1,p2) -> p1.interestScore - p2.interestScore);
//        
//        result = parabolaSort(result);
        //		  System.out.println("Photos: " + result);
        return result;
    }

    public static void write(final List<Slide> slides, final String name) {
        try (
                FileWriter fw = new FileWriter(name+".out");) {
            fw.write("" + slides.size());
            for (final Slide slide : slides) {
                fw.write('\n');
                fw.write("" + slide.photo1.index + (slide.photo2 != null ? " " + slide.photo2.index : "") );
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    public static int getScore(final Slide s1, final Slide s2 ) {
//        final AtomicInteger onlyS1 = new AtomicInteger();
//        final AtomicInteger common = new AtomicInteger();
//        final AtomicInteger onlyS2 = new AtomicInteger();
//        for (final String s : s1.tags) {
//            if(s2.tags.contains(s)) { 
//                common.getAndIncrement();
//            } else {
//                onlyS1.getAndIncrement();
//            }
//        }
//        for (final String s : s2.tags) {
//            if(!s1.tags.contains(s)) { 
//                onlyS2.getAndIncrement();
//            } 
//        }
//        return Math.min(Math.min(onlyS1.get(),common.get() ), onlyS2.get());
//    }
//
    public static int getScore(final Slide s1, final Slide s2 ) {
    	int common = 0;
    	for (final String s : s1.tags) {
    		if(s2.tags.contains(s)) { 
    			common++;
    		}
    	}
    	return Math.min(Math.min(s1.tags.size()-common,common), s2.tags.size()-common);
    }
    
//	private static <T> List<T> parabolaSort(List<T> values) {
//		List<T> parabolValues = new ArrayList<T>();
//		for (int i = 0; i < values.size(); i++) {
//			parabolValues.add(i/2, values.get(i));
//		}
//		return parabolValues;
//	}
    
	static SlideTransition findBestAndInit(final Slide slide, Collection<Slide> slides) {
//		System.out.println("What's best for " + slide + " in  " + slides);
		ForkJoinPool pool = ForkJoinPool.commonPool();
		int limit = 499;
		ArrayList<BestSlideTransition> tasks = new ArrayList<BestSlideTransition>();
		for (int i = 0; i*limit  < slides.size(); i++) {
			BestSlideTransition task = new BestSlideTransition(slide, slides, i, limit);
			pool.submit(task);
			tasks.add(task);
		}
		
		tasks.forEach(t -> {
			try {
				ConcurrentSkipListSet<SlideTransition> sl = t.get();
				if(!sl.isEmpty()) {
					if (App.best.get() == null || App.best.get().score < sl.first().score) {
						App.transitions.clear();
						App.transitions.addAll(sl);
						App.best.set(sl.first());
					} else if (App.best.get().score == sl.first().score) {
						App.transitions.addAll(sl);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		pool.shutdownNow();
		return App.best.get();
	}
	
	static SlideTransition findBest(final Slide slide, Collection<Slide> slides) {
//		System.out.println("What's best for " + slide + " in  " + slides);
		App.best.set(null);
		ForkJoinPool pool = ForkJoinPool.commonPool();
		int limit = 499;
		ArrayList<BestSlideTransition> tasks = new ArrayList<BestSlideTransition>();
		for (int i = 0; i*limit  < slides.size(); i++) {
			BestSlideTransition task = new BestSlideTransition(slide, slides, i, limit);
			pool.submit(task);
			tasks.add(task);
		}
		
		tasks.forEach(t -> {
			try {
				ConcurrentSkipListSet<SlideTransition> sl = t.get();
				if(!sl.isEmpty()) {
					if (App.best.get() == null || App.best.get().score < sl.first().score) {
						App.best.set(sl.first());
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		pool.shutdownNow();
		return App.best.get();
	}

}
