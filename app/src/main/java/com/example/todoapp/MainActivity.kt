package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

        //初期表示
        getText()

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
                    //引き渡す引数はInt型のviewHolder.adapterPosition
                    //送信するリクエストを指定
                    val jsonSendData="{\"ToDoId\":"+viewHolder.adapterPosition+"}"
                    val API=APIConnect()
                    API.ConnectAPI("UpdateToDo",jsonSendData)
                    //Update実行後Get実行
                    getText()
                }
            })
        //書いた処理内容をrecyclerViewに取り付ける
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    //Addボタン押下時の処理
    fun AddText(view: View) {
        //紐づけ
        val AddText=findViewById<TextView>(R.id.addText)
        if(AddText.text.length==0){
            AlertAddText("文字を入力してください")
        }
        else if(AddText.text.length>30){
            AlertAddText("30文字以内で入力してください")
        }

        else {
            //送信するリクエストを指定
            val jsonSendData = "{\"ToDoText\":\"" + AddText.text + "\"}"
            //APIを呼び出すメソッドを実行
            val API = APIConnect()
            API.ConnectAPI("AddtextToDo", jsonSendData)
            getText()
            //editTextボックス内クリア
            AddText.setText("")
        }
    }

    //文字数チェック
    fun AlertAddText(AlertText: String){
            AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage(AlertText)
                .setPositiveButton("OK", { dialog, which ->
                    // TODO:Yesが押された時の挙動
                })
                .show()
    }

    //文字列をビューに表示
    fun getText() {
        val API=APIConnect()
        //ConnectAPIのメソッドをGetで実行した返り値をresponseとする
        //以下Get実行メソッド
        val response=API.ConnectAPI("GetToDo","")
        //ToDoText個別リスト
        val ToDoText= mutableListOf<String>()
        //ToDoStatus個別リスト
        val ToDoStatus= mutableListOf<String>()

        try {
            //受け取ったJSON形式の文字列をJSONオブジェクトに格納
            val obj = JSONObject(response)
            //GetToDoListの配列の中身をオブジェクト配列に格納
            val ToDoListArray = obj.getJSONArray("GetToDoList")

            //GetToDoListの行数をカウント
            val rowCount = ToDoListArray.length()

            //ToDoTextとToDoStatusのデータでそれぞれのリストを作成
            for (i in 0 until rowCount) {
                val ToDoTextObj = ToDoListArray.getJSONObject(i)
                //ToDoTextをリストに追加
                ToDoText.add(ToDoTextObj.getString("ToDoText"))
                //ToDoStatusをリストに追加
                ToDoStatus.add(ToDoTextObj.getString("ToDoStatus"))
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
            println(stackTrace)
        }
    }

}