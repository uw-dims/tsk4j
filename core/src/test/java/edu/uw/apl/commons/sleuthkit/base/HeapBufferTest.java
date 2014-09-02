package edu.uw.apl.commons.sleuthkit.base;

public class HeapBufferTest extends junit.framework.TestCase {

	public void testOOM() throws Exception {

		try {
			/*
			  16GB - 1 byte will get narrowed to UINT_MAX on 32 bit platforms,
			  which is 4GB - 1 byte, surely still enough to blow the heap.
			  On 64-bit platforms, size_t is 64-bit, but a 16GB malloc
			  will still likely fail. Without the -1, 16GB reduces to 0
			  when cast to size_t on 32 bit platforms
			*/
			HeapBuffer hb = new HeapBuffer( 1024L * 1024 * 1024 * 16 - 1 );
			fail();
		} catch( OutOfMemoryError oom ) {
		}
	}
}

// eof
