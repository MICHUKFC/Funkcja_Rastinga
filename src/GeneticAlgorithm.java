import java.util.*;

public class GeneticAlgorithm {
    private static final double A = 10.0;
    private static final double OMEGA = 2 * Math.PI * 20;
    private static final int POPULATION_SIZE = 100;
    private static final int GENOME_LENGTH = 5; // Długość łańcucha dla jednej zmiennej
    private static final double MUTATION_RATE = 0.01;
    private static final int GENERATIONS = 100;
    private static final int DIMENSIONS = 4; // n-wymiarowy problem
    private static final int TOURNAMENT_SIZE = 5; // zmienna do selekcji turniejowej
    private static Random random = new Random();


    // Klasa reprezentująca pojedynczy osobnik
    private static class Individual {
        String genome;
        double fitness;

        Individual(String genome) {
            this.genome = genome;
            this.fitness = GeneticAlgorithm.fitness(genome);
        }

        void mutate() {
            this.genome = GeneticAlgorithm.mutate(this.genome);
            this.fitness = GeneticAlgorithm.fitness(this.genome);
        }

        void invert(double rate) {
            this.genome = GeneticAlgorithm.invert(this.genome, rate);
            this.fitness = GeneticAlgorithm.fitness(this.genome);
        }
    }

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


    //Sprawdzenie minimum lub maksimum przy zmianie znaku na returnie (- lub +)

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

    public static String[] twoPointCrossover(String parent1, String parent2) {
        int point1 = random.nextInt(parent1.length());
        int point2 = random.nextInt(parent1.length());
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }
        String child1 = parent1.substring(0, point1) + parent2.substring(point1, point2) + parent1.substring(point2);
        String child2 = parent2.substring(0, point1) + parent1.substring(point1, point2) + parent2.substring(point2);
        return new String[]{child1, child2};
    }

    public static String[] multiPointCrossover(String parent1, String parent2, int numberOfPoints) {
        Set<Integer> points = new TreeSet<>();
        while (points.size() < numberOfPoints) {
            points.add(random.nextInt(parent1.length()));
        }

        boolean switchParent = false;
        StringBuilder child1 = new StringBuilder();
        StringBuilder child2 = new StringBuilder();
        int start = 0;
        for (int point : points) {
            if (switchParent) {
                child1.append(parent2, start, point);
                child2.append(parent1, start, point);
            } else {
                child1.append(parent1, start, point);
                child2.append(parent2, start, point);
            }
            start = point;
            switchParent = !switchParent;
        }
        child1.append(switchParent ? parent2.substring(start) : parent1.substring(start));
        child2.append(switchParent ? parent1.substring(start) : parent2.substring(start));

        return new String[]{child1.toString(), child2.toString()};
    }

    public static String[] uniformCrossover(String parent1, String parent2) {
        StringBuilder child1 = new StringBuilder();
        StringBuilder child2 = new StringBuilder();

        for (int i = 0; i < parent1.length(); i++) {
            if (random.nextBoolean()) {
                child1.append(parent1.charAt(i));
                child2.append(parent2.charAt(i));
            } else {
                child1.append(parent2.charAt(i));
                child2.append(parent1.charAt(i));
            }
        }

        return new String[]{child1.toString(), child2.toString()};
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

    public static String invert(String genome, double inversionRate) {
        if (Math.random() < inversionRate) {
            int startIndex = random.nextInt(genome.length());
            int endIndex = random.nextInt(genome.length());
            if (startIndex > endIndex) {
                int temp = startIndex;
                startIndex = endIndex;
                endIndex = temp;
            }
            char[] chars = genome.toCharArray();
            while (startIndex < endIndex) {
                char temp = chars[startIndex];
                chars[startIndex] = chars[endIndex];
                chars[endIndex] = temp;
                startIndex++;
                endIndex--;
            }
            return new String(chars);
        }
        return genome;
    }

    // Metoda do wyboru rodzica z populacji
    public static Individual tournamentSelection(Individual[] population) {
        Individual best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int index = random.nextInt(population.length);
            if (population[index].fitness > bestFitness) {
                bestFitness = population[index].fitness;
                best = population[index];
            }
        }

        return new Individual(best.genome); // Tworzenie kopii
    }

    // Metoda generująca nową generację
    private static Individual[] generateNewGeneration(Individual[] population) {
        Individual[] newGeneration = new Individual[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i += 2) {
            Individual parent1 = tournamentSelection(population);
            Individual parent2 = tournamentSelection(population);

            String[] childrenGenomes = crossover(parent1.genome, parent2.genome);

            Individual child1 = new Individual(childrenGenomes[0]);
            Individual child2 = new Individual(childrenGenomes[1]);

            child1.mutate();
            child2.mutate();

            child1.invert(MUTATION_RATE);
            child2.invert(MUTATION_RATE);

            newGeneration[i] = child1;
            newGeneration[i + 1] = child2;
        }

        return newGeneration;
    }
    public static void main(String[] args) {
        Individual[] population = new Individual[POPULATION_SIZE];

        // Inicjalizacja populacji
        for (int i = 0; i < POPULATION_SIZE; i++) {
            StringBuilder genome = new StringBuilder();
            for (int j = 0; j < GENOME_LENGTH * DIMENSIONS; j++) {
                genome.append(random.nextInt(2));
            }
            population[i] = new Individual(genome.toString());
        }

        // Główna pętla algorytmu
        for (int generation = 0; generation < GENERATIONS; generation++) {
            Arrays.sort(population, Comparator.comparingDouble(a -> -a.fitness));
            population = generateNewGeneration(population);

            System.out.println("Generation " + generation + ": " + Arrays.toString(decode(population[0].genome)) + " = " + population[0].fitness);
        }
    }
}
