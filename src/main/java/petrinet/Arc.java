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
public class Arc extends Connector {


    @Override
    public int getWeight() {
        return multiplicity;
    }


    public Arc(NetElement from, NetElement to, int multiplicity) {
        super(from, to, multiplicity);
    }


    @Override
    public String toString() {
        return getFrom() + " " + multiplicity + "-> " + getTo();
    }


    public void draw(Graphics g) {
        draw(g, String.valueOf(getMultiplicity()));
    }
}
