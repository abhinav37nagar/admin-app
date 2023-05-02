package com.example.adminapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminapp.image.ImageClassificationActivity
import com.example.adminapp.`object`.FaceRecognitionActivity

internal interface AlgoListener {
    fun onAlgoSelected(algo: Algo<*>?)
}

class JavaMainActivity : Activity(), AlgoListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val arrayList = ArrayList<Algo<*>>(1)
        arrayList.add(
            Algo<ImageClassificationActivity?>(
                R.drawable.baseline_portrait_black_48,
                "Face recognition",
                FaceRecognitionActivity::class.java
            )
        )
        val algoAdapter = AlgoAdapter(arrayList, this)
        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.main_recycler_view)
        recyclerView.adapter = algoAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)
    }

    override fun onAlgoSelected(algo: Algo<*>?) {
        if (algo != null) {
            val intent = Intent(this, algo.activityClazz)
            intent.putExtra("name", algo.algoText)
            startActivity(intent)
        }
    }
}

internal class AlgoAdapter(
    private val algoList: List<Algo<*>>,
    private val algoListener: AlgoListener
) : RecyclerView.Adapter<AlgoViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlgoViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_icons, parent, false)
        return AlgoViewHolder(view, algoListener)
    }

    override fun onBindViewHolder(holder: AlgoViewHolder, position: Int) {
        holder.bind(algoList[position])
    }

    override fun getItemCount(): Int {
        return algoList.size
    }
}

internal class AlgoViewHolder(itemView: View, algoListener: AlgoListener?) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val iconImageView: ImageView
    private val algoTextView: TextView
    private val algoListener: AlgoListener?
    private var algo: Algo<*>? = null

    init {
        itemView.setOnClickListener(this)
        this.algoListener = algoListener
        iconImageView = itemView.findViewById(R.id.iconImageView)
        algoTextView = itemView.findViewById(R.id.algoTextView)
    }

    fun bind(algo: Algo<*>) {
        this.algo = algo
        iconImageView.setImageResource(algo.iconResourceId)
        algoTextView.text = algo.algoText
    }

    override fun onClick(v: View) {
        algoListener?.onAlgoSelected(algo)
    }
}

class Algo<T : ImageClassificationActivity?>(
    iconResourceId: Int,
    algoText: String,
    activityClazz: Class<FaceRecognitionActivity>
) {
    var iconResourceId = R.drawable.ic_launcher_foreground
    var algoText = ""
    var activityClazz: Class<FaceRecognitionActivity>

    init {
        this.iconResourceId = iconResourceId
        this.algoText = algoText
        this.activityClazz = activityClazz
    }
}