
public class Agent {

	int agentnum;
	String status; // それぞれのエージェントがA,B,AB,Pである
	double rateOfP;	

	// double rationOfStatusA = 0.2;
	// double rationOfStatusAB;
	// double rationOfStatusB;
	// double rationOfStatusP;

	Agent(int a,double rateOfP) {
		this.agentnum = a;
		this.status = statusDecide(rateOfP);

	}

	public String statusDecide(double rateOfP) {								//Pの割合を決めた時に他の割合を決める
		double r = Math.random();
		double rateOfA = 0.0;
		double rateOfB = 1-rateOfP;
		
		double rateOfAB = 0.0;


		if (0 < r && r < rateOfA) {
			this.status = "A";
		}
		else if (rateOfA < r && r < (rateOfA +rateOfB)) {
			this.status = "B";
		}
		else if ((rateOfA +rateOfB) < r && r < (rateOfA + rateOfB+ rateOfAB)) {
			this.status = "AB";
		} else {
			this.status = "P";
		}
		return this.status;

	}

}