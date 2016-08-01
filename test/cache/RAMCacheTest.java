/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.util.Iterator;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author v.yanchenko
 */
public class RAMCacheTest {

    public RAMCacheTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testFindLeastUsed() {
        System.out.println("findLeastUsed");
        RAMCache ramCache = new RAMCache();
        ramCache.addObject("123", new Object());
        ramCache.addObject("456", new Object());
        ramCache.addObject("789", new Object());
        ramCache.getObject("123");
        ramCache.getObject("123");
        ramCache.getObject("456");
        // The least used here is "789".
        assertEquals(ramCache.findLeastUsed(), "789");
    }

}
