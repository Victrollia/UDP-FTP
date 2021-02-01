package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import Common.MyLogger;
import Common.Settings;

public class UDP {
	private MyLogger logger = new MyLogger("Server.UDP");
  	private DatagramSocket serverSocket;
	private int clientPort = 0;
	private InetAddress clientAddress;
	
	private static boolean restarted = false;
	private static int totalData = 0;
	private static int totalDataWithHdr = 0;
	public void createService() throws SocketException {

		logger.info(restarted ? "Res" : "S" + "starting Server on Port: " + Settings.PORT);
		restarted = true;
		// Set the restarted state to true because this has just started.
		// also create a DatagramSocket object for the server socket on the PORT in the settings file
		// ...  
		// ...
		this.serverSocket = new DatagramSocket(Settings.PORT);
      
	}

	public byte[] getPacket(int timeout) throws SocketTimeoutException {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			serverSocket.setSoTimeout(timeout);
			// Receive the packet from the server socket
			// hint: server socket has a receive method to help
          	serverSocket.receive(receivePacket);
			logger.received(receiveData);
			if (clientPort == 0) {
				// store the client's IP address and port from the received Packet
				// ...
				clientAddress = receivePacket.getAddress();
                clientPort = receivePacket.getPort();
				logger.info("Established connection with: " + clientAddress + ", Port:" + clientPort);
			}
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Unable to get data from socket.", e);
		}
		// get the client message
		return receiveData;
	}

	public void sendPacket(int size, byte[] sendData) {

		totalData += size;
		totalDataWithHdr += size + 12;
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
		try {
			logger.sent(sendData);
			// Send the sendPacket data!  
			// hint: serverSocket has a 'send' method to help with this
			// ...
            serverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Unable to send reply", e);
		}
	}

	public void reset() {
		// this is restarting so set the restarted state to true and initialize the client port to 
		// show that there is no port (0 works well for this)
		// ...
        restarted = true;
        clientAddress = null;
        clientPort = 0;
	}
	
	public void close() {
		// Close any open sockets
		// ...
      serverSocket.close();
	}
	
	public void stats() {
		logger.info(String.format("    Total Data:                          %4d", totalData));
		logger.info(String.format("    Total Data Including Packet Headers: %4d", totalDataWithHdr));		
	}

}
;