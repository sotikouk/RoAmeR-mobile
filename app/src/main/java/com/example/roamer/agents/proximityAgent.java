package com.example.roamer.agents;

import com.example.roamer.ARPoint;

import java.util.ArrayList;
import java.util.Iterator;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

// Δημιουργεί ειδοποιήσεις όταν ο χρήστης ειναι κοντά σε μια επιχείρηση που
// τον ενδιαφέρει. Κάνει ειδοποιήσεις και στην περίπτωση που υπάρχει προσφορά
// σε κάποιο προιόν.

public class proximityAgent extends Agent implements arClientInterface{
    public static ArrayList<ARPoint> bPoints;

    protected void setup() {
        bPoints = new ArrayList<>();
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Roamer-client2b");
        sd.setName("Roamer-proximity");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new coordinatesBusiness(this));
        registerO2AInterface(arClientInterface.class, this);

        addBehaviour(new dist2poiNotify(this));
        addBehaviour(new dist2offerNotify(this));

    }

    @Override
    public ARPoint BusinessPoints() {
        int last;
        last = bPoints.lastIndexOf(bPoints);
        final ARPoint arPoint = bPoints.get(last);
        return arPoint;
    }

    class coordinatesBusiness extends CyclicBehaviour {
        coordinatesBusiness(Agent a) {super(a); }
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                String coordinates = msg.getContent();
                String[] bcoord = coordinates.split(",");
                double lat = Double.parseDouble(bcoord[0]);
                double lon = Double.parseDouble(bcoord[1]);
                double alt = Double.parseDouble(bcoord[2]);
                String bkind = bcoord[3];
                ARPoint bPoint;
                bPoint = new ARPoint(bkind, lat, lon, alt);
                bPoints.add(bPoint);
            }
        }

    }

        class dist2poiNotify extends CyclicBehaviour {
            dist2poiNotify(Agent a) {
                super(a);
            }

            public void action() {
            }
        }

        class dist2offerNotify extends CyclicBehaviour {
            dist2offerNotify(Agent a) {
                super(a);
            }

            public void action() {
            }
        }
    }
