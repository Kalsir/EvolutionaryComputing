public class Individual implements Comparable<Individual>{
	private double[] genotype;
	private double fitness;
	public int age;

	public Individual(double[] genotype, double fitness) {
		super();
		this.genotype = genotype;
		this.fitness = fitness;
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

	public  double getFitness() {
		return this.fitness;
	}

	public double[] getGenotype() {
		return this.genotype;
	}
}