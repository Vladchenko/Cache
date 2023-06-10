//import ru.cache.vlad.yanchenko.caches.RAMCache;
//import ru.cache.vlad.yanchenko.Repository;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
///**
// * Test for {@link RAMCache}
// * TODO
// *
// * @author v.yanchenko
// */
//public class RAMCacheTest {
//
//    public RAMCacheTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    @Test
//    public void testGetLeastUsedLRU() {
//
//        Repository repository = Repository.getInstance();
//        repository.setCacheKind(CacheKind.LRU);
//
//        RAMCache ramCache = new RAMCache();
//        ramCache.addObject("123", new Object());
//        ramCache.addObject("456", new Object());
//        ramCache.addObject("789", new Object());
//        ramCache.getObject("123");
//        ramCache.getObject("123");
//        ramCache.getObject("456");
//
//        // The least recently used here is "789".
//        assertEquals(ramCache.getLeastUsed(CacheKind.LRU), "789");
//    }
//
//    @Test
//    public void testGetLeastUsedMRU() {
//
//        Repository repository = Repository.getInstance();
//        repository.setCacheKind(CacheKind.MRU);
//
//        RAMCache ramCache = new RAMCache();
//        ramCache.addObject("123", new Object());
//        ramCache.addObject("456", new Object());
//        ramCache.addObject("789", new Object());
//        ramCache.getObject("123");
//        ramCache.getObject("123");
//        ramCache.getObject("456");
//
//        // The most recently used here is "456".
//        assertEquals(ramCache.getLeastUsed(CacheKind.MRU), "456");
//    }
//
//}
