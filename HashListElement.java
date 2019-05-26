public class HashListElement {
    private HashListElement next;
    private int value;

    HashListElement(int value, HashListElement next){
        this.next=next;
        this.value=value;

    }
    int getValue(){
        return  value;
    }
    //get the next node.
    HashListElement getNext(){
        return  next;
    }
    //Sets the next node.
    void setNext(HashListElement next){
        this.next=next;
    }


}
