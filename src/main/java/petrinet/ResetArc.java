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
public class ResetArc extends Connector {

    private final Place p;

    public ResetArc(Place from, Transition to) {
        super(from, to, 1);
        p = from;
    }

    @Override
    public int getWeight() {
        return p.getTokens();       //takes all the tokens from the place before
    }

    @Override
    public void setMultiplicity(int multiplicity) throws InvalidTransitionException {
        //this method is not needed for this type of connector
        //set multiplicity to 1 to avoid any errors
        super.setMultiplicity(1);
    }

    @Override
    public String toString() {
        return getFrom() + " " + "->R " + getTo();
    }

    @Override
    public void draw(Graphics g) {
        draw(g, "Reset");
    }


}
