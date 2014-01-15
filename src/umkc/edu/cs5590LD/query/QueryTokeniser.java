/**
 * 
 */
package umkc.edu.cs5590LD.query;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import umkc.edu.cs5590LD.impl.GraphBuilder;

/**
 * @author Rajasekar Rajendran
 * 
 */
public class QueryTokeniser {

	private static QueryDTO queryDTO = new QueryDTO();
	private static GraphBuilder graphBuilder = new GraphBuilder();
	private static HashMap<Integer, QueryDTO> splitQuery = new HashMap<Integer, QueryDTO>();

	public static void main(String[] args) {
		if (args.length >= 1) {
			try {
				FileInputStream fioStream = new FileInputStream(args[0]);
				DataInputStream dioStream = new DataInputStream(fioStream);
				BufferedReader buffReader = new BufferedReader(
						new InputStreamReader(dioStream));
				String line;
				StringTokenizer stringToken;

				String[] subjectRow = new String[10];
				String[] objectColumn = new String[10];
				List<String> tempSelect = null;
				List<String> tempPredicate = null;
				List<String> tempSubjRow = null;
				List<String> tempObjCol = null;

				boolean start = false, end = false, selectStart = false, isSpacedString = false;
				int triplePattern = 0;
				int tripleCount = 0;
				int queryCount = 0;
				int i = 0, j = 0;
				String tempString = null;
				char sample = '"';
				System.out.println(sample);
				while ((line = buffReader.readLine()) != null) {
					stringToken = new StringTokenizer(line);

					while (stringToken.hasMoreElements()) {
						String value = (String) stringToken.nextElement();
						System.out.println(value);

						if (!(sample == value.charAt(0) && sample == value
								.charAt(value.length() - 1))) {
							if (sample == value.charAt(0)) {
								tempString = value;
								isSpacedString = true;
								continue;
							} else if (isSpacedString
									&& !(sample == value
											.charAt(value.length() - 1))) {
								tempString = tempString + " " + value;
								continue;
							} else if (sample == value
									.charAt(value.length() - 1)) {
								value = tempString + " " + value;
								isSpacedString = false;
							}
						}
						if ("select".equalsIgnoreCase(value)) {
							selectStart = true;
							end = false;
							tempSelect = new ArrayList<String>();
							continue;
						} else if ("{".equals(value)) {
							selectStart = false;
							start = true;
							tempPredicate = new LinkedList<String>();
							tempSubjRow = new ArrayList<String>();
							tempObjCol = new ArrayList<String>();
							continue;
						} else if ("}".equals(value)) {
							end = true;
						} else if (".".equals(value)) {
							triplePattern++;
							tripleCount = 0;
							continue;
						}

						if (end) {
							queryDTO.setTempObjCol(tempObjCol);
							queryDTO.setTempSubjRow(tempSubjRow);
							queryDTO.setTempSelect(tempSelect);
							queryDTO.setTempPredicate(tempPredicate);
							queryDTO.setTriplePatCnt(triplePattern);
							splitQuery.put(queryCount, queryDTO);
							queryCount++;
							end = false;
							continue;
						}

						if (selectStart && !"where".equalsIgnoreCase(value)) {
							tempSelect.add(value);
						} else if (start) {
							if (tripleCount == 0) {
								tempSubjRow.add(value);
								if (!tempSubjRow.contains(value)) {
									subjectRow[i] = value;
								}
							} else if (tripleCount == 2) {
								tempObjCol.add(value);
								if (!tempObjCol.contains(value)) {
									objectColumn[j] = value;
								}
							} else {
								tempPredicate.add(value);
							}
							tripleCount++;
						}
					}

				}

				graphBuilder.constructMatrix(splitQuery);

//				System.out.println("tempSelect Query -->"
//						+ tempSelect.toString());
//				System.out.println("temp Predicate-->"
//						+ tempPredicate.toString());
//				System.out.println("temp Subject Row -->"
//						+ tempSubjRow.toString());
//				System.out.println("temp Object Column -->"
//						+ tempObjCol.toString());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
