/**
 * 
 */
package umkc.edu.cs5590LD.impl;

import java.util.List;
import java.util.Map;

/**
 * @author Rajasekar Rajendran
 *
 */
public class TripleJoinDTO {
	
	private Map<Integer, List<String>> triplesJoin;
	private List<PartialQueryDTO> partQryList;
	private List<String> tempSelect;
	private String[] subj;
	private String[] obj;
	private String[] pred;
	/**
	 * @return the triplesJoin
	 */
	public Map<Integer, List<String>> getTriplesJoin() {
		return triplesJoin;
	}
	/**
	 * @param triplesJoin the triplesJoin to set
	 */
	public void setTriplesJoin(Map<Integer, List<String>> triplesJoin) {
		this.triplesJoin = triplesJoin;
	}
	/**
	 * @return the partQryList
	 */
	public List<PartialQueryDTO> getPartQryList() {
		return partQryList;
	}
	/**
	 * @param partQryList the partQryList to set
	 */
	public void setPartQryList(List<PartialQueryDTO> partQryList) {
		this.partQryList = partQryList;
	}
	/**
	 * @return the tempSelect
	 */
	public List<String> getTempSelect() {
		return tempSelect;
	}
	/**
	 * @param tempSelect the tempSelect to set
	 */
	public void setTempSelect(List<String> tempSelect) {
		this.tempSelect = tempSelect;
	}
	/**
	 * @return the subj
	 */
	public String[] getSubj() {
		return subj;
	}
	/**
	 * @param subj the subj to set
	 */
	public void setSubj(String[] subj) {
		this.subj = subj;
	}
	/**
	 * @return the obj
	 */
	public String[] getObj() {
		return obj;
	}
	/**
	 * @param obj the obj to set
	 */
	public void setObj(String[] obj) {
		this.obj = obj;
	}
	/**
	 * @return the pred
	 */
	public String[] getPred() {
		return pred;
	}
	/**
	 * @param pred the pred to set
	 */
	public void setPred(String[] pred) {
		this.pred = pred;
	}

}
