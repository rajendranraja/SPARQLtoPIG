/**
 * 
 */
package umkc.edu.cs5590LD.query;

import java.util.List;
import java.util.Map;

/**
 * @author Rajasekar Rajendran
 *
 */
public class QueryDTO {
	
	private List<String> tempSelect;
	private List<String> tempPredicate;
	private List<String> tempSubjRow;
	private List<String> tempObjCol;
	private int triplePatCnt;
	private Map<Integer, TripleDTO> query;
	
	public int getTriplePatCnt() {
		return triplePatCnt;
	}
	public void setTriplePatCnt(int triplePatCnt) {
		this.triplePatCnt = triplePatCnt;
	}
	public List<String> getTempSelect() {
		return tempSelect;
	}
	public void setTempSelect(List<String> tempSelect) {
		this.tempSelect = tempSelect;
	}
	public List<String> getTempPredicate() {
		return tempPredicate;
	}
	public void setTempPredicate(List<String> tempQueryString) {
		this.tempPredicate = tempQueryString;
	}
	public List<String> getTempSubjRow() {
		return tempSubjRow;
	}
	public void setTempSubjRow(List<String> tempSubjRow2) {
		this.tempSubjRow = tempSubjRow2;
	}
	public List<String> getTempObjCol() {
		return tempObjCol;
	}
	public void setTempObjCol(List<String> tempObjCol2) {
		this.tempObjCol = tempObjCol2;
	}
	/**
	 * @return the query
	 */
	public Map<Integer, TripleDTO> getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(Map<Integer, TripleDTO> query) {
		this.query = query;
	}
}
