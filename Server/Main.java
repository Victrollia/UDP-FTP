package Server;                  //Student: Victoria Cadogan

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import Common.MyLogger;
import Common.Packet;
import Common.PacketType;
import Common.Settings;
import Server.UDP;
import Server.PacketManager;
import Server.Sender;

public class Main {
	public static void main(String[] args) {
		// Create a new logger
		MyLogger logger = new MyLogger("Server.main");
		
		// Log that we're starting
		logger.info("Starting");
		
		// Create a Packet to be used when we receive data from the client
		Packet receivedPacket;
		
		// Create some settings that can be used when the server is started.  If it has a "1",
		// then skip the first packet intentionally introducing some errors.  If it is a "2" then
		// skip all packets.
		if (args.length > 0) {
			if (args[0].equals("1"))
				Settings.SKIP_FIRST_PACKET = true;
			else if (args[0].equals("2"))
				Settings.SKIP_ALL_PACKETS = true;
		}
		
		// Dump some setting information
		logger.info("Skipping First Packet in Window: " + Settings.SKIP_FIRST_PACKET);
		logger.info("Skipping All Packets in Window:  " + Settings.SKIP_ALL_PACKETS);
		
		// We need a way to loop getting data and when we're done know to stop.  So create
		// a boolean for that purpose
		// hint: boolean ...
        boolean finished = false;
		try {
			// Create a variable to hold the file sender object but initialize it to null
			Sender sender = null;
			
         while(!finished){
         UDP udpSocket = new UDP();
         udpSocket.createService();
         
			// Loop while that boolean we created is true.  When we're done it will be false and
			// we can cleanup
			//while (...) {
				// Create a new udp socket from the UDP class
				// hint: UDP udpSocket = new ...
			
				// Create the service for the udpSocket
				// ...
			
				try {
					// Create a byte array buffer and initialize it to the udpSocket's packet with a timeout
					// of 0.
					// hint: byte[] buf ...
					byte[] buf = udpSocket.getPacket(0);
                    
					// convert the buf into a new Packet and store it in the receivedPacket
					// hint: receivedPacket = ...
					receivedPacket = new Packet(buf);
					// If the received packet's type is not a request (REQ), then generate an error
					// if (...) {
                  	if (receivedPacket.getType() != PacketType.REQ) {
						(new PacketManager(udpSocket)).send(PacketType.ERR,
								"Received " + PacketType.toString(receivedPacket.getType()) + 
								", not sure what that command does at this stage.");
						continue;
					}
				} catch (SocketTimeoutException e) {
					continue;
				}
				
				// Create an actual Sender object from the udp socket
				//sender = 
				sender = new Sender(udpSocket);
           
				try {
					// Tell the sender object to send the file!  To do this, get the data from
					// the receivePacket.  This will get a byte array.  Convert that to a new String,
					// and pass that to the sendFile method in the sender object.
					// ...
                    String file = new String(receivedPacket.getData());
					sender.sendFile(file);
                    udpSocket.close();
					// when done, close the udpSocket
					// ...
					finished = true;
					// Set the loop variable to False to stop looping
					// ...				
				} catch (IOException e) {
					// Ok, there was an exception here.  Create a packet object and send the error.
					// Get an ACK from the client that the error was received and close the socket.
					PacketManager pm = new PacketManager(udpSocket);
					pm.send(PacketType.ERR, "No such file or permission denied.");
                    pm.processAck();
                    udpSocket.close();
					// pm....
					// close socket
				}
				
			}
		// Process/catch/print other errors
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongPacketTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
