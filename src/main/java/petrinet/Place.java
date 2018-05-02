/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet;

import java.awt.*;

/**
 * @author Mark
 */
public class Place extends NetElement {
    private int tokens;

    public Place(int id, int tokens) {
        super(id);
        this.tokens = tokens >= 0 ? tokens : 0;       //tokens cant be negative
    }

    public Place(int id) {      //the default number of tokens is 0
        this(id, 0);
    }

    public int getTokens() {
        return tokens;
    }

    public int incTokens(int by) {
        if (by < 0) by = 0;              //no negative number allowed in by
        tokens += by;
        return tokens;
    }

    public int decTokens(int by) {
        if (by < 0) by = 0;              //no negative numbers allowed
        if (tokens - by < 0) {
            return -1;                  //if new value would be negative do nothing and return -1 to indicate error
        }
        tokens -= by;
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens >= 0 ? tokens : 0;       //cant be negative
    }

    @Override
    public Point getBorderCoors(Point to) {     //returns the coordinates on its circle in a given direction

        int dirX = to.x;
        int dirY = to.y;
        int xcenter = getCenterPoint().x,
                ycenter = getCenterPoint().y;

        double r = ELEMENT_SIZE / 2.0;

        double k = ((double) (dirY - ycenter)) / (dirX - xcenter);

        if (k > 10e4)        //could be +-infinity, if the x coordinates are the same
            k = 10e4;
        if (k < -10e4)
            k = -10e4;
        double q = dirY - k * dirX;

        double part1 = (2 * xcenter + 2 * ycenter * k - 2 * k * q);
        double part2 = Math.pow(((-2) * xcenter - 2 * ycenter * k + 2 * k * q), 2)
                - 4 * (k * k + 1) * (xcenter * xcenter + ycenter * ycenter - 2 * ycenter * q + q * q - r * r);
        double part3 = 2 * (k * k + 1);


        double x1 = (part1 + Math.sqrt(part2)) / part3;
        double x2 = (part1 - Math.sqrt(part2)) / part3;

        double y1 = k * x1 + q;
        double y2 = k * x2 + q;

        Point p1 = new Point((int) x1, (int) y1);       //the intersections of the line from this elements center to the point to
        Point p2 = new Point((int) x2, (int) y2);


        if (p1.distance(to) < p2.distance(to))           //return the closer one to the destionation
            return p1;
        else
            return p2;

    }


    public void draw(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), ELEMENT_SIZE, ELEMENT_SIZE);             //black circle
        new TokenPainter(getPoint(), getTokens()).paint(g);  //tokens represented by balls, or string if >= 10
        g.setColor(Color.BLACK);
        g.drawString(getLabel(), getX(), getY() + ELEMENT_SIZE + 15);       //the label
        g.setColor(c);
    }

    @Override
    public String toString() {
        return "Place " + super.toString();
    }
}
