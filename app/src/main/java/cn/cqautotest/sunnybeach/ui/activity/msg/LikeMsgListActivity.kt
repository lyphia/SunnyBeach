package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.LikeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.LikeMsgAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 点赞消息列表界面
 */
class LikeMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding<LikeMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mLikeMsgAdapter = LikeMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mLikeMsgAdapter

    override fun getLayoutId(): Int = R.layout.like_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
    }

    override fun initData() {
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.RESUMED) { loadListData() } }
    }

    override suspend fun loadListData() {
        mMsgViewModel.getLikeMsgList().collectLatest { mLikeMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mLikeMsgAdapter.snapshotList[position]?.let {
                it.hasRead = "1"
                mLikeMsgAdapter.notifyItemChanged(position)
            }
        }
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }
}