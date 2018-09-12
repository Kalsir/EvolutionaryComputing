import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;

public class player72 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;

    // Parameters
    private int population_size = 50;
    private int generation_size = 50;
    private boolean uniform_mutation = false;
    private boolean fitness_proportional = false;
    private double mutation_prob = 1;
    private double mutation_sd = 0.1;
    private double rank_selection_pressure = 2; // 1 < s <= 2
	
	public player72()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }
    
	public void run()
	{
		double best_score = 0;
        int evals = population_size;

        double cumulative_probabilities[] = new double[population_size];
        double accumulator = 0;
        for (int i = 0; i < population_size; i++){
        	accumulator += (2 - rank_selection_pressure) / population_size + (2*i*(rank_selection_pressure - 1))/(population_size*(population_size - 1));
			//accumulator += 1 - Math.pow(Math.E, -i);
        	cumulative_probabilities[i] = accumulator;
			System.out.println(cumulative_probabilities[i]);
		}
		/*for (int i = 1; i < population_size; i++) {
			cumulative_probabilities[i] /= cumulative_probabilities[population_size - 1];
			System.out.println(cumulative_probabilities[i]);
		}*/

        // Initialize population and fill it with random individuals
        Individual population[] = new Individual[population_size];
        for(int i = 0; i < population_size; i++)
		{
			double genotype[] = new double[10];
			for(int j=0; j<10; j++)
		    {
		        genotype[j] = rnd_.nextDouble() * 10 - 5;
		    }
			double fitness = (double) evaluation_.evaluate(genotype);
			if (fitness > best_score)
				best_score = fitness;
			population[i] = new Individual(genotype, fitness);
		}

		System.out.println("Best initial score: " + Double.toString(best_score));

		// Sort population based on fitness
		Arrays.sort(population);

		while(evals<evaluations_limit_)
		{
			if (evals % 10000 == 0)
				System.out.println(evals);

			Individual offspring[] = new Individual[generation_size];
			double rnd;

			if (fitness_proportional) {
				cumulative_probabilities[0] = population[0].getFitness();
				for (int i = 1; i < population_size; i++) {
					cumulative_probabilities[i] = cumulative_probabilities[i - 1] + population[i].getFitness();
				}
				for (int i = 1; i < population_size; i++) {
					cumulative_probabilities[i] /= cumulative_probabilities[population_size - 1];
				}
			}

			// Create offspring
			for (int j = 0; j < generation_size; j++)
			{
				// Select parent 1
				int parent1 = population_size - 1;
				rnd = rnd_.nextDouble();
				for (int i = 0; i < population_size; i++) {
					if (rnd <= cumulative_probabilities[i]) {
						parent1 = i;
						break;
					}
				}

				// Select parent 2
				int parent2 = parent1;
				rnd = rnd_.nextDouble();
				for (int i = 0; i < population_size; i++) {
					if (rnd < cumulative_probabilities[i]) {
						parent2 = i;
						break;
					}
				}

				//parent1 = population_size -1;
				//parent2 = population_size -2;

				// Create child genotype
				double child_genotype[] = new double[10];

				// Recombination
				for (int i = 0; i < 10; i++){
					child_genotype[i] += population[parent2].getGenotype()[i] + (population[parent1].getGenotype()[i] - population[parent2].getGenotype()[i])*1.1;
					child_genotype[i] = Math.max(-5, Math.min(5, child_genotype[i]));
				}

				// Mutation
				for (int i = 0; i < 10; i++){
					rnd = rnd_.nextDouble();
					if (rnd < mutation_prob)
					{
						if (uniform_mutation)
							child_genotype[i] = rnd_.nextDouble() * 10 - 5;
						else
							child_genotype[i] = Math.max(-5, Math.min(5, child_genotype[i] + rnd_.nextGaussian() * mutation_sd));
					}
				}

				double child_fitness = (double) evaluation_.evaluate(child_genotype);
				if (child_fitness > best_score) {
					best_score = child_fitness;
					mutation_sd = 0.2 - best_score/100;
					System.out.println("Score update: " + Double.toString(best_score));
				}
				offspring[j] = new Individual(child_genotype, child_fitness);
			}

			// Replace weakest individuals
			for (int j = 0; j < generation_size; j++)
				population[j] = offspring[j];

			// Resort population
			Arrays.sort(population);

			evals+= generation_size;
		}

		for (int i = 0; i < population_size; i++){
			System.out.println(population[i].getFitness());
		}
	}
}