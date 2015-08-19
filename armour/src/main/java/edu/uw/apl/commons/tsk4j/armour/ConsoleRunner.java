package edu.uw.apl.commons.tsk4j.armour;

/*
  For the purposes of creating a runnable jar with Armour invoked by
  jline.ConsoleRunner, providing very nice command line editing
  features.
*/

public class ConsoleRunner {

	static public void main( String[] args ) throws Exception {
		String[] args2 = new String[args.length+1];
		args2[0] = ConsoleRunner.class.getPackage().getName() + ".Armour";
		for( int i = 0; i < args.length; i++ )
			args2[i+1] = args[i];
		jline.ConsoleRunner.main( args2 );
	}
}

// eof
