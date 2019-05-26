import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HashTable {
    HashList[] table;
    int length;
    HashTable(String m2){
        length=Integer.parseInt(m2);
        table=new HashList[length];
        for(int i=0;i<length;++i){
            table[i]=new HashList();
        }
    }
    //Represents the hash function(replaces complex expression).
    private int hashFunction(int val){
        return ((val*243)%length +40) %length;
    }
    //A multimod function: i^c%15486907
    private int multiMod(int i, char c){
        int x=c;
        for(int j=0;j<i;++j){
            int t=x;
            for(int m=0;m<255;++m){
                x=(x+t)%15486907;
            }

        }
        return x;
    }
    //Translates string to int using Horner's rule.
    private int Horner(String str){
        int ret=0;
        for(int i=str.length()-1;i>0;--i){
            ret=ret+multiMod(str.length()-1-i,str.charAt(i));
        }
        return ret;
    }
    //Adds a new string to the list.
    void addKey(String s){
        int value=Horner(s);
        table[hashFunction(value)].add(value);

    }
    //Checks whether a string is in the list
    public boolean contains(String s){
        int value=Horner(s);
        return table[hashFunction(value)].contains(value);
    }
    //Updates table with a list of strings(which resides in a file whose path is path)
    public void updateTable(String path){
        File f=new File(path);
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String read=sc.nextLine();
                addKey(read);
            }
        }
        catch(FileNotFoundException e){ System.out.println("The file "+ path+" wasn't found.");}
    }

}
