package petrinetgui;


import generated.Document;
import petrinet.PetriNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class PetriNetGUI implements ActionListener {
    private PetriNet net;
    private JFrame frame;
    private PetriNetPanel canvas;
    private JPanel menu;
    private JButton newButton = new JButton("New");
    private JButton openButton = new JButton("Open");
    private JButton saveButton = new JButton("Save");
    private JButton playButton = new JButton("Play");
    private JButton plusButton = new JButton("+");
    private JButton minusButton = new JButton("-");
    private JButton addPlaceButton = new AddPlaceButton();
    private JButton addTransitionButton = new AddTransitionButton();
    private JButton setLabelButton = new JButton("Set label");
    private JButton addArcButton = new JButton("Add arc");
    private JButton addInhibitorButton = new JButton("Add inhibitor");
    private JButton addResetButton = new JButton("Add reset");
    private JButton deleteButton = new JButton("Remove");
    private JButton dragButton = new JButton("Drag");

    //the  order of the buttons
    private JButton[] buttons = new JButton[]{newButton, openButton, saveButton, playButton,
            plusButton, minusButton, addPlaceButton, addTransitionButton, setLabelButton, addArcButton, addInhibitorButton,
            addResetButton, deleteButton, dragButton};

    //the height of all the buttons should be the same
    private final int BTN_HEIGHT = 30;

    private JLabel label;

    private MouseActionsListener mouseListener;


    public void run() {
        constructGui();
        frame.setVisible(true);
    }


    private void constructGui() {
        frame = new JFrame("Petri Net Tester");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        canvas = new PetriNetPanel();
        frame.add(canvas, BorderLayout.CENTER);         //add the canvas to the center

        menu = new JPanel(new GridLayout(2, 1));        //the control menu consists of two rows
        JPanel submenuTop = new JPanel(new FlowLayout(FlowLayout.LEADING));     //layout of the rows
        JPanel submenuBot = new JPanel(new FlowLayout(FlowLayout.LEADING));

        submenuTop.setBorder(new EmptyBorder(0, 40, 0, 0));     //margin to the rows
        submenuBot.setBorder(new EmptyBorder(0, 60, 0, 0));

        for (JButton button : buttons) {         //set up the buttons
            button.addActionListener(this);
            int width = button.getPreferredSize().width;
            button.setPreferredSize(new Dimension(width, BTN_HEIGHT));      //change only the height
        }

        int i;
        for (i = 0; i < 8; ++i)
            submenuTop.add(buttons[i]);         //the first 8 buttons go in the first row
        for (; i < buttons.length; ++i)          //the rest to the second
            submenuBot.add(buttons[i]);

        menu.add(submenuTop);
        menu.add(submenuBot);                   //add the rows to the menu

        label = new JLabel("");             //the error label
        label.setHorizontalAlignment(JLabel.RIGHT);
        frame.add(label, BorderLayout.PAGE_END);        //ont the right bottom side of the frame
        frame.add(menu, BorderLayout.PAGE_START);       //on the top side of the frame
        net = new PetriNet();                       //default empty petri net
        canvas.setNet(net);
        mouseListener = new MouseActionsListener(net, frame, canvas);       //the mouseListener, with the same net as the canvas
        mouseListener.setLabel(label);
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);       //conncet the canvas and the listener

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newButton) {
            newNet();
        }
        if (e.getSource() == openButton) {
            loadFile();
        } else if (e.getSource() == saveButton) {
            saveFile();
        } else if (e.getSource() == plusButton) {
            mouseListener.setMode(MouseActionsListener.MODE_INC);
            label.setText("increasing...");
        } else if (e.getSource() == minusButton) {
            mouseListener.setMode(MouseActionsListener.MODE_DEC);
            label.setText("decreasing...");
        } else if (e.getSource() == playButton) {
            mouseListener.setMode(MouseActionsListener.MODE_FIRE_TRANSITION);
            label.setText("Firing transitions");
        } else if (e.getSource() == addPlaceButton) {
            mouseListener.setMode(MouseActionsListener.MODE_ADD_PLACE);
            label.setText("Add places");
        } else if (e.getSource() == addTransitionButton) {
            mouseListener.setMode(MouseActionsListener.MODE_ADD_TRANSITION);
            label.setText("Add transitions");
        } else if (e.getSource() == addArcButton) {
            mouseListener.setMode(MouseActionsListener.MODE_ADD_ARC);
            label.setText("Add arcs");
        } else if (e.getSource() == addInhibitorButton) {
            mouseListener.setMode(MouseActionsListener.MODE_ADD_INHIBITOR);
            label.setText("Add Inhibitor");
        } else if (e.getSource() == addResetButton) {
            mouseListener.setMode(MouseActionsListener.MODE_ADD_RESET);
            label.setText("Add Reset");
        } else if (e.getSource() == deleteButton) {
            mouseListener.setMode(MouseActionsListener.MODE_DELETE);
            label.setText("Remove");
        } else if (e.getSource() == dragButton) {
            mouseListener.setMode(MouseActionsListener.MODE_DRAG);
            label.setText("Dragging");
        } else if (e.getSource() == setLabelButton) {
            mouseListener.setMode(MouseActionsListener.MODE_SET_LABEL);
            label.setText("Setting labels");
        }
    }

    private void loadFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileFilter(new FileNameExtensionFilter("xml file", "xml"));       //only show xml files
        int returnVal = fc.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {     //user clicked OK
            File file = fc.getSelectedFile();
            net = new PetriNet(file);           //create a new net
            canvas.setNet(net);
            mouseListener.setNet(net);
            frame.repaint();
        }

    }

    private void saveFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setSelectedFile(new File("petrinet.xml"));       //the default file name
        fc.setFileFilter(new FileNameExtensionFilter("xml file", "xml"));       //.xml extension

        int result = fc.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.marshal(net.toDocument(), selectedFile);
                label.setText("File saved");
            } catch (JAXBException ex) {
                label.setText("Couldnt save the file");
            }
        }
    }

    private void newNet() {
        PetriNet net = new PetriNet();      //empty net
        mouseListener.setNet(net);
        canvas.setNet(net);
        frame.repaint();
    }
}
