package symboltable;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class symbolTableStruct {
    private  String name;
    private  int scope;
    public HashMap<String, symbolTableInfo> variables = new HashMap<String, symbolTableInfo>();   //empty map with variables
    public HashMap<String, method> methods = new HashMap<String, method>();

    public int endV;
    public int endM;
    public symbolTableStruct(String n){
        name = n;
        scope = 0;
        endV = 0;
        endM = 0;
    }

    public void insertVars(String name, String type) throws Exception{
        if(!variables.containsKey(name)){
            symbolTableInfo si = new symbolTableInfo(name, type, scope);     //create class
            variables.put(name,si);      //add in map
        }else{
            throw new Exception("Variable "+ name + " is already defined in this scope.");
        }
    }

    public void insertMethod(String name, String rType, int scope, ArrayList<String> vars, ArrayList<String> types) throws Exception {
        method m = new method(name, rType, scope);
        if(methods.containsKey(name)){
            throw new Exception("Method "+ name + " already defiend in scope");
        }
        if(!vars.isEmpty() && !types.isEmpty()){
            for(int i = 0; i < vars.size(); i++) {
                String v = vars.get(i);
                String t = types.get(i);
                m.parameters.put(v, t);
            }
        }


        methods.put(name, m);

        //methods.get(name).printMethod();
    }

    public void insertMethodVars(String key, String name, String type) throws Exception{

        int scope = methods.get(key).getScope();
        if(!methods.get(key).parameters.containsKey(name)) {

            methods.get(key).insertVariables(name, type, scope);
        }
        else
            throw new Exception("Variable '"+ name+"' is already defined in scope");
    }

    public void printMethods(){
        System.out.println("METHODS OF CLASS: "+ name);
        for (String name : methods.keySet()) {
            String key = name.toString();
            String type = methods.get(name).getType();
            int scope = methods.get(name).getScope();
            System.out.println("\t"+ key + " " + type + " " + scope);
            methods.get(name).printParams();
            methods.get(name).printVars();
        }


    }

    public void printVec(){
        System.out.println("VARIABLES MAP OF CLASS: "+ name);
        for (String name : variables.keySet()) {
            String key = name.toString();
            String type = variables.get(name).getType();
            int scope = variables.get(name).getScope();

            System.out.println("\t"+ key + " " + type + " " + scope);
        }

    }

    public String getName(){          //access modifiers functions
        return this.name;
    }       //access modifiers
    public void setName(String n){
        this.name = n;
    }
    public int getScope(){ return this.scope;}
    public void setScope(int s) {
        this.scope = s;
    }

    public void clear(){
        variables.clear();
        methods.clear();
        name = null;
        scope = 0;
        endV = 0;
        endM = 0;
    }
}
