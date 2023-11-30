package simpledb;

import java.io.Serializable;

/**
 * A RecordId is a reference to a specific tuple on a specific page of a
 * specific table.
 */
public class RecordId implements Serializable {

    private static final long serialVersionUID = 1L;
    private PageId PID; 
    private int TupleNo;

    
    /**
     * Creates a new RecordId referring to the specified PageId and tuple
     * number.
     * 
     * @param pid
     *            the pageid of the page on which the tuple resides
     * @param tupleno
     *            the tuple number within the page.
     */
    public RecordId(PageId pid, int tupleno) {
        this.PID = pid;
        this.TupleNo = tupleno;
    

    }

    /**
     * @return the tuple number this RecordId references.
     */
    public int getTupleNumber() {
    
        return this.TupleNo;
    }

    /**
     * @return the page id this RecordId references.
     */
    public PageId getPageId() {
        return this.PID;
    }

    /**
     * Two RecordId objects are considered equal if they represent the same
     * tuple.
     * 
     * @return True if this and o represent the same tuple
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RecordId ) ) {throw new UnsupportedOperationException("unsupported operation");}
        RecordId object = (RecordId) o;
        //if comparing the two pageids here even if they are equal we will have a false result as both are not the same object, so we need to access the attributes of pageid and make our comparison based on them 
        return this.getTupleNumber() == object.getTupleNumber() && this.getPageId().getPageNumber() == object.getPageId().getPageNumber() && 
        this.getPageId().getTableId() == object.getPageId().getTableId();
    }

    /**
     * You should implement the hashCode() so that two equal RecordId instances
     * (with respect to equals()) have the same hashCode().
     * 
     * @return An int that is the same for equal RecordId objects.
     */
    @Override
    public int hashCode() {
        String hash = "" + this.getTupleNumber() + this.getPageId().getPageNumber() + this.getPageId().getTableId();
        return hash.hashCode();
        //throw new UnsupportedOperationException("implement this");

    }

}
