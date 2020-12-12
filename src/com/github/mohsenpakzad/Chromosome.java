package com.github.mohsenpakzad;

interface Chromosome<T> {

    int getFitnessScore();

    void mutate();

    T crossover(T another);
}
