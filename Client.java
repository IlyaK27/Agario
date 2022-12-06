import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client {
    static Ball ball;
    private static JFrame window;
    private static MyPanel gamePanel;
    public static void main(String[] args){
        setup();
        while(true){          
            window.repaint();
        }
    }
    public static class GameKeyListener implements KeyListener{   
        public void keyPressed(KeyEvent e){
            char letter = e.getKeyChar();
            int radius = letter;
            radius = radius / 2 - 10;
            int red = (int)(Math. random() * 255);
            int blue = (int)(Math. random() * 255);
            int green = (int)(Math. random() * 255);
            ball = new Ball(letter, radius, new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
            Thread thread = new Thread(ball);
            thread.start();
        }
        public void keyTyped(KeyEvent e){}
        public void keyReleased(KeyEvent e){}
    }
    private static class MyPanel extends JPanel {
        public void paintComponent(Graphics g) {
            ball.draw(g);
        }
    }
    private static void setup(){
        window = new JFrame("Agar.io");
        window.setPreferredSize(new Dimension(Const.WIDTH, Const.HEIGHT));// adding because of window problems   
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setResizable(false);
        window.setSize(Const.WIDTH, Const.HEIGHT);
        window.addKeyListener(new GameKeyListener());

        gamePanel = new MyPanel();

        window.add(gamePanel);
    }
}