import syntaxtree.*;
import visitor.*;

import java.lang.*;
import symboltable.*;
import java.util.*;



class MyVisitor extends GJDepthFirst<String, String>{

    private void getVariables(String className, NodeListOptional varDecls, int f, String extendedClass) throws Exception {
        System.out.println("--Variables---");
        for (int i = 0; i < varDecls.size(); ++i) {
            VarDeclaration varDecl = (VarDeclaration) varDecls.elementAt(i);

            String varName = varDecl.f1.accept(this,null);     // get name of variable
            String varType = varDecl.f0.accept(this,null);  // get type of variable
            symbolTable.insertVars(className, varName, varType);
            if(f != 0){
                symbolTable.insertVarOffset(className, varName, varType, extendedClass);
            }
        }

    }

    private void getMethodVariables(String className, String methodName ,NodeListOptional varDecls) throws Exception {
        for (int i = 0; i < varDecls.size(); ++i) {
            VarDeclaration varDecl = (VarDeclaration) varDecls.elementAt(i);

            String varName = varDecl.f1.accept(this,null);     // get name of variable
            String varType = varDecl.f0.accept(this,null);  // get type of variable
            symbolTable.insertMethodVars(className, methodName, varName, varType);

        }

    }


    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() *)
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, String argu) throws Exception {
        String classname = n.f1.accept(this, null);
        symbolTable.insert(classname);


        NodeListOptional varDecls = n.f14;
        getVariables(classname, varDecls, 0, " ");

       // super.visit(n, argu);



        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, String argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("\n-----------Class " + classname+ "-----------");
        symbolTable.insert(classname);

        NodeListOptional varDecls = n.f3;
        symbolTable.flag = 1;
        getVariables(classname, varDecls, 1, " ");
        symbolTable.flag = 0;

        System.out.println("---Methods---");
        n.f4.accept(this, classname+", ");

        //super.visit(n, classname);


        symbolTable.flagM = 1;
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("\n-----------Class " + classname+ "-----------");
        symbolTable.insert(classname);

        String superclass = n.f3.accept(this, null);
        symbolTable.insertSuper(classname, superclass);

        NodeListOptional varDecls = n.f5;
        symbolTable.flag = 1;
        getVariables(classname, varDecls, 1, superclass);
        symbolTable.flag = 0;


        System.out.println("---Methods---");
        n.f6.accept(this, classname+","+superclass);

        symbolTable.flagM = 1;
        return null;
    }
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public String visit(VarDeclaration n, String argu) throws Exception {
        String _ret=null;
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        n.f2.accept(this, argu);

        return type + " " + name;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, String argu) throws Exception {
        String[] classes = argu.split(",");
        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);


        ArrayList<String> vars = new ArrayList<String>();
        ArrayList<String> types = new ArrayList<String>();
        if(!argumentList.equals("")){
            String[] arrOfStr = argumentList.split(",");
            String[] var;


            for (String a : arrOfStr){
                var = a.split("-");
                vars.add(var[1]);
                types.add(var[0]);
            }



        }
        symbolTable.insertMethod(classes[0], myName, myType, vars, types);
        symbolTable.checkSuperMethods(classes[0], myName);    //check if its overridden method

        NodeListOptional varMethodDecls = n.f7;
        getMethodVariables(classes[0],myName ,varMethodDecls);
        symbolTable.insertMethOffset(classes[0], myName, classes[1]);


        return null;
    }



    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, String argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterTerm n, String argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, String argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += "," + node.accept(this, null);      

        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, String argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + "-" + name;
    }

    @Override
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    public String visit(BooleanType n, String argu) {
        return "boolean";
    }

    public String visit(IntegerType n, String argu) {
        return "int";
    }


    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public String visit(Identifier n, String argu) {
        return n.f0.toString();
    }
}

