package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId t;
    private OpIterator child;
    private TupleDesc tpd;
    private boolean flag;
    
    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, OpIterator child) {
        this.t = t;
        this.child = child;
        Type[] typeAr = new Type[1];
        typeAr[0] = Type.INT_TYPE;
        String[] fieldAr = new String[1];
        fieldAr[0] = "countInsertedTuples";
        this.tpd = new TupleDesc(typeAr, fieldAr);
        this.flag= false;
        

    
    }

    public TupleDesc getTupleDesc() {
        return this.child.getTupleDesc();
    }

    public void open() throws DbException, TransactionAbortedException {
        super.open();
        this.child.open();
    }

    public void close() {
        super.close();
        this.child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        this.child.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        if(!this.flag){
        int deletedCount = 0;
        if(this.child.hasNext()){
        
        while (this.child.hasNext()) {
        Tuple tuple = this.child.next();
            try {
                Database.getBufferPool().deleteTuple(this.t, tuple);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            deletedCount++;
        }
    }
        this.flag = true;
        Tuple resultTuple = new Tuple(this.tpd);
        resultTuple.setField(0, new IntField(deletedCount));
        return resultTuple;
    }
    else 
        return null;
    }
    

    @Override
    public OpIterator[] getChildren() {
        OpIterator[] returnvalue = new OpIterator[1];
        returnvalue[0] = this.child;
        return returnvalue;
    }

    @Override
    public void setChildren(OpIterator[] children) {
        this.child = children[0];
    }

}
