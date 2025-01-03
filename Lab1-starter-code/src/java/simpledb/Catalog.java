package simpledb;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Catalog keeps track of all available tables in the database and their
 * associated schemas.
 * For now, this is a stub catalog that must be populated with tables by a
 * user program before it can be used -- eventually, this should be converted
 * to a catalog that reads a catalog table from disk.
 * 
 * @Threadsafe
 */

public class Catalog {

    /**
     * Constructor.
     * Creates a new, empty catalog.
     */
	public class Table {
	    private DbFile file;
	    private String name;
	    private String pkeyField;

	    public Table(DbFile file, String name, String pkeyField) {
	        this.file = file;
	        this.name = name;
	        this.pkeyField = pkeyField;
	    }
	    /*getters*/
        public DbFile getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public String getPkeyField() {
            return pkeyField;
        }
	}
	    private Vector<Table> tables;
    public Catalog() {
    	this.tables = new Vector<Table>();
    }

    /**
     * Add a new table to the catalog.
     * This table's contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     * @param name the name of the table -- may be an empty string.  May not be null.  If a name
     * conflict exists, use the last table to be added as the table for a given name.
     * @param pkeyField the name of the primary key field
     */
    public void addTable(DbFile file, String name, String pkeyField) {
        for (Table table : tables) {
            if (table.getName().equals(name) || table.getFile().getId() == file.getId()) {
                tables.remove(table);
                break;
            }
        }
        Table newTable = new Table(file, name, pkeyField);
        tables.add(newTable);
    }
    

    public void addTable(DbFile file, String name) {
        addTable(file, name, "");
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     */
    public void addTable(DbFile file) {
        addTable(file, (UUID.randomUUID()).toString());
    }

    /**
     * Return the id of the table with a specified name,
     * @throws NoSuchElementException if the table doesn't exist
     */
    public int getTableId(String name) throws NoSuchElementException {
        if(name == null){
            throw new NoSuchElementException("Null name");}
        for (Table table : tables) {
            if (table.getName().equals(name)) {
                return table.getFile().getId();
            }
        }
        throw new NoSuchElementException("Table not found with name: " + name);
    }

    /**
     * Returns the tuple descriptor (schema) of the specified table
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     * @throws NoSuchElementException if the table doesn't exist
     */
    public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException {
    	 for (Table table : tables) {
             if (table.getFile().getId() == tableid) {
                 return table.getFile().getTupleDesc();
             }
         }
         throw new NoSuchElementException("Table not found with ID: " + tableid);
     }
    

    /**
     * Returns the DbFile that can be used to read the contents of the
     * specified table.
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     */
    public DbFile getDatabaseFile(int tableid) throws NoSuchElementException {
    	 for (Table table : tables) {
             if (table.getFile().getId() == tableid) {
                 return table.getFile();
             }
         }

         // Table with the given ID not found
         throw new NoSuchElementException("Table not found with ID: " + tableid);
    }

    public String getPrimaryKey(int tableid) {
    	 for (Table table : tables) {
             if (table.getFile().getId() == tableid) {
                 // Found a table with the matching ID, return its associated primary key
                 return table.getPkeyField();
             }
         }

         throw new NoSuchElementException("Table not found with ID: " + tableid);
    }

    public Iterator<Integer> tableIdIterator() {
        Vector<Integer> tableIds = new Vector<>();
    
        for (Table table : tables) {
            tableIds.add(table.getFile().getId());
        }

    return tableIds.iterator();
    }

    public String getTableName(int id) {
    	 for (Table table : tables) {
             if (table.getFile().getId() == id) {
                 return table.getName();
             }
         }
         throw new NoSuchElementException("Table not found with ID: " + id);
    }
    
    /** Delete all tables from the catalog */
    public void clear() {
    	tables.clear();
    }
    
    /**
     * Reads the schema from a file and creates the appropriate tables in the database.
     * @param catalogFile
     */
    public void loadSchema(String catalogFile) {
        String line = "";
        String baseFolder=new File(new File(catalogFile).getAbsolutePath()).getParent();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(catalogFile)));
            
            while ((line = br.readLine()) != null) {
                //assume line is of the format name (field type, field type, ...)
                String name = line.substring(0, line.indexOf("(")).trim();
                //System.out.println("TABLE NAME: " + name);
                String fields = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
                String[] els = fields.split(",");
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<Type> types = new ArrayList<Type>();
                String primaryKey = "";
                for (String e : els) {
                    String[] els2 = e.trim().split(" ");
                    names.add(els2[0].trim());
                    if (els2[1].trim().toLowerCase().equals("int"))
                        types.add(Type.INT_TYPE);
                    else if (els2[1].trim().toLowerCase().equals("string"))
                        types.add(Type.STRING_TYPE);
                    else {
                        System.out.println("Unknown type " + els2[1]);
                        System.exit(0);
                    }
                    if (els2.length == 3) {
                        if (els2[2].trim().equals("pk"))
                            primaryKey = els2[0].trim();
                        else {
                            System.out.println("Unknown annotation " + els2[2]);
                            System.exit(0);
                        }
                    }
                }
                Type[] typeAr = types.toArray(new Type[0]);
                String[] namesAr = names.toArray(new String[0]);
                TupleDesc t = new TupleDesc(typeAr, namesAr);
                HeapFile tabHf = new HeapFile(new File(baseFolder+"/"+name + ".dat"), t);
                addTable(tabHf,name,primaryKey);
                System.out.println("Added table : " + name + " with schema " + t);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println ("Invalid catalog entry : " + line);
            System.exit(0);
        }
    }
}

