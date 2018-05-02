package petrinet;


import java.awt.*;
import java.awt.geom.AffineTransform;

public class Arrow {
    private static final int TRIANGLE_WIDTH = 10, TRIANGLE_HEIGHT = 16,
            TEXT_OFFSET = 10;

    private Point to, from;

    private String text = "";

    public void setText(String text) {
        this.text = text;
    }

    //a line with a triangle at its end pointing from point From to point To
    public Arrow(Point from, Point to) {
        this.to = to;
        this.from = from;
    }

    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;


        Polygon p = new Polygon();      //a triangle pointing upwards
        //top
        p.addPoint(to.x, to.y);
        //bot left
        p.addPoint(to.x - TRIANGLE_WIDTH / 2, to.y - TRIANGLE_HEIGHT);
        //bot right
        p.addPoint(to.x + TRIANGLE_WIDTH / 2, to.y - TRIANGLE_HEIGHT);

        AffineTransform tx = new AffineTransform();

        double fi = Math.atan2(to.y - from.y, to.x - from.x) - Math.PI / 2;       //the angle of the line - 90deg
        tx.rotate(fi, to.x, to.y);                 //rotate around the center of the end of the line

        Shape shape = tx.createTransformedShape(p);
        g.fill(shape);
        g.drawLine(from.x, from.y, to.x, to.y);
        g.drawString(text, (from.x + to.x) / 2 + TEXT_OFFSET, (from.y + to.y) / 2 + TEXT_OFFSET);
        //text at the center of the line with a small offset
    }

}