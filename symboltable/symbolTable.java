package symboltable;

import java.util.*;

public class symbolTable {
    private static LinkedHashMap<String, symbolTableStruct> ClassNames = new LinkedHashMap<String, symbolTableStruct>();
    private static LinkedHashMap<String, String> SuperClasses = new LinkedHashMap<String, String>();
    private static ArrayList<String> Arguments = new ArrayList<String>();
    private static ArrayList<Integer> VarOffsets = new ArrayList<Integer>();
    private static ArrayList<Integer> MethodOffsets = new ArrayList<Integer>();
    public static int flag, flagM;


    public symbolTable(){
        flag = 0;
        flagM = 0;
    }

    public static int getValueOffset(String type){
        if(type.equals("int"))
            return 4;
        else if(type.equals("boolean"))
            return 1;
        else
            return 8;

    }
    //functions for offset
    public static void insertVarOffset(String classname, String var, String type, String superClass){

        if(flag == 1) {           //new class
            int value;
            if (!superClass.equals(" ")) {
                value = VarOffsets.get(ClassNames.get(superClass).endV);
                VarOffsets.add(value);
            } else {
                value = getValueOffset(type);
                VarOffsets.add(value);
                value = 0;
            }
            ClassNames.get(classname).endV = VarOffsets.size() - 1;
            System.out.println(classname + "." + var + " : " + value);
            flag = 0;
        } else{

            int last;

            last = VarOffsets.get(VarOffsets.size() - 1);
            int value = last + getValueOffset(type);
            VarOffsets.add(value);
            ClassNames.get(classname).endV = VarOffsets.size() - 1;

            System.out.println(classname+ "."+ var + " : "+ last);
        }

    }
    public static void insertMethOffset(String classname, String method, String superClass){


        if(MethodOffsets.isEmpty()) {       //if the array is empty
            MethodOffsets.add(0);
            ClassNames.get(classname).endM = 0;
            System.out.println(classname+ "."+ method + " : "+ 0);

        }else if(flagM == 1) {           //new class
            int value;

            boolean print = true;
            if(!superClass.equals(" ")){
                value = MethodOffsets.get(ClassNames.get(superClass).endM) + 8;
                if(checkMethodOverload(superClass, method))
                    print = false;
            }else{
                value = 0;
            }
            MethodOffsets.add(value);
            ClassNames.get(classname).endM = MethodOffsets.size() - 1;
            if(print)
                System.out.println(classname+ "."+ method + " : "+ value);
            flagM = 0;
        }
        else{
            int last;

            boolean print = true;
            if(!superClass.equals(" ")){
                if(checkMethodOverload(superClass, method))
                    print = false;
            }

            last = MethodOffsets.get(MethodOffsets.size() - 1);

            int value = last + 8;
            MethodOffsets.add(value);
            ClassNames.get(classname).endM = MethodOffsets.size() - 1;

            if(print)
                System.out.println(classname+ "."+ method + " : "+ value);
        }

    }
    public static boolean checkMethodOverload(String className , String methodName){
        return ClassNames.get(className).methods.containsKey(methodName);
    }


    //functions for allocation

    public static void insert(String name) throws Exception {  //insert class
        if(!ClassNames.containsKey(name)){      //check if class exists
            symbolTableStruct s = new symbolTableStruct(name);
            ClassNames.put(name, s);
        }else{
            throw new Exception("Duplicate class "+ name);
        }


    }
    public static void insertSuper(String subClass, String superClass) throws Exception {

        if(ClassNames.containsKey(superClass)){      //check if class is previous declared
            SuperClasses.put(subClass, superClass);
        }else{
            throw new Exception("Extended class "+superClass+" is not declared.");
        }

    }


    public static void insertVars(String key, String name, String type) throws Exception { //insert variables in class
        ClassNames.get(key).insertVars(name,type);

    }

    public static void insertMethodVars(String key, String methodName,String name, String type) throws Exception { //insert variables in class
        ClassNames.get(key).insertMethodVars(methodName, name, type);

    }

    public static void insertMethod(String key, String name, String type, ArrayList<String> vars, ArrayList<String> types)throws Exception{
        int scope = ClassNames.get(key).getScope() + 1;
        ClassNames.get(key).insertMethod(name, type, scope, vars, types);


    }
    public static void checkSuperMethods(String classN, String method) throws Exception {
        if(SuperClasses.containsKey(classN)) {
            String superClass = SuperClasses.get((classN));
            if (ClassNames.get(superClass).methods.containsKey(method)) {
                if (!(ClassNames.get(superClass).methods.get(method).parameters).equals(ClassNames.get(classN).methods.get(method).parameters))
                    throw new Exception("Overridden method must have the same arguments");
            }
        }
    }

    public static boolean checkSuperType(String className, String type)throws Exception{
        if(className.equals(type))
            return true;

        String name = SuperClasses.get(className);
        while(name != null){
            if(name.equals(type))
                return true;
            name = SuperClasses.get(name);
        }
        return false;

    }

    //functions for checking
    public static String checkVarInitialized(String className, String methodName, String varName) throws Exception{
        boolean foundVar = false;
        String foundType = null;

        if(ClassNames.get(className).variables.containsKey(varName)) {      //check if variable is declared in class's scope
            foundVar = true;
            foundType = ClassNames.get(className).variables.get(varName).getType();
        }

        String superClass = SuperClasses.get(className);                    // check if variable is declared in super-class's scope
        if(superClass!=null){
            while(superClass!= null) {
                if (ClassNames.get(superClass).variables.containsKey(varName)) {
                    foundVar = true;
                    foundType = ClassNames.get(superClass).variables.get(varName).getType();
                }
                superClass = SuperClasses.get(superClass);
            }
        }

        if(!methodName.equals(" ")){       //if there is a method in class check if the var exists in method's scope
            if(ClassNames.get(className).methods.get(methodName).variables.containsKey(varName)) {
                foundVar = true;
                foundType = ClassNames.get(className).methods.get(methodName).variables.get(varName).getType();
            }
            if(ClassNames.get(className).methods.get(methodName).parameters.containsKey(varName)) {
                foundVar = true;
                foundType = ClassNames.get(className).methods.get(methodName).parameters.get(varName);
            }
        }

        if(foundVar == false)
            throw new Exception("Undefined variable "+ varName+ ".");
        return foundType;

    }


    public static void checkClassAssign(String className, String expType) throws Exception{

        if(!ClassNames.containsKey(className)){      //check if class is declared
            throw new Exception("Cannot resolve symbol '"+className+"'.");
        }



    }


    public static String getMethodType(String className, String methodName) throws Exception {
        if(!ClassNames.containsKey(className)){      //check if class is declared
            throw new Exception("Cannot resolve symbol '"+className+"'.");
        }

        if(ClassNames.get(className).methods.containsKey(methodName)){
           return ClassNames.get(className).methods.get(methodName).getType();
        }
        if(SuperClasses.containsKey(className)) {
            String superClass = SuperClasses.get(className);
            while (superClass != null) {
                if (ClassNames.get(superClass).methods.containsKey(methodName)) {
                    return ClassNames.get(superClass).methods.get(methodName).getType();
                }
                superClass = SuperClasses.get(superClass);
            }
        }
        throw new Exception("Class or superclass has no method "+methodName+"().");

    }
    public static void insertArgument(String argType){
        Arguments.add(argType);
    }
    public static void clearArguments(){
        Arguments.clear();
    }

    public static void checkArguments(String className, String methodName) throws Exception {

        if(ClassNames.get(className).methods.get(methodName) == null) {    //if the method is from superclass

            while(ClassNames.get(className).methods.get(methodName) == null){
                className = SuperClasses.get(className);
            }

        }

        int paramSize = ClassNames.get(className).methods.get(methodName).parameters.size();

        int argSize = Arguments.size();
        if(paramSize!= argSize )
            throw new Exception("wrong number of arguments(given "+argSize+", expected "+paramSize + ").");


        int i = 0;
        for (String arg : ClassNames.get(className).methods.get(methodName).parameters.keySet()) {
            String type = ClassNames.get(className).methods.get(methodName).parameters.get(arg);
            if(!Arguments.get(i).equals(type)){
                String classN = Arguments.get(i);
                if(SuperClasses.get(classN)!= null){     //check if the type is the superclass
                    boolean found = false;
                    while(SuperClasses.get(classN)!= null) {
                        if (SuperClasses.get(classN).equals(type))
                            found = true;
                        classN = SuperClasses.get(classN);
                    }
                    if(!found)
                        throw new Exception("Wrong argument " + (i + 1) + " type: found " + Arguments.get(i) + ", required: " + type);
                }else{
                    throw new Exception("Wrong argument " + (i + 1) + " type: found " + Arguments.get(i) + ", required: " + type);
                }
            }

            i++;
        }

    }


    public static void printLH() {
        for (String key : ClassNames.keySet()) {
            print(key);

        }

    }

    public static void print(String string) {
        ClassNames.get(string).printVec();
    }

    public static void printMethods() {
        for (String key : ClassNames.keySet()) {
            printm(key);

        }

    }

    public static void printm(String string) {
        ClassNames.get(string).printMethods();
    }

    public static void clear(){
        for (String key : ClassNames.keySet()) {
            ClassNames.get(key).clear();
        }
        ClassNames.clear();
        SuperClasses.clear();
        Arguments.clear();
        MethodOffsets.clear();
        VarOffsets.clear();
        flag = 0;
        flagM = 0;
    }



}
