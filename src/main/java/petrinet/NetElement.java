/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author Mark
 */
public abstract class NetElement implements Drawable {

    private final int id;

    private String label = "";

    public static final int ELEMENT_SIZE = 40;

    private int x = 0, y = 0;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    protected HashSet<Connector> before = new HashSet<>();

    protected HashSet<Connector> after = new HashSet<>();

    public final int getId() {
        return id;
    }

    public NetElement(int id) {
        this.id = id;               //the id is set only once
    }

    public Point getPoint() {
        return new Point(getX(), getY());
    }

    public Point getCenterPoint() {
        return new Point(getX() + ELEMENT_SIZE / 2, getY() + ELEMENT_SIZE / 2);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (!(obj instanceof NetElement))
            return false;

        NetElement n = (NetElement) obj;

        return n.id == this.id;             //two elements are equal if they have the same ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);            //hash only the ID parameter
    }

    public abstract Point getBorderCoors(Point to);         //return the Point on the elements border in the given direction

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public void delete() {
        ArrayList<Connector> connectors = new ArrayList<>(before);
        connectors.addAll(after);

        for (Connector c : connectors)       //call delete on all its Connectors
            c.delete();
    }
}
