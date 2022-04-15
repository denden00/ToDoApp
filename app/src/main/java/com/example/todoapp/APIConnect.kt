package com.example.todoapp

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.CountDownLatch

class APIConnect {
    //POST通信でAPIの呼び出しを行うメソッド　※現在テスト中
    fun ConnectAPI(ApiOpp:String,jsonSendData:String):String{
        //OkHttpを呼び出し
        val client= OkHttpClient()
        // CountDownLatchを呼び出し
        val latch = CountDownLatch(1)
        //送信するURLのベース　この末尾に各操作（Get,Add,Delete,Update）を付け加える
        val baseUrl="http://192.168.1.120/ToDoApi KS/api/"
        //引数のApiOppをつなげて各操作（Get,Add,Delete,Update）を決める
        val url=baseUrl+ApiOpp
        //メディアタイプの指定
        val jsonMedia: MediaType ="application/json; charset=utf-8".toMediaType()
        //送信するJSONデータの指定(リクエスト文)　ここでは一旦空文字
        val jsonData=jsonSendData
        //テスト用テキストビュー
        //val testtext3 = findViewById<TextView>(R.id.textView3)
        //返ってくるレスポンスを初期化（空文字）
        var responseBody ="空"

        //リクエスト送信
        val request: Request = Request.Builder().url(url).post(jsonData.toRequestBody(jsonMedia)).build()

        client.newCall(request).enqueue(object : Callback {
            //レスポンスがうまく帰ってきたとき
            override fun onResponse(call: Call, response: Response) {
                //レスポンスの情報を文字列変数に格納
                responseBody=response.body?.string().orEmpty()
                //テスト用表示
                //testtext3.text = responseBody
                //非同期での通信のためスレッドを待機させる
                latch.countDown()
            }
            //レスポンスがうまく帰ってこなかったとき
            override fun onFailure(call: Call, e: IOException) {
                //testtext3.text=e.toString()
                //非同期での通信のためスレッドを待機させる
                latch.countDown()
            }
        })
        try {
            //スレッドで割り込みが発生しないかぎり、ラッチのカウントダウンがゼロになるまで 現在のスレッドを待機させる。
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return responseBody
    }

    fun ConnectAPITest(ApiOpp:String,jsonSendData:String){
        //OkHttpを呼び出し
        val client= OkHttpClient()
        // CountDownLatchを呼び出し
        val latch = CountDownLatch(1)
        //送信するURLのベース　この末尾に各操作（Get,Add,Delete,Update）を付け加える
        val baseUrl="http://192.168.1.120/ToDoApi KS/api/"
        //引数のApiOppをつなげて各操作（Get,Add,Delete,Update）を決める
        val url=baseUrl+ApiOpp
        //メディアタイプの指定
        val jsonMedia: MediaType ="application/json; charset=utf-8".toMediaType()
        //送信するJSONデータの指定(リクエスト文)　ここでは一旦空文字
        val jsonData=jsonSendData
        //テスト用テキストビュー
        //val testtext3 = findViewById<TextView>(R.id.textView3)
        //返ってくるレスポンスを初期化（空文字）
        var responseBody ="空"

        //リクエスト送信
        val request: Request = Request.Builder().url(url).post(jsonData.toRequestBody(jsonMedia)).build()

        client.newCall(request).enqueue(object : Callback {
            //レスポンスがうまく帰ってきたとき
            override fun onResponse(call: Call, response: Response) {
                //レスポンスの情報を文字列変数に格納
                responseBody=response.body?.string().orEmpty()
                //テスト用表示
                //testtext3.text = responseBody
                //非同期での通信のためスレッドを待機させる
                latch.countDown()
            }
            //レスポンスがうまく帰ってこなかったとき
            override fun onFailure(call: Call, e: IOException) {
                //testtext3.text=e.toString()
                //非同期での通信のためスレッドを待機させる
                latch.countDown()
            }
        })
        try {
            //スレッドで割り込みが発生しないかぎり、ラッチのカウントダウンがゼロになるまで 現在のスレッドを待機させる。
            latch.await()
            val ma=MainActivity()
            ma.getText()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
