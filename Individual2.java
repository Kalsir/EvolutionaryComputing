public class Individual implements Comparable<Individual>{
	private double[] genotype;
	private double fitness;
	private Individual parent1;
	private Individual parent2;
	public int age;

	public Individual(double[] genotype, double fitness, Individual parent1, Individual parent2) {
		super();
		this.genotype = genotype;
		this.fitness = fitness;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.age = 0;
	}

	public int compareTo(Individual individual) {
		if (this.fitness > individual.fitness)
			return 1;
		else if (this.fitness == individual.fitness)
			return 0;
		else
			return -1;
	}

	public double getAncestorFitness(int depth){
		if (this.parent1 == null)
			return Integer.MIN_VALUE;
		if (depth == 0)
			return this.fitness;
		else
			return (this.parent1.getAncestorFitness(depth - 1) + this.parent2.getAncestorFitness(depth - 1))/2;
	}

	public  double getFitness() {
		return this.fitness;
	}

	public double[] getGenotype() {
		return this.genotype;
	}
}