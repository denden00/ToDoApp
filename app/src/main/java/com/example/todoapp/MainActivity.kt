package com.example.todoapp

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Looper.getMainLooper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.CountDownLatch
//新お試し
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //テスト用のテキスト表示
        val testtext1 = findViewById<TextView>(R.id.textView)
        //recyclerView紐づけ
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView

        //recyclerViewのドラッグ＆ドロップ操作とスワイプ操作のオーバーライド
        //今回はドラッグ＆ドロップは使用しないので処理内容は全て削除済み
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    testtext1.text = viewHolder.adapterPosition.toString()

                    //ここにAPIでUPDATEの処理を書く
                    //引き渡す引数はInt型のviewHolder.adapterPosition

                    //送信するリクエストを指定
                    val jsonSendData="{\"ToDoId\":"+viewHolder.adapterPosition+"}"
                    ConnectAPI("UpdateToDo",jsonSendData)
                    getText()
                }
            })
        //書いた処理内容をrecyclerViewに取り付ける
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    //Addボタン押下時の処理　※テスト用に作成したため修正の余地あり
    fun AddText(view: View) {

        //ここに”Add”ボタン用の処理を書く
        val testtext1 = findViewById<TextView>(R.id.textView)
        testtext1.text="動作しています"

        getText()

        //Log.d("MainActivity","DebugTest")

        //呼び出すAPIの種類を選択
        val ApiOpp="UpdateToDo"
        //送信するリクエストを指定
        val jsonSendData="{\"ToDoId\":0}"
        //APIを呼び出すメソッドを実行
        ConnectAPI(ApiOpp,jsonSendData)

    }

    //POST通信でAPIの呼び出しを行うメソッド　※現在テスト中
    fun ConnectAPI(ApiOpp:String,jsonSendData:String):String{
        //OkHttpを呼び出し
        val client=OkHttpClient()
        // CountDownLatchを呼び出し
        val latch = CountDownLatch(1)
        //送信するURLのベース　この末尾に各操作（Get,Add,Delete,Update）を付け加える
        val baseUrl="http://192.168.1.120/ToDoApi KS/api/"
        //引数のApiOppをつなげて各操作（Get,Add,Delete,Update）を決める
        val url=baseUrl+ApiOpp
        //メディアタイプの指定
        val jsonMedia:MediaType="application/json; charset=utf-8".toMediaType()
        //送信するJSONデータの指定(リクエスト文)　ここでは一旦空文字
        val jsonData=jsonSendData
        //テスト用テキストビュー
        val testtext3 = findViewById<TextView>(R.id.textView3)
        //返ってくるレスポンスを初期化（空文字）
        var responseBody ="空"

        //リクエスト送信
        val request:Request=Request.Builder().url(url).post(jsonData.toRequestBody(jsonMedia)).build()

        client.newCall(request).enqueue(object :Callback{
            //レスポンスがうまく帰ってきたとき
            override fun onResponse(call: Call, response: Response) {
                //レスポンスの情報を文字列変数に格納
                responseBody=response.body?.string().orEmpty()
                //テスト用表示
                testtext3.text = responseBody
                //非同期での通信のためスレッドを待機させる
                latch.countDown()
            }
            //レスポンスがうまく帰ってこなかったとき
            override fun onFailure(call: Call, e: IOException) {
                testtext3.text=e.toString()
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

    //文字列をビューに表示
    fun getText() {

        //ConnectAPIのメソッドをGetで実行した返り値をresponseとする
        //以下Get実行メソッド
        val response=ConnectAPI("GetToDo","")

        val ToDoText: Array<String?>
        val ToDoStatus: Array<String?>

        try {
            //受け取ったJSON形式の文字列をJSONオブジェクトに格納
            val obj = JSONObject(response)
            //GetToDoListの配列の中身をオブジェクト配列に格納
            val ToDoListArray = obj.getJSONArray("GetToDoList")

            //GetToDoListの行数をカウント
            val rowCount = ToDoListArray.length()
            //ToDoText配列を大きさ[rowCount]で初期化
            ToDoText = arrayOfNulls(rowCount)
            ToDoStatus = arrayOfNulls(rowCount)

            //ToDoTextのデータのみで配列を作成（IdとStatusは表示させないため）
            for (i in 0 until rowCount) {
                val ToDoTextObj = ToDoListArray.getJSONObject(i)
                //ToDoTextを配列に格納
                ToDoText[i] = ToDoTextObj.getString("ToDoText")
                //ToDoStatusを配列に格納
                ToDoStatus[i] = ToDoTextObj.getString("ToDoStatus")
            }

            //recyclerViewの初期化　レイアウト上のrecyclerViewと紐づけ
            val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
            //レイアウトマネージャでビューのレイアウトを指定
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            //レイアウトマネージャでビューのレイアウトを指定
            recyclerView.layoutManager = layoutManager

            //データをアダプタに格納
            val mAdapter: RecyclerView.Adapter<*> = MyAdapter(ToDoText, ToDoStatus)
            //アダプタをビューにセットして一覧表示
            recyclerView.adapter = mAdapter

        } catch (e: Exception) {
            // スタックトレースを文字列にします。
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))
            val stackTrace = stringWriter.toString()
            val testtext2 = findViewById<TextView>(R.id.textView2)
            testtext2.text = stackTrace
        }
    }
}

//RecyclerViewクラス
internal class MyAdapter     //リストに表示するデータを受け取る
    (  //リストに表示する文字列データの定義
    private val myDataset: Array<String?>
    //リストに表示するステータスの定義
    ,private val Statusset : Array<String?>
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    //一行分の View を保持する目的の内部クラス
    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    ) {

        val todotext:TextView
        val closebutton:ImageButton

        //TextViewを引数として受け取りセットするコンストラクタ
        init {
            //public MyViewHolder(TextView v) {
            todotext=view.findViewById(R.id.LayouttextView)
            //todotext.textSize=30f
            closebutton=view.findViewById(R.id.closeButton)
        }
    }

    //１行分の ViewHolder を組み立てて返す ※メインには登場しない　ここが怪しい
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_main, parent, false) //追加用
        //val v = TextView(parent.context) おそらく不要
        return MyViewHolder(view)
    }

    //ViewHolder にデータをバインド　※メインには登場しない
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.todotext.text = myDataset[position]
        //holder.closebutton.setImageResource(R.id.closeButton) todotextにより画像が変わるわけではないのでバインド不要と思われる

        //ステータスが”完了”の場合、グレーアウト＆取り消し線付与
        if(Statusset[position].equals("完了")) {
            //テキストの背景色を変更　Viewの場合はview.set~~
            holder.todotext.setBackgroundColor(Color.LTGRAY)
            //以下3行で取り消し線
            val paint = holder.todotext.paint
            paint.flags = holder.todotext.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
        }

        //×ボタン押下時の処理　あとで完成させる（API接続完了後）
        holder.closebutton.setOnClickListener{

            //ここにAPIでDeletetextの処理を書く

            holder.todotext.text ="ボタンが押下された"
        }
    }

    //表示するリストの件数（行数）を返す　※メインには登場しない
    override fun getItemCount(): Int {
        return myDataset.size
    }
}
