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


    addBehaviour(new POIradar(this));
    addBehaviour(new businessInfo(this));

    }

    class POIradar extends CyclicBehaviour {

        POIradar(Agent a){super(a);}
//
    public void action(){}
    }

    class businessInfo extends CyclicBehaviour {

        businessInfo(Agent a){super((a));}

    public void action(){}
    }
}
