# MiniJava Static Checking (Semantic Analysis)
This is a design and implementation of a compiler's semantic check for the MiniJava language (a small subset of Java) using the tools JavaCC and JTB.

 ##  Compilation: 
  ``` Ruby
  make
```
Remove object files: 
 ``` Ruby
  make clean
```
 ##  Run:
  ``` Ruby
./java Main filename filename filename ...
```
## Symbol Table
Symbol table is a data structure that helps keep track of semantics of variables. It stores information about the scope of variables, functions, names, classes and objects. 
 - class symbolTable: stores the names of classes, arguments and keeps track of variable and method offsets.
 - class symbolTableStruct: stores variables and method names.
 - class method: stores variables and parameters of method.
 - class symbolTableInfo: stores info about a variable.


