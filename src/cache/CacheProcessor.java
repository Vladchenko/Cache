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
public class CacheProcessor {
    
    private void processRequest(Object obj) {
        /** 
         * Initially, ram cache (RC) and disk cache (DC) are empty. 
         * Algorythm:
         * 1. CPU gets an object (obj) from some memory, checks if an RC is 
         * empty and 
         *      1.1 if it is, puts it to an RC.
         *      1.2 if it is not, checks if there is a room in DC for one obj.
         *              1.2.1 If there is, passes one obj, by preliminary picked 
         *                  removal algorythm to DC.
         *              1.2.2 If there is no room, removes some obj by 
         *                  preliminary picked removal algorythm and inserts the 
         *                  one that came from CPU.
         * 
         * removes some obj1 from RC, puts it to a DC. 
         *          Onwards, puts obj to RC.
         * 2. 
         * 
         * 
         */
    }
    
}
