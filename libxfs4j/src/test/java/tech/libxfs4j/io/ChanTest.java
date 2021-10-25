package tech.libxfs4j.io;

import org.junit.Test;
import tech.xfs.libxfs4j.io.Chan;

import java.util.concurrent.TimeoutException;

public class ChanTest {
    @Test
    public void test() throws TimeoutException {
        Chan<Object> objectChan = new Chan<>();
        Object obj = objectChan.takeTimeout(1000);
    }
}
