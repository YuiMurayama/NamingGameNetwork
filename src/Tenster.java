import java.util.ArrayList;
import java.util.List;

public class Tenster {

	public static void main(String[] args) {
		Tenster tenster = new Tenster();
		tenster.makeNetworkTest();
		// TODO 自動生成されたメソッド・スタブ

	}

	public void makeNetworkTest() {
		Main main = new Main();
		List<Agent> agentlist = new ArrayList<Agent>();
		for (int i = 0; i < 12; i++) {
			Agent agent = new Agent(i, 1.0);
			agentlist.add(agent);
		}
		List<Agent> resultAgentList = new ArrayList<Agent>();

		resultAgentList = main.makeNetwork(agentlist, 0, 6);

		for (int i = 0; i < resultAgentList.size(); i++) {
			System.out.println(resultAgentList.get(i).agentnum);
		}
	}
}
