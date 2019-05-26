public class HashList {
    HashListElement first;

    HashList(HashListElement first){
        this.first=first;
    }
    HashList(){
        first=null;
    }
    //Adds a new item to the list.
    public void add(int value){
        if(first==null){
            first=new HashListElement(value, null);
            return;
        }
        HashListElement it=first;
        while(it.getNext()!=null){
            it=it.getNext();
        }
        it.setNext(new HashListElement(value,null));


    }
    //Returns whether an item is inside the list.
    public boolean contains(int value){
        if(first==null){
            return false;
        }
        HashListElement it=first;
        while(it!=null){
            if(it.getValue()==value){
                return true;
            }
            it=it.getNext();
        }
        return false;
    }
}
