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
public class Transition extends NetElement {
    public Transition(int id) {
        super(id);
    }

    @Override
    public Point getBorderCoors(Point to) {
        int dirX = to.x;
        int dirY = to.y;

        int xcenter = getCenterPoint().x,
                ycenter = getCenterPoint().y,
                lineTop = getY(),
                lineLeft = getX(),
                lineBot = getY() + ELEMENT_SIZE,
                lineRight = getX() + ELEMENT_SIZE;

        Point[] junctions = new Point[4];

        int x, y;

        double k = ((double) (dirY - ycenter)) / (dirX - xcenter);
        if (k > 10e4)        //could be +- infinity
            k = 10e4;
        if (k < -10e4)
            k = -10e4;
        double q = dirY - k * dirX;

        //top junction
        y = lineTop;
        x = (int) ((y - q) / k);
        junctions[0] = new Point(x, y);

        //right junction
        x = lineRight;
        y = (int) (k * x + q);
        junctions[1] = new Point(x, y);

        //bottom junction
        y = lineBot;
        x = (int) ((y - q) / k);
        junctions[2] = new Point(x, y);

        //left junction
        x = lineLeft;
        y = (int) (k * x + q);
        junctions[3] = new Point(x, y);


        Point[] possible = new Point[4];
        int count = 0;

        for (Point p : junctions) {
            //find the points that are on the rectangle
            if (p.x <= lineRight && p.x >= lineLeft
                    && p.y >= lineTop && p.y <= lineBot) {
                possible[count++] = p;
            }
        }

        double bestDist = Double.MAX_VALUE;
        Point best = null;
        for (int i = 0; i < count; ++i) {            //find the closest point to the destination
            if (possible[i].distance(to) < bestDist) {
                bestDist = possible[i].distance(to);
                best = possible[i];
            }
        }

        return best;
    }


    public void draw(Graphics g) {
        Color c = g.getColor();
        if (isFireable())
            g.setColor(Color.GREEN);        //indicate if the transition is fireable or not
        else
            g.setColor(Color.RED);
        g.drawRect(getX(), getY(), ELEMENT_SIZE, ELEMENT_SIZE);
        g.setColor(Color.BLACK);
        g.drawString(getLabel(), getX(), getY() + ELEMENT_SIZE + 15);       //the label
        g.setColor(c);
    }


    public boolean isFireable() {
        for (Connector c : before) {
            if (((Place) c.getFrom()).getTokens() < c.getWeight())
                return false;           //the tokens in some Place were less than the corresponding connector
        }
        return true;
    }
}
