package petrinetgui;

import petrinet.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

public class MouseActionsListener implements MouseListener, MouseMotionListener {

    private PetriNet net;       //the net that is on the canvas
    private JFrame parent;      //its parent frame, to call repaint

    private final PetriNetPanel canvas;     //to add elements to the canvas

    private JLabel label = new JLabel();    //to send messages to the user

    public void setNet(PetriNet net) {
        this.net = net;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    //mode constants
    public static final int MODE_IDLE = 0;
    public static final int MODE_INC = 1;
    public static final int MODE_DEC = 2;
    public static final int MODE_FIRE_TRANSITION = 3;
    public static final int MODE_ADD_PLACE = 4;
    public static final int MODE_ADD_TRANSITION = 5;
    public static final int MODE_ADD_ARC = 6;
    private static final int MODE_ADD_ARC_STAGE2 = 7;
    public static final int MODE_DELETE = 8;
    public static final int MODE_DRAG = 9;
    private static final int MODE_DRAG_STAGE_2 = 10;
    public static final int MODE_ADD_INHIBITOR = 11;
    public static final int MODE_ADD_RESET = 12;
    private static final int MODE_ADD_INHIBITOR_STAGE2 = 13;
    private static final int MODE_ADD_RESET_STAGE2 = 14;
    public static final int MODE_SET_LABEL = 15;
    private static final int MODE_ADD_BREAKPOINT_STAGE2 = 17;


    private int mode = MODE_IDLE;       //the default mode is idle

    private NetElement selected = null; //the clicked element

    private Connector selectedConnector = null; //the clicked connector

    private int offsetX = 0, offsetY = 0;       //offset of the click on an element

    public void setMode(int mode) {
        selected = null;
        if (mode != MODE_ADD_ARC_STAGE2 && mode != MODE_DRAG_STAGE_2)  //these modes should not be set externally
            this.mode = mode;

        if (mode == MODE_IDLE)
            label.setText("Idle");          //idle mode is set from within

    }

    public MouseActionsListener(PetriNet net, JFrame parent, PetriNetPanel canvas) {
        super();
        this.net = net;
        this.parent = parent;
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {         //right click used to cancel
            if (mode == MODE_ADD_ARC_STAGE2)
                setMode(MODE_ADD_ARC);                      //move one stage back
            else if (mode == MODE_ADD_INHIBITOR_STAGE2)
                setMode(MODE_ADD_INHIBITOR);
            else if (mode == MODE_ADD_RESET_STAGE2)
                setMode(MODE_ADD_RESET);
            else
                setMode(MODE_IDLE);                         //if not 2-stage operation, set mode to idle
            return;
        }

        NetElement clickedElement = findElement(e.getX(), e.getY());
        Connector connector = findConnector(e.getX(), e.getY());
        switch (mode) {
            case MODE_DEC:
                findAndEdit(e.getX(), e.getY(), -1);
                break;

            case MODE_INC:
                findAndEdit(e.getX(), e.getY(), 1);
                break;

            case MODE_FIRE_TRANSITION:
                findAndFire(e.getX(), e.getY());
                break;

            case MODE_ADD_PLACE:
                addPlace(e.getX(), e.getY());
                break;

            case MODE_ADD_TRANSITION:
                addTransition(e.getX(), e.getY());
                break;

            case MODE_ADD_ARC:
                if (clickedElement != null) {
                    selected = clickedElement;
                    mode = MODE_ADD_ARC_STAGE2;     //if clicked on an element move on to the next stage
                } else {
                    selected = null;
                }
                break;

            case MODE_ADD_ARC_STAGE2:
                addArc(e.getX(), e.getY());         //add the arc from the selected element to the current position
                break;

            case MODE_ADD_INHIBITOR_STAGE2:
                addInhibitor(e.getX(), e.getY());       //add the arc from the selected element to the current position
                break;

            case MODE_ADD_RESET_STAGE2:
                addReset(e.getX(), e.getY());       //add the arc from the selected element to the current position
                break;

            case MODE_ADD_RESET:
                if (clickedElement instanceof Place) {
                    selected = clickedElement;
                    mode = MODE_ADD_RESET_STAGE2;   //if clicked on an element move on to the next stage
                } else selected = null;

                break;
            case MODE_ADD_INHIBITOR:
                if (clickedElement instanceof Place) {
                    selected = clickedElement;
                    mode = MODE_ADD_INHIBITOR_STAGE2;       //if clicked on an element move on to the next stage
                } else selected = null;

                break;

            case MODE_DELETE:
                delete(e.getX(), e.getY());
                break;
            case MODE_DRAG:
                if (clickedElement != null) {
                    selected = clickedElement;              //if clicked on an element move on to the next stage
                    offsetX = e.getX() - selected.getX();       //save the click offset, for smooth movement
                    offsetY = e.getY() - selected.getY();
                    mode = MODE_DRAG_STAGE_2;
                } else if (connector != null) {
                    selectedConnector = connector;
                    mode = MODE_ADD_BREAKPOINT_STAGE2;
                }
                break;

            case MODE_SET_LABEL:
                if (clickedElement != null) {        //show a dialog, with default text
                    String text = JOptionPane.showInputDialog(parent, "New label: ", clickedElement.getLabel());
                    if (text != null)        //if user clicked OK
                        clickedElement.setLabel(text);
                }
                break;

        }

        parent.repaint();
    }

    private NetElement findElement(int x, int y) {
        for (NetElement e : net.getElementsList()) {
            if (x > e.getX() && x < e.getX() + NetElement.ELEMENT_SIZE
                    && y > e.getY() && y < e.getY() + NetElement.ELEMENT_SIZE) {
                return e;       //the click was inside the borders of the element
            }
        }
        return null;
    }

    private Connector findConnector(int x, int y) {
        HashSet<Connector> arcs = net.getConnectors();
        for (Connector c : arcs) {
            Point click = new Point(x, y);
            if (c.pointOnThis(click)) {
                return c;       //the user clicked on this connector
            }
        }
        return null;
    }

    private void findAndFire(int x, int y) {
        label.setText("Firing transitions");
        NetElement e = findElement(x, y);
        if (e instanceof Transition) {          //user clicked on a transition
            Transition t = (Transition) e;
            try {
                net.fireTransition(t);
            } catch (InvalidTransitionException ex) {
                label.setText(ex.getMessage());             //msg to user
                System.out.println(ex.getMessage());
            }
        }
    }

    private void findAndEdit(int x, int y, int by) {
        NetElement e = findElement(x, y);
        if (e instanceof Place) {
            Place p = (Place) e;
            p.setTokens(p.getTokens() + by);        //user clicked on a Place, alter the tokens
        } else {
            Connector c = findConnector(x, y);
            if (c != null) {
                try {
                    c.setMultiplicity(c.getMultiplicity() + by);      //user clicked on a Connector, alter its multiplicity
                } catch (InvalidMultiplicityException ex) {
                    label.setText(ex.getMessage());             //multiplicity <1
                }

            }
        }
    }

    private void addPlace(int x, int y) {
        Place p = new Place(net.getNextId());
        p.setX(x - NetElement.ELEMENT_SIZE / 2);        //the mouse points to the center of the element
        p.setY(y - NetElement.ELEMENT_SIZE / 2);
        net.addElement(p);
    }

    private void addTransition(int x, int y) {
        Transition t = new Transition(net.getNextId());
        t.setX(x - NetElement.ELEMENT_SIZE / 2);        //the mouse points to the center of the element
        t.setY(y - NetElement.ELEMENT_SIZE / 2);
        net.addElement(t);
    }

    private void addArc(int x, int y) {
        if (findElement(x, y) != null) {                //the mouse points to an element, the from is already set, this is the to
            NetElement to = findElement(x, y);
            try {
                net.connect(selected, to);              //connect with arc
            } catch (ConnectionWithNullException | ConnectionTypeException |
                    InvalidMultiplicityException | ConnectionAlreadyExistsException ex) {
                System.out.println(ex.getMessage());
                label.setText(ex.getMessage());
            }
            label.setText("Add arcs");
            mode = MODE_ADD_ARC;
            selected = null;
        }
    }

    private void addInhibitor(int x, int y) {
        NetElement to = findElement(x, y);
        if (to instanceof Transition && selected instanceof Place) {
            try {
                net.connectInhibitor((Place) selected, (Transition) to, 1);
            } catch (ConnectionAlreadyExistsException ex) {
                label.setText(ex.getMessage());
            }
            mode = MODE_ADD_INHIBITOR;
            selected = null;
        }

    }

    private void addReset(int x, int y) {
        NetElement to = findElement(x, y);
        if (to instanceof Transition && selected instanceof Place) {
            try {
                net.connectReset((Place) selected, (Transition) to);
            } catch (ConnectionAlreadyExistsException ex) {
                label.setText(ex.getMessage());
            }
            mode = MODE_ADD_RESET;
            selected = null;
        }
    }

    private void delete(int x, int y) {
        NetElement e = findElement(x, y);
        if (e != null)
            net.deleteElement(e);
        else {
            Connector c = findConnector(x, y);
            if (c != null)
                c.delete();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mode == MODE_DRAG_STAGE_2 || mode == MODE_ADD_BREAKPOINT_STAGE2) {
            selected = null;
            selectedConnector = null;
            mode = MODE_DRAG;       //stop dragging
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    @Override
    public void mouseDragged(MouseEvent e) {
        if (mode == MODE_DRAG_STAGE_2) {
            selected.setX(e.getX() - offsetX);      //drag the chosen element
            selected.setY(e.getY() - offsetY);

        } else if (mode == MODE_ADD_BREAKPOINT_STAGE2) {
            selectedConnector.setBreakPoint(new Point(e.getX(), e.getY()));
            //set the chosen arcs breakpoint to current mouse position
        }
        parent.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() - NetElement.ELEMENT_SIZE / 2,
                y = e.getY() - NetElement.ELEMENT_SIZE / 2,
                size = NetElement.ELEMENT_SIZE;

        Drawable floatingElement = null;        //only set, if adding place/transition or arc

        if (mode == MODE_ADD_PLACE)
            floatingElement = g2d -> g2d.drawOval(x, y, size, size);        //draw a circle around the pointer

        else if (mode == MODE_ADD_TRANSITION)
            floatingElement = g2d -> g2d.drawRect(x, y, size, size);        //draw a rectangle around the pointer

        else if (mode == MODE_ADD_ARC_STAGE2 || mode == MODE_ADD_RESET_STAGE2 || mode == MODE_ADD_INHIBITOR_STAGE2) {
            if (selected != null) {     //if an element was selected, draw an arrow towards the pointer
                floatingElement = g2d -> {
                    if (selected == null) return;
                    Point to = new Point(e.getX(), e.getY());
                    Point pfrom = selected.getBorderCoors(new Point(e.getX(), e.getY()));
                    new Arrow(pfrom, to).draw(g2d);
                };

            }
        }
        canvas.setFloatingElement(floatingElement);         //add the floating element to the canvas
        parent.repaint();

    }
}
