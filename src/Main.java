import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

		List<Agent> agentList = new ArrayList<Agent>();
		Main main = new Main(); // staticじゃないメソッドを呼び出すためのおまじない

		int numOfAgent = 1000; // agentの数を定義
		double tendencyA = 1.0; // Aになる傾向の確率
		double tendencyB = 1.0; // Bになる傾向の確率
		
		double firstRateP = 0.3; // 最初のPの値
		double lastRateP = 0.6; // 最後のPの値
		double intervalOfP = 0.025; // Pの感覚

		int N =5 ;		//試行回数
		
		for(tendencyA = 1.0; tendencyA >0.61 ; tendencyA -= 0.1){
		
		System.out.println("・Aになる傾向が"+tendencyA+"のとき");
		System.out.println();
			
		String writeFileName = "TendencyA" +"=" +tendencyA+ ".csv";
		File f = new File(writeFileName);
		PrintStream pw = new PrintStream(f);
		
		for (double rateOfP = firstRateP; rateOfP < lastRateP+ intervalOfP; rateOfP += intervalOfP) { // Pの割合を変えるごとに必要なステップを表示させる
		
			int countstep = 0;

			System.out.println("P=" + rateOfP + "で");
			
			for (int i = 0; i < N; i++) {
//				System.out.println("・P=" + rateOfP + "のとき");
				agentList = main.buildAgentList(numOfAgent, rateOfP);

//				List<Agent> agentListLate = new ArrayList<Agent>();
				int numOfStep = main.meetAgent(agentList, rateOfP, pw,
						tendencyA, tendencyB); // 初期値のリストを出会わせて操作している
//				int[] resultLate = main.countStatus(agentListLate); // 操作後のそれぞれの値を計算して出力

				countstep += numOfStep;				//ステップ数を試行回数ごとに足していく
			}
			
			double average = countstep/N;
			System.out.println("平均は"+average);
			System.out.println();
			pw.println(rateOfP + "," + average);// csvにそれぞれのPの値で必要なステップ数を表示させる
		
			
		}
		
	}
	}

	// agentを生成してそれをリストにいれるメソッド

	public List<Agent> buildAgentList(int numOfAgent, double rateOfP) {
		List<Agent> tempAgentList = new ArrayList<Agent>();// tempAgentListという名前のagentを入れるListの箱を用意

		for (int i = 0; i < numOfAgent; i++) {
			Agent agent = new Agent(i, rateOfP); // 自分の番号とPの確率をもったagentを指定された数の分だけ生成
			tempAgentList.add(agent);
		}

		return tempAgentList;
	}

	// agent同士が出会って意見が交換された後のリストを返すメソッド
	public int meetAgent(List<Agent> agentList, double rateOfP,
			PrintStream pw, double tendencyA, double tendencyB) { // AとBの傾向パラメータを追加

		List<Agent> tempAgentList = new ArrayList<Agent>();
		tempAgentList = agentList;
		int countStep = 0;

		int resultTemp[] = countStatus(tempAgentList);
		// 何回で収束するかを数える

		while (resultTemp[1] != 0) { // ステップをBの人がいなくなるまで繰り返す

			countStep++;

			// for (int i = 0; i < stepNum; i++) {

			double r = Math.random(); // 一つ目の乱数発生！
			double R = Math.random(); // 二つ目の乱数発生！

			int speakerNum = (int) (Math.random() * agentList.size());
			int listenerNum = (int) (Math.random() * agentList.size());

			while (speakerNum == listenerNum
			// || tempAgentList.get(listenerNum).status.equals("P")//
			) { // スピーカーとリスナーが同じ人だった場合は選び直す

				listenerNum = (int) (Math.random() * agentList.size());
			}
			
			
			// System.out.println("前の"+speakerNum+"番目のSは"+tempAgentList.get(speakerNum).status+","+listenerNum+"番目のLは"+tempAgentList.get(listenerNum).status);

			String[][] changeStatusToA1 = { { "A", "B", "AB" },
					{ "A", "AB", "A" }, // Aよりになるとき
					{ "P", "B", "AB" }, { "P", "AB", "A" },

			};

			for (int i = 0; i < changeStatusToA1.length; i++) {

				String[] array = changeStatusToA1[i];

				if (tempAgentList.get(speakerNum).status == array[0]
						&& tempAgentList.get(listenerNum).status == array[1]) {
					if (R < tendencyA) {
						tempAgentList.get(listenerNum).status = array[2];
					}					
				}
				if(tempAgentList.get(listenerNum-2).status.equals("A")&&tempAgentList.get(listenerNum-1).status.equals("A")
						&&tempAgentList.get(listenerNum+1).status.equals("A")&&tempAgentList.get(listenerNum+2).status.equals("A"))
				{
					tempAgentList.get(listenerNum).status = "A";
				}
	
				makeNetwork(tempAgentList, listenerNum);			//ネットワークを作動
				
			}

			String[][] changeStatusToB1 = { { "B", "A", "AB" },
					{ "B", "AB", "B" } // Bよりになるとき
			};

			for (int i = 0; i < changeStatusToB1.length; i++) {
				String[] array = changeStatusToB1[i];

				if (tempAgentList.get(speakerNum).status == array[0]
						&& tempAgentList.get(listenerNum).status == array[1]) {
					if (r < tendencyB) {
						tempAgentList.get(listenerNum).status = array[2];
					}
				}
				makeNetwork(tempAgentList, listenerNum);			//ネットワークを作動

			}

			String[][] changeStatusToA2 = { { "AB", "B", "A" },
					{ "AB", "AB", "A" }, // Aより、かつ1/2の確率で起こるもの
			};

			for (int i = 0; i < changeStatusToA2.length; i++) {

				String[] array = changeStatusToA2[i];
				if (tempAgentList.get(speakerNum).status == array[0]
						&& tempAgentList.get(listenerNum).status == array[1]) {

					if (r < 1.0 / 2.0) {
						if (r < tendencyA) {
							tempAgentList.get(listenerNum).status = array[2];
						}
					}
				}
				makeNetwork(tempAgentList, listenerNum);
			}

			String[][] changeStatusToB2 = { { "AB", "A", "B" },
					{ "AB", "AB", "B" } // Bより、かつ１・２の確率で起こるもの
			};
			for (int i = 0; i < changeStatusToB2.length; i++) {

				String[] array = changeStatusToB2[i];
				if (tempAgentList.get(speakerNum).status == array[0]
						&& tempAgentList.get(listenerNum).status == array[1]) {

					if (r < 1.0 / 2.0) {
						if (r < tendencyB) {
							tempAgentList.get(listenerNum).status = array[2];
						}
					}
				}
				makeNetwork(tempAgentList, listenerNum);
			}

			// System.out.println("後の"+speakerNum+"番目のSは"+tempAgentList.get(speakerNum).status+","+listenerNum+"番目のLは"+tempAgentList.get(listenerNum).status);
			// System.out.println();

			resultTemp = countStatus(tempAgentList);
			
		}

//		pw.println(rateOfP + "," + countStep);// csvにそれぞれのPの値で必要なステップ数を表示させる
//		System.out.println("必要なステップは" + countStep); // 何回meetさせたのかを表示
		return countStep;
	}

	// リストのなかのA,B,AB,Pの数を数えるメソッド

	public int[] countStatus(List<Agent> agentList) {
		int numOfA = 0;
		int numOfB = 0;
		int numOfAB = 0;
		int numOfP = 0;

		for (int i = 0; i < agentList.size(); i++) {

			if (agentList.get(i).status.equals("A")) {
				numOfA++;
			} else if (agentList.get(i).status.equals("B")) {
				numOfB++;
			} else if (agentList.get(i).status.equals("AB")) {
				numOfAB++;
			} else {
				numOfP++;
			}
		}
		int[] result = { numOfA, numOfB, numOfAB, numOfP };

		return result;
	}

	// Agentの状態を出力するメソッド
	public void printAgentStatus(List<Agent> agentList) {

		for (int i = 0; i < agentList.size(); i++) {
			System.out.println("AgentNumber:" + i + "のStatusは"
					+ agentList.get(i).status);
		}
		System.out.println();
	}

	//AからPの数を出力するメソッド
	public void printCountStatus(int[] array) {
		System.out.println("出会った後の");
		System.out.println("Aの数は" + array[0]);
		System.out.println("Bの数は" + array[1]);
		System.out.println("ABの数は" + array[2]);
		System.out.println("Pの数は" + array[3]);
		System.out.println();

	}
	//ネットワークを作るメソッド、周りの４エージェントがある意見なら自分の意見は変えない
	
   public void makeNetwork(List<Agent> agentList,int i, double rateOfP){
	   
	   int linkNum =4;
	   List<Agent> statusList = new ArrayList<Agent>();
	  
	   //i番目のagentだけ抜いたリンク数まわりのリストの作成
		for (int s = i- linkNum/2; s < i ; s ++) {		//ネットワークの前半リストの作成
			Agent agent = new Agent(s,rateOfP);
			statusList.add(agent);
			}
		for (int s = i+1; s < i +linkNum/2 + 1; s ++) {		//ネットワークの後半リストの作成
			Agent agent = new Agent(s,rateOfP);
			statusList.add(agent);
		}
	  
		if(checkCondition(statusList)){
		}
   }
//
//		   
//	   if(agentList.get(i-2).status.equals("A")&&agentList.get(i-1).status.equals("A")
//				&&agentList.get(i+1).status.equals("A")&&agentList.get(i+2).status.equals("A"))
//		{
//			agentList.get(i).status = "A";
//		}	
//	   else if(agentList.get(i-2).status.equals("B")&&agentList.get(i-1).status.equals("B")
//				&&agentList.get(i+1).status.equals("B")&&agentList.get(i+2).status.equals("B"))
//		{
//			agentList.get(i).status = "B";
//		}
//	   else if(agentList.get(i-2).status.equals("P")&&agentList.get(i-1).status.equals("P")
//				&&agentList.get(i+1).status.equals("P")&&agentList.get(i+2).status.equals("P"))
//		{
//			agentList.get(i).status = "P";
//		}	
   
   
   public boolean checkCondition(List<Agent> statusList, int agentNum){
	   
	   boolean result;
	   
	   for(int i = 0 ;i < statusList.size();i++){
	   result = (statusList.get(i).status == statusList.get(i+1).status);	   
	   }
	   return result;
	   }
	   
   }
   
   
   
   
}
