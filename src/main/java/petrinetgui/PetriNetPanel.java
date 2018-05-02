package petrinetgui;

import petrinet.Connector;
import petrinet.Drawable;
import petrinet.NetElement;
import petrinet.PetriNet;

import javax.swing.*;
import java.awt.*;

public class PetriNetPanel extends JPanel {

    private Drawable floatingElement = null;

    public void setFloatingElement(Drawable floatingElement) {
        this.floatingElement = floatingElement;

    }

    public void deleteFloatingElement(Drawable floatingElement) {
        this.floatingElement = null;

    }


    public PetriNetPanel() {
        super();
        setLayout(null);
    }

    private PetriNet net = new PetriNet();

    private final Color bgColor = Color.WHITE;

    public void setNet(PetriNet net) {
        this.net = net;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        setBackground(bgColor);
        for (NetElement n : net.getElementsList()) {
            if (n.getX() > getWidth() - NetElement.ELEMENT_SIZE / 2)     //check if the element fits inside the panel
                n.setX(getWidth() - NetElement.ELEMENT_SIZE / 2);
            if (n.getY() > getHeight() - NetElement.ELEMENT_SIZE / 2)
                n.setY(getHeight() - NetElement.ELEMENT_SIZE / 2);
            if (n.getX() < 0)
                n.setX(0);
            if (n.getY() < 0)
                n.setY(0);

            n.draw(g2d);        //draw the element
        }

        for (Connector c : net.getConnectors())
            c.draw(g2d);        //draw the connectors

        if (floatingElement != null) floatingElement.draw(g2d);      //if floatingElement is present, draw it too
    }
}
