package com.example.myapplication

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.system.measureNanoTime
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
        } finally
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
            launch(Job()) {
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

    @Test
    fun test12()
    {
        fun simple(): Sequence<Int> = sequence {
            for (i in 1..3) {
                Thread.sleep(100)
                yield(i)
            }
        }

        simple().forEach {
            value -> println(value)
        }
    }

    @Test
    fun test13()
    {
        suspend fun simple(): List<Int> {
            delay(100)
            return listOf(1, 2, 3)
        }

        runBlocking {
            launch {
                for (k in 1..3) {
                    println("I'm blocked $k")
                    delay(100)
                }
            }
            simple().forEach{ value -> println(value) }
        }

        println("end test13")
    }

    @Test
    fun test14()
    {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        runBlocking {
            launch {
                for (k in 1..3) {
                    println("I'm not blocked $k")
                    delay(100)
                }
            }
            simple().collect {
                value -> println(value)
            }
        }
    }

    @Test
    fun test15()
    {
        fun simple(): Flow<Int> = flow {
            println("Flow started...")
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        runBlocking {
            println("Calling simple function...")
            val flow = simple()
            println("Calling collect...")
            flow.collect {
                value -> println(value)
            }
            println("Calling collect again...")
            flow.collect {
                value -> println(value)
            }
        }
    }

    @Test
    fun test16()
    {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100)
                println("Emitting $i")
                emit(i)
            }
        }

        runBlocking {
            withTimeoutOrNull(250) {
                simple().collect {
                    value -> println(value)
                }
            }
            println("done")
        }

        suspend fun rangeToFlow() {
            (1..3).asFlow().collect { value ->
                println(value)
            }
        }

        runBlocking {
            rangeToFlow()
        }

        println("end test16")
    }

    @Test
    fun test17()
    {
        suspend fun performRequest(request: Int): String {
            delay(100)
            return "response $request"
        }

        runBlocking {
            (1..3).asFlow().map {
                request -> performRequest(request)
            }.collect {
                response -> println(response)
            }
        }

        runBlocking {
            (1..3).asFlow().transform {
                request ->
                emit("making request $request")
                emit(performRequest(request))
            }.collect {
                value -> println(value)
            }
        }

        println("end test17")
    }

    @Test
    fun test18()
    {
        fun numbers(): Flow<Int> = flow {
            println("begin numbers...")
            try
            {
                emit(1)
                emit(2)
                println("This line will not execute")
                emit(3)
            }finally
            {
                println("Finally in numbers")
            }
            println("end numbers")
        }

        runBlocking {
            numbers().take(2).collect { value -> println(value) }
            println("end runblocking")
        }

        println("end test18")
    }

    @Test
    fun test19()
    {
        suspend fun sum() = (1..5).asFlow().map { it * it }.reduce{ a, b -> a + b}
        runBlocking {
            println(sum())
        }
        suspend fun run() = (1..5).asFlow().filter { println("Filter $it"); it % 2 == 0 }.map { println("Map $it"); "string $it" }.collect { println("Collect $it") }
        runBlocking {
            run()
        }
    }

    @Test
    fun test20()
    {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                Thread.sleep(100)
                println("[${Thread.currentThread()}] Emitting $i")
                emit(i)
            }
        }.flowOn(Dispatchers.Default)

        runBlocking {
            simple().collect { value ->
                println("[${Thread.currentThread()}] Collect $value")
            }
        }
    }

    @Test
    fun test21()
    {
        fun simple(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        runBlocking {
            val time = measureTimeMillis {
                simple().collect { value ->
                    delay(300)
                    println(value)
                }
            }
            println("Collected in $time ms")
        }

        runBlocking {
            val time = measureTimeMillis {
                simple().buffer().collect { value ->
                    delay(300)
                    println(value)
                }
            }
            println("Collected in $time ms")
        }

        runBlocking {
            val time = measureNanoTime {
                simple().conflate().collect { value ->
                    delay(300)
                    println(value)
                }
            }
            println("Collected in $time ms")
        }

        runBlocking {
            val time = measureTimeMillis {
                simple().collectLatest { value ->
                    println("Collecting $value")
                    delay(300)
                    println("done $value")
                }
            }

            println("Collected in $time ms")
        }

        println("end test21")
    }

    @Test
    fun test22()
    {
        suspend fun run()
        {
            val nums = (1..3).asFlow()
            val strs = flowOf("one", "two", "three")
            nums.zip(strs) { a, b -> "$a -> $b" }.collect { println(it) }
        }
        runBlocking {
            run()
        }

        suspend fun run1()
        {
            val nums = (1..3).asFlow().onEach { delay(300) }
            val strs = flowOf("one", "two", "three").onEach { delay(400) }
            val startTime = System.currentTimeMillis()
            nums.combine(strs) { a, b -> "$a -> $b" }.collect { value ->
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
        }

        runBlocking {
            run1()
        }
        println("end test22")
    }

    @Test
    fun test23()
    {
        fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }
        runBlocking {
            events().onEach { v -> println("[${Thread.currentThread()}] Event: $v") }.launchIn(this)
            println("[${Thread.currentThread()}]end runBlocking.")
        }
        println("[${Thread.currentThread()}]end test23")
    }

    @Test
    fun test24()
    {
        fun foo(): Flow<Int> = flow {
            for (i in 1..5) {
                println("Emitting $i")
                emit(i)
            }
        }

        runBlocking {
            try
            {
                foo().collect { value ->
                    try
                    {
                        if (value == 3) cancel()
                        println(value)
                    }catch (e: java.lang.Exception)
                    {
                        println("e: $e")
                    }finally
                    {
                        println("finally in collect")
                    }
                }
            }catch (e: java.lang.Exception)
            {
                println("exception: $e")
            }finally
            {
                println("finally")
            }
        }

        println("end test24")
    }

    @Test
    fun test25()
    {
        runBlocking {
            (1..5).asFlow().collect { value ->
                if (value == 3) cancel()
                println(value)
            }
        }

        println("end test25")
    }

    @Test
    fun test26()
    {
        runBlocking {
            (1..5).asFlow().cancellable().collect { value ->
                if (value == 3) cancel()
                println(value)
            }
        }

        println("end test26")
    }

    @Test
    fun test27()
    {
        runBlocking {
            (1..5).asFlow().onEach { println(it) }.onEmpty { println("no data") }.collect { v ->
                println("v: $v")
            }

            (1..0).asFlow().onEach { println(it) }.onEmpty { println("no data") }.collect { v ->
                println("v: $v")
            }
        }

        println("end test27")
    }

}