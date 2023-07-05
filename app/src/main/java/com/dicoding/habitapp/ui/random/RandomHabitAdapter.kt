package com.dicoding.habitapp.ui.random

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit

class RandomHabitAdapter(
    private val onClick: (Habit) -> Unit
) : RecyclerView.Adapter<RandomHabitAdapter.PagerViewHolder>() {

    private val habitMap = LinkedHashMap<PageType, Habit>()

    fun submitData(key: PageType, habit: Habit) {
        habitMap[key] = habit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pager_item, parent, false)
        )

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val key = getIndexKey(position) ?: return
        val pageData = habitMap[key] ?: return
        holder.bind(key, pageData)
    }

    override fun getItemCount() = habitMap.size

    private fun getIndexKey(position: Int) = habitMap.keys.toTypedArray().getOrNull(position)

    enum class PageType {
        HIGH, MEDIUM, LOW
    }

    inner class PagerViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        //TODO 14 : Create view and bind data to item view
        private val tvActName: TextView = itemView.findViewById(R.id.tv_act_name)
        private val tvStarted: TextView = itemView.findViewById(R.id.tv_time_started)
        private val tvMinute: TextView = itemView.findViewById(R.id.tv_minute)
        private val ivCircle: ImageView = itemView.findViewById(R.id.iv_circle_red)
        private val btnOpenCountdown: Button = itemView.findViewById(R.id.btn_countdown)


        fun bind(pageType: PageType, pageData: Habit) {

            tvActName.text = pageData.title
            tvStarted.text = pageData.startTime
            tvMinute.text = pageData.minutesFocus.toString()

            btnOpenCountdown.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val key = getIndexKey(position)
                    val habit = habitMap[key]
                    habit?.let { onClick(it) }
                }
            }
            when (pageType) {
                PageType.HIGH -> ivCircle.setImageResource(R.drawable.ic_priority_high)
                PageType.MEDIUM -> ivCircle.setImageResource(R.drawable.ic_priority_medium)
                else -> ivCircle.setImageResource(R.drawable.ic_priority_low)
            }
            btnOpenCountdown.setOnClickListener { onClick(pageData) }
        }
    }
}
