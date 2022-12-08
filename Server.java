//imports for network communication
import java.io.*;
import java.util.*;
import java.net.*;

public class Server {    
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter = 0;

    HashSet<PlayerHandler> handlers;
    
    public static void main(String[] args) throws Exception{ 
        Server server = new Server();
        server.go();
    }
    
    public void go() throws Exception{ 
        //create a socket with the local IP address and wait for connection request       
        System.out.println("Launching server...");
        serverSocket = new ServerSocket(Const.PORT);                //create and bind a socket
        // Create a thread that updates the game state
        
        while (true) {
            clientSocket = serverSocket.accept();             //wait for connection request
            clientCounter = clientCounter + 1;
            System.out.println("Player " + clientCounter + " connected");
            Thread connectionThread = new Thread(new PlayerHandler(clientSocket));
            connectionThread.start();                         //start a new thread to handle the connection
        }
    }
    
//------------------------------------------------------------------------------
    class PlayerHandler extends Thread { 
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        public boolean alive = true;
        Heartbeat heartbeat;
        
        public PlayerHandler(Socket socket) { 
            this.socket = socket;
            this.heartbeat = new Heartbeat(this);
            this.heartbeat.start();
        }
        
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                while (true) {
                    
                }
                //receive a message from the client
                String msg = input.readLine();
                System.out.println("Message from the client: " + msg);
                //send a response to the client
                output.println("Client "+clientCounter+", you are connected!");
                output.flush();         
                //after completing the communication close the streams but do not close the socket!
                input.close();
                output.close();
            }catch (IOException e) {e.printStackTrace();}
        }
        @Override
        public void interrupt() {
            // Stop the heartbeat subthread
            this.heartbeat.interrupt();
            // TODO: kill the player
            super.interrupt();
        }
        // If there is no "heartbeat" from the client for 60 seconds, assume the connection has failed
        class Heartbeat extends Thread {
            PlayerHandler playerHandler;

            Heartbeat(PlayerHandler playerHandler) {
                this.playerHandler = playerHandler;
            }
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000);
                    } catch (Exception e) {}
                    if (this.playerHandler.alive) {
                        this.playerHandler.alive = false;
                    } else {
                        this.playerHandler.interrupt();
                        break;
                    }
                }
            }
        }
    }    
}