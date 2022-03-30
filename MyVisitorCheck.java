import syntaxtree.*;
import visitor.*;

import java.lang.*;
import symboltable.*;
import java.util.*;



class MyVisitorCheck extends GJDepthFirst<String, String>{

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
        String cl = n.f1.accept(this, null);
        String[] str = cl.split(",");
        String classname = str[0];


        if(n.f15.present()){
            n.f15.accept(this, classname +", ");
        }
        n.f16.accept(this, classname);
        n.f17.accept(this, classname);

        //super.visit(n, argu);

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
        String cl = n.f1.accept(this, null);
        String[] str = cl.split(",");
        String classname = str[0];

        n.f4.accept(this, classname);

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
        String cl = n.f1.accept(this, null);
        String[] str = cl.split(",");
        String classname = str[0];


        n.f6.accept(this, classname);


        return null;
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

        String myType = n.f1.accept(this, null);
        String[] s = myType.split(",");
        String expT = s[0];


        String ret = n.f2.accept(this, null);
        String[] str = ret.split(",");
        String myName = str[0];


        n.f8.accept(this, argu + "," + myName);

        String retType =  n.f10.accept(this, argu + "," + myName);
        String [] r = retType.split(",");
        String returned = r[1];
        if(r[1].equals("id"))
            returned = symbolTable.checkVarInitialized(argu, myName, r[0]);
        if(r[1].equals("this"))
            returned = argu;
        if(r[1].equals("class")){
            returned = r[0];

        }

        if(!returned.equals(expT)){
            if(symbolTable.checkSuperType(returned,expT))
                return null;
            throw new Exception("Invalid Method declaration expected return type '"+expT+"' but found '"+returned+"'");
        }

        return null;
    }




    @Override
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, String argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, String argu) {
        return "int";
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    @Override
    public String visit(Statement n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }



    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    @Override
    public String visit(Block n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n, String argu) throws Exception {
        String _ret=null;

        String[] arg = argu.split(",");


        String var = n.f0.accept(this, argu);       //format: variable, id
        String[] str = var.split(",");
        String variableName = str[0];


        String val = n.f2.accept(this, argu);
        String[] s = val.split(",");
        String valueType = s[1];

        String varType = symbolTable.checkVarInitialized(arg[0], arg[1], variableName);

        if(s[1].equals("class")){//if the assignment is class allocation
            symbolTable.checkClassAssign(s[0], varType);
            valueType = s[0];
        }

        if(s[1].equals("id")){      //if the assignment is identifier
            valueType = symbolTable.checkVarInitialized(arg[0], arg[1], s[0]);

        }
        if(s[1].equals("this")){      //if the assignment is this
            valueType = arg[0];
        }

        if(!varType.equals(valueType) && !symbolTable.checkSuperType(valueType,varType)){
            throw new Exception("Incompatible types "+varType+" and "+ valueType+".");
        }


        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
        String _ret=null;

        String[] arg = argu.split(",");

        String i = n.f0.accept(this, argu);
        String[] str = i.split(",");
        String id = str[0], idType;

        idType = symbolTable.checkVarInitialized(arg[0], arg[1], id);
        if(!idType.equals("int[]"))
            throw new Exception("Array type expected; Found:'"+idType+"'");


        String exp1 = n.f2.accept(this, argu);
        String[] s = exp1.split(",");
        String expType = s[1];

        if(s[1].equals("id")){      //if it is identifier
            expType = symbolTable.checkVarInitialized(arg[0], arg[1], s[0]);  //check if exists and get type
        }
        if(s[1].equals("this")){      //if the assignment is this
            expType = arg[0];
        }

        if(!expType.equals("int"))
            throw new Exception("Array index required type 'int' but found '"+expType+"'");

        String exp2 = n.f5.accept(this, argu);
        String[] s2 = exp2.split(",");
        expType = s2[1];

        if(s2[1].equals("id")){      //if it is identifier
            expType = symbolTable.checkVarInitialized(arg[0], arg[1], s2[0]);  //check if exists and get type
        }

        if(!expType.equals("int"))
            throw new Exception("Array assigment required type 'int' but found '"+expType+"'");



        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public String visit(IfStatement n, String argu) throws Exception {
        String _ret=null;
        String[] arg = argu.split(",");

        String exp = n.f2.accept(this, argu);
        String[] s = exp.split(",");
        String valueType = s[1];

        if(s[1].equals("id")){      //if it is identifier
            valueType = symbolTable.checkVarInitialized(arg[0], arg[1], s[0]);  //check if exists and get type
        }

        if(!valueType.equals("boolean"))
            throw new Exception("If statement expected type of 'boolean' but found '"+valueType+"'");


        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n, String argu) throws Exception {
        String _ret=null;
        String[] arg = argu.split(",");

        String exp = n.f2.accept(this, argu);
        String[] s = exp.split(",");
        String valueType = s[1];

        if(s[1].equals("id")){      //if it is identifier
            valueType = symbolTable.checkVarInitialized(arg[0], arg[1], s[0]);  //check if exists and get type
        }

        if(!valueType.equals("boolean"))
            throw new Exception("While statement expected type of 'boolean' but found '"+valueType+"'");
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n, String argu) throws Exception {
        String _ret=null;
        String[] arg = argu.split(",");

        String exp = n.f2.accept(this, argu);
        String[] s = exp.split(",");
        String valueType = s[1];

        if(s[1].equals("id")){      //if it is identifier
            valueType = symbolTable.checkVarInitialized(arg[0], arg[1], s[0]);  //check if exists and get type
        }

        if(!valueType.equals("int"))
            throw new Exception("System.out.println expected type of 'int' but found '"+valueType+"'");

        return _ret;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    @Override
    public String visit(Expression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(AndExpression n, String argu) throws Exception {
        String[] arg = argu.split(",");
        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        n.f1.accept(this, argu);
        String ret2 =n.f2.accept(this, argu);
        String[] PE2 = ret2.split(",");

        String type1 = PE1[1];
        String type2 = PE2[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
            type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if(PE2[1].equals("id")) {
            type2 = symbolTable.checkVarInitialized(arg[0], arg[1], PE2[0]);
        }

        if (!(type1.equals("boolean") && type2.equals("boolean"))) {        //checking if their type is right for operation
            throw new Exception("Operator '&&' cannot be applied to " + "'" + type1 + "'," + "'" + type2 + "'");
        }
        return "boolean,boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, String argu) throws Exception {
        String[] arg = argu.split(",");

        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        n.f1.accept(this, argu);
        String ret2 =n.f2.accept(this, argu);
        String[] PE2 = ret2.split(",");

        String type1 = PE1[1];
        String type2 = PE2[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
             type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if(PE2[1].equals("id")) {
             type2 = symbolTable.checkVarInitialized(arg[0], arg[1], PE2[0]);
        }

        if (!(type1.equals("int") && type2.equals("int"))) {        //checking if their type is right for operation
            throw new Exception("Operator '<' cannot be applied to " + "'" + type1 + "'," + "'" + type2 + "'");
        }

        return "boolean,boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, String argu) throws Exception {
        String[] arg = argu.split(",");

        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        n.f1.accept(this, argu);
        String ret2 =n.f2.accept(this, argu);
        String[] PE2 = ret2.split(",");

        String type1 = PE1[1];
        String type2 = PE2[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
            type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if(PE2[1].equals("id")) {
            type2 = symbolTable.checkVarInitialized(arg[0], arg[1], PE2[0]);
        }

        if (!(type1.equals("int") && type2.equals("int"))) {        //checking if their type is right for operation
            throw new Exception("Operator '+' cannot be applied to " + "'" + type1 + "'," + "'" + type2 + "'");
        }
        return "int,int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, String argu) throws Exception {
        String[] arg = argu.split(",");
        String _ret=null;
        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        n.f1.accept(this, argu);
        String ret2 =n.f2.accept(this, argu);
        String[] PE2 = ret2.split(",");

        String type1 = PE1[1];
        String type2 = PE2[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
            type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if(PE2[1].equals("id")) {
            type2 = symbolTable.checkVarInitialized(arg[0], arg[1], PE2[0]);
        }

        if (!(type1.equals("int") && type2.equals("int"))) {        //checking if their type is right for operation
            throw new Exception("Operator '-' cannot be applied to " + "'" + type1 + "'," + "'" + type2 + "'");
        }
        return "int,int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, String argu) throws Exception {
        String[] arg = argu.split(",");

        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        n.f1.accept(this, argu);
        String ret2 =n.f2.accept(this, argu);
        String[] PE2 = ret2.split(",");

        String type1 = PE1[1];
        String type2 = PE2[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
            type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if(PE2[1].equals("id")) {
            type2 = symbolTable.checkVarInitialized(arg[0], arg[1], PE2[0]);
        }

        if (!(type1.equals("int") && type2.equals("int"))) {        //checking if their type is right for operation
            throw new Exception("Operator '*' cannot be applied to " + "'" + type1 + "'," + "'" + type2 + "'");
        }
        return "int,int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, String argu) throws Exception {  //na mhn einai out of bounds
        String[] arg = argu.split(",");
        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        n.f1.accept(this, argu);
        String ret2 =n.f2.accept(this, argu);
        String[] PE2 = ret2.split(",");

        String type1 = PE1[1];
        String type2 = PE2[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
            type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if(PE2[1].equals("id")) {
            type2 = symbolTable.checkVarInitialized(arg[0], arg[1], PE2[0]);
        }

        if (!type1.equals("int[]")) {        //checking if their type is right for operation
            throw new Exception("Array type expected but found "+"'"+type1+"'");
        }
        if(!type2.equals("int")){
            throw new Exception("The required type of array index is 'int' but found '"+ type2+"'");
        }

        return "int,int";       //we only have arrays of ints
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, String argu) throws Exception {
        String[] arg = argu.split(",");
        String ret1 =n.f0.accept(this, argu);
        String[] PE1 = ret1.split(",");

        String type1 = PE1[1];

        if(PE1[1].equals("id")) {    //for checking if variable is defined and type is right
            type1 = symbolTable.checkVarInitialized(arg[0], arg[1], PE1[0]);
        }

        if (!type1.equals("int[]")) {        //checking if their type is right for operation
            throw new Exception("Array type expected but found "+"'"+ type1 +"'");
        }

        return "int,int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    @Override
    public String visit(MessageSend n, String argu) throws Exception {
        String[] arg = argu.split(",");
        String className = arg[0], method = arg[1], peType;

        String p = n.f0.accept(this, argu);
        String[] pe = p.split(",");

        peType = pe[1];
        if(pe[1].equals("id")){
            peType = symbolTable.checkVarInitialized(className, method, pe[0]);

        }
        if(pe[1].equals("this")){
            peType = className;

        }
        if(pe[1].equals("class")){
            peType = pe[0];

        }

        String id = n.f2.accept(this, argu);
        String [] s = id.split(",");
        String methodName = s[0];


        String methodType = symbolTable.getMethodType(peType,methodName);

        n.f4.accept(this, argu);

        symbolTable.checkArguments(peType, methodName);

        symbolTable.clearArguments();

        return methodType + "," + methodType;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    @Override
    public String visit(ExpressionList n, String argu) throws Exception {
        String _ret=null;
        String [] arg = argu.split(",");
        String className = arg[0], method = arg[1];

        String s = n.f0.accept(this, argu);
        String[] t = s.split(",");
        String type = t[1];
        if(t[1].equals("id")){
            type = symbolTable.checkVarInitialized(className, method, t[0]);

        }
        if(t[1].equals("this")){
            type = className;

        }
        symbolTable.insertArgument(type);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    @Override
    public String visit(ExpressionTail n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, String argu) throws Exception {
        String _ret=null;
        String [] arg = argu.split(",");
        String className = arg[0], method = arg[1];

        n.f0.accept(this, argu);
        String s = n.f1.accept(this, argu);
        String[] t = s.split(",");
        String type = t[1];
        if(t[1].equals("id")){
            type = symbolTable.checkVarInitialized(className, method, t[0]);

        }
        symbolTable.insertArgument(type);

        return _ret;
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    @Override
    public String visit(PrimaryExpression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public String visit(IntegerLiteral n, String argu) throws Exception {
        return n.f0.toString() + ",int";
    }

    /**
     * f0 -> "true"
     */
    @Override
    public String visit(TrueLiteral n, String argu) throws Exception {
        return n.f0.toString()+ ",boolean";
    }

    /**
     * f0 -> "false"
     */
    @Override
    public String visit(FalseLiteral n, String argu) throws Exception {
        return n.f0.toString()+ ",boolean";
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n, String argu) throws Exception {
        return n.f0.toString()+ ",this";
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public String visit(ArrayAllocationExpression n, String argu) throws Exception {
        n.f0.accept(this, argu);
        String type = n.f1.accept(this, argu);


        n.f3.accept(this, argu);
        return ",int[]";
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n, String argu) throws Exception {
        String[] _ret;
        n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);
        _ret = id.split(",");
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret[0] + ",class" ;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public String visit(Identifier n, String argu) {
        return n.f0.toString() + ",id";
    }

    /**
     * f0 -> "!"
     * f1 -> PrimaryExpression()
     */
    @Override
    public String visit(NotExpression n, String argu) throws Exception {
        return n.f1.accept(this, argu);

    }
    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public String visit(BracketExpression n, String argu) throws Exception {
         return n.f1.accept(this, argu);
    }
}

