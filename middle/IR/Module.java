package middle.IR;

import java.util.ArrayList;

public class Module {
    String name;
    public ArrayList<Function> functions;
    public ArrayList<GlobalVariable> globalVariables;
    public Function mainFunction;
    public Context context;

    public Module(String name) {
        this.name = name;
        this.functions = new ArrayList<>();
        this.globalVariables = new ArrayList<>();
        this.context = new Context();
    }

    public void addFunction(Function function) {
        this.functions.add(function);
    }

    public void addMainFunction(Function function) {
        this.mainFunction = function;
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        this.globalVariables.add(globalVariable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GlobalVariable globalVariable : globalVariables) {
            sb.append(globalVariable).append("\n");
        }
        for (Function function : functions) {
            sb.append(function).append("\n");
        }
        sb.append(mainFunction).append("\n");
        return sb.toString();
    }
}
