package simpledb;

import java.util.*;

import javax.xml.crypto.Data;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;
    private JoinPredicate pred;
    private OpIterator child1;
    private OpIterator child2;
    /**
     * Constructor. Accepts two children to join and the predicate to join them
     * on
     * 
     * @param p
     *            The predicate to use to join the children
     * @param child1
     *            Iterator for the left(outer) relation to join
     * @param child2
     *            Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, OpIterator child1, OpIterator child2) {
        this.pred = p;
        this.child1 = child1;
        this.child2 = child2;
    }

    public JoinPredicate getJoinPredicate() {
        return this.pred;
    }

    /**
     * @return
     *       the field name of join field1. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField1Name() {
    int field1_number = this.pred.getField1();
    return this.child1.getTupleDesc().getFieldName(field1_number);

    }

    /**
     * @return
     *       the field name of join field2. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField2Name() {
    int field2_number = this.pred.getField2();
    return this.child1.getTupleDesc().getFieldName(field2_number);
    }

    /**
     * @see simpledb.TupleDesc#merge(TupleDesc, TupleDesc) for possible
     *      implementation logic.
     */
    public TupleDesc getTupleDesc() {
        TupleDesc tupledesc1 = this.child1.getTupleDesc();
        TupleDesc tupledesc2 = this.child2.getTupleDesc();
       return TupleDesc.merge(tupledesc1, tupledesc2);
    }

    public void open() throws DbException, NoSuchElementException,
        TransactionAbortedException {
        super.open();
        this.child1.open();
        this.child2.open();
    }

    public void close() {
        super.close();
        this.child1.close();
        this.child2.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        this.child1.rewind();
        this.child2.rewind();
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     * 
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    private boolean not_end = false;
    private Tuple t1 = null;
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        TupleDesc t = this.getTupleDesc();
        while(not_end || this.child1.hasNext()) {
            if(!not_end){
            t1 = this.child1.next();
            }
            while (this.child2.hasNext()) {
                Tuple t2 = this.child2.next();
                if (this.pred.filter(t1, t2)) {
                    not_end = true;
                    Tuple result = new Tuple(t);
                    int j = 0;
                    for (int i = 0; i < t1.getTupleDesc().numFields(); i++) {
                        result.setField(j, t1.getField(i));
                        j += 1;
                    }
                    for (int i = 0; i < t2.getTupleDesc().numFields(); i++) {
                        result.setField(j, t2.getField(i));
                        j += 1;
                    }
                    return result;
                }
            }
            not_end = false;
            this.child2.rewind();
        }
        return null;
    }

    @Override
    public OpIterator[] getChildren() {
        OpIterator[] toReturn = new OpIterator[2];
        toReturn[0] = this.child1;
        toReturn[1] = this.child2;
        return toReturn;
    }

    @Override
    public void setChildren(OpIterator[] children) {
        this.child1 = children[0];
        this.child2 = children[1];
    }

}
