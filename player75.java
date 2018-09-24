import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class player75 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;

    // Parameters
    private int initial_population_size = 50;
    private int generation_size = 50;
    private double improvement_treshold = 0.01;
    private int ancestor_depth = 5;
    private double mutation_prob = 1;
    private double mutation_sd = 0.5;
    private int max_age = 5;
	
	public player75()
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
		int total_evals = 0;
		int population_count = 0;

		while(total_evals<evaluations_limit_)
		{
			population_count++;
			double best_score = 0;
	        int evals = initial_population_size;

	        // Initialize population and fill it with random individuals
	        ArrayList<Individual> population = new ArrayList<Individual>();
	        for(int i = 0; i < initial_population_size; i++)
			{
				double genotype[] = new double[10];
				for(int j=0; j<10; j++)
			    {
			        genotype[j] = rnd_.nextDouble() * 10 - 5;
			    }
				double fitness = (double) evaluation_.evaluate(genotype);
				if (fitness > best_score)
					best_score = fitness;
				population.add(new Individual(genotype, fitness, null, null));
			}

			System.out.println("Best initial score: " + Double.toString(best_score));

			while (population.size() > 0 && evals<evaluations_limit_)
			{
				// Create offspring
				for (int j = 0; j < generation_size; j++)
				{
					Individual parent1 = population.get(rnd_.nextInt(population.size()));
					Individual parent2 = population.get(rnd_.nextInt(population.size()));

					// Create child genotype
					double child_genotype[] = new double[10];

					// Recombination
					for (int i = 0; i < 10; i++){
						child_genotype[i] += parent1.getGenotype()[i];
						child_genotype[i] += parent2.getGenotype()[i];
						child_genotype[i] /= 2;
					}

					// Mutation
					for (int i = 0; i < 10; i++){
						double rnd = rnd_.nextDouble();
						if (rnd < mutation_prob)
						{
								child_genotype[i] = Math.max(-5, Math.min(5, child_genotype[i] + rnd_.nextGaussian() * mutation_sd));
						}
					}

					double child_fitness = (double) evaluation_.evaluate(child_genotype);
					if (child_fitness > best_score) {
						best_score = child_fitness;
						mutation_sd = 0.5 - best_score/20;
						System.out.println("Score update: " + Double.toString(best_score));
					}
					population.add(new Individual(child_genotype, child_fitness, parent1, parent2));
				}

				Collections.sort(population);

				// Kill all individuals that do not meet fitness and improvement criteria
				ArrayList<Individual> rejects = new ArrayList<Individual>();
				for (int i = 0; i < population.size(); i++)
				{
					Individual individual = population.get(i);
					double fitness = individual.getFitness();
					if (i < population.size() - 10 || individual.age >= max_age)
						rejects.add(individual);
					else 
					{
						double ancestor_fitness = individual.getAncestorFitness(ancestor_depth);
						if (fitness - ancestor_fitness  < (10 - ancestor_fitness) * improvement_treshold)
							rejects.add(individual);
					}

					individual.age++;
				}
				population.removeAll(rejects);	

				System.out.println("Population size:" + Integer.toString(population.size()));
				evals+= generation_size;	
			}

			System.out.println("Population " + Integer.toString(population_count) + " died out after " + Integer.toString(evals) + " evaluations.");
			System.out.println("Best score achieved: " + Double.toString(best_score) + ".");
			total_evals += evals;					
		}
	}
}