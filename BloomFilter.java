import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class BloomFilter {
    int[] hashFunctions;
    boolean[] filterArr;
    private void initializeArray(){
        for(int i=0;i<filterArr.length;++i){
            filterArr[i]=false;
        }
    }
    //Parses a string and puts its components in the array.
    private void parseAdd(String parse, int index){

        String[] parseArr=parse.split("_");
        hashFunctions[index]=Integer.parseInt(parseArr[0]);
        hashFunctions[index+1]=Integer.parseInt(parseArr[1]);



    }
    //Counts the number of hash functions
    private int numberOfHashFunc(String path){
        File f=new File(path);
        int length=0;
        try {
            Scanner sc = new Scanner(f);

            while(sc.hasNextLine()){
                sc.nextLine();
                length=length+1;
            }
        }
        catch(FileNotFoundException e){

        }
        return length;
    }
    //Constructor.
    public BloomFilter(String m1, String path){
        File f=new File(path);
        try {
            Scanner sc = new Scanner(f);
            int length=numberOfHashFunc(path);
            hashFunctions=new int[length*2];
            int index=0;
            while(sc.hasNextLine()){
                String read=sc.nextLine();
                parseAdd(read,index);
                index=index+2;
            }
        }
        catch(FileNotFoundException e){ System.out.println("The file "+ path+" wasn't found.");}
        filterArr=new boolean[Integer.parseInt(m1)];
        initializeArray();
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
    //The hash function(replacement for a complex expression).
    private int hashFunction(int a, int b, int k){
        return (((a*k)+b)%15486907)%filterArr.length;
    }
    //Adds a string to the bloomfilter.
    private void addToFilter(String password){
        int value=Horner(password);
        for(int i=0;i<hashFunctions.length;i=i+2){
            int index=hashFunction(hashFunctions[i],hashFunctions[i+1],value);
            filterArr[index]=true;
        }

    }
    //updates the table with new passwords. path is the path to the file containing the passwords.
    public void updateTable(String path){
        File f=new File(path);
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String read=sc.nextLine();
                addToFilter(read);
            }
        }
        catch(FileNotFoundException e){ System.out.println("The file "+ path+" wasn't found.");}
    }
    //Checks whether a password is inside the BloomFilter.
    public boolean isIn(String s){
        int value=Horner(s);
        for(int i=0;i<hashFunctions.length;i=i+2){
            if(!filterArr[hashFunction(hashFunctions[i],hashFunctions[i+1],value)]){
                return false;
            }
        }
        return true;
    }
    //Returns the percentage of false positives.
    public String getFalsePositivePercentage(HashTable hashTable, String path){
        File f=new File(path);
        double goodPasswords=0;
        double falsePositive=0;
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String read=sc.nextLine();
                if(!hashTable.contains(read)){
                    goodPasswords=goodPasswords+1;
                    if(isIn(read)){  falsePositive=falsePositive+1; }
                }
            }
        }
        catch(FileNotFoundException e){ System.out.println("The file "+ path+" wasn't found.");}
        return Double.toString(falsePositive/goodPasswords);
    }
    //Returns the amount of the rejected passwords by the BloomFilter.
    public String getRejectedPasswordsAmount(String path){
        File f=new File(path);
        int badPasswords=0;
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String read=sc.nextLine();
                if(isIn(read)){
                    badPasswords=badPasswords+1;
                }
            }

        }
        catch(FileNotFoundException e){ System.out.println("The file "+ path+" wasn't found.");}
        return ""+badPasswords;
    }
}
