package simpledb;

import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor accepts a predicate to apply and a child operator to read
     * tuples to filter from.
     * 
     * @param p
     *            The predicate to filter tuples with
     * @param child
     *            The child operator
     */
    private Predicate p;
    private OpIterator child;
    public Filter(Predicate p, OpIterator child) {
        this.p = p;
        this.child = child;
        }

    public Predicate getPredicate() {
        return this.p;
    }

    public TupleDesc getTupleDesc() {
        return this.child.getTupleDesc();
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
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
     * AbstractDbIterator.readNext implementation. Iterates over tuples from the
     * child operator, applying the predicate to them and returning those that
     * pass the predicate (i.e. for which the Predicate.filter() returns true.)
     * 
     * @return The next tuple that passes the filter, or null if there are no
     *         more tuples
     * @see Predicate#filter
     */
    protected Tuple fetchNext() throws NoSuchElementException,
            TransactionAbortedException, DbException {
            while (this.child.hasNext()){
                Tuple child_tuple = this.child.next();
                if (this.p.filter(child_tuple)){
                    return child_tuple;
                }
            }
                return null;
    }

    @Override
    public OpIterator[] getChildren() {
        return new OpIterator[] { this.child };
    }

    @Override
    public void setChildren(OpIterator[] children) {
	if (this.child!=children[0])
	{
	    this.child = children[0];
	}
    }
    
}
