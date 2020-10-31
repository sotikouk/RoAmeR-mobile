package com.example.roamer.agents;

import android.content.Context;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

// Ο proximity Agent υπολογίζει την απόσταση απο τις επιχειρήσεις.
// Οταν η απόσταση ειναι μικρότερη των 4m χρονομετρά για πόση ώρα παραμένει ο χρήστης
// εκεί. Με αυτό τον τρόπο αντιλαμβανόμαστε τι ενδιαφέρει τον χρήστη αφού περνά χρόνο μπροστά ή μέσα σε
// μια επιχείρηση. Επικοινωνεί με τον Business agent για το είδος της επιχείρησης.
// Επικοινωνεί με τον client Agent για να καταγράψει την προτήμηση του στο συγκεκριμένο
// είδος επιχειρήσεων.
public class proximityAgent extends Agent {
private Context context;

    protected void setup() {


    addBehaviour(new cust2bDistance(this));
    addBehaviour(new cust2bTimer(this));

    }

    class cust2bDistance extends CyclicBehaviour {
        cust2bDistance(Agent a){super(a);}
//
    public void action(){}
    }

    class cust2bTimer extends CyclicBehaviour {

        cust2bTimer(Agent a){super((a));}

    public void action(){}
    }
}
