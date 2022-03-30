package symboltable;

public class symbolTableInfo {
    private  String symbol;
    private  String type;
    private  int scope;

    public symbolTableInfo(){};
    public symbolTableInfo(String name, String type,  int scope){
        this.symbol = name;
        this.type = type;
        this.scope = scope;
    }

    // Overriding the hashcode() function
    @Override
    public int hashCode() {     //from https://stackoverflow.com/questions/2265503/why-do-i-need-to-override-the-equals-and-hashcode-methods-in-java

        // uses roll no to verify the uniqueness
        // of the object of  class
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((symbol == null) ? 0 : symbol.hashCode());
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
        symbolTableInfo other = (symbolTableInfo)o;
        if (!this.symbol.equals(other.symbol) && !this.type.equals(other.type) && this.scope != other.scope) {
            return false;
        }
        return true;
    }


    public String getSymbol(){          //access modifiers functions
        return this.symbol;
    }           //access modifiers
    public void setSymbol(String s){
        this.symbol = s;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String t){
        this.type = t;
    }
    public int getScope(){
        return this.scope;

    }
    public void setScope(int scope) {
        this.scope = scope;
    }
}
