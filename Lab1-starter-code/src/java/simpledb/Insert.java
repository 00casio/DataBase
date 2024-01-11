package simpledb;

import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableId specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId tid;
    private OpIterator child;
    private int tableId; 
    private TupleDesc td;

    /**
     * Constructor.
     *
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableId
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t, OpIterator child, int tableId)
            throws DbException {
        this.tid = t;
        this.child = child;
        this.tableId = tableId;
        this.td = Utility.getTupleDesc(1, "countInsertedRecords");
        }

    public TupleDesc getTupleDesc() {
        return this.td;   }

    public void open() throws DbException, TransactionAbortedException {
        super.open();
        this.child.open();
}

    public void close() {
        super.close();
        this.child.close();   }

    public void rewind() throws DbException, TransactionAbortedException {
        this.child.rewind();    }


    /**
     * Inserts tuples read from child into the tableId specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     *
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */

    protected Tuple fetchNext() throws TransactionAbortedException, DbException {

        if (child.hasNext()) {
            int counter = 0;
            while (child.hasNext()) {
                try {
                    Database.getBufferPool().insertTuple(tid, tableId, child.next());
                    counter++;
                } catch (IOException e) {
                    throw new DbException("Error inserting tuple");
                }
            }
            Tuple resTup = new Tuple(td);
            resTup.setField(0, new IntField(counter));
    
            return resTup;
        } else {
            return null;
        }
    }
    


    @Override
    public OpIterator[] getChildren() {
        return new OpIterator[]{this.child};}


    @Override
    public void setChildren(OpIterator[] children) {
        this.child = children[0];    }
    }
