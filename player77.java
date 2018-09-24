import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

public class player77 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private double initial_speed = 0.01;
    private double base_acceleration = 1.1;
	
	public player77()
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

    private double[] generate_random_velocity(int acceleration){
    	double velocity[] = new double[10];
    	double sum_of_squares = 0;
    	for(int i=0; i<10; i++)
    	{
    		double rnd = rnd_.nextDouble() - 0.5; 
    		velocity[i] = rnd;
    		sum_of_squares += Math.pow(rnd, 2);
    	}
    	double speed = Math.sqrt(sum_of_squares);
    	for(int i=0; i<10; i++)
    	{
    		velocity[i] /= speed;
    		velocity[i] *= initial_speed * Math.pow(base_acceleration, acceleration);
		}		
		return velocity;
    }

    private boolean satisfies_bounds(double solution[]){
		for(int i=0; i<10; i++)
    		if (solution[i] > 5 || solution[i] < -5)
    			return false;
    	return true;
    }
    
	public void run()
	{
		// Generate random initial solution.
		double solution[] = new double[10];
		for(int i=0; i<10; i++)
				solution[i] = rnd_.nextDouble() * 10 - 5;
		double score = (double) evaluation_.evaluate(solution);
		System.out.println("Initial score: " + Double.toString(score));
		
		double velocity[] = generate_random_velocity(0);     
        
        int evals = 0;
        int failure_counter = 0;
        int acceleration = 0;
        int streak = 0;

        while(evals<evaluations_limit_)
        {
            double new_solution[] = new double[10];
            for (int i = 0; i < 10; i++)
            	new_solution[i] = solution[i] + velocity[i];
            if (satisfies_bounds(new_solution))
            {
            	double new_score = (double) evaluation_.evaluate(new_solution);
            	evals++;
            	if (new_score > score){
            		solution = new_solution;
            		score = new_score;
            		if (streak > 0)
    					for(int i=0; i<10; i++)
    						velocity[i] *= base_acceleration;
    				streak++;
            		System.out.println("Score update: " + Double.toString(score) + " at " + Integer.toString(evals) + " evaluations.");
            	}
            	else 
            	{
            		velocity = generate_random_velocity(acceleration);
            		//if (streak > 0)
            			acceleration += streak -1;
            		failure_counter++;
            		streak = 0;
            	}
            }
            else 
            {
        		velocity = generate_random_velocity(acceleration);
        		//if (streak > 0)
            		acceleration += streak -1;
        		failure_counter++;
        		streak = 0;
            }
        }

        System.out.println(acceleration);
	}
}
