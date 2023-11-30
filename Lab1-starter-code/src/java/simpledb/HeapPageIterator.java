package simpledb;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

    
public class HeapPageIterator implements Iterator<Tuple> {

    private Iterator<Tuple> iterator;

    public HeapPageIterator(HeapPage heapPage) {
        Vector<Tuple> VectorTuple = new Vector<>();
        for(int i = 0; i< heapPage.numSlots; i++){
            if (heapPage.isSlotUsed(i)){
            VectorTuple.add(heapPage.tuples[i]);}
            }
        this.iterator =   VectorTuple.iterator();  
        }

      

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();   }

    @Override
    public Tuple next() {
        if (!hasNext()) {throw new NoSuchElementException(); }
        return this.iterator.next();  }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");    }
    }
