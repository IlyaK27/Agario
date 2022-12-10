//imports for network communication
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;

public class Game {    
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter = 0;
    public int idCounter = Integer.MIN_VALUE;

    ThreadMachine threadMachine;

    HashSet<PlayerHandler> handlers;
    HashMap<Integer, Ball> balls;
    HashMap<Integer, Pellet> pellets;
    // Ball id to playerHandler
    HashMap<Integer, PlayerHandler> handlerMap;
    
    public static void main(String[] args) throws Exception{ 
        Game game = new Game();
        game.go();
    }
    
    public void go() throws Exception{ 
        //create a socket with the local IP address and wait for connection request       
        System.out.println("Launching server...");
        serverSocket = new ServerSocket(Const.PORT);                //create and bind a socket
        this.setup();
        // Create a thread that updates the game state
        while (true) {
            clientSocket = serverSocket.accept();             //wait for connection request
            clientCounter = clientCounter + 1;
            System.out.println("Player " + clientCounter + " connected");
            PlayerHandler handler = new PlayerHandler(clientSocket);
            handlers.add(handler);
            handler.start();
        }
    }
    public void setup() {
        this.handlers = new HashSet<PlayerHandler>();
        this.balls = new HashMap<Integer, Ball>();
        this.pellets = new HashMap<Integer, Pellet>();
        for (int i = 0; i < Const.START_PELLETS; i++) {
            createPellet();
        }
        this.threadMachine = new ThreadMachine(this);
        this.threadMachine.start();
    }
    public void createPellet() {
        Pellet pellet = new Pellet(idCounter++, (int)(Math.random() * Const.WIDTH), (int)(Math.random() * Const.HEIGHT));
        this.pellets.put(pellet.getId(), pellet);
    }
    public Ball createBall(PlayerHandler handler, Color color, String name) {
        Ball ball = new Ball(idCounter++, (int)(Math.random() * Const.WIDTH), (int)(Math.random() * Const.HEIGHT), (int)(Math.random() * 360), color, name);
        this.balls.put(ball.getId(), ball);
        this.handlerMap.put(ball.getId(), handler);
        return ball;
    }
    public void killPellet(int id) {
        this.pellets.remove(id);
    }
    public void killBall(int id) {
        this.balls.remove(id);
        this.handlerMap.get(id).kill();
        this.handlerMap.remove(id);
    }
    public void cleanSockets() {
        for (PlayerHandler handler: this.handlers) {
            if (handler.isDead()) {
                this.handlers.remove(handler);
            }
        }
    }
//------------------------------------------------------------------------------
    class PlayerHandler extends Thread { 
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        public boolean alive = true;
        Heartbeat heartbeat;
        Ball ball;
        
        public PlayerHandler(Socket socket) { 
            this.socket = socket;
            this.heartbeat = new Heartbeat(this);
            this.heartbeat.start();
        }
        public boolean hasBall() {
            return this.ball != null;
        }
        // Whether the socket is dead
        public boolean isDead() {
            return this.socket == null;
        }
        // Kill the player's ball
        public void kill() {
            this.ball = null;
        }
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                String msg;
                while (true) {
                    //receive a message from the client
                    msg = input.readLine();
                    if (msg != null) {
                        System.out.println("Message from the client: " + msg);
                        String[] args = msg.split(" ");
                        try {
                            // JOIN {red} {green} {blue} {*name}
                            if (args[0].equals("JOIN")) {
                                if (!this.hasBall()) {
                                    this.print("ERROR Player already has joined game");
                                } else {
                                    Color color = new Color(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
                                    String name = "";
                                    for (int i = 4; i < args.length; i++) {
                                        name += args[i] + " ";
                                    }
                                    this.ball = createBall(this, color, name);
                                    this.print(this.ball.getX() + " " + this.ball.getY() + " " + this.ball.getRadius());
                                }
                            } 
                            // PING
                            else if (args[0].equals("PING")) {
                                this.alive = true;
                            }
                            // TURN {degree}
                            else if (args[0].equals("TURN")) {
                                if (this.hasBall()) {
                                    this.ball.setAngle(Integer.valueOf(args[1]));
                                } else {
                                    this.print("ERROR Player has not joined the game");
                                }
                            }
                        } catch (Exception e) {
                            this.print("ERROR invalid arguments");
                        }
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
                this.close();
            }
        }
        public void print(String text) {
            if (this.isDead()) {return;};
            output.println(text);
            output.flush();
        }
        public void close() {
            this.interrupt();
            // Stop the heartbeat subthread
            this.heartbeat.interrupt();
            try {
                input.close();
                output.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.hasBall()) {
                killBall(this.ball.getId());
            }
            killBall(this.ball.getId());
            this.socket = null;
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
                        Thread.sleep(Const.HEARTBEAT_RATE);
                    } catch (Exception e) {}
                    if (this.playerHandler.alive) {
                        this.playerHandler.alive = false;
                    } else {
                        this.playerHandler.close();
                        break;
                    }
                }
            }
        }
    } 
    class ThreadMachine {
        Game game;
        PelletThread pelletThread;
        BallThread ballThread;
        ThreadMachine(Game game) {
            this.game = game;
            this.pelletThread = new PelletThread(this.game);
            this.ballThread = new BallThread(this.game);
        }
        public void start() {
            this.pelletThread.start();
            this.ballThread.start();
        }
        class PelletThread extends Thread {
            Game game;
            PelletThread(Game game) {
                this.game = game;
            }
            public void run() {
                try {Thread.sleep(Const.PELLET_SPAWN_RATE);} catch (Exception e) {};
                this.game.createPellet();
            }
        }
        class BallThread extends Thread {
            Game game;
            BallThread(Game game) {
                this.game = game;
            }
            public void run() {
                while (true) {
                    try {Thread.sleep(20);} catch (Exception e) {};
                    
                }
            }
        }
    }
}