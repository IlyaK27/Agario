import java.util.*;
import java.awt.*; 

public class Ball implements Runnable{
    private char letter;
    private int x;
    private int y;
    private int radius;
    private int newRadius;
    private int xChange;
    private int yChange;
    private boolean delete;
    private Color color;
    
    public Ball(char letter, int radius, Color color){
        this.letter = letter;
        this.x = Const.STARTING_X;
        this.y = Const.STARTING_Y;
        this.radius = radius;
        this.newRadius = radius;
        this.xChange = (int)(Math. random() * Const.MAX_HORIZONTAL_SPEED);
        this.yChange = (int)(Math. random() * Const.MAX_VERTICAL_SPEED);
        int negativeX = (int)(Math. random() * 2);
        int negativeY = (int)(Math. random() * 2);
        if(negativeX == 1){xChange = -xChange;}
        if(negativeY == 1){yChange = -yChange;}
        this.delete = false;
        this.color = color;
    }

    @Override
    public void run(){
        while(delete == false){
            try{Thread.sleep(50);} catch (Exception e){e.printStackTrace();}  
            for (int i = 0; i < balls.size(); i++) {
                Ball ball = balls.get(i);
                if(intersects(ball) == true && ball != this && (radius > ball.getRadius())){
                    ball.setDelete(true);
                    newRadius += ball.getRadius();
                }
            }
            if(x - radius + xChange <= 0 || x + xChange + radius >= Const.WIDTH - 11){
                if(xChange < 0){x = radius;}
                else{x = Const.WIDTH - radius - 11;}
                xChange = -xChange;
            }else{x += xChange;}
            if(y - radius + yChange <= 0 || y + yChange + radius >= Const.HEIGHT - 35){
                if(yChange < 0){y = radius;}
                else{y = Const.HEIGHT - radius - 35;}
                yChange = -yChange;
            }else{y += yChange;}
            if(radius <= newRadius){
                radius += Const.BALL_GROWTH_RATE;
            }
        }
    }
    public boolean intersects(Ball otherBall){
        boolean intersects = false;
        int distanceBetweenCenters = (int)(Math.sqrt(Math.pow(x - otherBall.getX(),2)  + Math.pow(y - otherBall.getY(),2)));
        if(distanceBetweenCenters <= radius + otherBall.getRadius()){
            intersects = true;
        }
        return intersects;
    }
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x - radius, y - radius, this.diameter(), this.diameter());
        g.setColor(Const.FONT_COLOR);
        g.setFont(Const.BALL_FONT);
        g.drawString(String.valueOf(letter), x, y);
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public char getLetter(){
        return this.letter;
    }
    public int getRadius(){
        return this.radius;
    }
    public int diameter() {
        return this.radius * 2;
    }
    public boolean getDelete(){
        return this.delete;
    }
    public void setDelete(boolean delete){
        this.delete = delete;
    }
}
