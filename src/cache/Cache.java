/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

/**
 *
 * @author v.yanchenko
 */
public class Cache {
    
    private ProcessArguments prc;
    private CacheProcessor cacheProcessor;
    private Repository repository = Repository.getInstance();
    
    public Cache(String[] args) {
        prc = new ProcessArguments(repository);
        prc.processArgs(args);
        cacheProcessor = new CacheProcessor(repository);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Cache cache = new Cache(args);
        cache.cacheProcessor.performCachingProcess();
    }
    
}
