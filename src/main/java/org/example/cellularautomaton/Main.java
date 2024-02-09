package org.example.cellularautomaton;

import org.example.cellularautomaton.automaton1d.Automaton1D;

public class Main {
    public static void main(String[] args) {
        Automaton1D automaton1D = new Automaton1D(100, 30);
        automaton1D.initializeAutomaton();
        automaton1D.printCurrentStateOfAutomaton();
        for (int ii = 0; ii < 50; ii++) {
            automaton1D.simulateAGeneration();
            automaton1D.printCurrentStateOfAutomaton();
        }
    }
}
