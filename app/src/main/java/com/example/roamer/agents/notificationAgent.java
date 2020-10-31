package com.example.roamer.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

// Δημιουργεί ειδοποιήσεις όταν ο χρήστης ειναι κοντά σε μια επιχείρηση που
// τον ενδιαφέρει. Κάνει ειδοποιήσεις και στην περίπτωση που υπάρχει προσφορά
// σε κάποιο προιόν.

public class notificationAgent extends Agent {
    protected void setup() {

        addBehaviour(new dist2poiNotify(this));
        addBehaviour(new dist2offerNotify(this));

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
