package petrinet;

import generated.BreakPoint;
import generated.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.io.File;
import java.util.HashSet;


public class PetriNet {
    private HashSet<NetElement> elements = new HashSet<>();         //So we dont have elements with the same id

    private int nextId = 0;                             //the next valid id that can be used

    private static final String ARC_TYPE_REGULAR = "regular",   //XML attributes for compatibility with petriflow.com
            ARC_TYPE_INHIBITOR = "inhibitor",
            ARC_TYPE_RESET = "reset";

    public int getNextId() {                    //returns a valid id that has not been used
        nextId++;
        return nextId;
    }


    public PetriNet() {
    }

    public PetriNet(File srcxml) {

        Document document = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);          //parsing the xml using jaxb
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            document = (Document) jaxbUnmarshaller.unmarshal(srcxml);
        } catch (JAXBException ex) {
            System.out.println("Couldn't parse xml");
        }

        if (document != null) {          //if then parsing succeeded
            for (generated.Place p : document.getPlace()) {        //full name to avoid collision
                Place place = new Place(p.getId());                                 //between the real and generated classes
                place.setTokens(p.getTokens());
                place.setLabel(p.getLabel());
                place.setX(p.getX());
                place.setY(p.getY());
                addElement(place);
            }

            for (generated.Transition t : document.getTransition()) {
                Transition transition = new Transition(t.getId());
                transition.setLabel(t.getLabel());
                transition.setX(t.getX());
                transition.setY(t.getY());
                addElement(transition);
            }

            for (generated.Arc a : document.getArc()) {
                try {
                    Connector c = null;                     //one of these should always be true, if the xml was valid
                    switch (a.getType()) {
                        case ARC_TYPE_REGULAR:
                            c = connect(a.getSourceId(), a.getDestinationId(), a.getMultiplicity());
                            break;
                        case ARC_TYPE_RESET:
                            c = connectReset(a.getSourceId(), a.getDestinationId());
                            break;
                        case ARC_TYPE_INHIBITOR:
                            c = connectInhibitor(a.getSourceId(), a.getDestinationId(), a.getMultiplicity());
                            break;
                    }
                    if (c != null && a.getBreakPoint() != null) {         //if there was a breakpoint
                        Point p = new Point(a.getBreakPoint().getX(), a.getBreakPoint().getY());
                        c.setBreakPoint(p);
                    }

                } catch (ConnectionWithNullException | ConnectionTypeException |
                        InvalidMultiplicityException | ConnectionAlreadyExistsException ex) {
                    System.out.println(ex.getMessage());                //should never happen for valid xml files
                }
            }
        }
    }

    public HashSet<NetElement> getElementsList() {
        return elements;
    }

    public Connector connectInhibitor(int from, int to, int multiplicity) {
        NetElement a = getReference(from);
        NetElement b = getReference(to);

        if ((a instanceof Place) && (b instanceof Transition))               //inhibitor arc can only be created between Place->Transition
            return connectInhibitor((Place) a, (Transition) b, multiplicity);
        else
            throw new ConnectionTypeException("Invalid Elements for an Inhibitor Arc");
    }

    public Connector connectInhibitor(Place from, Transition to, int multiplicity) {
        return new InhibitorArc(from, to, multiplicity);
    }

    public Connector connectReset(int from, int to) {
        NetElement a = getReference(from);
        NetElement b = getReference(to);

        if ((a instanceof Place) && (b instanceof Transition))
            return connectReset((Place) a, (Transition) b);     //reset arc can only be created between Place->Transition
        else
            throw new ConnectionTypeException("Invalid Elements for a Reset Arc");

    }

    public Connector connectReset(Place from, Transition to) {
        return new ResetArc(from, to);
    }

    public Connector connect(NetElement from, NetElement to) {
        return connect(from, to, 1);
    }

    public Connector connect(NetElement from, NetElement to, int multiplicity) {
        return new Arc(from, to, multiplicity);
    }

    public Connector connect(int from, int to, int multiplicity) {
        return connect(getReference(from), getReference(to), multiplicity);
    }

    public NetElement getReference(int id) {
        for (NetElement e : elements) {
            if (e.getId() == id)
                return e;
        }
        return null;

    }

    public boolean addElement(NetElement n) {
        if (elements.add(n)) {            //if the element wasnt in the set
            if (n.getId() > nextId)
                nextId = n.getId();     //if its id is bigger than the current valid id, set the curr id to the new one
            return true;                    //to avoid collision
        }
        return false;
    }

    public void fireTransition(Transition t) throws InvalidTransitionException {

        if (!t.isFireable())
            throw new ConnectionTypeException("Invalid Elements for an Inhibitor Arc");

        for (Connector c : t.before) {          //if fireable
            Place p = (Place) c.getFrom();
            p.decTokens(c.getWeight());         //take the tokens
        }


        for (Connector c : t.after) {
            Place p = (Place) c.getTo();
            p.incTokens(c.getWeight());         //add the tokens
        }

    }

    @Override
    public String toString() {
        return "Petri Net with " + elements.size() + " elements";
    }

    public void deleteElement(NetElement e) {
        elements.remove(e);             //remove from the set
        e.delete();                     //call delete on the element, to clean up
    }

    public HashSet<Connector> getConnectors() {
        HashSet<Connector> connectors = new HashSet<>();        //set to return every connector only once
        for (NetElement e : elements) {
            connectors.addAll(e.before);
            connectors.addAll(e.after);
        }
        return connectors;
    }

    public Document toDocument() {                      //returns the generated class that is saved to xml
        Document doc = new Document();

        for (NetElement e : elements) {
            if (e instanceof Place) {
                generated.Place p = new generated.Place();
                p.setId((short) e.getId());
                p.setLabel(e.getLabel());
                p.setStatic(false);
                p.setTokens((short) ((Place) e).getTokens());
                p.setX((short) e.getX());
                p.setY((short) e.getY());
                doc.getPlace().add(p);
            } else if (e instanceof Transition) {
                generated.Transition t = new generated.Transition();

                t.setLabel(e.getLabel());
                t.setId((short) e.getId());
                t.setX((short) e.getX());
                t.setY((short) e.getY());

                doc.getTransition().add(t);
            }
        }

        for (Connector c : getConnectors()) {
            generated.Arc arc = new generated.Arc();

            arc.setBreakPoint(null);
            arc.setDestinationId((short) c.getTo().getId());
            arc.setSourceId((short) c.getFrom().getId());
            arc.setId((short) 0);
            arc.setMultiplicity((short) c.getMultiplicity());
            if (c instanceof Arc) arc.setType(ARC_TYPE_REGULAR);
            else if (c instanceof ResetArc) arc.setType(ARC_TYPE_RESET);
            else if (c instanceof InhibitorArc) arc.setType(ARC_TYPE_INHIBITOR);

            if (c.getBreakPoint() != null) {
                BreakPoint breakPoint = new generated.BreakPoint();
                breakPoint.setX((short) c.getBreakPoint().x);
                breakPoint.setY((short) c.getBreakPoint().y);
                arc.setBreakPoint(breakPoint);
            }

            doc.getArc().add(arc);
        }

        return doc;
    }

}
