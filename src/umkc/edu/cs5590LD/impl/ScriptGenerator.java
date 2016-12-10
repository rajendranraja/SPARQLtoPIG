/**
 * 
 */
package umkc.edu.cs5590LD.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rajasekar Rajendran
 * 
 */
public class ScriptGenerator {

	private TripleJoinDTO tripleJoinDTO;
	private String load = "LOAD";
	private String filter = "FILTER";
	private String forEach = "FOREACH";
	private String generate = "GENERATE";
	private String by = "BY";
	private String using = "USING";
	private String pigStorage = "PigStorage";
	private String as = "AS";
	private String charArray = "chararray";
	private String spacer = " ";

	private Map<Integer, String> queryPosition = new HashMap<Integer, String>();


	private String[] var = { "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "X", "Y",
			"Z" };
	private int index = 0;
	private int loadIndex = 0;
	private String[] subject;
	private String[] object;
	private String[] predicate;

	private List<String> pigQuery = new ArrayList<String>();

	public ScriptGenerator(TripleJoinDTO tripleJoinDTO) {
		this.tripleJoinDTO = tripleJoinDTO;
		subject = tripleJoinDTO.getSubj();
		object = tripleJoinDTO.getObj();
		predicate = tripleJoinDTO.getPred();
		createScript();
		interJoins();

		try {
			FileWriter writer = new FileWriter("pigOutput.pig");
			writer.write(tripleJoinDTO.getTempSelect().toString());
			writer.write("\n");

			System.out
					.println("###########################################################");
			for (String a : pigQuery) {
				System.out.println(a);
				writer.write(a);
				writer.write("\n");
			}
			writer.close();
			System.out
					.println("###########################################################");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Keys --> " + queryPosition.toString());
	}

	private void createScript() {
		String asd = generateLoad("file.nt");
		pigQuery.add(asd);
		System.out.println("Load stmt --> " + asd);

		for (int i = 0; i < tripleJoinDTO.getPred().length; i++) {
			variableGenerator();

			System.out.println("FOR LOOP  --> Entering --" + i);

			if (tripleJoinDTO.getPartQryList().get(i).isFilter()
					|| tripleJoinDTO.getPartQryList().get(i).isSelfJoin()) {

				List<String> literal = new ArrayList<String>();
				if (tripleJoinDTO.getPartQryList().get(i).getLiteral()
						.isObject()) {
					String[] tmpObj = tripleJoinDTO.getObj();
					literal.add(tmpObj[i]);
				}
				if (tripleJoinDTO.getPartQryList().get(i).getLiteral()
						.isSubject()) {
					String[] tmpSubj = tripleJoinDTO.getSubj();
					literal.add(tmpSubj[i]);
				}
				if (tripleJoinDTO.getPartQryList().get(i).getLiteral()
						.isPredicate()) {
					String[] tmpPred = tripleJoinDTO.getPred();
					literal.add(tmpPred[i]);
				}
				generateFilter(literal, tripleJoinDTO.getPartQryList().get(i)
						.getLiteral().isSubject(), tripleJoinDTO
						.getPartQryList().get(i).getLiteral().isPredicate(),
						tripleJoinDTO.getPartQryList().get(i).getLiteral()
								.isObject(), i, tripleJoinDTO.getPartQryList()
								.get(i).isSelfJoin());
			} else {
				List<String> literal = new ArrayList<String>();
				generateFilter(literal, tripleJoinDTO.getPartQryList().get(i)
						.getLiteral().isSubject(), tripleJoinDTO
						.getPartQryList().get(i).getLiteral().isPredicate(),
						tripleJoinDTO.getPartQryList().get(i).getLiteral()
								.isObject(), i, tripleJoinDTO.getPartQryList()
								.get(i).isSelfJoin());
			}

			if (!tripleJoinDTO.getPartQryList().get(i).getSelfJoins().isEmpty()) {
				String join = tripleJoinDTO.getPartQryList().get(i)
						.getSelfJoins().toString();
				if (join.contains(",")) {
					join.split(",");
				}
			}
			// String lineVar = variableGenerator();
			// currVariable = lineVar;
		}
	}

	private void interJoins() {
		if (!tripleJoinDTO.getTriplesJoin().isEmpty()) {
			String varTracker = "j";
			int counter = 0;
			Set<String> tempSet = new LinkedHashSet<String>();
			String litColl = "";
			for (int keySet : tripleJoinDTO.getTriplesJoin().keySet()) {
				List<String> values = tripleJoinDTO.getTriplesJoin()
						.get(keySet);

				switch (keySet) {
				case 1:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ subject[Integer.parseInt(one)].substring(
											1, subject[Integer.parseInt(one)]
													.length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ predicate[Integer.parseInt(two)]
											.substring(1, predicate[Integer
													.parseInt(two)].length())
									+ two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ subject[Integer.parseInt(one)].substring(
											1, subject[Integer.parseInt(one)]
													.length())
													//TODO
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ predicate[Integer.parseInt(two)]
											.substring(1, predicate[Integer
													.parseInt(two)].length())
									+ two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
						//TODO
						//////////////////////////////////////////////////////////////////////////
						////////////////////////////// change 2////////////////////////////////
						sampLiteral = tripleJoinDTO
								.getPartQryList().get(twoInt).getLiteral();
						
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 2:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ subject[Integer.parseInt(one)].substring(
											1, subject[Integer.parseInt(one)]
													.length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ object[Integer.parseInt(two)].substring(
											1, object[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ subject[Integer.parseInt(one)].substring(
											1, subject[Integer.parseInt(one)]
													.length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
								    + ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ object[Integer.parseInt(two)].substring(
											1, object[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 3:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ predicate[Integer.parseInt(one)]
											.substring(1, predicate[Integer
													.parseInt(one)].length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ subject[Integer.parseInt(two)].substring(
											1, subject[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ predicate[Integer.parseInt(one)]
											.substring(1, predicate[Integer
													.parseInt(one)].length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ subject[Integer.parseInt(two)].substring(
											1, subject[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 4:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ predicate[Integer.parseInt(one)]
											.substring(1, predicate[Integer
													.parseInt(one)].length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ object[Integer.parseInt(two)].substring(
											1, object[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ predicate[Integer.parseInt(one)]
											.substring(1, predicate[Integer
													.parseInt(one)].length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ object[Integer.parseInt(two)].substring(
											1, object[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 5:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ object[Integer.parseInt(one)].substring(
											1, object[Integer.parseInt(one)]
													.length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ subject[Integer.parseInt(two)].substring(
											1, subject[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ object[Integer.parseInt(one)].substring(
											1, object[Integer.parseInt(one)]
													.length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ subject[Integer.parseInt(two)].substring(
											1, subject[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 6:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ object[Integer.parseInt(one)].substring(
											1, object[Integer.parseInt(one)]
													.length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ predicate[Integer.parseInt(two)]
											.substring(1, predicate[Integer
													.parseInt(two)].length())
									+ two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ object[Integer.parseInt(one)].substring(
											1, object[Integer.parseInt(one)]
													.length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ predicate[Integer.parseInt(two)]
											.substring(1, predicate[Integer
													.parseInt(two)].length())
									+ two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 7:

					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ subject[Integer.parseInt(one)].substring(
											1, subject[Integer.parseInt(one)]
													.length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ subject[Integer.parseInt(two)].substring(
											1, subject[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ subject[Integer.parseInt(one)].substring(
											1, subject[Integer.parseInt(one)]
													.length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ subject[Integer.parseInt(two)].substring(
											1, subject[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 8:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ object[Integer.parseInt(one)].substring(
											1, object[Integer.parseInt(one)]
													.length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ object[Integer.parseInt(two)].substring(
											1, object[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ object[Integer.parseInt(one)].substring(
											1, object[Integer.parseInt(one)]
													.length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ object[Integer.parseInt(two)].substring(
											1, object[Integer.parseInt(two)]
													.length()) + two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();

						if (counter == 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				case 9:
					for (String a : values) {
						String one = a.substring(0, 1);
						String two = a.substring(2, 3);
						int oneInt = Integer.parseInt(one);
						int twoInt = Integer.parseInt(two);
						// String tmpVar = variableGenerator();
						String dummy = null;
						if (counter == 0) {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ queryPosition.get(Integer.parseInt(one))
									+ " by "
									+ predicate[Integer.parseInt(one)]
											.substring(1, predicate[Integer
													.parseInt(one)].length())
									+ one
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ predicate[Integer.parseInt(two)]
											.substring(1, predicate[Integer
													.parseInt(two)].length())
									+ two + ";";
							pigQuery.add(dummy);
						} else {
							dummy = varTracker
									+ counter
									+ " = JOIN "
									+ varTracker
									+ (counter - 1)
									+ " by "
									+ predicate[Integer.parseInt(one)]
											.substring(1, predicate[Integer
													.parseInt(one)].length())
									+ "0"////////////////////////////////////////////change 3
									/////////////////////////////////////////////////////////
									+ ", "
									+ queryPosition.get(Integer.parseInt(two))
									+ " by "
									+ predicate[Integer.parseInt(two)]
											.substring(1, predicate[Integer
													.parseInt(two)].length())
									+ two + ";";
							pigQuery.add(dummy);
						}

						LiteralTriple sampLiteral = tripleJoinDTO
								.getPartQryList().get(oneInt).getLiteral();
						
						System.out.println("inside case 9 "+ tripleJoinDTO.getPartQryList().get(1).toString());

						if (counter == 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ subject[oneInt].substring(1,
												subject[oneInt].length())
										+ oneInt;
								tempSet.add(subject[oneInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ predicate[oneInt].substring(1,
													predicate[oneInt].length())
											+ oneInt;
								}
								tempSet.add(predicate[oneInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								if ("".equals(litColl)) {
									litColl = litColl
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								} else {
									litColl = litColl
											+ ", "
											+ object[oneInt].substring(1,
													object[oneInt].length())
											+ oneInt;
								}
								tempSet.add(object[oneInt]);
							}
						}
//////////////////////////////////////////////////////////////////////////
////////////////////////////// change 2////////////////////////////////
sampLiteral = tripleJoinDTO
.getPartQryList().get(twoInt).getLiteral();
						if (tempSet.size() > 0) {
							if (!sampLiteral.isSubject()
									&& !tempSet.contains(subject[twoInt])) {
								litColl = litColl
										+ ", "
										+ subject[twoInt].substring(1,
												subject[twoInt].length())
										+ twoInt;
								tempSet.add(subject[twoInt]);
							}
							if (!sampLiteral.isPredicate()
									&& !tempSet.contains(predicate[twoInt])) {
								litColl = litColl
										+ ", "
										+ predicate[twoInt].substring(1,
												predicate[twoInt].length())
										+ twoInt;
								tempSet.add(predicate[twoInt]);
							}
							if (!sampLiteral.isObject()
									&& !tempSet.contains(object[twoInt])) {
								litColl = litColl
										+ ", "
										+ object[twoInt].substring(1,
												object[twoInt].length())
										+ twoInt;
								tempSet.add(object[twoInt]);
							}
						}
						dummy = varTracker + counter + " = " + forEach + spacer
								+ varTracker + counter + spacer + generate
								+ spacer + "$0 AS " + litColl + ";";
						counter++;

						System.out.println("Inter Join 7-->" + dummy);
						pigQuery.add(dummy);

						// dummy = variableGenerator() +
					}
					break;
				default:
					break;
				}
			}
			tripleJoinDTO.getTempSelect();
			String dump = "DUMP " + varTracker + (counter - 1) + ";";
			pigQuery.add(dump);
			//
		}
		
		//////////////////////////////////////////////////////////////////// new new new new new 1
		//////////////////////////////////////////////////////////////////////////////////////////
		else
		{
			String dump;
			for (int i = 0; i<subject.length; i++)
			{
				dump = "DUMP " + queryPosition.get(i)+";";
				pigQuery.add(dump);
				
			}
		}
	}

	private String generateLoad(String name) {
		String a = "A" + " = " + load + spacer + "'" + name + "'" + spacer
				+ using + spacer + pigStorage + "(' ')" + spacer + as + spacer
				+ "(subject" + loadIndex + ":" + charArray + "," + spacer
				+ "object" + loadIndex + ":" + charArray + "," + spacer
				+ "predicate" + loadIndex + ":" + charArray + ");";
		loadIndex++;
		return a;
	}

	private String generateFilter(List<String> filterStmts, boolean subj,
			boolean pred, boolean obj, int cnt, boolean selfJoin) {
		int size = filterStmts.size();
		String tmpVar = null;
		if (queryPosition.containsKey(cnt)) {
			tmpVar = queryPosition.get(cnt);
		}
		// String subVar = subVariableGenerator(tmpVar);
		String filterQuery = null;
		if (size == 1) {

			boolean selfJoinCheck = false;
			if (subj && tmpVar != null) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$0 == '" + filterStmts.get(0) + "';";

				if (filterQuery != null) {
					pigQuery.add(filterQuery);
				}

				if (checkSelfJoin(predicate, object, cnt)) {
					selfJoinCheck = true;
					// TODO - Change if not working
					tripleJoinDTO.getPartQryList().get(cnt).getLiteral()
							.setObject(!selfJoinCheck);
					filterQuery = tmpVar + " = " + filter + spacer + tmpVar
							+ spacer + by + spacer + "$1 == $2;";
					pigQuery.add(filterQuery);
					filterQuery = tmpVar
							+ " = "
							+ forEach
							+ spacer
							+ tmpVar
							+ spacer
							+ generate
							+ spacer
							+ "$1 AS "
							+ predicate[cnt].substring(1,
									predicate[cnt].length()) + cnt + ";";
				}

				if (!selfJoinCheck) {
					filterQuery = tmpVar
							+ " = "
							+ forEach
							+ spacer
							+ tmpVar
							+ spacer
							+ generate
							+ spacer
							+ "$1 AS "
							+ predicate[cnt].substring(1,
									predicate[cnt].length()) + cnt + ", "
							+ "$2 AS "
							+ object[cnt].substring(1, object[cnt].length())
							+ cnt + ";";
				}
			} else if (pred && tmpVar != null) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$1 == '" + filterStmts.get(0) + "';";

				if (filterQuery != null) {
					pigQuery.add(filterQuery);
				}

				if (checkSelfJoin(subject, object, cnt)) {
					selfJoinCheck = true;
					tripleJoinDTO.getPartQryList().get(cnt).getLiteral()
							.setObject(!selfJoinCheck);
					filterQuery = tmpVar + " = " + filter + spacer + tmpVar
							+ spacer + by + spacer + "$0 == $2;";
					pigQuery.add(filterQuery);
					filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
							+ spacer + generate + spacer + "$0 AS "
							+ subject[cnt].substring(1, subject[cnt].length())
							+ cnt + "; ";
				}

				if (!selfJoinCheck) {

					filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
							+ spacer + generate + spacer + "$0 AS "
							+ subject[cnt].substring(1, subject[cnt].length())
							+ cnt + ", " + "$2 AS "
							+ object[cnt].substring(1, object[cnt].length())
							+ cnt + ";";
				}
			} else if (obj && tmpVar != null) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$2 == '" + filterStmts.get(0) + "';";

				if (filterQuery != null) {
					pigQuery.add(filterQuery);
				}

				if (checkSelfJoin(subject, predicate, cnt)) {
					selfJoinCheck = true;
					tripleJoinDTO.getPartQryList().get(cnt).getLiteral()
							.setPredicate(!selfJoinCheck);
					filterQuery = tmpVar + " = " + filter + spacer + tmpVar
							+ spacer + by + spacer + "$0 == $1;";
					pigQuery.add(filterQuery);
					filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
							+ spacer + generate + spacer + "$0 AS "
							+ subject[cnt].substring(1, subject[cnt].length())
							+ cnt + "; ";
				}

				if (!selfJoinCheck) {
					filterQuery = tmpVar
							+ " = "
							+ forEach
							+ spacer
							+ tmpVar
							+ spacer
							+ generate
							+ spacer
							+ "$0 AS "
							+ subject[cnt].substring(1, subject[cnt].length())
							+ cnt
							+ ", "
							+ "$1 AS "
							+ predicate[cnt].substring(1,
									predicate[cnt].length()) + cnt + ";";
				}
			}
		} else if (size == 2) {
			if (subj && pred) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$0 == '" + subject[cnt]
						+ "' AND $1 == '" + predicate[cnt] + "';";

				if (filterQuery != null) {
					pigQuery.add(filterQuery);
				}

				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$2 AS "
						+ object[cnt].substring(1, object[cnt].length()) + cnt
						+ ";";

			} else if (subj && obj) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$0 == '" + subject[cnt]
						+ "' AND $2 == '" + object[cnt] + "';";

				if (filterQuery != null) {
					pigQuery.add(filterQuery);
				}

				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$1 AS "
						+ predicate[cnt].substring(1, predicate[cnt].length())
						+ cnt + ";";

			} else if (pred && obj) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$1 == '" + predicate[cnt]
						+ "' AND $2 == '" + object[cnt] + "';";

				if (filterQuery != null) {
					pigQuery.add(filterQuery);
				}

				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$0 AS "
						+ subject[cnt].substring(1, subject[cnt].length())
						+ cnt + ";";
			}

		} else if (size == 3) {
			filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer + by
					+ spacer + "$0 == '" + subject[cnt] + "' AND $1 == '"
					+ predicate[cnt] + "' AND $2 == '" + object[cnt] + "';";
		} else {
			if (subject[cnt].equals(object[cnt])
					&& object[cnt].equals(predicate[cnt])) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$0 == $1 AND $1 == $2;";
				pigQuery.add(filterQuery);
				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$0 AS "
						+ subject[cnt].substring(1, subject[cnt].length())
						+ cnt + ";";
			} else if (checkSelfJoin(subject, object, cnt)) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$0 == $2;";
				pigQuery.add(filterQuery);
				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$0 AS "
						+ subject[cnt].substring(1, subject[cnt].length())
						+ cnt + ", $1 AS "
						+ predicate[cnt].substring(1, predicate[cnt].length())
						+ cnt + "; ";
			} else if (checkSelfJoin(subject, predicate, cnt)) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$0 == $1;";
				pigQuery.add(filterQuery);
				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$0 AS "
						+ subject[cnt].substring(1, subject[cnt].length())
						+ cnt + ", $2 AS "
						+ object[cnt].substring(1, object[cnt].length()) + cnt
						+ "; ";
			} else if (checkSelfJoin(predicate, object, cnt)) {
				filterQuery = tmpVar + " = " + filter + spacer + "A" + spacer
						+ by + spacer + "$1 == $2;";
				pigQuery.add(filterQuery);
				filterQuery = tmpVar + " = " + forEach + spacer + tmpVar
						+ spacer + generate + spacer + "$0 AS "
						+ subject[cnt].substring(1, subject[cnt].length())
						+ cnt + ", $1 AS "
						+ predicate[cnt].substring(1, predicate[cnt].length())
						+ cnt + "; ";
			} else {
				//TODO
				filterQuery = tmpVar + " = " + forEach + spacer + "A"
						+ spacer + generate + spacer + "$0 AS "
						+ subject[cnt].substring(1, subject[cnt].length())
						+ cnt + ", " + "$1 AS "
						+ predicate[cnt].substring(1, predicate[cnt].length())
						+ cnt + ", " + "$2 AS "
						+ object[cnt].substring(1, object[cnt].length()) + cnt
						+ ";";
				// pigQuery.add(filterQuery);
			}
		}
		if (filterQuery != null) {
			System.out.println("Filter Query --> " + filterQuery);
			pigQuery.add(filterQuery);
		}
		return filterQuery;
	}

	private String variableGenerator() {
		String variable = var[index];
		queryPosition.put(index, variable);
		index++;
		return variable;
	}

	private boolean checkSelfJoin(String[] obj1, String[] obj2, int index) {
		return obj1[index].equals(obj2[index]);
	}

}
