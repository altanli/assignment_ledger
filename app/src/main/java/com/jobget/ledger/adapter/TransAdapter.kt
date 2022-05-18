package com.jobget.ledger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.jobget.ledger.R
import com.jobget.ledger.adapter.TransAdapter.const.DATE_CELL
import com.jobget.ledger.adapter.TransAdapter.const.TRANS_CELL
import com.jobget.ledger.database.UserDatabase
import com.jobget.ledger.model.TransItem
import com.jobget.ledger.util.NumUtils
import java.text.SimpleDateFormat
import java.util.*


class TransAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataList = emptyList<TransItem>()
    var indexByDate = emptyList<CellItem>()
    lateinit var onItemClickListener : OnItemClickListener

    internal fun setDataList(dataList: List<TransItem>) {
        this.dataList = dataList

        var dateList = mutableListOf<Date>()
        var dateStrList = mutableListOf<String>()
        for (transItem in dataList) {
            val dateStr = transItem.dateStr
            if (dateStrList.contains(dateStr)) continue
            dateStrList.add(dateStr)
            dateList.add(SimpleDateFormat(NumUtils.Default_Date_Format).parse(transItem.dateStr))
        }
        dateStrList.clear()

        dateList.sortByDescending { it.time }
        for (date in dateList) {
            dateStrList.add(SimpleDateFormat(NumUtils.Default_Date_Format).format(date))
        }

        var indexByDate = mutableListOf<CellItem>()
        for (dateStr in dateStrList) {
            indexByDate.add(CellItem(DATE_CELL, dateStr, 0))
            for (i in 0 until dataList.size) {
                val transItem = dataList.get(i)
                if (transItem.dateStr.equals(dateStr)) {
                    indexByDate.add(CellItem(TRANS_CELL, "", i))
                }
            }
        }
        this.indexByDate = indexByDate
        notifyDataSetChanged()
    }

    data class CellItem
        (var cellType : Int, var value : String, var index : Int)

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title_txt: TextView
        init {
            title_txt = itemView.findViewById(R.id.title_txt)
        }
    }

    class TransViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var back_view: LinearLayout
        var title_txt: TextView
        var value_txt: TextView
        init {
            back_view = itemView.findViewById(R.id.back_view)
            title_txt = itemView.findViewById(R.id.title_txt)
            value_txt = itemView.findViewById(R.id.price_txt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DATE_CELL) {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.top_item_layout, parent, false)
            return DateViewHolder(view)
        } else { // TRANS_CELL
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.normal_item_layout, parent, false)
            return TransViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var data = indexByDate[position]
        if (data.cellType == DATE_CELL) {
            val dateHolder = holder as DateViewHolder
            dateHolder.title_txt.text = data.value
        } else {
            val transHolder = holder as TransViewHolder
            val transData = dataList.get(data.index)
            transHolder.title_txt.text = transData.title
            transHolder.value_txt.text = NumUtils.convertToCurrency(transData.value)

            if (position == indexByDate.size - 1 || indexByDate.get(position + 1).cellType == DATE_CELL) {
                transHolder.back_view.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bottom_rounded_shape, null)
            } else {
                transHolder.back_view.background = ResourcesCompat.getDrawable(context.resources, R.drawable.side_shape, null)
            }
            transHolder.back_view.setOnLongClickListener {
                if (onItemClickListener != null && transData != null)
                    onItemClickListener.onLongClick(transData)
                true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return indexByDate.get(position).cellType
    }

    override fun getItemCount() = indexByDate.size

    private object const {
        const val DATE_CELL = 0
        const val TRANS_CELL = 1
    }

    interface OnItemClickListener{
        fun onLongClick(transItem: TransItem)
    }
}