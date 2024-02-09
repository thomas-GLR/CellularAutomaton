package org.example.cellularautomaton.fireforest;

import org.example.cellularautomaton.automaton.LinkedCell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.example.cellularautomaton.fireforest.ForestState.*;

public class ForestAutomaton {
    // La valeur en abscisse de la grille
    private int x;
    // La valeur en ordonnée de la grille
    private int y;
    // Le nombre de voisin à prendre en compte pour la simulation
    private int nbNextCell;
    private int density;
    // La probabilité de propagation du feux.
    private int probabilityOfSpread;
    // la force du vent d'ouest (si négatif alors vent d'est).
    private int westWindSpeed;
    // la force du vent du sud (si négatif alors vent du nord).
    private int southWindSpeed;
    private LinkedCell<ForestState> forest;

    /**
     * Constructeur de Forest.
     *
     * @param x la valeur en abscisse de la grille.
     * @param y la valeur en ordonnée de la grille.
     * @param nbNextCell le nombre de voisin à prendre en compte pour la simulation.
     */
    public ForestAutomaton(int x, int y, int nbNextCell) {
        this.x = x;
        this.y = y;
        this.nbNextCell = nbNextCell;
    }

    public ForestAutomaton(int x, int y, int nbNextCell, int density, int probabilityOfSpread, int westWindSpeed, int southWindSpeed) {
        this.x = x;
        this.y = y;
        this.nbNextCell = nbNextCell;
        this.density = density;
        this.probabilityOfSpread = probabilityOfSpread;
        this.westWindSpeed = westWindSpeed;
        this.southWindSpeed = southWindSpeed;
        initForest();
    }
/*
    public ForestAutomaton(FireForestConfigurationFile fireForestConfigurationFile) {
        this.x = fireForestConfigurationFile.getX();
        this.y = fireForestConfigurationFile.getY();
        this.nbNextCell = fireForestConfigurationFile.getNbNextCell();
        this.density = fireForestConfigurationFile.getDensity();
        this.probabilityOfSpread = fireForestConfigurationFile.getProbabilityOfSpread();
        this.westWindSpeed = fireForestConfigurationFile.getWestWindSpeed();
        this.southWindSpeed = fireForestConfigurationFile.getSouthWindSpeed();
        initForest();
    }
*/
    /**
     * Initialisation de la grille qui représente la forêt.
     */
    public void initForest() {

        this.forest = new LinkedCell<>(EMPTY);

        for (int ii = this.y - 1; ii >= 0; ii--) {
            for (int jj = this.x - 1; jj >= 0; jj--) {

                ForestState stateOfCell = getStateOfCellWithDensity(density);

                // La fonction d'ajout de cellule n'est pas la même si une nouvelle ligne est créée.
                if (jj == this.x - 1) {
                    forest.addNewLine(stateOfCell);
                }
                else {
                    if (jj == 0) {
                        // Pour le moment toute les cellules sur la colonne de gauche seront en feu.
                        forest.add(FIRE);
                    }
                    else {
                        forest.add(stateOfCell);
                    }
                }
            }
        }
    }

    /**
     * Renvoi un état (forêt ou vide) pour l'initialisation de la forêt selon la densité de la forêt.
     *
     * @param density la densité de la forêt
     * @return l'état de la cellule.
     */
    private ForestState getStateOfCellWithDensity(int density) {
        // le paramètre bound est exclusif.
        int random = ThreadLocalRandom.current().nextInt(1, 101);
        // Si le chiffre random entre 1 et 100 est compris dans la densité alors j'ajoute une cellule forêt, vide sinon.
        if (random <= density) {
            return FOREST;
        }
        return EMPTY;
    }

    /**
     * Affiche la forêt avec des émoji.
     */
    public void printForestCurrentState() {
        LinkedCell<ForestState> currentCell = this.forest;

        for (int ii = 0; ii < this.y; ii++) {
            for (int jj = 0; jj < this.x; jj++) {
                switch (currentCell.getCellValue()) {
                    case EMPTY -> System.out.print("\uD83D\uDFEB");
                    case FOREST -> System.out.print("\uD83C\uDF32");
                    case FIRE -> System.out.print("\uD83D\uDD25");
                    case BURNED -> System.out.print("\uD83D\uDD32");
                }
                currentCell = currentCell.getNextCell();
            }
            System.out.println();
        }
    }

    public ForestState[][] getForestState() {
        LinkedCell<ForestState> currentCell = this.forest;
        // StringBuilder forestState = new StringBuilder();

        ForestState[][] forestState = new ForestState[this.x][this.y];

        for (int ii = 0; ii < this.y; ii++) {
            for (int jj = 0; jj < this.x; jj++) {
                switch (currentCell.getCellValue()) {
                    case EMPTY -> {
                        // forestState.append("\uD83D\uDFEB");
                        forestState[ii][jj] = EMPTY;
                    }
                    case FOREST -> {
                        // forestState.append("\uD83C\uDF32");
                        forestState[ii][jj] = FOREST;
                    }
                    case FIRE -> {
                        // forestState.append("\uD83D\uDD25");
                        forestState[ii][jj] = FIRE;
                    }
                    case BURNED -> {
                        // forestState.append("\uD83D\uDD32");
                        forestState[ii][jj] = BURNED;
                    }
                }
                currentCell = currentCell.getNextCell();
            }
        }

        return forestState;
    }

    /**
     * Renvoi le prochain état de la cellule passée en paramètre selon la probabilité de propagation du feu et
     * de la force du vent.
     *
     * @param cellToDeterminateState la cellule pour laquelle on veut déterminer le prochain état.
     * @return le prochain état de la cellule passée en paramètre.
     */
    public ForestState determineNextState(LinkedCell<ForestState> cellToDeterminateState) {

        ForestState stateOfCell = EMPTY;

        int currentProbabilityOfSpread = this.probabilityOfSpread;

        // Génération d'un nombre entre 1 et 100 (le paramètre bound est exclusif) qui servira à la probabilité de propagation
        int random = ThreadLocalRandom.current().nextInt(1, 101);

        switch (cellToDeterminateState.getCellValue()) {
            // Une cellule avec l'état feu devient brulé et une cellule brulé reste à l'état brulé
            case FIRE, BURNED -> stateOfCell = BURNED;
            // Une cellule forêt reste forêt si tous ses voisins sont à l'état forêt
            // elle devient feu si l'un de ces voisins est en feu et que la le nombre aléatoire généré précédemment est
            // inférieur ou égal à la probabilité de propagation.
            // S'il y a du vent j'ajoute la force du vent à la probabilité (donc plus de probabilité de brulé) si la cellule
            // est dans la même direction du vent sinon je soustrait la force du vent à la probabilité.
            case FOREST -> {
                // Compte le nombre de cellule en feux
                int nbNextCellWithFire = 0;
                // On récupère les cellules voisines en fonction du nombre de voisin défini
                switch (this.nbNextCell) {
                    case 4 -> {
                        if (cellToDeterminateState.getLeftCell() != null) {
                            if (cellToDeterminateState.getLeftCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += westWindSpeed;
                            }
                        }
                        if (cellToDeterminateState.getTopCell() != null) {
                            if (cellToDeterminateState.getTopCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += southWindSpeed;
                            }
                        }
                        if (cellToDeterminateState.getRightCell() != null) {
                            if (cellToDeterminateState.getRightCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += westWindSpeed;
                            }
                        }
                        if (cellToDeterminateState.getBottomCell() != null) {
                            if (cellToDeterminateState.getBottomCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += southWindSpeed;
                            }
                        }
                        stateOfCell = random <= nbNextCellWithFire * currentProbabilityOfSpread ? FIRE : FOREST;
                    }
                    case 8 -> {
                        //Verification à gauche
                        if (cellToDeterminateState.getLeftCell() != null) {
                            if (cellToDeterminateState.getLeftCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += westWindSpeed;
                            }
                            //Verification en bas à gauche
                            if (cellToDeterminateState.getLeftCell().getBottomCell() != null) {
                                if (cellToDeterminateState.getLeftCell().getBottomCell().getCellValue() == FIRE) {
                                    nbNextCellWithFire++;
                                }
                            }
                            //Verification en haut à gauche
                            if (cellToDeterminateState.getLeftCell().getTopCell() != null) {
                                if (cellToDeterminateState.getLeftCell().getTopCell().getCellValue() == FIRE) {
                                    nbNextCellWithFire++;
                                }
                            }
                        }
                        //Verification en haut
                        if (cellToDeterminateState.getTopCell() != null) {
                            if (cellToDeterminateState.getTopCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread -= southWindSpeed;
                            }
                            //Verification en haut à droite
                            if (cellToDeterminateState.getTopCell().getRightCell() != null) {
                                if (cellToDeterminateState.getTopCell().getRightCell().getCellValue() == FIRE) {
                                    nbNextCellWithFire++;
                                }
                            }
                        }
                        //Verification à droite
                        if (cellToDeterminateState.getRightCell() != null) {
                            if (cellToDeterminateState.getRightCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread -= westWindSpeed;
                            }
                            //Verification en bas à droite
                            if (cellToDeterminateState.getRightCell().getBottomCell() != null) {
                                if (cellToDeterminateState.getRightCell().getBottomCell().getCellValue() == FIRE) {
                                    nbNextCellWithFire++;
                                }
                            }
                        }
                        //Verification en bas
                        if (cellToDeterminateState.getBottomCell() != null) {
                            if (cellToDeterminateState.getBottomCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += southWindSpeed;
                            }
                        }
                        stateOfCell = random <= nbNextCellWithFire * currentProbabilityOfSpread ? FIRE : FOREST;
                    }
                    // grille hexagonale
                    case 6 -> {
                        //Verification à gauche
                        if (cellToDeterminateState.getLeftCell() != null) {
                            if (cellToDeterminateState.getLeftCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += westWindSpeed;
                            }

                            //Verification en haut à gauche
                            if (cellToDeterminateState.getLeftCell().getTopCell() != null) {
                                if (cellToDeterminateState.getLeftCell().getTopCell().getCellValue() == FIRE) {
                                    nbNextCellWithFire++;
                                }
                            }
                        }
                        //Verification en haut
                        if (cellToDeterminateState.getTopCell() != null) {
                            if (cellToDeterminateState.getTopCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread -= southWindSpeed;
                            }

                        }
                        //Verification à droite
                        if (cellToDeterminateState.getRightCell() != null) {
                            if (cellToDeterminateState.getRightCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread -= westWindSpeed;
                            }

                            //Verification en bas à droite
                            if (cellToDeterminateState.getRightCell().getBottomCell() != null) {
                                if (cellToDeterminateState.getRightCell().getBottomCell().getCellValue() == FIRE) {
                                    nbNextCellWithFire++;
                                }
                            }
                        }
                        //Verification en bas
                        if (cellToDeterminateState.getBottomCell() != null) {
                            if (cellToDeterminateState.getBottomCell().getCellValue() == FIRE) {
                                nbNextCellWithFire++;
                                currentProbabilityOfSpread += southWindSpeed;
                            }

                        }
                        stateOfCell = random <= nbNextCellWithFire * currentProbabilityOfSpread ? FIRE : FOREST;
                    }
                }
            }
        }
        return stateOfCell;
    }

    public int getPercentageBurnedForest() {
        LinkedCell<ForestState> currentCell = this.forest;

        int numberInitialForestCell = 0;
        int numberBurnedCell = 0;

        for (int ii = 0; ii < this.y; ii++) {
            for (int jj = 0; jj < this.x; jj++) {
                if (currentCell.getCellValue() == BURNED) {
                    numberBurnedCell++;
                    // Il faut le nombre de cellule à l'état forêt dès le début
                    numberInitialForestCell++;
                }
                if (currentCell.getCellValue() == FOREST) {
                    numberInitialForestCell++;
                }
                currentCell = currentCell.getNextCell();
            }
        }

        if (numberInitialForestCell != 0) {
            return (numberBurnedCell * 100 / numberInitialForestCell);
        }

        return -1;
    }

    public boolean isStillFireInForest() {
        boolean stillFireInForest = false;

        LinkedCell<ForestState> currentCell = this.forest;
        for (int ii = 0; ii < this.y; ii++) {
            for (int jj = 0; jj < this.x; jj++) {
                if (currentCell.getCellValue() == FIRE) {
                    return true;
                }
                currentCell = currentCell.getNextCell();
            }
        }
        return stillFireInForest;
    }

    /**
     * Simule le prochaine état de la forêt.
     */
    public void simulateForestFire() {
        // Liste du prochain état de toutes les cellules de la forêt.
        // Il n'est pas possible de modifier l'état de la forêt pendant le parcours car le calcul de l'état de la prochaine cellule sera faussé
        List<ForestState> nextStateOfForest = new ArrayList<>();
        LinkedCell<ForestState> currentCell = this.forest;

        // Calcul du prochain état de toutes les cellules.
        for (int ii = 0; ii < this.y; ii++) {
            for (int jj = 0; jj < this.x; jj++) {
                nextStateOfForest.add(determineNextState(currentCell));
                currentCell = currentCell.getNextCell();
            }
        }

        LinkedCell<ForestState> newForest = new LinkedCell<ForestState>(EMPTY);

        // Création de la forêt avec l'état n + 1
        for (int ii = nextStateOfForest.size() - 1; ii >= 0; ii--) {
            if ((ii + 1) % this.x == 0) {
                newForest.addNewLine(nextStateOfForest.get(ii));
            }
            else {
                newForest.add(nextStateOfForest.get(ii));
            }
        }

        this.forest = newForest;
    }

    /**
     * Vérifie si il reste du feu dans la forêt
     *
     * @param probabilityOfSpread la probabilité de propagation du feux.
     * @param westWindSpeed la force du vent ouest (si négatif la force du vent d'est).
     * @param southWindSpeed la force du vent sud (si négatif la force du vent du nord).
     */
    public boolean Extinctionfeu(int probabilityOfSpread, int westWindSpeed, int southWindSpeed){
        boolean Dufeu=true;
        int nbenfeu=0;
        // Liste du prochain état de toutes les cellules de la forêt.
        // Il n'est pas possible de modifier l'état de la forêt pendant le parcours car le calcul de l'état de la prochaine cellule sera faussé
        List<ForestState> nextStateOfForest = new ArrayList<>();
        LinkedCell<ForestState> currentCell = this.forest;

        // Calcul du prochain état de toutes les cellules.
        for (int ii = 0; ii < this.y; ii++) {
            for (int jj = 0; jj< this.x; jj++) {
                nextStateOfForest.add(determineNextState(currentCell));
                currentCell = currentCell.getNextCell();
                if (currentCell.getCellValue()==FIRE){
                    nbenfeu+=1;
                }
            }
        }
        if (nbenfeu==0){
            Dufeu=false;
            return Dufeu;
        }
        else {
            Dufeu=true;
            return Dufeu;
        }
    }

    /**
     * Renvoi la valeur des abscisses de la grille.
     *
     * @return la valeur des abscisses de la grille.
     */
    public int getX() {
        return x;
    }

    /**
     * Modifie la valeur des abscisses de la grille.
     *
     * @param x la nouvelle valeur des abscisses.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Renvoi la valeur des ordonnées de la grille.
     *
     * @return la valeur des ordonnées de la grille.
     */
    public int getY() {
        return y;
    }

    /**
     * Modifie la valeur des ordonnées de la grille.
     *
     * @param y la nouvelle valeur des ordonnées.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Renvoi le nombre de cellule voisine utilisé pour définir le prochain état d'une cellule.
     *
     * @return le nombre de cellule voisine utilisé pour définir le prochain état d'une cellule.
     */
    public int getNbNextCell() {
        return nbNextCell;
    }

    /**
     * Modifie le nombre de cellule voisine utilisé pour définir le prochain état d'une cellule.
     *
     * @param nbNextCell le nombre de cellule voisine utilisé pour définir le prochain état d'une cellule.
     */
    public void setNbNextCell(int nbNextCell) {
        this.nbNextCell = nbNextCell;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }

    public int getProbabilityOfSpread() {
        return probabilityOfSpread;
    }

    public void setProbabilityOfSpread(int probabilityOfSpread) {
        this.probabilityOfSpread = probabilityOfSpread;
    }

    public int getWestWindSpeed() {
        return westWindSpeed;
    }

    public void setWestWindSpeed(int westWindSpeed) {
        this.westWindSpeed = westWindSpeed;
    }

    public int getSouthWindSpeed() {
        return southWindSpeed;
    }

    public void setSouthWindSpeed(int southWindSpeed) {
        this.southWindSpeed = southWindSpeed;
    }

    public LinkedCell<ForestState> getForest() {
        return forest;
    }

    public void setForest(LinkedCell<ForestState> forest) {
        this.forest = forest;
    }
}
