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
         * Initially, ram cache (RC) and disk cache (DC) are empty. CPU receives 
         * a command to get data.
         * Algorythm:
         * 1. CPU checks if an RC has this data, i.e. object (obj)
         *      1.1 If so, retrieves it and does (1).
         *      1.2 If not, checks if it is present in a DC.
         *          1.2.1 If present, retrieves it to CPU and does (1).
         *          1.2.2 If not, addresses to some memory, gets an (obj) from it, 
         *      checks if there is a room in RC for it and 
         *          1.2.1 if there is, add obj to RC and does (1).
         *          1.2.2 if there is no, checks if there is a room in DC for it.
         *              1.2.2.1 If there is, passes one obj to DC and does (1).
         *              1.2.2.2 If there is no room, removes some obj by 
         *                  preliminary picked removal algorythm and inserts the 
         *                  one that came from CPU and does (1).
         * 
         * removes some obj1 from RC, puts it to a DC. 
         *          Onwards, puts obj to RC.
         * 2. 
         * 
         * (1) - Some manipulation with a data that goes along with an algorythm.
         */
    }
    
}
