import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BTree {
    private int t;
    public BTreeNode root;

    public BTree(String t){
        this.t = Integer.parseInt(t);
        root = new BTreeNode(this.t);
    }

    public void insert(String s){
        root.insert(s);
    }

    public void delete(String s){
        root.delete(s);
    }

    public void deleteKeysFromTree(String path){
        File file = new File(path);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }
        while (sc.hasNextLine()){
            this.delete(sc.nextLine());
        }
    }

    public BTreeNode search(String s){
        return root.search(s);
    }

    public void createFullTree(String path){
        File file = new File(path);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }
        while (sc.hasNextLine()){
            this.insert(sc.nextLine());
        }
    }

    @Override
    public String toString() {
        String s = root.toString(0);
        return s.substring(0, s.length() - 1);
    }
}
