package com.github.mohsenpakzad;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final Random random = new Random();
    private static List<Sudoku> population;

    public static void main(String[] args) {

        int[][] solvedTable = new int[][]{
                new int[]{5, 3, 4, 6, 7, 8, 9, 1, 2},
                new int[]{6, 7, 2, 1, 9, 5, 3, 4, 8},
                new int[]{1, 9, 8, 3, 4, 2, 5, 6, 7},

                new int[]{8, 5, 9, 7, 6, 1, 4, 2, 3},
                new int[]{4, 2, 6, 8, 5, 3, 7, 9, 1},
                new int[]{7, 1, 3, 9, 2, 4, 8, 5, 6},

                new int[]{9, 6, 1, 5, 3, 7, 2, 8, 4},
                new int[]{2, 8, 7, 4, 1, 9, 6, 3, 5},
                new int[]{3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        Sudoku solved = new Sudoku(solvedTable);
//        System.out.println(solved.calculateFitness());

        int[][] assumptionTable = new int[][]{
                new int[]{5, 3, 0, 0, 7, 0, 0, 0, 0},
                new int[]{6, 0, 0, 1, 9, 5, 0, 0, 0},
                new int[]{0, 9, 8, 0, 0, 0, 0, 6, 0},

                new int[]{8, 0, 0, 0, 6, 0, 0, 0, 3},
                new int[]{4, 0, 0, 8, 0, 3, 0, 0, 1},
                new int[]{7, 0, 0, 0, 2, 0, 0, 0, 6},

                new int[]{0, 6, 0, 0, 0, 0, 2, 8, 0},
                new int[]{0, 0, 0, 4, 1, 9, 0, 0, 5},
                new int[]{0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        Sudoku sudoku = new Sudoku(assumptionTable);

        GeneticSearch<Sudoku> search = new GeneticSearch<>(
                makeInitialPopulation(sudoku, 100),
                50,
                10,
                30 // It's better to purge rate not greater than of double of crossover rate
        );
        System.out.println("Solved sudoku:\n" + search.run(0));
    }

    private static List<Sudoku> makeInitialPopulation(Sudoku initialSudoku, int populationNumber) {

        List<Sudoku> population = new LinkedList<>();

        for (int i = 0; i < populationNumber; i++) {
            Sudoku copiedSudoku = initialSudoku.deepCopy();
            copiedSudoku.fillUnknownCellsRandomly();
            population.add(copiedSudoku);
        }
        return population;
    }
}

