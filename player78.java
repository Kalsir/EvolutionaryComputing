import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;

public class player78 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
	private int evaluations_limit_;

	// Parameters
	private int population_size = 10;
	private int generation_size = 10;

	public player78()
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

	private double[] radius_vector(double child[], double parent1[], double parent2[], double parent3[], double parent4[])
	{
		double sum_of_squares = 0;
		double sum_of_squares2 = 0;
		double random_vector[] = new double[10];

		for(int i=0; i<10; i++)
    	{
    		double rnd = rnd_.nextDouble() - 0.5; 
    		random_vector[i] = rnd;
    		sum_of_squares += Math.pow(rnd, 2);
    	}

    	double distance = 0;

    	for(int i=0; i<10; i++)
    		sum_of_squares2 += Math.pow(child[i] - parent1[i], 2);
    	double new_distance = Math.sqrt(sum_of_squares2)/2;
    	if (new_distance > distance)
    		distance = new_distance;

    	sum_of_squares2 = 0;
    	for(int i=0; i<10; i++)
    		sum_of_squares2 += Math.pow(child[i] - parent2[i], 2);
    	new_distance = Math.sqrt(sum_of_squares2)/2;
    	if (new_distance > distance)
    		distance = new_distance;

    	sum_of_squares2 = 0;
    	for(int i=0; i<10; i++)
    		sum_of_squares2 += Math.pow(child[i] - parent3[i], 2);
    	new_distance = Math.sqrt(sum_of_squares2)/2;
    	if (new_distance > distance)
    		distance = new_distance;

    	sum_of_squares2 = 0;
    	for(int i=0; i<10; i++)
    		sum_of_squares2 += Math.pow(child[i] - parent4[i], 2);
    	new_distance = Math.sqrt(sum_of_squares2)/2;
    	if (new_distance > distance)
    		distance = new_distance;

    	for(int i=0; i<10; i++)
    	{
    		random_vector[i] /= sum_of_squares;
    		random_vector[i] *= distance;
		}		

		return random_vector;				
	}


	public void run()
	{
		int evals = 0;
		// Run evolutionary algorithm
		while(evals<evaluations_limit_)
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
			evals += population_size;

			// Sort population based on fitness
			Arrays.sort(population);

			// Report best score in initial population
			double best_score = population[population_size - 1].getFitness();
			System.out.println("Best initial score: " + Double.toString(best_score));

			boolean contin = true;

			// Run evolutionary algorithm
			while(evals<evaluations_limit_ && contin)
			{
				// Keep track of how many evaluations have been done
				if (evals % 10000 == 0)
					System.out.println(evals);
				evals+= generation_size;

				// Create array to hold offspring
				Individual offspring[] = new Individual[generation_size];

				contin = false;
				// Create offspring
				for (int j = 0; j < generation_size; j++)
				{
					// Select parents
					int parent1 = population_size -1;
					int parent2 = population_size -2;
					int parent3 = population_size -3;
					int parent4 = population_size -4;

					// Create child genotype
					double child_genotype[] = new double[10];

					// Recombination
					for (int i = 0; i < 10; i++){
						child_genotype[i] += population[parent1].getGenotype()[i];
						child_genotype[i] += population[parent2].getGenotype()[i];
						child_genotype[i] += population[parent3].getGenotype()[i];
						child_genotype[i] += population[parent4].getGenotype()[i];
						child_genotype[i] /= 4;
					}

					// Mutation
					double random_vector[] = radius_vector(child_genotype, population[parent1].getGenotype(),population[parent2].getGenotype(), population[parent3].getGenotype(),population[parent4].getGenotype());
					for (int i = 0; i < 10; i++){
							child_genotype[i] += random_vector[i];
					}

					// Evaluate new child
					double child_fitness = (double) evaluation_.evaluate(child_genotype);
					if (child_fitness > best_score) {
						contin = true;
						best_score = child_fitness;
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
}