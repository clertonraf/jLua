package tests;

import junit.framework.TestCase;
import meialuaesoco.codeGenerator.ConstTable;

public class ConstTableTest extends TestCase {

	public void testAll() {
		ConstTable t = new ConstTable();
		t.addConstant("teste");
		assertEquals(1, t.getNumConstants());
		t.addConstant("teste");
		assertEquals(1, t.getNumConstants());
		t.addConstant("teste2");
		assertEquals(2, t.getNumConstants());
		t.addConstant(null);
		assertEquals(3, t.getNumConstants());
		t.addConstant(null);
		assertEquals(3, t.getNumConstants());
		t.addConstant(true);
		assertEquals(4, t.getNumConstants());
		t.addConstant(true);
		assertEquals(4, t.getNumConstants());
		t.addConstant(false);
		assertEquals(5, t.getNumConstants());
		t.addConstant(false);
		assertEquals(5, t.getNumConstants());
		t.addConstant(25);
		assertEquals(6, t.getNumConstants());
	}
}
