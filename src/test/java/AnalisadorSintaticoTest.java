import java.io.StringReader;

import junit.framework.TestCase;
import meialuaesoco.ErrorReporter;
import meialuaesoco.MeiaLuaESoco;

public class AnalisadorSintaticoTest extends TestCase {

	private void test(String code) throws Exception {
		MeiaLuaESoco sintatico = new MeiaLuaESoco(new StringReader(code));
		sintatico.disableCodeGeneration();
		assertTrue(sintatico.compile());
	}
	
	private void testFalse(String code) throws Exception {
		MeiaLuaESoco sintatico = new MeiaLuaESoco(new StringReader(code));
		sintatico.disableCodeGeneration();
		assertFalse(sintatico.compile());
	}

	public void setUp() {
		ErrorReporter.getInstance().reset();
	}

	public void testCaso1() throws Exception {
		test("do end");
	}

	public void testCaso2() throws Exception {
		test("return a + b;");
	}

	public void testCaso3() throws Exception {
		test("do do do end end end");
	}

	public void testCaso4() throws Exception {
		test("teste(2);");
	}

	public void testCaso5() throws Exception {
		test(	"while teste(2) do" +
				"	if 2 + 2 == 4 then" +
				"		break;" +
				"	end" +
				"end");
	}

	public void testCaso6() throws Exception {
		test(	"fact = function()" +
				"end");
	}

	public void testCaso7() throws Exception {
		test(	"fact = function(n)" +
				"end");
	}

	public void testCaso8() throws Exception {
		test(	"fact = function(n)" +
				"	if n == 0 then" +
				"		n = \"teste\"" +
				"	end" +
				"end");
	}
	public void testCaso9() throws Exception {
		testFalse(	"fact = function(n)" +
				"	if n == 0 then" +
				"		n = \"teste\"" +
				"	end");
	}
	
	public void testCaso10() throws Exception {
		testFalse("a,b,c = 2;");
	}
	
	public void testCase11() throws Exception {
		testFalse("2 = 3");
	}
	
	public void testCase12() throws Exception {
		testFalse("a = 2 ++ b;");
	}

	public void testCase13() throws Exception {
		test("do a = 2; end");
	}

	public void testCase14() throws Exception {
		test("do a = b; end");
	}

	public void testCase15() throws Exception {
		test(	"media = function(a, b)" +
				"	c = a + b;" +
				"	return c/2;" +
				"end;" +
				"res = media(2,4);");
	}

	public void testCase16() throws Exception {
		test("abc();");
	}

	public void testCase17() throws Exception {
		test("abc(\"hello\");");
	}

	public void testCase18() throws Exception {
		test("abc(d);");
	}
	
	public void testCase19() throws Exception {
		test("");
	}

	public void testCase20() throws Exception {
		test(	"if a then" +
				"	b = 2;" +
				"else" +
				"	b = 3;" +
				"end");
	}
}
