package meialuaesoco.codeGenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ConstTable {
	private HashMap<String, Constant> constants;
	private LinkedList<Constant> orderedConstants;
	
	public ConstTable() {
		constants = new HashMap<String, Constant>();
		orderedConstants = new LinkedList<Constant>();
	}
	
	public int getNumConstants() {
		return constants.size();
	}

	public Constant addConstant(String value) {
		return addConstants(new Constant(value, constants.size()));
	}

	public Constant addConstant(boolean value) {
		return addConstants(new Constant(value, constants.size()));
	}

	public Constant addConstant(double value) {
		return addConstants(new Constant(value, constants.size()));
	}

	private Constant addConstants(Constant constant) {
		if (!constants.containsKey(constant.getName())) {
			constants.put(constant.getName(), constant);
			orderedConstants.add(constant);
			return constant;
		} else
			return constants.get(constant.getName());
	}
	
	public List<Constant> getConstants() {
		return orderedConstants;
	}
}
