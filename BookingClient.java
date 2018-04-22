/* MULTITHREADING BookingClient.java
 * EE422C Project 6 submission by
 * Daniel Canterino
 * djc3323
 * 15460
 * Slip days used: <0>
 * Spring 2018
 */
package assignment6;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.lang.Thread;

public class BookingClient {
  /*
   * @param office maps box office id to number of customers in line
   * @param theater the theater where the show is playing
   */
	private Map<String, Integer> boxOffice = new HashMap<String, Integer>();
	private Theater thisTheater;
  
	public BookingClient(Map<String, Integer> office, Theater theater) {
	  boxOffice = office;
	  thisTheater = theater;
  }

  /*
   * Starts the box office simulation by creating (and starting) threads
   * for each box office to sell tickets for the given theater
   *
   * @return list of threads used in the simulation,
   *         should have as many threads as there are box offices
   */
  
	public List<Thread> simulate() {
		Set<String> officeKeys = boxOffice.keySet();
	  	int i = 0;
	  	Thread[] threads = new Thread[boxOffice.size()];
	  	Theater.BoxOffice[] boxOffices = new Theater.BoxOffice[boxOffice.size()];
	  	List<Thread> thread = new LinkedList<Thread>();
	  	for (String s : officeKeys) {
	  		Theater.BoxOffice newOffice = new Theater.BoxOffice(thisTheater, s, boxOffice.get(s));
	  		boxOffices[i] = newOffice;
		  	threads[i] = new Thread(boxOffices[i]);
		  	threads[i].start();
		  	i++;
	  	}
	  	for (Thread t : threads) {
	  		try {
				t.join();
			} catch (InterruptedException e) {}
	  	}
	  	return thread;
	}
  
	public static void main(String args[]) {
		Map<String, Integer> boxOffice = new HashMap<String, Integer>();
		boxOffice.put("BX1", 3);
		boxOffice.put("BX3", 3);
		boxOffice.put("BX2", 4);
		boxOffice.put("BX5", 3);
		boxOffice.put("BX4", 3);
		Theater theater = new Theater(3, 5, "Ouija");
		BookingClient booking = new BookingClient(boxOffice, theater);
		List<Thread> threads = booking.simulate();
	}
  
}
