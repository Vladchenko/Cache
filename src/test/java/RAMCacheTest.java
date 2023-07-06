//import ru.cache.vlad.yanchenko.caches.hierarchy.memory.RAMCache;
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
//        RAMCache memoryCache = new RAMCache();
//        memoryCache.addObject("123", new Object());
//        memoryCache.addObject("456", new Object());
//        memoryCache.addObject("789", new Object());
//        memoryCache.getObject("123");
//        memoryCache.getObject("123");
//        memoryCache.getObject("456");
//
//        // The least recently used here is "789".
//        assertEquals(memoryCache.getLeastUsed(CacheKind.LRU), "789");
//    }
//
//    @Test
//    public void testGetLeastUsedMRU() {
//
//        Repository repository = Repository.getInstance();
//        repository.setCacheKind(CacheKind.MRU);
//
//        RAMCache memoryCache = new RAMCache();
//        memoryCache.addObject("123", new Object());
//        memoryCache.addObject("456", new Object());
//        memoryCache.addObject("789", new Object());
//        memoryCache.getObject("123");
//        memoryCache.getObject("123");
//        memoryCache.getObject("456");
//
//        // The most recently used here is "456".
//        assertEquals(memoryCache.getLeastUsed(CacheKind.MRU), "456");
//    }
//
//}
