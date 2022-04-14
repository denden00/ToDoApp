package com.example.todoapp

import org.junit.Test
import android.os.Looper.getMainLooper

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun calcTest(){
        val ma=MainActivity()
        assertEquals(4,ma.totalCalc(2,2))
    }
}