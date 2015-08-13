package edu.uw.apl.commons.tsk4j.digests;

/**
 * @author Stuart Maclean
 *
 */

public abstract class BodyFileOperator {

	abstract public int arity();
	
	protected BodyFileOperator( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	final String name;
}

// eof
		