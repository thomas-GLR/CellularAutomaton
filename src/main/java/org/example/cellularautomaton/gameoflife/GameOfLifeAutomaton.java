package org.example.cellularautomaton.gameoflife;

import org.example.cellularautomaton.Automaton;
import org.example.cellularautomaton.automaton.LinkedCell;

public class GameOfLifeAutomaton extends Automaton {

    private int x;
    private int y;
    private LinkedCell<Integer> cells;
    /*
    0 : voisin état 1 < 2
        OU
        voisin été 1 > 3
    1 : voisin état 1 = 3
        OU
        voisin état 1 = 2 ET cellule courante = 1
     */

    public GameOfLifeAutomaton(int x, int y) {

    }

    @Override
    public void initializeAutomaton() {

    }

    @Override
    public void simulate() {

    }
}
