import java.util.Random;
import java.util.Arrays;
import java.util.stream.IntStream;

public class GeneticAlgorithm {
    private static final double A = 10.0;
    private static final double OMEGA = 2 * Math.PI * 20;
    private static final int POPULATION_SIZE = 100;
    private static final int GENOME_LENGTH = 5; // Długość łańcucha dla jednej zmiennej
    private static final double MUTATION_RATE = 0.01;
    private static final int GENERATIONS = 1000;
    private static final int DIMENSIONS = 3; // n-wymiarowy problem
    private static final int TOURNAMENT_SIZE = 5;
    private static Random random = new Random();

    public static double rastrigin(double[] x) {
        int n = x.length;
        double sum = A * n;

        for (int i = 0; i < n; i++) {
            sum += x[i] * x[i] - A * Math.cos(OMEGA * x[i]);
        }

        return sum;
    }

    public static double[] decode(String genome) {
        double[] x = new double[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            String subGenome = genome.substring(i * GENOME_LENGTH, (i + 1) * GENOME_LENGTH);
            int intValue = Integer.parseInt(subGenome, 2);
            x[i] = -1.0 + (2.0 / (Math.pow(2, GENOME_LENGTH) - 1)) * intValue;
        }
        return x;
    }

    public static double fitness(String genome) {
        double[] x = decode(genome);
        return -rastrigin(x);
    }

    public static String mutate(String genome) {
        char[] chars = genome.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Math.random() < MUTATION_RATE) {
                chars[i] = chars[i] == '0' ? '1' : '0';
            }
        }
        return new String(chars);
    }

    public static String[] crossover(String parent1, String parent2) {
        int crossoverPoint = random.nextInt(parent1.length());
        return new String[]{
                parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint),
                parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint)
        };
    }

    public static String tournamentSelection(String[] population) {
        String best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int index = random.nextInt(population.length);
            double currentFitness = fitness(population[index]);
            if (currentFitness > bestFitness) {
                bestFitness = currentFitness;
                best = population[index];
            }
        }

        return best;
    }

    public static String rouletteSelection(String[] population) {
        double totalFitness = Arrays.stream(population).mapToDouble(GeneticAlgorithm::fitness).sum();
        double rouletteSpin = Math.random() * totalFitness;
        double currentSum = 0;

        for (String individual : population) {
            currentSum += fitness(individual);
            if (currentSum >= rouletteSpin) {
                return individual;
            }
        }

        return population[population.length - 1];
    }

    public static String rankSelection(String[] population) {
        String[] sortedPopulation = Arrays.stream(population).sorted((a, b) -> Double.compare(fitness(a), fitness(b))).toArray(String[]::new);
        int rankSum = (POPULATION_SIZE * (POPULATION_SIZE + 1)) / 2;
        int rankSpin = random.nextInt(rankSum);
        int currentSum = 0;

        for (int i = 0; i < sortedPopulation.length; i++) {
            currentSum += (i + 1);
            if (currentSum >= rankSpin) {
                return sortedPopulation[i];
            }
        }

        return sortedPopulation[sortedPopulation.length - 1];
    }

    public static void main(String[] args) {
        String[] population = new String[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++) {
            StringBuilder genome = new StringBuilder();
            for (int j = 0; j < GENOME_LENGTH * DIMENSIONS; j++) {
                genome.append(random.nextInt(2));
            }
            population[i] = genome.toString();
        }

        for (int generation = 0; generation < GENERATIONS; generation++) {
            population = Arrays.stream(population).sorted((a, b) -> Double.compare(fitness(b), fitness(a))).toArray(String[]::new);
            String[] newPopulation = new String[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i += 2) {
                String parent1 = tournamentSelection(population);
                String parent2 = rouletteSelection(population);
                String[] children = crossover(parent1, parent2);
                newPopulation[i] = mutate(children[0]);
                newPopulation[i + 1] = mutate(children[1]);
            }
            population = newPopulation;

            System.out.println("Generation " + generation + ": " + Arrays.toString(decode(population[0])) + " = " + (-fitness(population[0])));
        }
    }
}