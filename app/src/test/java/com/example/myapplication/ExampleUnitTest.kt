package com.example.myapplication

import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest
{
    @Test
    fun addition_isCorrect()
    {
        assertEquals(4, 2 + 2)
        println("Thread: [${Thread.currentThread()}}]")
    }

    @Test
    fun test1() = runBlocking {
        launch {
            delay(1000L)
            println("[${Thread.currentThread()}]world")
        }
        println("[${Thread.currentThread()}]hello")
    }

    @Test
    fun test2() = runBlocking {
        suspend fun doWorld()
        {
            delay(1000L)
            println("[${Thread.currentThread()}]World!")
        }
        launch {
            doWorld()
        }
        println("[${Thread.currentThread()}]hello")
    }

    @Test
    fun test3() = runBlocking {
        suspend fun doWorld() = coroutineScope {
            launch {
                delay(1000)
                println("world")
            }
            print("[${Thread.currentThread()}]hello")
        }
        println("[${Thread.currentThread()}]begin test3...")
        doWorld()
        println("[${Thread.currentThread()}]end test3.")
    }

    @Test
    fun test4() = runBlocking {
        suspend fun doWorld() = coroutineScope {
            launch {
                println("no delay with msg: hello")
            }
            launch {
                delay(2000)
                println("[${Thread.currentThread()}]world 2")
            }
            println("middle of two launches...")
            launch {
                delay(1000)
                println("[${Thread.currentThread()}]world 1")
            }
            println("[${Thread.currentThread()}]hello")
        }

        doWorld()
        println("[${Thread.currentThread()}]Done")
    }

    @Test
    fun test5() = runBlocking {
        val job = launch {
            delay(1000)
            println("world")
        }
        println("hello")
        job.join()
        println("done test5.")
    }

    @Test
    fun test6() = runBlocking {
        println("start test6...")
        suspend fun do1(): Int
        {
            println("thread do1: ${Thread.currentThread()}")
            delay(1000)
            return 1
        }

        suspend fun do2(): Int
        {
            println("thread do2: ${Thread.currentThread()}")
            delay(1000)
            return 2
        }

        val time = measureTimeMillis {
            val one = do1()
            val two = do2()
            println("the answer is ${one + two} in thread: ${Thread.currentThread()}")
        }
        println("completed in $time ms")
    }

    @Test
    fun test7() = runBlocking {
        println("start test7...")
        suspend fun do1(): Int
        {
            println("thread do1: ${Thread.currentThread()}")
            delay(1000)
            return 1
        }

        suspend fun do2(): Int
        {
            println("thread do2: ${Thread.currentThread()}")
            delay(1000)
            return 2
        }

        val time = measureTimeMillis {
            val one = async { do1() }
            val two = async { do2() }
            println("the answer is ${one.await() + two.await()} in thread: ${Thread.currentThread()}")
        }
        println("completed in $time ms")
    }

    @Test
    fun test8() = runBlocking {
        suspend fun failedConcurrentSum(): Int = coroutineScope {
            val one = async<Int> {
                try
                {
                    delay(Long.MAX_VALUE)
                    1
                } finally
                {
                    println("First child was cancelled.")
                }
            }
            val two = async<Int> {
                println("Second child throws an exception")
                throw java.lang.ArithmeticException()
            }
            one.await() + two.await()
        }
        println("begin testing...")
        try
        {
            failedConcurrentSum()
        } catch (e: ArithmeticException)
        {
            println("exception: ${e}")
        }finally
        {
            println("end of testing.")
        }
        println("end.")
    }

    @Test
    fun test9() = runBlocking {
        fun launchCoroutine(dispatcher: CoroutineDispatcher)
        {
            launch(dispatcher) {
                println("thread: ${Thread.currentThread()}")
            }
        }
        for (s in arrayOf(Dispatchers.Default, Dispatchers.IO, Dispatchers.Unconfined))
        {
            launchCoroutine(s)
        }
        println("done")
    }

    @Test
    fun test10() = runBlocking {
        println("start testing...")
        val request = launch {
            launch (Job()) {
                println("job1: I run in my own Job and execute independently!")
                delay(1000)
                println("job1: I am not affecte4d by cancellation of the request")
            }
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not excute this line if my parent request is cancelled.")
            }
        }
        delay(500)
        request.cancel()
        println("main: Who has survived request cancellation?")
        delay(1000)
        println("done testing.")
    }

    @Test
    fun test11() = runBlocking {
        println("start testing...")
        val request = launch {
            repeat(3) { i ->
                println("launching new coroutine...${i}")
                launch {
                    delay((i + 1) * 200L)
                    println("Coroutine $i is done.")
                }
            }
            println("request: I'm done and I don't' explicitly join my children that are still active.")
        }
        request.join()
        println("end of testing.")
    }
}