package com.nada.calculatortask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.nada.calculatortask.R

internal class MathQuestionAdapter(
    private var mathQuestionList: List<String>
) :
    RecyclerView.Adapter<MathQuestionAdapter.TextViewHolder>() {
    internal inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mathQuestion: TextView = view.findViewById(R.id.text_view_math_question)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_math_question, parent, false)
        return TextViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.mathQuestion.text = mathQuestionList[position]

    }

    override fun getItemCount(): Int {
        return mathQuestionList.size
    }
}
