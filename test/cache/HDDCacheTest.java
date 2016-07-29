/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author v.yanchenko
 */
public class HDDCacheTest {
    
    String uid = "1784568996454";
    Object obj = new String("Test Object");
    
    public HDDCacheTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
     //* Test of both - addObject method and a getObject method.
    @Test
    public void testAddAndGetObject() throws Exception {
        
        HDDCache instance = new HDDCache();
        
        // Serialization
        instance.addObject(uid, obj);
        // Deserialization
        Object result = instance.getObject(uid);
        
        // Tells if a serialized and deserialized objects match.
        assertEquals(obj, result);
    }
    
}
