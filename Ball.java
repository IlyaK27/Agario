import java.util.*;
import java.awt.*; 

public class Ball extends Circle {
    private String name;
    private int angle;
    // todo: implement ball growth
    
    public Ball(int id, int x, int y, int angle, Color color, String name){
        super(id, x, y, Const.STARTING_RADIUS, color);
        this.name = name;
        this.angle = angle;
    }
    public String getName() {
        return this.name;
    }
    public int getAngle() {
        return this.angle;
    }
    public void setAngle(int angle) {
        this.angle = angle % 360;
    }
    public boolean intersects(Ball other){
        return (Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2))) <= this.radius + other.getRadius();
    }
}
