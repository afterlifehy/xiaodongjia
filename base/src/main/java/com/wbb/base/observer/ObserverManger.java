/**
 * @TODO Author 苟俊 jndx_taibai@163.com
 * Date  @2013-10-25
 * Copyright 2013 www.daoxila.com. All rights reserved.
 */
package com.wbb.base.observer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 通用监听者
 *
 * <p>
 * 注意事项：所有registerObserver的地方必须存在removeObserver
 * </p>
 */
public class ObserverManger implements ObserverChanger {

    static HashMap<String, ObserverManger> mangerMap = new HashMap<>();


    /***********************************************************************/

    private ObserverManger() {
        super();
    }

    public static ObserverManger getInstance(String token) {
        if (mangerMap.get(token) == null) {
            mangerMap.put(token, new ObserverManger());
        }
        return mangerMap.get(token);
    }

    private final ArrayList<OnObserver> observers = new ArrayList();

    @Override
    public void registerObserver(OnObserver o) {
        if (!observers.contains(o))
            observers.add(o);
    }

    @Override
    public void removeObserver(OnObserver o) {
        // TODO Auto-generated method stub
        int i = observers.indexOf(o);
        if (i >= 0) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObserver(Object obj) {
        for (OnObserver observer : observers) {
            observer.update(obj);
        }
    }

}
