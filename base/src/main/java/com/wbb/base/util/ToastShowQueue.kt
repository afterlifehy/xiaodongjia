package com.wbb.base.util

import java.util.*

/**
 * Created by zj on 2021/3/16.
 */
class ToastShowQueue<T>(
    val maxSize: Int,
    private val realQueue: LinkedList<T> = LinkedList()
) : Deque<T> by realQueue {

    val map = linkedMapOf<T, Long>()

    /**
     * 移除队尾元素
     */
    override fun removeLast(): T {
        val last = realQueue.removeLast()
        if (last != null) {
            map.remove(last)
        }
        return last
    }

    /**
     * 如果存在则放到队头,更新访问时间,否则插入,如果队满,则删除队尾元素
     */
    override fun addFirst(e: T) {
        if (contains(e)) {
            map[e] = System.currentTimeMillis()
            return
        }
        if (map.size + 1 > maxSize) {
            removeLast()
        }
        realQueue.addFirst(e)
        map[e] = System.currentTimeMillis()
    }


    /**
     * 如果包含则放到队头
     */
    override fun contains(element: T): Boolean {
        val contains = realQueue.contains(element)
        if (contains) {
            realQueue.remove(element)
            realQueue.addFirst(element)
        }
        return contains
    }

    /**
     * 获取e对应的时间值,如果e不存在则会插入.如果存在则放到队头,更新访问时间
     */
    fun getAndUpdate(e: T): Long {
        if (contains(e)) {
            val old = map[e] ?: 0L
            map[e] = System.currentTimeMillis()
            return old
        } else {
            addFirst(e)
        }
        return 0L
    }

    /**
     * 查看效果
     */
    fun print() {
        forEach {
            println("key=${it.toString()} value = ${map[it]}")
        }
    }

}

