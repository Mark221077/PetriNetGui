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
public class InhibitorArc extends Connector {


    public InhibitorArc(Place from, Transition to, int multiplicity) {
        super(from, to, multiplicity);
    }

    @Override
    public int getWeight() {
        Place p = (Place) getFrom();
        if (p.getTokens() < multiplicity) {       //if the place before has less tokens than the multiplicity
            return 0;                            //dont take any tokens
        }
        return p.getTokens() + 1;                //else try to take more, thus not fireable transition
    }

    @Override
    public String toString() {
        return getFrom() + " " + multiplicity + "->o " + getTo();
    }


    @Override
    public void draw(Graphics g) {
        draw(g, "Inh " + getMultiplicity());
    }

}
