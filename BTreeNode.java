public class BTreeNode {
    private int t;
    private String[] values;
    private int occ;
    private final int NOT_HERE = -1;
    public BTreeNode[] sons;
    public BTreeNode father;

    public BTreeNode(int t){
        this.t = t;
        values = new String[2*t-1];
        sons = new BTreeNode[2*t];
        occ = 0;
    }

    public void insert(String s){
        if (occ == values.length) {     //if the node is full, we can't insert
            this.split();
            if (this.father != null){
                this.father.fatherInsert(s);
                return;
            }
        }
        if (occ == 0){
            values[0] = s;
            occ++;
            return;
        }
        if (sons[0] == null) {
            add(s);
            return;
        }
        sons[searchSon(s)].insert(s);
    }

    public void split(){
        final int MIDDLE = occ / 2;
        BTreeNode l = new BTreeNode(t);
        BTreeNode r = new BTreeNode(t);
        int index = 0;
        for (; index < values.length; index++){
            if (index < MIDDLE){
                l.values[index] = this.values[index];
                l.sons[index] = this.sons[index];
                if (this.sons[index] != null) {
                    this.sons[index].father = l;
                }
                l.occ++;
            }
            else if (index > MIDDLE){
                r.values[index - MIDDLE - 1] = this.values[index];
                r.sons[index - MIDDLE - 1] = this.sons[index];
                if (this.sons[index] != null) {
                    this.sons[index].father = r;
                }
                r.occ++;
            }
        }
        l.sons[MIDDLE] = this.sons[MIDDLE];
        if (this.sons[index] != null) {
            this.sons[MIDDLE].father = l;
        }
        r.sons[MIDDLE] = this.sons[index];
        if (this.sons[index] != null) {
            this.sons[index].father = r;
        }
        if (this.father != null){
            int i = this.father.add(this.values[MIDDLE]);
            l.father = this.father;
            r.father = this.father;
            father.insertSons(l, r, i);
        }
        else {
            String mid = this.values[MIDDLE];
            for (int i = 0; i < occ; i++){
                this.values[i] = null;
            }
            this.values[0] = mid;
            l.father = this;
            r.father = this;
            this.sons[0] = l;
            this.sons[1] = r;
            for (int i = 2; i < sons.length; i++) {
                sons[i] = null;
            }
            occ = 1;
        }
    }

    private void insertSons(BTreeNode l, BTreeNode r, int i){
        for (int k = occ - 1; k >= i; k--){
            this.sons[k + 1] = this.sons[k];
        }
        this.sons[i] = l;
        this.sons[i + 1] = r;
    }

    public String toString(int depth) {
        String s = "";
        for (int i = 0; i < occ; i++){
            if (sons[0] == null){
                s += values[i] + "_" + depth + ",";
            }
            else{
                s += sons[i].toString(depth + 1);
                s += values[i] + "_" + depth + ",";
            }
        }
        if (sons[0] != null) {
            s += sons[occ].toString(depth + 1);
        }
        return s;
    }

    private void fatherInsert(String s){
        int index = 0;
        while (index < values.length && values[index] != null) {   //finding the right son to enter the string
            if (s.compareTo(values[index]) <= 0) {
                sons[index].insert(s);
                return;
            }
            index++;
        }
        sons[index].insert(s);
    }

    public void delete(String s){
        int index = this.searchNode(s);
        if(index != NOT_HERE){
            if (this.sons[0] == null){
                this.remove(s);
                return;
            }
            if (this.sons[index].occ != t - 1){
                this.values[index] = this.sons[index].predecessor();
                return;
            }
            else if (this.sons[index + 1].occ != t - 1){
                this.values[index] = this.sons[index + 1].successor();
                return;
            }
            index = merge(index);
            this.sons[index].delete(s);
            return;
        }
        index = searchSon(s);
        if (this.sons[index].occ != t - 1){
            this.sons[index].delete(s);
            return;
        }
        if (index != 0 && sons[index - 1].occ > t - 1){
            shiftLeft(index);
            this.sons[index].delete(s);
            return;
        }
        if (index != occ && sons[index + 1].occ > t - 1){
            shiftRight(index);
            this.sons[index].delete(s);
            return;
        }
        index = merge(index);
        if (this.occ == 2 * t - 1){
			this.delete(s);//this.sons[searchSon(s)].delete(s);
			return;
		}
        this.sons[index].delete(s);
        return;
    }


    //region searches
    private int searchNode(String s){
        for (int k = 0; k < occ && values[k].compareTo(s) <= 0; k++){
            if (values[k].compareTo(s) == 0){
                return k;
            }
        }
        return NOT_HERE;
    }

    public BTreeNode search(String s){
        int a = this.searchNode(s);
        if (a != NOT_HERE){
            return this;
        }
        for (int i = 0; i < this.sons.length; i++){
            BTreeNode n = this.sons[i].search(s);
            if (n != null){
                return n;
            }
        }
        return null;
    }

    private int searchSon(String s) {
        int i = 0;
        for (; i < occ; i++) {
            if (values[i].compareTo(s) > 0){
                break;
            }
        }
        return i;
    }
    //endregion

    //region shifts
    private void shiftRight(int i) {
        this.sons[i].values[this.sons[i].occ] = this.values[i];                     //added the left son the value from this node
        this.values[i] = this.sons[i + 1].values[0];                                //replaced the taken value with the value from the right son
        this.sons[i + 1].remove(this.sons[i + 1].values[0]);                        //deleting the taken value from right son
        this.sons[i].sons[occ] = this.sons[i + 1].sons[0];                          //adding left son the son of the right son
        if(this.sons[i].sons[occ] != null) {
			this.sons[i].sons[occ].father = this.sons[i];                               //updating the moved son's father
		}
        this.sons[i + 1].removeSon(0);                                         //removing right son's son
        this.sons[i].occ++;                                                         //updating occ
    }

    private void shiftLeft(int i) {
        this.sons[i].add(this.values[i - 1]);                                       //added the right son the value from this node
        this.values[i - 1] = this.sons[i - 1].values[occ - 1];                      //replaced the taken value with the value from the left son
        this.sons[i - 1].values[occ - 1] = null;                                    //deleting the taken value from left son
        this.sons[i].addSon(0, this.sons[i - 1].sons[this.sons[i - 1].occ]);   //adding right son the son of the left son
		if (this.sons[i - 1].sons[this.sons[i - 1].occ] != null) {
		this.sons[i - 1].sons[this.sons[i - 1].occ].father = this.sons[i];          //updating the moved son's father
		}
        this.sons[i - 1].sons[this.sons[i - 1].occ] = null;                         //removing left son's son
        this.sons[i - 1].occ--;                                                     //updating occ
    }
    //endregion

    //region adders
    private void addSon(int index, BTreeNode son) {
        for (int i = occ; i >= index; i--) {
            sons[i + 1] = sons[i];
        }
        sons[index] = son;
    }

    private int add(String s){
        for (int i = 0; i <= occ - 1; i++) {   //finding the right spot to enter the string
            if (s.compareTo(values[i]) <= 0) {
                for (int k = occ - 1; k >= i; k--){
                    values[k + 1] = values[k];
                }
                values[i] = s;
                occ++;
                return i;
            }
        }
        values[occ] = s;
        occ++;
        return  occ - 1;
    }
    //endregion

    //region removers
    private void removeSon(int index) {
        for (int i = index; i <= occ; i++) {
            sons[i] = sons[i + 1];
        }
        sons[occ + 1] = null;
    }

    private int remove(String s) {
        boolean found = false;
        int ret = 0;
        for (int i = 0; i < occ - 1; i++) {
            if (values[i].equals(s)){
                found = true;
                ret = i;
            }
            if (found){
                values[i] = values[i + 1];
            }
        }
        values[occ - 1] = null;
        occ--;
        return ret;
    }
    //endregion

    //region mergers
    private int merge(int i) {
        if (i > 0) {
            mergeLeft(i);
            return i - 1;
        }
        mergeRight();
        return i;
    }

    private void mergeRight() {
        BTreeNode merged = new BTreeNode(t);
        for (int i = 0; i < 2 * t - 1; i++) {
			if (i < t - 1) {
				merged.values[i] = sons[0].values[i];       //giving merged the values of the left son
				merged.sons[i] = sons[0].sons[i];           //giving merged the sons of the left son
			} else if (i == t - 1) {
				merged.values[i] = this.values[0];          //giving merged the value of the separating value
				this.remove(this.values[0]);                //removing the separating value from this node
				merged.sons[i] = sons[0].sons[t - 1];       //giving merged the last son of the left son
			} else {
				merged.values[i] = sons[1].values[i - t];   //giving merged the values of the right son
				merged.sons[i] = sons[1].sons[i - t];       //giving merged the sons of the right son
			}
			if (merged.sons[i] != null) {
            merged.sons[i].father = merged;                 //updating the father
			}
		}
        merged.occ = 2 * t - 1;                             //updating occ
        merged.sons[merged.occ] = sons[1].sons[t - 1];      //giving merged the last son of the right son
        this.removeSon(1);                             //removing the right son
		if (this.values[0] == null){
			for (int i = 0; i < merged.occ; i++) {
				this.values[i] = merged.values[i];
				occ++;
				this.sons[i] = merged.sons[i];
				this.sons[i].father = this;
			}
			this.sons[occ] = merged.sons[occ];
			this.sons[occ].father = this;
			return;
		}
        this.sons[0] = merged;                              //updating sons to include merged
        merged.father = this;                               //updating merged father
    }

    private void mergeLeft(int index) {
        BTreeNode merged = new BTreeNode(t);
        for (int i = 0; i < 2 * t - 1; i++) {
            if (i < t - 1){
                merged.values[i] = sons[index - 1].values[i];   //giving merged the values of the left son
                merged.sons[i] = sons[index - 1].sons[i];       //giving merged the sons of the left son
            }
            else if (i == t - 1){
                merged.values[i] = this.values[index - 1];      //giving merged the value of the separating value
                this.remove(this.values[index - 1]);            //removing the separating value from this node
                merged.sons[i] = sons[index - 1].sons[t - 1];   //giving merged the last son of the left son
            }
            else{
                merged.values[i] = sons[index].values[i - t];   //giving merged the values of the right son
                merged.sons[i] = sons[index].sons[i - t];       //giving merged the sons of the right son
            }
			if (merged.sons[i] != null) {
				merged.sons[i].father = merged;                 //updating the father
			}
        }
        merged.occ = 2 * t - 1;                                 //updating occ
        merged.sons[merged.occ] = sons[index].sons[t - 1];      //giving merged the last son of the right son
        this.removeSon(index);                                  //removing the right son
		if (this.values[0] == null){
			for (int i = 0; i < merged.occ; i++) {
				this.values[i] = merged.values[i];
				occ++;
				this.sons[i] = merged.sons[i];
				this.sons[i].father = this;
			}
			this.sons[occ] = merged.sons[occ];
			this.sons[occ].father = this;
			return;
		}
        this.sons[index - 1] = merged;                          //updating sons to include merged
        merged.father = this;                                   //updating merged father
    }
    //endregion

    //region prev\next
    private String predecessor() {
        if (sons[0] == null){
            String s = this.values[occ - 1];            //takes the biggest value of the rightest node
            this.values[occ - 1] = null;                //deletes that value
            occ--;
            return s;
        }
        if (sons[occ].occ == t - 1){
            if (sons[occ - 1].occ > t - 1){
                shiftLeft(occ);
                return sons[occ].predecessor();
            }
            mergeLeft(occ);
            return sons[occ].predecessor();
        }
        return sons[occ].predecessor();
    }

    private String successor() {
        if (sons[0] == null){
            String s = this.values[0];
            this.remove(this.values[0]);
            return s;
        }
        if (sons[0].occ == t - 1){
            if (sons[1].occ > t - 1){
                shiftRight(0);
                return sons[0].successor();
            }
            mergeRight();
            return sons[0].successor();
        }
        return sons[0].successor();
    }
    //endregion
}
