package petrinetgui;

import javax.swing.*;
import java.awt.*;

public class AddTransitionButton extends JButton {

    public AddTransitionButton() {
        super("+");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = getWidth() / 2,
                centerY = getHeight() / 2,
                width = 18;

        g.drawRect(centerX - width / 2, centerY - width / 2, width, width);         //rectangle
    }
}
