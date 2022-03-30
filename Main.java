import syntaxtree.*;
import visitor.*;

import java.io.*;
import java.lang.*;
import symboltable.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.err.println("Usage: java Main <inputFile> <inputFile> ..");
            System.exit(1);
        }


        FileInputStream fis = null;
        for (String arg : args) {
            try {
                fis = new FileInputStream(arg);
                MiniJavaParser parser = new MiniJavaParser(fis);

                Goal root = parser.Goal();

                System.err.println("\nProgram parsed successfully.");

                MyVisitor eval = new MyVisitor();       // visitor 1
                root.accept(eval, null);

                MyVisitorCheck check = new MyVisitorCheck();    // visitor 2
                root.accept(check, null);

                System.out.println("\nProgram passed semantic check successfully.");
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (fis != null) fis.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

        symbolTable.clear();        //delete all data
    }
}
