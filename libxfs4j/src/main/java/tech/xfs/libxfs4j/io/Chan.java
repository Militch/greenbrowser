package tech.xfs.libxfs4j.io;

import java.util.concurrent.TimeoutException;

public class Chan<T> {
    private T msg;
    private boolean empty = true;
    public synchronized T take(){
        while (empty) {
            try {
                wait();
            }catch (InterruptedException e){
                // empty
            }
        }
        empty = true;
        notifyAll();
        return msg;
    }
    public synchronized T takeTimeout(long t) throws TimeoutException {
        long start = System.currentTimeMillis();
        while (empty) {
            try {
                wait(t);
                long end = System.currentTimeMillis();
                long diff = end - start;
                if (diff > t){
                    throw new TimeoutException();
                }
            }catch (InterruptedException | TimeoutException e){
                // empty
                if (e instanceof TimeoutException){
                    throw (TimeoutException) e;
                }
            }
        }
        empty = true;
        notifyAll();
        return msg;
    }
    public synchronized void put(T msg){
        while (!empty) {
            try {
                wait();
            }catch (InterruptedException e){
                // empty
            }
        }
        empty = false;
        this.msg = msg;
        notifyAll();
    }
}
