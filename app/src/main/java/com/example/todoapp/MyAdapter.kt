package com.example.todoapp

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.PrintWriter
import java.io.StringWriter

internal class MyAdapter     //リストに表示するデータを受け取る
    (  //リストに表示する文字列データの定義
    private val myDataset: MutableList<String>
    //リストに表示するステータスの定義
    ,private val Statusset : MutableList<String>
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    //一行分の View を保持する目的の内部クラス
    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    ) {
        val todotext: TextView
        val closebutton: ImageButton
        //TextViewを引数として受け取りセットするコンストラクタ
        init {
            //テキストボックス
            todotext = view.findViewById(R.id.LayouttextView)
            //×ボタン
            closebutton = view.findViewById(R.id.closeButton)
        }
    }

    //１行分の ViewHolder を組み立てて返す
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_main, parent, false)
        return MyViewHolder(view)
    }

    //ViewHolder にデータをバインド
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.todotext.text = myDataset[position]

        //ステータスが”完了”の場合、グレーアウト＆取り消し線付与
        if (Statusset[position].equals("完了")) {
            //テキストの背景色を変更　Viewの場合はview.set~~
            holder.todotext.setBackgroundColor(Color.LTGRAY)
            //以下3行で取り消し線
            val paint = holder.todotext.paint
            paint.flags = holder.todotext.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
        }

        //×ボタン押下時の処理
        holder.closebutton.setOnClickListener(View.OnClickListener() {
            //送信するリクエストを指定
            val jsonSendData = "{\"ToDoId\":" + position + "}"
            //APIを呼び出すメソッドを実行
            try {
                APIConnect().ConnectAPI("DeletetextToDo", jsonSendData)
                //×ボタンを押した行を削除（RecyclerViewの表示上）
                myDataset.removeAt(position)
                //RecyclerViewの再表示通知送信（SQLを操作するわけではない）
                notifyDataSetChanged()

            } catch (e: Exception) {
                // スタックトレースを文字列にします。
                val stringWriter = StringWriter()
                e.printStackTrace(PrintWriter(stringWriter))
                val stackTrace = stringWriter.toString()
                println(stackTrace)
            }
        })
    }

    //表示するリストの件数（行数）を返す　※メインには登場しない
    override fun getItemCount(): Int {
        return myDataset.size
    }

}