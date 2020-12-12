package com.github.mohsenpakzad;

import java.util.*;
import java.util.stream.Collectors;

class GeneticSearch<T extends Chromosome<T>> {

    private final List<T> population;
    private final int crossoverRate;
    private final int mutationRate;
    private final int purgeRate;

    public GeneticSearch(List<T> population, int crossoverRate, int mutationRate, int purgeRate) {
        this.population = population;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.purgeRate = purgeRate;
    }

    public T run(int goalScore) {

        T bestScoreElement = findBestScoreElement();
        int globalMaxScore = bestScoreElement.getFitnessScore();
        System.out.println("Best founded of initial generation, Score: " + globalMaxScore);

        int generationNumber;
        for (generationNumber = 1; bestScoreElement.getFitnessScore() != goalScore; generationNumber++) {

            if (bestScoreElement.getFitnessScore() > globalMaxScore) {
                globalMaxScore = bestScoreElement.getFitnessScore();
                System.out.print("New Best founded in generation(" + generationNumber + ") Score: " + globalMaxScore);
                System.out.println(", Current population : " + population.size());
            }

            List<T> selectElements = selection();

            if (probabilityToHappen(crossoverRate)) crossover(selectElements);

            if (probabilityToHappen(mutationRate)) mutate(selectElements);

            if (probabilityToHappen(purgeRate)) purge();

            bestScoreElement = findBestScoreElement();
        }
        System.out.println("Solution found in generation" + generationNumber);
        return bestScoreElement;
    }

    private T findBestScoreElement() {
        return population.stream()
                .max(Comparator.comparing(T::getFitnessScore))
                .get();
    }

    private List<T> selection() {
        return getRandomCountOfSelectedPopulation(population);
    }

    public List<T> getRandomCountOfSelectedPopulation(List<T> selectedPopulation) {
        return selectedPopulation.size() > 1 ?
                new Random().ints(
                        (long) (Math.random() * selectedPopulation.size()),
                        0,
                        selectedPopulation.size())
                        .boxed()
                        .map(selectedPopulation::get)
                        .collect(Collectors.toList())
                : Collections.emptyList();
    }

    private void crossover(List<T> selectElements) {

        List<T> newGeneration = new LinkedList<>();
        for (int i = 0; i < Math.random() * selectElements.size(); i++) {
            T parent1 = selectElements.get((int) (Math.random() * selectElements.size()));
            T parent2 = null;
            while(parent2 != parent1) parent2 = selectElements.get((int) (Math.random() * selectElements.size()));
            newGeneration.add(parent1.crossover(parent2));
        }
        population.addAll(newGeneration);

    }

    private void mutate(List<T> selectElements) {
        getRandomCountOfSelectedPopulation(selectElements).forEach(Chromosome::mutate);
    }

    private void purge() {
        int averageScore = population.stream()
                .mapToInt(T::getFitnessScore)
                .sum() / population.size();

        population.removeAll(population.stream()
                .filter(s -> s.getFitnessScore() < averageScore)
                .limit((long) (Math.random() * (population.size() / 2)))
                .collect(Collectors.toSet())
        );
    }

    private boolean probabilityToHappen(int rate) {
        return Math.random() * 100 < rate;
    }
}
