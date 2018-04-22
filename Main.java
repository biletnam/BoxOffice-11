package assignment6;


public class Main {
	
	public static void main(String args[]) {
		Theater theater = new Theater(7, 1, "Rampage");
		for (int i = 0; i < 8; i++) {
			Theater.Seat newSeat = theater.bestAvailableSeat();
			if (newSeat != null) {
				theater.printTicket("BX1", newSeat, 4);
			}
		}
	}
}
