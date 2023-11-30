package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

	/**
	 * A help class to facilitate organizing the information of each field
	 */
	public static class TDItem implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * The type of the field
		 */
		public final Type fieldType;

		/**
		 * The name of the field
		 */
		public final String fieldName;

		public TDItem(Type t, String n) {
			this.fieldName = n;
			this.fieldType = t;
		}

		public String toString() {
			return fieldName + "(" + fieldType + ")";
		}
	}

	public Vector<TDItem> TDItemVect;

	/**
	 * @return An iterator which iterates over all the field TDItems that are
	 *         included in this TupleDesc
	 */
	public Iterator<TDItem> iterator() {
		return TDItemVect.iterator();
	}

	private static final long serialVersionUID = 1L;

	/**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
    	    if (typeAr.length != fieldAr.length ||typeAr.length < 1) {
    	        throw new IllegalArgumentException("TypeAr must contain at least one entry");}
    	    TDItemVect = new Vector<>();
    	    for (int i = 0; i < typeAr.length; i++) {
    	    		TDItemVect.add(new TDItem(typeAr[i], fieldAr[i]));
    	    
    	    }}

	/**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
    	if (typeAr.length < 1) {
	        throw new IllegalArgumentException("TypeAr must contain at least one entry");}
    	TDItemVect = new Vector<>();
	    for (int i = 0; i < typeAr.length; i++) {
	    		TDItemVect.add(new TDItem(typeAr[i],null));} 		
    	}
    

	/**
	 * @return the number of fields in this TupleDesc
	 */
	public int numFields() {
		return TDItemVect.size();
	}

	/**
	 * Gets the (possibly null) field name of the ith field of this TupleDesc.
	 * 
	 * @param i index of the field name to return. It must be a valid index.
	 * @return the name of the ith field
	 * @throws NoSuchElementException if i is not a valid field reference.
	 */

	public String getFieldName(int i) throws NoSuchElementException {
		return TDItemVect.get(i).fieldName;
	}

	/**
	 * Gets the type of the ith field of this TupleDesc.
	 * 
	 * @param i The index of the field to get the type of. It must be a valid index.
	 * @return the type of the ith field
	 * @throws NoSuchElementException if i is not a valid field reference.
	 */
	public Type getFieldType(int i) throws NoSuchElementException {
		return TDItemVect.get(i).fieldType;
	}

	/**
	 * Find the index of the field with a given name.
	 * 
	 * @param name name of the field.
	 * @return the index of the field that is first to have the given name.
	 * @throws NoSuchElementException if no field with a matching name is found.
	 */
	public int fieldNameToIndex(String name) throws NoSuchElementException {
		for (int i = 0; i < numFields(); i++) {
			if (getFieldName(i) == null) continue;
			if (getFieldName(i).equals(name)) {
				return i;
			}
		}
		throw new NoSuchElementException("No field with the name: " + name);
	}

	/**
	 * @return The size (in bytes) of tuples corresponding to this TupleDesc. Note
	 *         that tuples from a given TupleDesc are of a fixed size.
	 */
	public int getSize() {
		int size = 0;
		for (int i = 0; i < numFields(); i++) {
			size += TDItemVect.get(i).fieldType.getLen();
		}
		return size;
	}

	/**
	 * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
	 * with the first td1.numFields coming from td1 and the remaining from td2.
	 * 
	 * @param td1 The TupleDesc with the first fields of the new TupleDesc
	 * @param td2 The TupleDesc with the last fields of the TupleDesc
	 * @return the new TupleDesc
	 */
	public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        
		ArrayList<Type> typesList = new ArrayList<>();
        ArrayList<String> namesList = new ArrayList<>();

        for (int i = 0; i < td1.numFields(); i++) {
            typesList.add(td1.getFieldType(i));
            namesList.add(td1.getFieldName(i));
        }
        for (int i = 0; i < td2.numFields(); i++) {
            typesList.add(td2.getFieldType(i));
            namesList.add(td2.getFieldName(i));
        }
        Type[] typesArray = typesList.toArray(new Type[0]);
        String[] namesArray = namesList.toArray(new String[0]);
	
        return new TupleDesc(typesArray, namesArray);}

	/**
	 * Compares the specified object with this TupleDesc for equality. Two
	 * TupleDescs are considered equal if they have the same number of items and if
	 * the i-th type in this TupleDesc is equal to the i-th type in o for every i.
	 * 
	 * @param o the Object to be compared for equality with this TupleDesc.
	 * @return true if the object is equal to this TupleDesc.
	 */

	public boolean equals(Object o) {
		if (!(o instanceof TupleDesc ) ) {return false;}
    	TupleDesc object = (TupleDesc) o;
		if (object.numFields() != this.numFields()) {return false;}
		if (object.getSize() != this.getSize()) {return false;}
		for (int i = 0; i < object.numFields(); i++) {
			if (!this.getFieldType(i).equals(object.getFieldType(i))) 
			{return false;}}
		
		return true;}

	public int hashCode() {
		// If you want to use TupleDesc as keys for HashMap, implement this so
		// that equal objects have equals hashCode() results
    	return this.toString().hashCode();	}

	/**
	 * Returns a String describing this descriptor. It should be of the form
	 * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although the
	 * exact format does not matter.
	 * 
	 * @return String describing this descriptor.
	 */
	public String toString() {
		String descriptor = null;
		for (int i=0; 1< this.numFields();i++)
		{ descriptor += this.getFieldType(i)+"("+ this.getFieldName(i)+"),";}
        descriptor.substring(0, descriptor.length() - 1);
	return descriptor;}
}