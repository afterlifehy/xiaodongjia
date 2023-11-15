/**
 * @TODO
 * Author 苟俊 jndx_taibai@163.com
 * Date  @2013-10-25
 * Copyright 2013 www.daoxila.com. All rights reserved.
 */
package com.wbb.base.observer;

/**
 * @author goujun
 * 
 */
public interface ObserverChanger {

	 void registerObserver(OnObserver o);

	 void removeObserver(OnObserver o);

	 void notifyObserver(Object obj);

}
