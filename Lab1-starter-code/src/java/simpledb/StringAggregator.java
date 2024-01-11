package simpledb;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;
    private HashMap<Field, String> grpAggResMap;  
    private HashMap<Field, Integer> grpCounterMap;  
    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.afield = afield;
        this.what = what;
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.grpAggResMap = new HashMap<>();
        this.grpCounterMap = new HashMap<>();    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field field = tup.getField(this.gbfield);

        if (this.grpCounterMap.size() == 0 && this.gbfield == Aggregator.NO_GROUPING) {
            // In case of the first tuple and no grouping, set the key of the map to null
            this.grpCounterMap.put(null, 1);
        } else if (this.gbfield == Aggregator.NO_GROUPING) {
            int count = this.grpCounterMap.getOrDefault(field, 0);
            this.grpCounterMap.put(null, count + 1);
        } else {
            int count = this.grpCounterMap.getOrDefault(field, 0);
            this.grpCounterMap.put(field, count + 1);
        }
    
        }    

    /**
     * Create a OpIterator over group aggregate results.
     *
     * @return a OpIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public OpIterator iterator() {
            TupleDesc td;
            Tuple tuple;
            Field field;
            ArrayList<Tuple> tuples = new ArrayList<>();

            if (this.gbfield == Aggregator.NO_GROUPING) {
            // Aggregator.NO_GROUPING, in that case, the result is a single tuple of the form (aggregateValue)
            Type[] typeArray = new Type[]{Type.INT_TYPE};
            String[] fieldArray = new String[]{"aggregateValue"};
            td = new TupleDesc(typeArray, fieldArray);

            field = new IntField(this.grpCounterMap.getOrDefault(null, 0));
            tuple = new Tuple(td);
            tuple.setField(0, field);
            tuples.add(tuple);
            return new TupleIterator(td, tuples);
        } else {
            // Here in TupleDesc, we have also the groupValue
            Type[] typeArray = new Type[]{this.gbfieldtype, Type.INT_TYPE};
            String[] fieldArray = new String[]{"groupValue", "aggregateValue"};
            td = new TupleDesc(typeArray, fieldArray);

            for (Field group_key : this.grpCounterMap.keySet()) {
                field = new IntField(this.grpCounterMap.get(group_key));
                tuple = new Tuple(td);
                tuple.setField(0, group_key);
                tuple.setField(1, field);
                tuples.add(tuple);
            }
            return new TupleIterator(td, tuples);
        }
    }
}