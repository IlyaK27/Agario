package Client;

import java.util.*;
import java.awt.*; 

public class Ball extends Circle{
    private String name;
    // todo: implement ball growth
    
    public Ball(String name, Color color){
        super(Const.STARTING_RADIUS, color);
        this.name = name;
    }
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x - radius, y - radius, this.diameter(), this.diameter());
        g.setColor(Const.FONT_COLOR);
        g.setFont(Const.BALL_FONT);
        g.drawString(name, x, y);
    }
    private int diameter() {
        return this.radius * 2;
    }
}
