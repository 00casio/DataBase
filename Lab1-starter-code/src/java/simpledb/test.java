package simpledb;

import java.io.File;
public class test{
    public static void main(String[] argv) {
        // Construct table schema
        Type types[] = new Type[]{ Type.INT_TYPE, Type.INT_TYPE };
        String names[] = new String[]{ "ID", "Salary" };
        TupleDesc descriptor = new TupleDesc(types, names);

        // Create the table, associate it with the new data file, and inform the catalog about the schema of this table
        HeapFile table1 = new HeapFile(new File("salary_data.dat"), descriptor);
        Database.getCatalog().addTable(table1, "employee");

        // Construct the query: select the maximum salary from the table
        TransactionId tid = new TransactionId();
        SeqScan f = new SeqScan(tid, table1.getId());
        try {
        f.open();
        int maxSalary = Integer.MIN_VALUE;
            while (f.hasNext()) {
                Tuple tup = f.next();
                int salary = ((IntField) tup.getField(1)).getValue();
                if (salary > maxSalary) {
                    maxSalary = salary;
                }
            }
            System.out.println(maxSalary);

            f.close();
            Database.getBufferPool().transactionComplete(tid);
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
    }}