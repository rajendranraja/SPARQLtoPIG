/**
 * 
 */
package umkc.edu.cs5590LD.impl;

/**
 * @author Rajasekar Rajendran
 *
 */
public class LiteralTriple {
	
	private boolean subject;
	private boolean object;
	private boolean predicate;
	/**
	 * @return the subject
	 */
	public boolean isSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(boolean subject) {
		this.subject = subject;
	}
	/**
	 * @return the object
	 */
	public boolean isObject() {
		return object;
	}
	/**
	 * @param object the object to set
	 */
	public void setObject(boolean object) {
		this.object = object;
	}
	/**
	 * @return the predicate
	 */
	public boolean isPredicate() {
		return predicate;
	}
	/**
	 * @param predicate the predicate to set
	 */
	public void setPredicate(boolean predicate) {
		this.predicate = predicate;
	}
	
}
