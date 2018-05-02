package petrinetgui;

import javax.swing.*;
import java.awt.*;

public class AddPlaceButton extends JButton {

    public AddPlaceButton() {
        super("+");         //text in the middle
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = getWidth() / 2,
                centerY = getHeight() / 2,
                r = 12;
        g.drawOval(centerX - r, centerY - r, 2 * r, 2 * r);            //circle around it
    }
}
