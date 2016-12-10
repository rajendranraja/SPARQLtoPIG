/**
 * 
 */
package umkc.edu.cs5590LD.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import umkc.edu.cs5590LD.query.QueryDTO;

/**
 * @author Rajasekar Rajendran
 * 
 */
public class GraphBuilder {
	private List<String> tempSelect = null;

	private Map<Integer, List<String>> triplesJoin = new LinkedHashMap<Integer, List<String>>();

	private PartialQueryDTO partialQueryDTO;
	private TripleJoinDTO tripleJoinDTO;
	private List<PartialQueryDTO> partQryList = new ArrayList<PartialQueryDTO>();

	String[] subj;
	String[] obj;
	String[] pred;
	String[] row;
	String[] column;
	String[][] elements;

	public void constructMatrix(Map<Integer, QueryDTO> query) {

		for (QueryDTO q : query.values()) {
			this.tempSelect = q.getTempSelect();
			// this.tempPredicate = q.getTempPredicate();
			// this.tempSubjRow = q.getTempSubjRow();
			// this.tempObjCol = q.getTempObjCol();

			subj = Arrays.copyOf(q.getTempSubjRow().toArray(), q
					.getTempSubjRow().toArray().length, String[].class);
			pred = Arrays.copyOf(q.getTempPredicate().toArray(), q
					.getTempPredicate().toArray().length, String[].class);
			obj = Arrays.copyOf(q.getTempObjCol().toArray(), q.getTempObjCol()
					.toArray().length, String[].class);
			HashSet<String> tempRow = new HashSet<String>(q.getTempSubjRow());
			HashSet<String> tempCol = new HashSet<String>(q.getTempObjCol());

			row = Arrays.copyOf(tempRow.toArray(), tempRow.toArray().length,
					String[].class);
			column = Arrays.copyOf(tempCol.toArray(), tempCol.toArray().length,
					String[].class);

			detectJoins();
			tripleJoinDTO = new TripleJoinDTO();
			tripleJoinDTO.setObj(obj);
			tripleJoinDTO.setSubj(subj);
			tripleJoinDTO.setPred(pred);
			tripleJoinDTO.setPartQryList(partQryList);
			tripleJoinDTO.setTempSelect(tempSelect);
			tripleJoinDTO.setTriplesJoin(triplesJoin);
			new ScriptGenerator(tripleJoinDTO);

		}
	}

	// private void constructMatrix() {
	// elements = new String[row.length][column.length];
	// for (int i = 0; i < subj.length; i++) {
	// detectJoins(i);
	// }
	// }

	private void detectJoins() {
		for (int i = 0; i < subj.length; i++) {
			detectSelfJoins(i);
		}
		List<String> joinList;
		for (int i = 9; i > 0; i--) {
			joinList = new ArrayList<String>();
			switch (i) {
			case 1:
				joinList = detectSOPTripleJoins(subj, pred, obj);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 2:
				joinList = detectSOPTripleJoins(subj, obj, pred);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 3:
				joinList = detectTripleJoins(pred, subj, subj, subj);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 4:
				joinList = detectTripleJoins(pred, obj, subj, subj);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 5:
				joinList = detectTripleJoins(obj, subj, subj, pred);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 6:
				joinList = detectTripleJoins(obj, pred, subj, pred);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 7:
				joinList = detectSOPTripleJoins(subj, subj, pred);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 8:
				joinList = detectTripleJoins(obj, obj, subj, pred);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			case 9:
				joinList = detectTripleJoins(pred, pred, subj, subj);
				if (!joinList.isEmpty()) {
					triplesJoin.put(i, joinList);
					System.out.println(i + "->" + joinList.toString());
				}
				break;
			default:
				if (!joinList.isEmpty()) {
					triplesJoin.put(-100, joinList);
				}
				break;
			}

		}
	}

	private void detectSelfJoins(int index) {
		boolean isSOJoin = false;
		boolean isSPJoin = false;
		boolean isPOJoin = false;
		boolean allVariables = false;
		partialQueryDTO = new PartialQueryDTO();
		List<String> selfJoins = new ArrayList<String>();
		LiteralTriple literal = new LiteralTriple();

		if (detectVariable(subj, index) && detectVariable(obj, index)) {
			isSOJoin = isSelfJoin(subj, obj, index);
			System.out.println("SO Join -->" + index + "->" + isSOJoin);
			if (isSOJoin) {
				selfJoins.add("SO");
			}
		}
		if (detectVariable(pred, index) && detectVariable(obj, index)) {
			isPOJoin = isSelfJoin(pred, obj, index);
			System.out.println("PO Join -->" + index + "->" + isPOJoin);
			if (isPOJoin) {
				selfJoins.add("PO");
			}
		}
		if (detectVariable(subj, index) && detectVariable(pred, index)) {
			isSPJoin = isSelfJoin(subj, pred, index);
			System.out.println("SP Join -->" + index + "->" + isSPJoin);
			if (isSPJoin) {
				selfJoins.add("SP");
			}
		}

		if (detectVariable(subj, index) && detectVariable(obj, index)
				&& detectVariable(pred, index)) {
			allVariables = true;
		} else {
			literal.setSubject(!detectVariable(subj, index));
			// str.add(0 + ":#$%" + subj[index]);
			literal.setPredicate(!detectVariable(pred, index));
			// str.add(1 + ":#$%" + pred[index]);
			literal.setObject(!detectVariable(obj, index));
			// str.add(2 + ":#$%" + obj[index]);
		}
		partialQueryDTO.setFilter(!allVariables);
		partialQueryDTO.setSelfJoin(isSOJoin || isPOJoin || isSPJoin);
		partialQueryDTO.setSelfJoins(selfJoins);
		partialQueryDTO.setLiteral(literal);
		partQryList.add(partialQueryDTO);
	}

	private boolean isSelfJoin(String[] obj1, String[] obj2, int index) {
		return obj1[index].equals(obj2[index]);
	}

	private boolean detectVariable(String[] obj, int index) {
		return ('?' == obj[index].charAt(0));
	}

	private List<String> detectTripleJoins(String[] obj1, String[] obj2,
			String[] obj3, String[] obj4) {
		List<String> indexes = new ArrayList<String>();
		for (int i = 0; i < obj1.length; i++) {
			if (!(obj1[i].equals(obj3[i]) || obj1[i].equals(obj4[i]))) {
				for (int j = i + 1; j < obj2.length; j++) {
						if (obj1[i].equals(obj2[j])) {
							String a = i + ":" + j;
							indexes.add(a);
							break;
						}
				}
			}
		}
		return indexes;
	}

	private List<String> detectSOPTripleJoins(String[] obj1, String[] obj2,
			String[] obj3) {
		List<String> indexes = new ArrayList<String>();
		for (int i = 0; i < obj1.length; i++) {
			for (int j = i + 1; j < obj2.length; j++) {
				if (obj1[i].equals(obj2[j])) {
					String a = i + ":" + j;
					indexes.add(a);
					break;
				}
			}
		}
		return indexes;
	}
}