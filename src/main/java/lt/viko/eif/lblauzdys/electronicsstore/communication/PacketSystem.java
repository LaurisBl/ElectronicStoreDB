package lt.viko.eif.lblauzdys.electronicsstore.communication;

import lt.viko.eif.lblauzdys.electronicsstore.Main;
import lt.viko.eif.lblauzdys.electronicsstore.db.DBLoader;
import lt.viko.eif.lblauzdys.electronicsstore.model.Computer;
import lt.viko.eif.lblauzdys.electronicsstore.model.Shelf;
import lt.viko.eif.lblauzdys.electronicsstore.model.Store;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;


/**
 * PacketSystem class communicates between the client and the server.
 *
 * @author laurynas.blauzdys@stud.viko.lt
 * @see Main
 * @since 1.0
 */


public class PacketSystem {

	public void SendPacket() { //send a packet of data over TCP
		ServerSocket server = null;

		try {
			System.out.println();
			System.out.println("Starting TCP server on port 9094...");

			// Initialize the server socket
			try {
				server = new ServerSocket(9094);

				System.out.println("Server successfully started, awaiting connection...");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// Accept a client connection
			Socket socket = server.accept();

			System.out.println("Client connected, creating data...");

			// Create a Store object and populate it with Shelves and Computers
			Store store = new Store("LaurisGeneral", "Old town");

			Shelf shelf1 = new Shelf(1);
			Shelf shelf2 = new Shelf(2);
			Shelf shelf3 = new Shelf(3);
			Shelf shelf4 = new Shelf(4);

			Computer computer1 = new Computer("ThinkPad X1 Carbon", "Lenovo", "Business Laptop", "i7 Processor, 16GB RAM, 512GB SSD", 1499.99f);
			Computer computer2 = new Computer("MacBook Air", "Apple", "Everyday Use", "M1 Chip, 8GB RAM, 256GB SSD", 999.00f);
			Computer computer3 = new Computer("XPS 13", "Dell", "Ultrabook", "i7 Processor, 16GB RAM, 512GB SSD", 1399.99f);
			Computer computer4 = new Computer("ROG Strix G15", "Asus", "Gaming Laptop", "RTX 3070 GPU, Ryzen 7 Processor, 16GB RAM, 1TB SSD", 1799.99f);
			Computer computer5 = new Computer("Surface Laptop Studio", "Microsoft", "Content Creation", "i7 Processor, 16GB RAM, 1TB SSD", 1599.99f);
			Computer computer6 = new Computer("Chromebook Spin 713", "Acer", "Budget Laptop", "i3 Processor, 8GB RAM, 128GB SSD", 649.00f);
			Computer computer7 = new Computer("Mac Mini", "Apple", "Home Office", "M1 Chip, 8GB RAM, 256GB SSD", 699.00f);
			Computer computer8 = new Computer("IdeaCentre AIO 3", "Lenovo", "All-in-One PC", "i5 Processor, 8GB RAM, 512GB SSD", 799.99f);

			// Add computers to shelves
			shelf1.getComputers().add(computer1);
			shelf1.getComputers().add(computer2);

			shelf2.getComputers().add(computer3);
			shelf2.getComputers().add(computer4);

			shelf3.getComputers().add(computer5);
			shelf3.getComputers().add(computer6);

			shelf4.getComputers().add(computer7);
			shelf4.getComputers().add(computer8);

			// Add shelves to the store
			store.getShelves().add(shelf1);
			store.getShelves().add(shelf2);
			store.getShelves().add(shelf3);
			store.getShelves().add(shelf4);

			// Prepare to send data to the client
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// Marshal the store object to XML
			StringWriter data = new StringWriter(); // create output
			JAXBContext context = JAXBContext.newInstance(Store.class); // set up serializer
			Marshaller marshaller = context.createMarshaller(); // initalize serializer
			marshaller.marshal(store, data); // convert the class to xml

			System.out.println("Data created, validating...");

			// Validate the XML data
			if (!validate(data.toString())) {
				System.out.println("Data validation failed, exiting...");
				System.exit(1);
			}

			System.out.println("Data validated, sending packet...");

			// Send the data to the client
			out.println(data.toString());

			System.out.println("Packet sent successfully!");

			// Close the socket and server
			socket.close();

			server.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Store FetchPacket() { //fetch a packet of data over TCP
		System.out.println();
		System.out.println("Connecting to TCP socket on port 9094...");

		Socket socket;

		// Attempt to connect to the server
		try {
			socket = new Socket("localhost", 9094);
		} catch (Exception e) {
			System.out.println("Exception has occurred whilst attempting to connect! Attaching exception...");
			System.out.println(e.toString());

			return null;
		}

		System.out.println("Socket successfully connected! Listening for data");

		String data = "";

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Read data from the server
		try {
			int a;

			while ((a = in.read()) != -1) data += (char) a;
		} catch (Exception e) {
			System.out.println("Finished reading data. Starting to parse");
		}

		System.out.println("Data received:");
		System.out.println(data);

		Store store = null;

		// Validate the received data
		if (!validate(data)) {
			System.exit(1);
		}

		// Unmarshal the data back to a Store object
		try {
			JAXBContext context = JAXBContext.newInstance(Store.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			store = (Store) unmarshaller.unmarshal(new ByteArrayInputStream(data.getBytes()));
		} catch (Exception e) {
			System.out.println("Exception has occurred whilst reading data! Attaching exception...");
			System.out.println(e.toString());
		}

		// Close the socket
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("Saving to DB:");
		DBLoader.SaveStore(store);

		return store;
	}

	private boolean validate(String xmlFile) { //validate the XML data against the schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new File(getResource("schema.xsd")));

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(xmlFile)));
			return true;
		} catch (SAXException | IOException e) {
			e.printStackTrace();

			System.out.println("XML validation failed... Transfer terminating");
			return false;
		}
	}

	private String getResource(String filename) throws FileNotFoundException { //get the resource file path
		URL resource = getClass().getClassLoader().getResource(filename);
		Objects.requireNonNull(resource);

		return resource.getFile();
	}
}
