/**
 * 
 */
package umkc.edu.cs5590LD.impl;

/**
 * @author Rajasekar Rajendran
 *
 */
public class CommandTracker {
	
	private String command;
	private int tripleLevel;
	private int variable;
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	/**
	 * @return the tripleLevel
	 */
	public int getTripleLevel() {
		return tripleLevel;
	}
	/**
	 * @param tripleLevel the tripleLevel to set
	 */
	public void setTripleLevel(int tripleLevel) {
		this.tripleLevel = tripleLevel;
	}
	/**
	 * @return the variable
	 */
	public int getVariable() {
		return variable;
	}
	/**
	 * @param variable the variable to set
	 */
	public void setVariable(int variable) {
		this.variable = variable;
	}

}
