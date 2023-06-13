package com.holland.graph_robot.filter

import org.lionsoul.ip2region.xdb.Searcher
import java.util.concurrent.TimeUnit


object SearcherTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val dbPath = "/Users/holland/repo/holland/graph-robot/src/main/resources/ip2region.xdb"

        // 1、从 dbPath 加载整个 xdb 到内存。
        val cBuff: ByteArray
        cBuff = try {
            Searcher.loadContentFromFile(dbPath)
        } catch (e: Exception) {
            System.out.printf("failed to load content from `%s`: %s\n", dbPath, e)
            return
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        val searcher: Searcher
        searcher = try {
            Searcher.newWithBuffer(cBuff)
        } catch (e: Exception) {
            System.out.printf("failed to create content cached searcher: %s\n", e)
            return
        }

        // 3、查询
        val ip = "125.70.177.218"
        try {
            val sTime = System.nanoTime()
            val region = searcher.search(ip)
            val cost = TimeUnit.NANOSECONDS.toMicros((System.nanoTime() - sTime))
            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.ioCount, cost)
        } catch (e: Exception) {
            System.out.printf("failed to search(%s): %s\n", ip, e)
        }

        // 4、关闭资源 - 该 searcher 对象可以安全用于并发，等整个服务关闭的时候再关闭 searcher
        // searcher.close();

        // 备注：并发使用，用整个 xdb 数据缓存创建的查询对象可以安全的用于并发，也就是你可以把这个 searcher 对象做成全局对象去跨线程访问。
    }
}