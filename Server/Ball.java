import java.util.*;
import java.awt.*; 

public class Ball extends Circle {
    private String name;
    // todo: implement ball growth
    
    public Ball(String name, Color color){
        super(Const.STARTING_RADIUS, color);
        this.name = name;
    }

    public boolean intersects(Ball otherBall){
        boolean intersects = false;
        int distanceBetweenCenters = (int)(Math.sqrt(Math.pow(x - otherBall.getX(),2)  + Math.pow(y - otherBall.getY(),2)));
        if(distanceBetweenCenters <= radius + otherBall.getRadius()){
            intersects = true;
        }
        return intersects;
    }
    public int diameter() {
        return this.radius * 2;
    }
}
