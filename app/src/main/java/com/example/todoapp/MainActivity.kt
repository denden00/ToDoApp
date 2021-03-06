package com.example.todoapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {
    //private var instance: MainActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Mainからメソッドを呼び出すときのお試し
        //instance=this

        //初期表示
        getText()

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
                    val API=APIConnect()
                    API.ConnectAPI("UpdateToDo",jsonSendData)
                    getText()
                }
            })
        //書いた処理内容をrecyclerViewに取り付ける
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    //Addボタン押下時の処理　※テスト用に作成したため修正の余地あり
    fun AddText(view: View) {
        val AddText=findViewById<TextView>(R.id.addText)
        //送信するリクエストを指定
        val jsonSendData="{\"ToDoText\":\""+AddText.text+"\"}"
        //APIを呼び出すメソッドを実行
        val API=APIConnect()
        API.ConnectAPI("AddtextToDo",jsonSendData)
        getText()

    }

    //Getボタン押下時の処理　
    fun GetTest(view: View) {
        getText()

        val API=APIConnect()
        //API.ConnectAPITest("AddtextToDo","{\"ToDoText\":\"Test\"}")
    }

    //文字列をビューに表示
    fun getText() {
        val API=APIConnect()
        //ConnectAPIのメソッドをGetで実行した返り値をresponseとする
        //以下Get実行メソッド
        val response=API.ConnectAPI("GetToDo","")

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

    /*
    fun getInstance(): MainActivity? { //インスタンスを取得
        return instance
    }

     */
}