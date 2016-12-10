/**
 * 
 */
package umkc.edu.cs5590LD.impl;

import java.util.List;

/**
 * @author Rajasekar Rajendran
 * 
 */
public class PartialQueryDTO {
	private List<String> str;
	private boolean isSelfJoin;
	private boolean isFilter;
	private List<String> selfJoins;
	private LiteralTriple literal;
	/**
	 * @return the str
	 */
	public List<String> getStr() {
		return str;
	}
	/**
	 * @param str2 the str to set
	 */
	public void setStr(List<String> str2) {
		this.str = str2;
	}
	/**
	 * @return the isSelfJoin
	 */
	public boolean isSelfJoin() {
		return isSelfJoin;
	}
	/**
	 * @param isSelfJoin the isSelfJoin to set
	 */
	public void setSelfJoin(boolean isSelfJoin) {
		this.isSelfJoin = isSelfJoin;
	}
	/**
	 * @return the idFilter
	 */
	public boolean isFilter() {
		return isFilter;
	}
	/**
	 * @param idFilter the idFilter to set
	 */
	public void setFilter(boolean isFilter) {
		this.isFilter = isFilter;
	}
	/**
	 * @return the selfJoins
	 */
	public List<String> getSelfJoins() {
		return selfJoins;
	}
	/**
	 * @param selfJoins the selfJoins to set
	 */
	public void setSelfJoins(List<String> selfJoins) {
		this.selfJoins = selfJoins;
	}
	/**
	 * @return the literal
	 */
	public LiteralTriple getLiteral() {
		return literal;
	}
	/**
	 * @param literal the literal to set
	 */
	public void setLiteral(LiteralTriple literal) {
		this.literal = literal;
	}
}
