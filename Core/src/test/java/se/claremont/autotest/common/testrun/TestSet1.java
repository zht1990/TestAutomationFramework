package se.claremont.autotest.common.testrun;

import org.junit.Assert;
import org.junit.Test;
import se.claremont.autotest.common.testset.TestSet;

public class TestSet1 extends TestSet {

    @Test
    public void test1() throws InterruptedException {
        Assert.assertTrue(true);
    }

    @Test
    public void test2() throws InterruptedException {
        Assert.assertTrue(true);
    }
}