package com.github.mohsenpakzad;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Sudoku implements Chromosome<Sudoku> {

    private final int[][] assumptionTable;
    private final int[][] unknownTable;

    public Sudoku(int[][] assumptionTable) {
        this.assumptionTable = assumptionTable;
        this.unknownTable = new int[9][9];
    }

    public Sudoku(int[][] assumptionTable, int[][] unknownTable) {
        this.assumptionTable = assumptionTable;
        this.unknownTable = unknownTable;
    }

    public int[][] getUnknownTable() {
        return unknownTable;
    }

    public Sudoku deepCopy() {
        final int[][] assumptionTable = new int[9][9];
        final int[][] unknownTable = new int[9][9];
        for (int i = 0; i < 9; i++) {
            assumptionTable[i] = Arrays.copyOf(this.assumptionTable[i], this.assumptionTable[i].length);
            unknownTable[i] = Arrays.copyOf(this.unknownTable[i], this.unknownTable[i].length);
        }
        return new Sudoku(assumptionTable, unknownTable);
    }

    public void fillUnknownCellsRandomly() {

        List<Integer> rows = IntStream.range(0,9).boxed().collect(Collectors.toList());
        Collections.shuffle(rows);
        for (int i : rows) {
            List<Integer> columns = IntStream.range(0,9).boxed().collect(Collectors.toList());
            Collections.shuffle(columns);
            for (int j : columns) {

                if (isAssumption(i, j)) continue;

                List<Integer> unfilledNumberInRow = findUnfilledNumberInRow(i);

                int matchInt = unfilledNumberInRow.stream()
                        .filter(findUnfilledNumberInColumn(j)::contains)
                        .filter(findUnfilledNumberInCurrentSquare(i, j)::contains)
                        .findAny()
                        .orElse(0);

                if (matchInt != 0) unknownTable[i][j] = matchInt;
                else unknownTable[i][j] = unfilledNumberInRow.get((int) (Math.random() * unfilledNumberInRow.size()));
            }
        }
    }

    private List<Integer> findUnfilledNumberInRow(int rowNumber) {

        List<Integer> unfilledNumbers = IntStream.rangeClosed(1, 9)
                .boxed()
                .collect(Collectors.toList());

        for (int j = 0; j < 9; j++) {
            if (isValidCellNumber(getCellValue(rowNumber, j))) {
                unfilledNumbers.remove((Integer) assumptionTable[rowNumber][j]);
            }
        }
        return unfilledNumbers;
    }

    private List<Integer> findUnfilledNumberInColumn(int columnNumber) {

        List<Integer> unfilledNumbers = IntStream.rangeClosed(1, 9)
                .boxed()
                .collect(Collectors.toList());

        for (int j = 0; j < 9; j++) {
            if (isValidCellNumber(getCellValue(j, columnNumber))) {
                unfilledNumbers.remove((Integer)assumptionTable[j][columnNumber]);
            }
        }
        return unfilledNumbers;
    }

    private List<Integer> findUnfilledNumberInCurrentSquare(int rowNumber, int columnNumber) {

        List<Integer> unfilledNumbers = IntStream.rangeClosed(1, 9)
                .boxed()
                .collect(Collectors.toList());

        for (int k = 0; k < 9; k++) {

            int targetRow = k / 3 + rowNumber / 3 * 3;
            int targetColumn = k % 3 + columnNumber / 3 * 3;

            if (isValidCellNumber(getCellValue(targetRow, targetColumn))) {
                unfilledNumbers.remove((Integer)assumptionTable[targetRow][targetColumn]);
            }
        }
        return unfilledNumbers;
    }

    @Override
    public int getFitnessScore() {

        int score = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                score += calculateVerticalFitness(i, j);
                score += calculateSquareFitness(i, j);
            }
        }
        return score;
    }

    private int calculateSquareFitness(int rowNumber, int columnNumber) {
        int score = 0;
        for (int k = 0; k < 9; k++) {

            int targetRow = k / 3 + rowNumber / 3 * 3;
            int targetColumn = k % 3 + columnNumber / 3 * 3;

            if (rowNumber == targetRow && columnNumber == targetColumn) continue;

            if (getCellValue(rowNumber, columnNumber) == getCellValue(targetRow, targetColumn)) {
                score -= isAssumption(targetRow, targetColumn) ? 10000 : 1;
            }
        }
        return score;
    }

    private int calculateVerticalFitness(int rowNumber, int columnNumber) {
        int score = 0;
        for (int k = 0; k < 9; k++) {

            if (k == rowNumber) continue;

            if (getCellValue(rowNumber, columnNumber) == getCellValue(k, columnNumber)) {
                score -= isAssumption(k, columnNumber) ? 10000 : 1;
            }
        }
        return score;
    }

    @Override
    public Sudoku crossover(Sudoku another) {

        List<Integer> listOfIndex = IntStream.range(0, 9).boxed().collect(Collectors.toList());
        Collections.shuffle(listOfIndex);

        final int[][] newUnknownTable = new int[9][];

        for (int i : listOfIndex.subList(0, listOfIndex.size() / 2)) {
            newUnknownTable[i] = Arrays.copyOf(unknownTable[i], unknownTable.length);
        }
        for (int i : listOfIndex.subList(listOfIndex.size() / 2, listOfIndex.size())) {
            newUnknownTable[i] = Arrays.copyOf(another.getUnknownTable()[i], another.getUnknownTable().length);
        }
        return new Sudoku(this.assumptionTable, newUnknownTable);
    }

    @Override
    public void mutate() {
        shuffleRow((int) (Math.random() * 9));
    }

    private void shuffleRow(int rowNumber) {

        LinkedList<Integer> unknownNumbers = IntStream.rangeClosed(1, 9)
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));

        Arrays.stream(assumptionTable[rowNumber])
                .filter(this::isValidCellNumber)
                .forEach(value -> unknownNumbers.remove((Integer) value));

        Collections.shuffle(unknownNumbers);
        for (int j = 0; j < 9; j++) {
            if (!isAssumption(rowNumber, j)) {
                unknownTable[rowNumber][j] = unknownNumbers.poll();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (isAssumption(i, j)) { // if assumptionTable have valid number
                    builder.append("!" + assumptionTable[i][j] + "! ");
                } else {
                    builder.append("?" + unknownTable[i][j] + "? ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private int getCellValue(int row, int column) {
        return isAssumption(row, column) ? assumptionTable[row][column] : unknownTable[row][column];
    }

    private boolean isAssumption(int row, int column) {
        return isValidCellNumber(assumptionTable[row][column]);
    }

    private boolean isValidCellNumber(int value) {
        return value >= 1 && value <= 9;
    }
}
