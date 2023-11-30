package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */

public class Tuple implements Serializable {

	private TupleDesc td;	
	private RecordId rid;
	private Field[] fields;
	
    private static final long serialVersionUID = 1L;

    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
    	if  (!(td instanceof TupleDesc)) 
    	{throw new IllegalArgumentException("A tuple must be of type TupleDesc");};
  	    if (td.numFields() < 1) {
 	        throw new IllegalArgumentException("A tuple must contain at least one entry");};
 	    this.td = td;
        this.fields = new Field[td.numFields()];
        
 	    }
 	    
    

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        return td;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
    	return this.rid;
    }

    /**
     * Set the RecordId information for this tuple.
     *
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        this.rid=rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    
    public void setField(int i, Field f) {
        if (i<0 || i>=this.fields.length)
        {throw new IllegalArgumentException("Non Valid index");
        };
        this.fields[i]=f;}      
          
        
	   
    /**
     * @return the value of the ith field, or null if it has not been set.
     *
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
    	if (i>=this.fields.length||i<0) 
    	{return null;};
        return fields[i];
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     *
     * column1\tcolumn2\tcolumn3\t...\tcolumnN
     *
     * where \t is any whitespace (except a newline)
     */
    private String TupleString = "";

    public String toString() {
        for (int i=0;i<fields.length;i++) 
        { TupleString += getField(i) + "\t";};
        TupleString = TupleString.substring(0, TupleString.length() - 1);
        return TupleString;}

    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {       return Arrays.asList(fields).iterator();
    }

    /**
     * reset the TupleDesc of this tuple (only affecting the TupleDesc)
     * */
    public void resetTupleDesc(TupleDesc td)
    {        this.td = td;    }
}