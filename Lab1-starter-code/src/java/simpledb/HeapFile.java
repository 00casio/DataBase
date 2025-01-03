package simpledb;

import java.io.*;
import java.util.*;
/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    private File f;
    private TupleDesc td;
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        this.f = f;
        this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
      return this.f.getAbsoluteFile().hashCode();}
        

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        int pageSize = BufferPool.getPageSize();
        int offset = pid.getPageNumber() * pageSize;
        Page result = null;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.f, "r")) {
            randomAccessFile.seek(offset);
            byte[] buffer = new byte[pageSize];  // Corrected buffer size
            int bytesRead = randomAccessFile.read(buffer);
            randomAccessFile.close();

            if (bytesRead != -1) {
                result = new HeapPage((HeapPageId) pid, buffer);
                return result;
            }  }
            catch(FileNotFoundException f){
                System.out.println("File not found");}
           
            catch(IOException i){
                System.out.println("Wrong offset");
        }        
        return result;
    }
    

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        int offset = BufferPool.getPageSize() * page.getId().getPageNumber();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.f, "rw")) {
            randomAccessFile.seek(offset);
            randomAccessFile.write(page.getPageData());
        }
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        long pageSize = (long) BufferPool.getPageSize();
        return  (int) (this.f.length() / pageSize);}

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {

        HeapPage HPage = null;
        ArrayList<Page> ResI = new ArrayList<>();

        for(int i=0; i<this.numPages(); i++){
            PageId pid = new HeapPageId(getId(), i);
            HeapPage heapPage = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
            if(heapPage.getNumEmptySlots() > 0) {
                HPage = heapPage;
                HPage.insertTuple(t);
                ResI.add(HPage);
                    return ResI;} 
                       }

        HeapPageId newPid = new HeapPageId(getId(), this.numPages());
        HeapPage newPage = new HeapPage(newPid, HeapPage.createEmptyPageData());
        newPage.insertTuple(t);
    
        try (RandomAccessFile file = new RandomAccessFile(this.f, "rw")) {
            int pos = BufferPool.getPageSize() * this.numPages();
            file.seek(pos);
            file.write(newPage.getPageData());
        }
        ResI.add(newPage);
        return ResI;

    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        PageId pid = t.getRecordId().getPageId();
        ArrayList<Page> ResD = new ArrayList<>();
        HeapPage heapPage = (HeapPage) Database.getBufferPool().getPage(tid,pid, Permissions.READ_WRITE);
        heapPage.deleteTuple(t);
        ResD.add(heapPage);
        return ResD;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        return new HeapFileIterator(tid, this);
}
}

