/* MULTITHREADING Theater.java
 * EE422C Project 6 submission by
 * Daniel Canterino
 * djc3323
 * 15460
 * Slip days used: <0>
 * Spring 2018
 */
package assignment6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Daniel Canterino
 * @version 1.0
 * This class creates a Theater
 * contains the subclasses BoxOffice, Seat, and Ticket
 */
public class Theater {
	private int numOfRows;
	private int numSeatsInRows;
	private String theaterShow;
	private Integer bestRow;
	private Integer bestSeatInRow;
	private boolean theaterIsFull;
	private List<Ticket> transactionLog = new ArrayList<Ticket>();
	private int clientNum;
	
	/**
	 * @author Daniel Canterino
	 * @version 1.0
	 * This BoxOffice is a subclass of the theater representing a box office which can sell tickets to the theater
	 * It implements Runnable for multithreading so each box office can act as its own thread
	 */
	static class BoxOffice implements Runnable{
		private String officeId;
		private int numOfClients;
		private Theater itsTheater;
		private ReentrantLock lock = new ReentrantLock();
		
		public BoxOffice (Theater theater, String boxOfficeId, int clients) {
			officeId = boxOfficeId;
			numOfClients = clients;
			itsTheater = theater;
		}
		
		public String getOfficeId() {
			return officeId;
		}
		
		public int getNumClients(){
			return numOfClients;
		}
		
		/**
		 * Run for the threads representing the box offices
		 * goes through each client in line at the box office and services them with a ticket if available
		 * each thread locks the reentry lock before creating the new seat
		 * synchronizes the theater as well to update the information
		 * unlocks the lock after creating the new seat so other threads can reserve a seat while this one concludes processing
		 */
		public void run() {
			for (int i = 0; i < getNumClients(); i++) {
				lock.lock();
				synchronized(itsTheater) {
					Theater.Seat newSeat = itsTheater.bestAvailableSeat();
					lock.unlock();
					if (newSeat == null) {
						break;
					}else {
						itsTheater.printTicket(getOfficeId(), newSeat, itsTheater.clientNum);
						itsTheater.clientNum++;
						if (itsTheater.theaterIsFull) {
							System.out.println("Sorry, we are sold out!");
							break;
						}
					}
				}
			}
		}
	}
	
	/*
	 * Represents a seat in the theater
	 * A1, A2, A3, ... B1, B2, B3 ...
	 */
	/**
	 * @author Daniel Canterino
	 * @version 1.0
	 * This Seat is a subclass of the theater representing a seat in the theater
	 */
	static class Seat {
		private int rowNum;
		private int seatNum;

		public Seat(int rowNum, int seatNum) {
			this.rowNum = rowNum;
			this.seatNum = seatNum;
		}

		public int getSeatNum() {
			return seatNum;
		}

		public int getRowNum() {
			return rowNum;
		}

		/*
		 * treats the row number as a base 26 number with 1 = A
		 * @return a string representation of the seat A1, B1...AA1
		 */
		@Override
		public String toString() {
			String string = new String();
			string = toAlpha(rowNum - 1);
			string += seatNum;
			return string;
		    
		}
		
		/*
		 * recursive helper function for the seat toString method that recursively solves for the alpha representation of the number
		 * base 26 converter essentially
		 */
		private String toAlpha(int i) {
		    int quot = i / 26;
		    int rem = i % 26;
		    char letter = (char)((int)'A' + rem);
		    if( quot == 0 ) {
		        return "" + letter;
		    } else {
		        return toAlpha(quot - 1) + letter;
		    }
		}
		
	}

  /*
	 * Represents a ticket purchased by a client
	 */
	/**
	 * @author Daniel Canterino
	 * @version 1.0
	 * This Ticket is a subclass of Theater and represents a ticket sold at the theater
	 */
	static class Ticket {
		private String show;
		private String boxOfficeId;
		private Seat seat;
		private int client;

		public Ticket(String show, String boxOfficeId, Seat seat, int client) {
			this.show = show;
			this.boxOfficeId = boxOfficeId;
			this.seat = seat;
			this.client = client;
		}

		public Seat getSeat() {
			return seat;
		}

		public String getShow() {
			return show;
		}

		public String getBoxOfficeId() {
			return boxOfficeId;
		}

		public int getClient() {
			return client;
		}

		/*
		 * returns a string representation of the ticket
		 * @returns a string that looks like a ticket would
		 */
		@Override
		public String toString() {
			String ticket = new String();
			for (int i = 0; i < 31; i++) {
				ticket += "-";
			}
			ticket += "\n";
			ticket += "| Show: " + getShow();
			for (int i = 8 + getShow().length(); i < 30; i++) {
				ticket += " ";
			}
			ticket += "|";
			ticket += "\n";
			ticket += "| Box Office ID: " + getBoxOfficeId();
			for (int i = 17 + getBoxOfficeId().length(); i < 30; i++) {
				ticket += " ";
			}
			ticket += "|";
			ticket += "\n";
			ticket += "| Seat: " + getSeat().toString();
			for (int i = 8 + getSeat().toString().length(); i < 30; i++) {
				ticket += " ";
			}
			ticket += "|";
			ticket += "\n";
			ticket += "| Client: " + getClient();
			Integer client = getClient();
			for (int i = 10 + client.toString().length(); i < 30; i++) {
				ticket += " ";
			}
			ticket += "|";
			ticket += "\n";
			for (int i = 0; i < 31; i++) {
				ticket += "-";
			}
			ticket += "\n";
			return ticket;
		}
	}
	
	/*
	 * constructer for a theater
	 * initializes the best row and seat to 1, 1
	 * initializes the number of clients served to 1 also
	 * intializes the theater is full boolean to false
	 */
	public Theater(int numRows, int seatsPerRow, String show) {
		numOfRows = numRows;
		numSeatsInRows = seatsPerRow;
		theaterShow = show;
		bestRow = 1;
		bestSeatInRow = 1;
		clientNum = 1;
		theaterIsFull = false;
	}

	/*
	 * Calculates the best seat not yet reserved
	 *
 	 * @return the best seat or null if theater is full
   */
	public Seat bestAvailableSeat() {
		if (theaterIsFull) {
			return null;
		}else {
			Seat newSeat = new Seat(bestRow, bestSeatInRow);
			bestSeatInRow++;
			if (bestSeatInRow > numSeatsInRows) {
				bestSeatInRow = 1;
				bestRow ++;
				if (bestRow > numOfRows) {
					theaterIsFull = true;
				}
			}
			return newSeat;
		}
	}

	/*
	 * Prints a ticket for the client after they reserve a seat
   * Also prints the ticket to the console
	 *
   * @param seat a particular seat in the theater
   * @return a ticket
   */
	public Ticket printTicket(String boxOfficeId, Seat seat, int client) {
		Ticket newTicket = new Ticket (theaterShow, boxOfficeId, seat, client);
		transactionLog.add(newTicket);
		System.out.println(newTicket.toString());
		return newTicket;
	}

	/*
	 * Lists all tickets sold for this theater in order of purchase
	 *
   * @return list of tickets sold
   */
	public List<Ticket> getTransactionLog() {
		return transactionLog;
	}
	
	
	
}
