package org.example.cellularautomaton.automaton1d;

import org.example.cellularautomaton.automaton.LinkedCell;

import java.util.ArrayList;
import java.util.List;

public class Automaton1D {
    private int size;
    private int rule;
    private LinkedCell<Integer> cells;

    public Automaton1D(int size, int rule) {
        this.size = size;
        this.rule = rule;
        initializeAutomaton();
    }

    public void initializeAutomaton() {
        LinkedCell<Integer> current = new LinkedCell<>(0);
        for (int ii = this.size - 1; ii >= 0; ii--) {
            if (ii == this.size) {
                current.addNewLine(0);
            }
            else {
                current.add(ii == size / 2 ? 1 : 0);
            }
        }

        this.cells = current;
    }

    public void simulateAGeneration() {
        List<Integer> nextStateOf1DAutomaton = new ArrayList<>();
        LinkedCell<Integer> currentCell = this.cells;

        for (int cellIndex = 0; cellIndex < this.size; cellIndex++) {
            nextStateOf1DAutomaton.add(getNextState(currentCell));
            currentCell = currentCell.getNextCell();
        }

        LinkedCell<Integer> new1DAutomaton = new LinkedCell<Integer>(0);

        for (int ii = nextStateOf1DAutomaton.size() - 1; ii >= 0; ii--) {
            // The first cell we add don't have right cell neighbor
            if (ii == nextStateOf1DAutomaton.size()) {
                new1DAutomaton.addNewLine(nextStateOf1DAutomaton.get(ii));
            }
            else {
                new1DAutomaton.add(nextStateOf1DAutomaton.get(ii));
            }
        }

        this.cells = new1DAutomaton;
    }

    private Integer getNextState(LinkedCell<Integer> cellToDeterminateState) {
        // When there is no next cell then the opposite cell value is return like an infinite loop
        Integer rightCellValue = cellToDeterminateState.getRightCell() != null ?
                cellToDeterminateState.getRightCell().getCellValue() :
                cellToDeterminateState.getFirstCell().getCellValue();
        Integer leftCellValue = cellToDeterminateState.getLeftCell() != null ?
                cellToDeterminateState.getLeftCell().getCellValue() :
                cellToDeterminateState.getLastCell().getCellValue();

        String initialPattern = String.valueOf(leftCellValue);
        initialPattern += String.valueOf(cellToDeterminateState.getCellValue());
        initialPattern += String.valueOf(rightCellValue);

        List<Integer> binaryRule = convertRuleToBinary();

        int indexToGetTheNewValue = Integer.parseInt(initialPattern, 2);
        return binaryRule.get(indexToGetTheNewValue);
    }

    private List<Integer> convertRuleToBinary() {
        String binaryRule = Integer.toBinaryString(this.rule);
        List<Integer> newValues = new ArrayList<>();

        // The last byte must be at index 0 because 0 in binary represent the last byte of the rule
        for (int indexBinary = binaryRule.length() - 1; indexBinary >= 0; indexBinary--) {
            newValues.add(Character.getNumericValue(binaryRule.charAt(indexBinary)));
        }

        // If rule 30 is selected for example then it's only 5 bytes (11110) and we need a list with 8 byte the
        // it will add 3 bytes
        if (newValues.size() < 8) {
            int numberOfByteToAdd = 8 - newValues.size();

            for (int ii = 0; ii < numberOfByteToAdd; ii++) {
                newValues.add(0);
            }
        }

        return newValues;
    }

    public void printCurrentStateOfAutomaton() {
        LinkedCell<Integer> currentCell = this.cells;

        StringBuilder stringToPrint = new StringBuilder();

        for (int ii = 0; ii < this.size; ii++) {
            if (currentCell.getCellValue() == 1) {
                stringToPrint.append("*");
            }
            else {
                stringToPrint.append(" ");
            }
            currentCell = currentCell.getNextCell();
        }
        System.out.println(stringToPrint);
    }
}
