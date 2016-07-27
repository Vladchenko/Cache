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
public interface ICache {
    public void clearCache();
    public Object getObject();
    public void addObject();
    public void removeObject(Object obj);
    public int getCacheSize();
}
