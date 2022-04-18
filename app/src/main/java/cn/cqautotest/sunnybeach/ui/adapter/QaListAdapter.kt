package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.QaListItemBinding
import cn.cqautotest.sunnybeach.model.QaInfo
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 问答列表的适配器
 */
class QaListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<QaInfo.QaInfoItem, QaListAdapter.QaListViewHolder>(QaDiffCallback()) {

    class QaDiffCallback : DiffUtil.ItemCallback<QaInfo.QaInfoItem>() {
        override fun areItemsTheSame(
            oldItem: QaInfo.QaInfoItem,
            newItem: QaInfo.QaInfoItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: QaInfo.QaInfoItem,
            newItem: QaInfo.QaInfoItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class QaListViewHolder(val binding: QaListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: QaListAdapter.QaListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val tvQaTitle = binding.tvQaTitle
        val tvDesc = binding.tvDesc
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        tvQaTitle.text = item.title
        tvDesc.text = DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QaListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QaListItemBinding.inflate(inflater, parent, false)
        return QaListViewHolder(binding)
    }
}