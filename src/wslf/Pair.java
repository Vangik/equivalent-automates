package wslf;

/**
 * @author Wsl_F
 */
public class Pair <E extends Comparable<E>> implements Comparable<Pair<E>>{
    public E first;
    public E second;

    public Pair(E first, E second) {
        this.first= first;
        this.second= second;
    }
    
    public Pair() {
        first= null;
        second= null;
    }
    
    @Override
    public String toString() {
        return "("+first+","+second+")";
    }
    
    public boolean equals(Pair<E> p2) {
        return first.equals(p2.first)&&second.equals(p2.second);
    }
    
    @Override
    public int compareTo(Pair<E> p2) {
        if (first.equals(p2.first)) {
            return second.compareTo(p2.second);
        } else {
            return first.compareTo(p2.first);
        }
    }
}
