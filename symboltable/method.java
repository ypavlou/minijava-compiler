package symboltable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class method {
    private String name;
    private String returnType;
    private int scope;

    public  HashMap<String, String> parameters = new LinkedHashMap<String, String>();      //to maintain insertion order
    public HashMap<String, symbolTableInfo> variables = new HashMap<String, symbolTableInfo>();   //empty map with variables

    public method(String n, String t, int s){
        name = n;
        returnType = t;
        scope = s;
    };


    public void printMethod(){
        System.out.println("METHOD : "+name+ " " + returnType + " " +  scope);
    }

    public void insertVariables(String name, String type, int scope) throws Exception{
        if(!variables.containsKey(name)){
            symbolTableInfo si = new symbolTableInfo(name, type, scope);     //create class
            variables.put(name,si);      //add in map
        }else{
            throw new Exception("Variable "+ name + " is already defined in this scope.");
        }
    }


    public void printParams(){
        System.out.println("PARAMS OF METHOD "+ name + ": "+ Collections.singletonList(parameters));

    }

    public void printVars(){
        System.out.println("VARIABLES MAP OF METHOD: "+ name);
        for (String name : variables.keySet()) {
            String key = name.toString();
            String type = variables.get(name).getType();
            int scope = variables.get(name).getScope();

            System.out.println("\t"+ key + " " + type + " " + scope);
        }


    }

    public String getName(){          //access modifiers functions
        return this.name;
    }           //access modifiers
    public void setName(String n){
        this.name = n;
    }
    public String getType(){ return this.returnType; }
    public void setType(String t) { this.returnType = t; }
    public int getScope(){ return this.scope; }
    public void setScope(int s) {
        this.scope = s;
    }

    public void clear(){
        parameters.clear();
        variables.clear();
    }

    // Overriding the hashcode() function
    @Override
    public int hashCode() {     //from https://stackoverflow.com/questions/2265503/why-do-i-need-to-override-the-equals-and-hashcode-methods-in-java

        // uses roll no to verify the uniqueness
        // of the object of  class
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    // Equal objects must produce the same
    // hash code as long as they are equal
    @Override
    public boolean equals(Object o)     //from https://www.geeksforgeeks.org/how-to-create-a-java-hashmap-of-user-defined-class-type/
    {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        method other = (method)o;
        if (!this.name.equals(other.name) && !this.returnType.equals(other.returnType) && this.scope != other.scope) {
            return false;
        }
        return true;
    }


}
