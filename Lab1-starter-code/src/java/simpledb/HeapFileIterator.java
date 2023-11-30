package simpledb;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class HeapFileIterator implements DbFileIterator {
    private TransactionId tid;
    private HeapFile file;
    private Iterator<Tuple> tupleIterator;
    private int currentPageNum;

    public HeapFileIterator(TransactionId tid, HeapFile file) {
        this.tid = tid;
        this.file = file;
    }

    @Override
    public void open() throws DbException, TransactionAbortedException {
        currentPageNum = 0;
        PageId pid = new HeapPageId(file.getId(), currentPageNum);
        HeapPage page = null;
        try {
            page = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
        } catch (TransactionAbortedException | DbException e) {
            e.printStackTrace();
        }
        if (page != null) {
            tupleIterator = page.iterator();
        }
    }

    @Override
    public boolean hasNext() throws DbException, TransactionAbortedException {
        if (tupleIterator == null) {
            return false;
        }
        if (tupleIterator.hasNext()) {
            return true;
        } else {
            while (currentPageNum < file.numPages() - 1) {
                currentPageNum++;
                PageId pid = new HeapPageId(file.getId(), currentPageNum);
                HeapPage page = null;
                try {
                    page = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
                } catch (TransactionAbortedException | DbException e) {
                    e.printStackTrace();
                }
                if (page != null) {
                    tupleIterator = page.iterator();
                    if (tupleIterator.hasNext()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return tupleIterator.next();
    }

    @Override
    public void rewind() throws DbException, TransactionAbortedException {
        close();
        open();
    }

    @Override
    public void close() {
        tupleIterator = null;
    }
}
