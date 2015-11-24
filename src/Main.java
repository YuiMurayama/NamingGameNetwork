import java.util.ArrayList;
import java.util.Random;
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

		int numOfAgent = 10; // agentの数を定義
		double tendencyA = 1.0; // Aになる傾向の確率
		double lastTendencyA = 0.9;
		double intervalTendencyA = 0.1;
		double tendencyB = 1.0; // Bになる傾向の確率

		double firstRateP = 0.4; // 最初のPの値
		double lastRateP = 0.5; // 最後のPの値
		double intervalOfP = 0.1; // Pの感覚

		int N = 1; // 試行回数

		for (tendencyA = 1.0; tendencyA > lastTendencyA; tendencyA -= intervalTendencyA) {

			System.out.println("・Aになる傾向が" + tendencyA + "のとき");
			System.out.println();

			String writeFileName = "Network" + "=" + tendencyA + ".csv";
			File f = new File(writeFileName);
			PrintStream pw = new PrintStream(f);

			for (double rateOfP = firstRateP; rateOfP < lastRateP ; rateOfP += intervalOfP) { // Pの割合を変えるごとに必要なステップを表示させる

				int countstep = 0;

				System.out.println("P=" + rateOfP + "で");

				for (int i = 0; i < N; i++) {

					agentList = main.buildAgentList(numOfAgent, rateOfP);
					System.out.println("meetagent前");
					
					for(int t = 0; t < numOfAgent; t++){			//初期状態の出力
						System.out.print(agentList.get(t).status);
					}
					main.printCountStatus(main.countStatus(agentList));
			
					System.out.println();
					
					int numOfStep = main.meetAgent(agentList, rateOfP, pw,
							tendencyA, tendencyB, numOfAgent); // 初期値のリストを出会わせて操作している
					System.out.println("meetagent後");
					main.printCountStatus(main.countStatus(agentList));
					countstep += numOfStep; // ステップ数を試行回数ごとに足していく
				}

				double average = countstep / N;
				System.out.println("平均は" + average);
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
	public int meetAgent(List<Agent> agentList, double rateOfP, PrintStream pw,
			double tendencyA, double tendencyB, int numOfAgent) { // AとBの傾向パラメータを追加

		List<Agent> tempAgentList = new ArrayList<Agent>();
		tempAgentList = agentList;

		int countStep = 0;

		int resultTemp[] = countStatus(tempAgentList);
		// 何回で収束するかを数える

		// while (resultTemp[1] > numOfAgent*0.8) { // ステップをBの人がいなくなるまで繰り返す

	//	while (resultTemp[1] > numOfAgent* 0.05) {
		
		for(int s =0; s< 50; s++){
			countStep++;
					
			int linkNum = 8;
			// for (int i = 0; i < stepNum; i++) {

			double r = Math.random(); // 一つ目の乱数発生！
			double R = Math.random(); // 二つ目の乱数発生！

			int speakerNum = (int) (Math.random() * agentList.size());

			List<Agent> linkList = new ArrayList<Agent>();
			linkList = makeNetwork(tempAgentList, speakerNum, linkNum);

			Random rnd = new Random();
			int listenerNum = rnd.nextInt(linkList.size());

			while (speakerNum == listenerNum
					// || tempAgentList.get(listenerNum).status.equals("P")//
					) { // スピーカーとリスナーが同じ人だった場合は選び直す

						listenerNum = rnd.nextInt(linkList.size());
					}
			
			
			tempAgentList.get(listenerNum).status = linkList.get(listenerNum).status;

			System.out.println("前の" + speakerNum + "番目のSpeakerは"
					+ tempAgentList.get(speakerNum).status + "," + listenerNum
					+ "番目のListernerは" + tempAgentList.get(listenerNum).status);

			String[][] changeStatusToA1 = { { "A", "B", "AB" },
					{ "A", "AB", "A" }, // Aよりになるとき
					{ "P", "B", "AB" }, { "P", "AB", "A" },
			};

			boolean havechanged = false;
			for (int i = 0; i < changeStatusToA1.length; i++) {

				String[] array = changeStatusToA1[i];

				if (tempAgentList.get(speakerNum).status == array[0]
						&& tempAgentList.get(listenerNum).status == array[1]) {
					havechanged = true;
					System.out.println("ToA1");
					if (R < tendencyA) {
						tempAgentList.get(listenerNum).status = array[2];
					}
					break;
				}
			}

			if (havechanged == false) {
				String[][] changeStatusToB1 = { { "B", "A", "AB" },
						{ "B", "AB", "B" } // Bよりになるとき
				};

				for (int i = 0; i < changeStatusToB1.length; i++) {
					String[] array = changeStatusToB1[i];

					if (tempAgentList.get(speakerNum).status == array[0]
							&& tempAgentList.get(listenerNum).status == array[1]) {
						havechanged = true;
						System.out.println("ToB1");
						if (r < tendencyB) {
							tempAgentList.get(listenerNum).status = array[2];
						}
						break;
					}
				}
			}

			if (havechanged == false) {
				if (agentList.get(speakerNum).status.equals("AB")
						&& agentList.get(listenerNum).status.equals("AB")) {
					havechanged = true;

					if (r < tendencyA/(tendencyA+tendencyB)) {
						agentList.get(listenerNum).status = "A";
					} else {
						agentList.get(listenerNum).status = "B";
					}
				}
			}

			if (havechanged == false) {
				String[][] changeStatusToA2 = { { "AB", "B", "A" },
				// Aより、かつ1/2の確率で起こるもの
				};

				for (int i = 0; i < changeStatusToA2.length; i++) {

					String[] array = changeStatusToA2[i];
					if (tempAgentList.get(speakerNum).status == array[0]
							&& tempAgentList.get(listenerNum).status == array[1]) {
						havechanged = true;
						System.out.println("ToA2");
						if (r < 1.0 / 2.0) {
							if (r < tendencyA) {
								tempAgentList.get(listenerNum).status = array[2];
							}
						}
						break;
					}
				}
			}
			if (havechanged == false) {
				String[][] changeStatusToB2 = { { "AB", "A", "B" },
				// Bより、かつ１・２の確率で起こるもの
				};
				for (int i = 0; i < changeStatusToB2.length; i++) {

					String[] array = changeStatusToB2[i];
					if (tempAgentList.get(speakerNum).status == array[0]
							&& tempAgentList.get(listenerNum).status == array[1]) {
						havechanged = true;
						System.out.println("ToB2");
						if (r < 1.0 / 2.0) {
							if (r < tendencyB) {
								tempAgentList.get(listenerNum).status = array[2];
							}
						}
						break;
					}
				}
			}

			System.out.println("前の" + speakerNum + "番目のSpeakerは"
					+ tempAgentList.get(speakerNum).status + "," + listenerNum
					+ "番目のListenerは" + tempAgentList.get(listenerNum).status);

			
			 System.out.println();

			resultTemp = countStatus(tempAgentList);
			printCountStatus(resultTemp);

		}

		// pw.println(rateOfP + "," + countStep);// csvにそれぞれのPの値で必要なステップ数を表示させる
		// System.out.println("必要なステップは" + countStep); // 何回meetさせたのかを表示
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

	// AからPの数を出力するメソッド
	public void printCountStatus(int[] array) {
	//	System.out.println("出会った後の");
		System.out.println("Aの数は" + array[0]);
		System.out.println("Bの数は" + array[1]);
		System.out.println("ABの数は" + array[2]);
		System.out.println("Pの数は" + array[3]);
		System.out.println();

	}

	// ネットワークを作るメソッド、周りの４エージェントがある意見なら自分の意見は変えない

	public List<Agent> makeNetwork(List<Agent> agentList, int agentNum,
			int linkNum) {

		List<Agent> linkList = new ArrayList<Agent>();

		// i番目のagentだけ抜いたリンク数まわりのリストの作成

		// 前半リスト
		int numOfAgent = agentList.size();

		if (agentNum >= linkNum / 2) { // 普通のとき
			for (int s = agentNum - linkNum / 2; s < agentNum; s++) {
				Agent agent = agentList.get(s); // あるエージェントの前のエージェント達をリストに追加
				linkList.add(agent);
			}
		}

		else if (agentNum < linkNum / 2) { // ちっちゃくてはみ出たときの前半
			for (int s = numOfAgent - (linkNum / 2 - agentNum); s < numOfAgent; s++) { // 大きい方へ回る分
				Agent agent = agentList.get(s); // あるエージェントの前のエージェント達をリストに追加
				linkList.add(agent);
			}

			for (int s = 0; s < agentNum; s++) { // ちっちゃくてはみ出た時後
				Agent agent = agentList.get(s);
				linkList.add(agent);
			}

			// ここまでおっけい

			// 後半リスト
		}

		if (numOfAgent - linkNum / 2 >= agentNum) { // 普通の時
			for (int s = agentNum ; s < agentNum + linkNum / 2 ; s++) {
				Agent agent = agentList.get(s); // あるエージェントの後のエージェント達をリストに追加
				linkList.add(agent);
			}
		} else if (agentNum > numOfAgent - linkNum / 2) { // おっきくてはみ出したとき前半
			for (int s = 0; s < agentNum - (numOfAgent - linkNum / 2); s++) {
				Agent agent = agentList.get(s); // あるエージェントの後のエージェント達をリストに追加
				linkList.add(agent);
			}
			for (int s = agentNum; s < numOfAgent; s++) { // おっきくてはみ出した時後半
				Agent agent = agentList.get(s); // あるエージェントの後のエージェント達をリストに追加
				linkList.add(agent);
			}
		}

		return linkList;
	}
}
