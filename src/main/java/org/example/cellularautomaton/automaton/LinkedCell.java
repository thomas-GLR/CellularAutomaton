package org.example.cellularautomaton.automaton;

public class LinkedCell<E> implements Cloneable {
    private E cellValue;
    // La cellule à la suite de la liste chainée.
    private LinkedCell<E> nextCell;
    // Les cellules voisines de la cellule en cours.
    private LinkedCell<E> leftCell;
    private LinkedCell<E> topCell;
    private LinkedCell<E> rightCell;
    private LinkedCell<E> bottomCell;

    /**
     * Constructeur par défaut qui initialise avec une valeur
     * @param cellValue la valeur de la cellule.
     */
    public LinkedCell(E cellValue) {
        this.cellValue = cellValue;
    }

    /**
     * Constructeur qui créé une cellule en renseignant sa valeur et la prochaine cellule.
     * @param cellValue la valeur de la cellule.
     * @param linkedCell la cellule d'après.
     */
    public LinkedCell(E cellValue, LinkedCell<E> linkedCell) {
        this.cellValue = cellValue;
        this.nextCell = linkedCell;
    }

    /**
     * Ajoute un élément au début de la liste chainée. Set les cellules voisines.
     * @param cellValue la valeur de la nouvelle cellules
     */
    public void add(E cellValue) {
        // Clonage de la cellule courante, elle remplacera la cellule courante qui sera lié à cette dernière
        LinkedCell<E> newCell = this.clone();
        // Les voisins de la nouvelle cellules sont renseignés
        newCell.setRightCell(this.rightCell);
        newCell.setBottomCell(this.bottomCell);
        newCell.setTopCell(this.topCell);

        // La cellule courante devient la nouvelle cellule qu'on ajoute
        this.cellValue = cellValue;
        this.nextCell = newCell;

        this.nextCell.setLeftCell(this);
        // Comme un clone a été créé les cellules d'avant pointent sur la cellule en cours.
        // Il faut donc qu'elles pointent sur le clone
        if (this.nextCell.nextCell != null) {
            this.nextCell.nextCell.setLeftCell(this.nextCell);
        }
        this.setRightCell(this.nextCell);

        if (this.getBottomCell() != null) {
            this.getBottomCell().getLeftCell().setTopCell(this);
            this.nextCell.getBottomCell().setTopCell(this.nextCell);
            this.setBottomCell(this.getBottomCell().getLeftCell());
        }
    }

    /**
     * Ajoute une nouvelle cellule à l'avant de la liste chainée en créant une nouvelle ligne lorsque la liste chainée est utilisée
     * comme une grille. La valeur de la celulle de droite est donc à NULL.
     * @param cellValue la valeur de la nouvelle cellule
     */
    public void addNewLine(E cellValue) {
        LinkedCell<E> newCell = this.clone();
        newCell.setRightCell(this.rightCell);
        newCell.setBottomCell(this.bottomCell);
        newCell.setTopCell(this.topCell);

        // Une nouvelle ligne est ajoutée donc pas de cellule à droite (l'ajout d'un cellule s'effectue de droite à gauche)
        this.rightCell = null;
        this.cellValue = cellValue;
        this.nextCell = newCell;

        LinkedCell<E> bottomLinkedCell = getFirstCellWithRightNull(this.nextCell);
        this.setBottomCell(bottomLinkedCell);

        if (this.nextCell.getBottomCell() != null) {
            this.nextCell.getBottomCell().setTopCell(this.nextCell);
        }

        this.nextCell.setLeftCell(this);
        // La deuxième cellule ne peut pas avoir 2 enfants
        if (this.nextCell.nextCell != null) {
            this.nextCell.nextCell.setLeftCell(this.nextCell);
        }
    }

    /**
     * Renvoi la prochaine cellule la plus à droite. Cette fonction permet de récupérer la cellule du bas
     * lorsqu'une nouvelle ligne dans une grille est créée.
     * @param nextLinkedCell la cellule de départ.
     * @return la cellule qui n'a pas de cellule à sa droite
     */
    private LinkedCell<E> getFirstCellWithRightNull(LinkedCell<E> nextLinkedCell) {
        // Si je suis à la première cellule de la liste chainée alors je renvoi null
        if (nextLinkedCell.nextCell == null) {
            return null;
        }
        if (nextLinkedCell.getRightCell() == null) {
            return nextLinkedCell;
        } else {
            return getFirstCellWithRightNull(nextLinkedCell.nextCell);
        }
    }

    /**
     * Renvoi tout le contenu de la list chainée sous forme d'une chaine (contient également les 4 cellules voisines de chaque cellule).
     * @return la chaine avec les informations de la liste chainée.
     */
    @Override
    public String toString() {
        LinkedCell<E> currentCell = this;
        String res = "";
        do {
            String leftValue = currentCell.getLeftCell() == null ? "x" : currentCell.getLeftCell().getCellValue().toString();
            String rightValue = currentCell.getRightCell() == null ? "x" : currentCell.getRightCell().getCellValue().toString();
            String topValue = currentCell.getTopCell() == null ? "x" : currentCell.getTopCell().getCellValue().toString();
            String bottomValue = currentCell.getBottomCell() == null ? "x" : currentCell.getBottomCell().getCellValue().toString();
            String voisin = " (left : " + leftValue +
                    ", top : " + topValue +
                    ", right : " + rightValue +
                    ", bottom : " + bottomValue + ")";
            res += currentCell.cellValue.toString() + voisin + " -> \n";
            currentCell = currentCell.nextCell;
        }
        while (currentCell!=null);
        return res;
    }

    /**
     * Renvoi un clone de la cellule courante.
     * @return le clone de la celulle courante.
     */
    @Override
    public LinkedCell<E> clone() {
        return (new LinkedCell<E>(this.cellValue, this.nextCell));
    }

    public LinkedCell<E> getFirstCell() {
        LinkedCell<E> currentCell = this;
        while (currentCell.getLeftCell() != null) {
            currentCell = currentCell.getLeftCell();
        }
        return currentCell;
    }

    public LinkedCell<E> getLastCell() {
        LinkedCell<E> currentCell = this;
        while (currentCell.getRightCell() != null) {
            currentCell = currentCell.getNextCell();
        }
        return currentCell;
    }

    /**
     * Renvoie la valeur stockée dans la cellule courante.
     * @return la valeur stockée dans la cellule courante.
     */
    public E getCellValue() {
        return cellValue;
    }

    /**
     * Renvoie la prochaine cellule de la liste chainée.
     * @return la prochaine cellule.
     */
    public LinkedCell<E> getNextCell() {
        return nextCell;
    }

    public LinkedCell<E> getLeftCell() {
        return leftCell;
    }

    /**
     * Renvoi la cellule à gauche de la cellule courante.
     * @return la cellule à gauche de la cellule courante.
     */
    public LinkedCell<E> getTopCell() {
        return topCell;
    }

    /**
     * Renvoi la cellule à droite de la cellule courante.
     * @return la cellule à droite de la cellule courante.
     */
    public LinkedCell<E> getRightCell() {
        return rightCell;
    }

    /**
     * Renvoi la cellule en bas de la cellule courante.
     * @return la cellule en bas de la cellule courante.
     */
    public LinkedCell<E> getBottomCell() {
        return bottomCell;
    }

    /**
     * Met à jour la cellule en haut de la cellule courante.
     * @param topCell la cellule en haut de la cellule courante.
     */
    public void setTopCell(LinkedCell<E> topCell) {
        this.topCell = topCell;
    }

    /**
     * Met à jour la cellule à droite de la cellule courante.
     * @param rightCell la cellule à droite de la cellule courante.
     */
    public void setRightCell(LinkedCell<E> rightCell) {
        this.rightCell = rightCell;
    }

    /**
     * Met à jour la cellule à gauche de la cellule courante.
     * @param leftCell la cellule à gauche de la cellule courante.
     */
    public void setLeftCell(LinkedCell<E> leftCell) {
        this.leftCell = leftCell;
    }

    /**
     * Met à jour la cellule en bas de la cellule courante.
     * @param bottomCell la cellule en bas de la cellule courante.
     */
    public void setBottomCell(LinkedCell<E> bottomCell) {
        this.bottomCell = bottomCell;
    }
}
