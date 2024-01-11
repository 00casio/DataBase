package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;
    private HashMap<Field, Integer> grpAggResMap;  
    private HashMap<Field, Integer> grpCounterMap;  
    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.afield = afield;
        this.what = what;
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.grpAggResMap = new HashMap<>();
        this.grpCounterMap = new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        IntField intfield = (IntField) tup.getField(this.afield);  
        int fieldval = intfield.getValue(); // Aggregate value
        Field field = tup.getField(this.gbfield);   


        if(this.grpAggResMap.size() == 0 && this.gbfield == Aggregator.NO_GROUPING){
            // in case  of first tuple and 
            // in case of no grouping the result is a single tuple of the form (fieldval)
            this.grpAggResMap.put(null, fieldval); 
            this.grpCounterMap.put(null, 1);
        }

        else if(this.gbfield == Aggregator.NO_GROUPING){
                // in case of no grouping
                int value = this.grpAggResMap.getOrDefault(field, 0);
                int count = this.grpCounterMap.getOrDefault(field, 0);
                
                switch(this.what){
                    case COUNT:
                        this.grpCounterMap.put(null, count + 1);
                        break;
                    case SUM:
                        this.grpAggResMap.put(null, value + fieldval);
                        break;
                    case AVG:
                        this.grpAggResMap.put(null, value + fieldval);
                        this.grpCounterMap.put(null, count + 1);
                        break;
                    case MIN:
                        this.grpAggResMap.put(null, Math.min(value, fieldval));
                        break;
                    case MAX:
                        this.grpAggResMap.put(null, Math.max(value, fieldval));
                        break;
                }
        }

        else {
            if (!this.grpAggResMap.containsKey(field)) {
                // if this field isn't among the map's keys
                this.grpAggResMap.put(field, fieldval);
                this.grpCounterMap.put(field, 1);
            }
            else {
                int value = this.grpAggResMap.get(field);
                int count = this.grpCounterMap.get(field);
                switch (this.what) {
                    case COUNT:
                        this.grpCounterMap.put(field, count + 1);
                        break;
                    case SUM:
                        this.grpAggResMap.put(field, value + fieldval);
                        break;
                    case AVG:
                        this.grpAggResMap.put(field, value + fieldval);
                        this.grpCounterMap.put(field, count + 1);
                        break;
                    case MIN:
                        this.grpAggResMap.put(field, Math.min(value, fieldval));
                        break;
                    case MAX:
                        this.grpAggResMap.put(field, Math.max(value, fieldval));
                        break;
                }
            }
        }


    }
    
    /**
     * Create a OpIterator over group aggregate results.
     * 
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
        public OpIterator iterator() {
        TupleDesc td;
        Tuple tuple;
        Field field;
        ArrayList<Tuple> tuples = new ArrayList<>();

        if(this.gbfield == Aggregator.NO_GROUPING){
            // Aggregator.NO_GROUPING, in that case the result is a single tuple of the form (aggregateValue)
            Type[] typeAr = new Type[]{Type.INT_TYPE};
            String[] fieldAr = new String[]{"aggregateValue"};
            td = new TupleDesc(typeAr, fieldAr);

            switch(this.what){
                case COUNT:
                    field = new IntField(this.grpCounterMap.get(null));
                    break;
                case AVG:
                    field = new IntField(this.grpCounterMap.get(null) == 0 ? 0 : this.grpAggResMap.get(null) / this.grpCounterMap.get(null));
                    break;
                case SUM:
                    field = new IntField(this.grpAggResMap.get(null));
                    break;
                case MIN:
                    field = new IntField(this.grpAggResMap.get(null));
                    break;
                case MAX:
                    field = new IntField(this.grpAggResMap.get(null));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported aggregation operation");
            }
            tuple = new Tuple(td);
            tuple.setField(0,field);
            tuples.add(tuple);
            return new TupleIterator(td, tuples);
        }
        else{
            Type[] typeAr = new Type[]{this.gbfieldtype, Type.INT_TYPE};
            String[] fieldAr = new String[]{"groupValue", "aggregateValue"};
            td = new TupleDesc(typeAr, fieldAr);
            for (Field group_key : this.grpAggResMap.keySet()){
                switch(this.what){
                    case COUNT:
                        field = new IntField(this.grpCounterMap.get(group_key));
                        break;
                    case AVG:
                        field = new IntField(this.grpCounterMap.get(group_key) == 0 ? 0 : this.grpAggResMap.get(group_key) / this.grpCounterMap.get(group_key));
                        break;
                    case SUM:
                        field = new IntField(this.grpAggResMap.get(group_key));
                        break;
                    case MIN:
                        field = new IntField(this.grpAggResMap.get(group_key));
                    break;
                    case MAX:
                        field = new IntField(this.grpAggResMap.get(group_key));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported aggregation operation");
                }
                tuple = new Tuple(td);
                tuple.setField(0,group_key);
                tuple.setField(1,field);
                tuples.add(tuple);
            }
            return new TupleIterator(td, tuples);
        }
    }

}


