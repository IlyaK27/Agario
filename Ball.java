import java.util.*;
import java.awt.*; 

public class Ball extends Circle {
    private String name;
    // todo: implement ball growth
    
    public Ball(int id, int x, int y, Color color, String name){
        super(id, x, y, Const.STARTING_RADIUS, color);
        this.name = name;
    }

    public boolean intersects(Ball other){
        return (Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2))) <= this.radius + other.getRadius();
    }
    public int diameter() {
        return this.radius * 2;
    }
}
