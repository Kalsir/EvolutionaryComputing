import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;

public class player73 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
	private int evaluations_limit_;

	// Parameters
	private int population_size = 100;
	private int generation_size = 100;
	private double mutation_prob = 1;
	private double mutation_sd = 0.5;

	public player73()
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
			population[i] = new Individual(genotype, fitness);
		}

		// We evaluated once for each starting individual
		int evals = population_size;

		// Sort population based on fitness
		Arrays.sort(population);

		// Report best score in initial population
		double best_score = population[population_size - 1].getFitness();
		System.out.println("Best initial score: " + Double.toString(best_score));
		mutation_sd = 0.5 - best_score/20;

		// Run evolutionary algorithm
		while(evals<evaluations_limit_)
		{
			// Keep track of how many evaluations have been done
			if (evals % 10000 == 0)
				System.out.println(evals);
			evals+= generation_size;

			// Create array to hold offspring
			Individual offspring[] = new Individual[generation_size];

			// Create offspring
			for (int j = 0; j < generation_size; j++)
			{
				// Select parents
				int parent1 = population_size -1;
				int parent2 = population_size -2;

				// Create child genotype
				double child_genotype[] = new double[10];

				// Recombination
				for (int i = 0; i < 10; i++){
					child_genotype[i] += population[parent1].getGenotype()[i];
					child_genotype[i] += population[parent2].getGenotype()[i];
					child_genotype[i] /= 2;
				}

				// Mutation
				for (int i = 0; i < 10; i++){
					double rnd = rnd_.nextDouble();
					if (rnd < mutation_prob)
						child_genotype[i] = Math.max(-5, Math.min(5, child_genotype[i] + rnd_.nextGaussian() * mutation_sd));
				}

				// Evaluate new child
				double child_fitness = (double) evaluation_.evaluate(child_genotype);
				if (child_fitness > best_score) {
					best_score = child_fitness;
					mutation_sd = 0.5 - best_score/20;
					System.out.println("Score update: " + Double.toString(best_score));
				}
				offspring[j] = new Individual(child_genotype, child_fitness);
			}

			// Replace weakest individuals
			for (int j = 0; j < generation_size; j++)
				population[j] = offspring[j];

			// Resort population
			Arrays.sort(population);
		}
	}
}