package com.example.todoapp

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class MyAdapter     //リストに表示するデータを受け取る
    (  //リストに表示する文字列データの定義
    private val myDataset: Array<String?>
    //リストに表示するステータスの定義
    ,private val Statusset : Array<String?>
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    var m_line = 0
    lateinit var m_listener:View.OnClickListener

    //一行分の View を保持する目的の内部クラス
    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    ) {

        val todotext: TextView
        val closebutton: ImageButton

        //TextViewを引数として受け取りセットするコンストラクタ
        init {
            //public MyViewHolder(TextView v) {
            todotext = view.findViewById(R.id.LayouttextView)
            //todotext.textSize=30f
            closebutton = view.findViewById(R.id.closeButton)
        }
    }

    //１行分の ViewHolder を組み立てて返す ※メインには登場しない　ここが怪しい
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_main, parent, false) //追加用
        //val v = TextView(parent.context) おそらく不要

        return MyViewHolder(view)
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        m_listener = listener
    }

    //ViewHolder にデータをバインド　※メインには登場しない
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.todotext.text = myDataset[position]
        //holder.closebutton.setImageResource(R.id.closeButton) todotextにより画像が変わるわけではないのでバインド不要と思われる


        //ステータスが”完了”の場合、グレーアウト＆取り消し線付与
        if (Statusset[position].equals("完了")) {
            //テキストの背景色を変更　Viewの場合はview.set~~
            holder.todotext.setBackgroundColor(Color.LTGRAY)
            //以下3行で取り消し線
            val paint = holder.todotext.paint
            paint.flags = holder.todotext.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
        }

        //×ボタン押下時の処理　あとで完成させる（API接続完了後）
        holder.closebutton.setOnClickListener(View.OnClickListener() {

            fun onClick(view:View) {
                m_line = position; //行数を登録
                m_listener.onClick(view); //登録した直後にMainのOnClickを呼び出す
            }

            //ここにAPIでDeletetextの処理を書く
            /*
            //送信するリクエストを指定
            val jsonSendData = "{\"ToDoId\":" + position + "}"
            //APIを呼び出すメソッドを実行
            //val ma = APIConnect()
            try {
                APIConnect().ConnectAPI("DeletetextToDo", jsonSendData)
                //MainActivity().getText() //これはできない
                holder.todotext.text = position.toString()

            } catch (e: Exception) {
                // スタックトレースを文字列にします。
                val stringWriter = StringWriter()
                e.printStackTrace(PrintWriter(stringWriter))
                val stackTrace = stringWriter.toString()
                //val testtext2 = findViewById<TextView>(R.id.textView2)
                //testtext2.text = stackTrace
                holder.todotext.text = stackTrace
            }
             */

        })


    }

    //表示するリストの件数（行数）を返す　※メインには登場しない
    override fun getItemCount(): Int {
        return myDataset.size
    }

    fun getLine(): Int {
        return m_line //行数を取得
    }

}