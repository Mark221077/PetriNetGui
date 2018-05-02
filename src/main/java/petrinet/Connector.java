/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet;


import java.awt.*;
import java.util.Objects;

/**
 * @author Mark
 */
public abstract class Connector implements Drawable {

    private final NetElement from;
    private final NetElement to;

    private Point breakPoint = null;

    public Point getBreakPoint() {
        return breakPoint;
    }

    public void setBreakPoint(Point breakPoint) {
        this.breakPoint = breakPoint;
    }

    protected int multiplicity;

    /**
     * Sets the multiplicity of the <code>Connector</code>
     * @param multiplicity a positive integer
     * @throws InvalidTransitionException if called with a negative or zero argument
     */
    public void setMultiplicity(int multiplicity) throws InvalidTransitionException {
        if (multiplicity < 1) {
            throw new InvalidMultiplicityException("Multiplicity <1");          //must be positive integer
        }
        this.multiplicity = multiplicity;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    /**
     *  Draws an arrow shape representing the connection
     *  betwwen the elements
     *
     * @param g The <code>Graphics</code> context in which to paint
     * @param text  The text to be shown on the arrow
     */
    protected void draw(Graphics g, String text) {
        Point from = getEndPoints()[0],        //point on the from element
                to = getEndPoints()[1];        //point on the to element
        Arrow arrow;
        if (breakPoint != null) {            //if there is a breakpoint
            g.drawLine(from.x, from.y, breakPoint.x, breakPoint.y);     //draw a line from from to the breakpoint first
            arrow = new Arrow(breakPoint, to);                      //arrow from breakpoint to to
        } else
            arrow = new Arrow(from, to);            //no breakpoint, arrow from->to

        arrow.setText(text);                        //the text by the arrow, set by children
        arrow.draw(g);

    }

    public abstract int getWeight();                //returns the number of tokens to be taken

    public NetElement getFrom() {
        return from;
    }

    public NetElement getTo() {
        return to;
    }


    /**
     *  Connects two <code>NetElements</code> in the petri net
     * @param from The <code>NetElement</code> before
     * @param to The <code>NetElement</code> after
     * @param multiplicity  A positive integer
     * @throws ConnectionWithNullException  thrown if one of the parameters was null
     * @throws ConnectionTypeException  thrown if both parameters are of the same class
     * @throws InvalidMultiplicityException thrown if the multiplicity is not valid
     * @throws ConnectionAlreadyExistsException thrown if there already is a connection between the <code>NetElements</code>
     */
    public Connector(NetElement from, NetElement to, int multiplicity) throws ConnectionWithNullException, ConnectionTypeException,
            InvalidMultiplicityException, ConnectionAlreadyExistsException {
        if (from == null || to == null) {
            throw new ConnectionWithNullException("Can't point to/from null");         //cant point to/from null
        }

        if (from.getClass().equals(to.getClass())) {
            throw new ConnectionTypeException("Cant connect elemnets of the same type");
        }

        if (multiplicity < 1)
            throw new InvalidMultiplicityException("Multiplicity <1");


        this.multiplicity = multiplicity;
        this.from = from;
        this.to = to;

        if (!from.after.add(this) || !to.before.add(this)) {
            from.after.remove(this);
            throw new ConnectionAlreadyExistsException("Connection between " + from + " and " + to + " already present");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Connector)) {
            return false;
        }

        Connector c = (Connector) obj;

        return (c.getFrom().equals(this.getFrom()) && c.getTo().equals(this.getTo()));      //is equal if the direction is the same too
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    /**
     * Returns the points on its <code>NetElement</code>s.
     * The points are on the border of the elements
     * @return  an array of 2 <code>Point</code> objects
     */
    public Point[] getEndPoints() {         //returns the points on the from and to elements
        Point pFrom, pTo;
        if (breakPoint != null) {            //if there is a breakpoint,
            pFrom = from.getBorderCoors(breakPoint);        //the first line should point towards that
            pTo = to.getBorderCoors(breakPoint);            //the second line is connected with the breakpoint
        } else {      //the line points from the center of the elements
            pFrom = from.getBorderCoors(new Point(to.getX() + NetElement.ELEMENT_SIZE / 2, to.getY() + NetElement.ELEMENT_SIZE / 2));
            pTo = to.getBorderCoors(new Point(from.getX() + NetElement.ELEMENT_SIZE / 2, from.getY() + NetElement.ELEMENT_SIZE / 2));
        }

        return new Point[]{pFrom, pTo};
    }

    /**
     * safely deletes itself
     */
    public void delete() {
        from.after.remove(this);
        to.before.remove(this);
    }

    /**
     * Returns true if the <code>Point</code> p is on this Connector, or close to it
     * @param p The <code>Point</code> to be checked
     * @return  true if p is on this connector, false otherwise
     */
    public boolean pointOnThis(Point p) {       //checks if the Point p is on this line(close to it
        Point pFrom = getEndPoints()[0],
                pTo = getEndPoints()[1];
        if (breakPoint == null) {
            return (pFrom.distance(p) + p.distance(pTo) - pFrom.distance(pTo) < 0.5);
        } else {
            if (pFrom.distance(p) + p.distance(breakPoint) - pFrom.distance(breakPoint) < 0.5)
                return true;        //p is on the line between pFrom and breakpoint
            if (breakPoint.distance(p) + p.distance(pTo) - breakPoint.distance(pTo) < 0.5)
                return true;        //p is on the line between breakpoint and pTo
        }

        return false;
    }

}