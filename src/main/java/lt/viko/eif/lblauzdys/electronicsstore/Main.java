package lt.viko.eif.lblauzdys.electronicsstore;

import lt.viko.eif.lblauzdys.electronicsstore.communication.PacketSystem;
import lt.viko.eif.lblauzdys.electronicsstore.model.Store;

import java.util.Scanner;

/**
 * Main class starts the system and starts the PacketSystem class.
 *
 * @author laurynas.blauzdys@stud.viko.lt
 * @see PacketSystem
 * @since 1.0
 */

public class Main {

	public static void main(String[] args) {
		System.out.println();
		System.out.println("Select operation:\n[0] Send packet\n[1] Fetch packet\n[2] Quit");
		System.out.print("Your selection: ");

		Scanner in = new Scanner(System.in);
		int operation = in.nextInt();

		if (operation == 0) { // send packet
			new PacketSystem().SendPacket();
		} else if(operation == 1)  {
			Store store = new PacketSystem().FetchPacket();

			System.out.println("Result:\n" + store);
		} else {
			return;
		}

		main(args);
	}
}