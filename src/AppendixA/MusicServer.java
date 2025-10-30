package Appendix_A;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class MusicServer {
    // List to keep track of all connected clients' output streams
    private final List<ObjectOutputStream> clientOutputStreams = new ArrayList<>();

    public static void main(String[] args) {
        // Start the server
        new MusicServer().go();
    }

    public void go() {
        try {
            // Create a server socket listening on port 4242
            ServerSocket serverSock = new ServerSocket(4242);

            // Create a thread pool to handle multiple client connections
            ExecutorService threadPool = Executors.newCachedThreadPool();

            // Continuously listen for client connections
            while (!serverSock.isClosed()) {
                // Accept incoming client connection
                Socket clientSocket = serverSock.accept();

                // Create an output stream to send data to the client
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

                // Store this client's output stream
                clientOutputStreams.add(out);

                // Create a new handler for this client and run it in a separate thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                threadPool.execute(clientHandler);

                // Log that a new connection has been made
                System.out.println("Got a connection");
            }
        } catch (IOException e) {
            // Print any server-side errors
            e.printStackTrace();
        }
    }

    // Method to send messages and beat data to all connected clients
    public void tellEveryone(Object usernameAndMessage, Object beatSequence) {
        for (ObjectOutputStream clientOutputStream : clientOutputStreams) {
            try {
                // Send the message (e.g., username + chat) to each client
                clientOutputStream.writeObject(usernameAndMessage);
                // Send the beat pattern data to each client
                clientOutputStream.writeObject(beatSequence);
            } catch (IOException e) {
                // Handle errors in writing to a client
                e.printStackTrace();
            }
        }
    }

    // Inner class to handle input from a connected client
    public class ClientHandler implements Runnable {
        private ObjectInputStream in;

        // Constructor sets up input stream from client
        public ClientHandler(Socket socket) {
            try {
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Continuously read objects sent by the client
        public void run() {
            Object userNameAndMessage;
            Object beatSequence;
            try {
                // As long as data is coming from the client
                while ((userNameAndMessage = in.readObject()) != null) {
                    // Read beat data after the message
                    beatSequence = in.readObject();

                    // Log and forward both objects to all clients
                    System.out.println("read two objects");
                    tellEveryone(userNameAndMessage, beatSequence);
                }
            } catch (IOException | ClassNotFoundException e) {
                // Handle communication errors
                e.printStackTrace();
            }
        }
    }
}

