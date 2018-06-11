package com.qiushi.wechatshop.ui.manage

//class ManagerAdapter(var context: Context, goods: ArrayList<ShopOrder>, var data: MyShop)
//    : BaseAdapter<ShopOrder>(context, goods, data, object : MultipleType<ShopOrder> {
//
//    override fun getLayoutId(position: Int): Int {
//        return when (position) {
//            0 -> R.layout.manager_item_head
//            1 -> R.layout.manager_item_icon
//            2 -> R.layout.manager_item_orther
//            else -> R.layout.manager_item_orther
//        }
//    }
//
//}) {
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        super.onBindViewHolder(holder, position)
//        when (position) {
//            0 -> setHeadData(holder, (this.data as MyShop?)!!)
//            1 -> setItemData(holder, (this.data as MyShop?)!!.function, position)
//            2 -> setOrderData(holder, mData[position - 2])
//        }
//    }
//
//    override fun bindData(holder: ViewHolder, data: ShopOrder, position: Int) {
//    }
//
//    /**
//     * 商品条目
//     */
//    private fun setOrderData(holder: ViewHolder, data: ShopOrder) {
//        ImageHelper.loadImage(mContext, holder.getView(R.id.iv_shop), data.cover, DensityUtils.dp2px(93f), DensityUtils.dp2px(94f), 10f)
//
//    }
//
//    private var mList: ArrayList<Function>? = null
//
//    /**
//     * 列表表格
//     */
//    private fun setItemData(holder: ViewHolder, function: ArrayList<Function>, position: Int) {
//        this.mList = function
//        val mRecyclerview = holder.getView<RecyclerView>(R.id.mRecyclerView)
//        mRecyclerview.layoutManager = linearLayoutManager
//        mRecyclerview.adapter = mAdapter
//    }
//
//    /**
//     * 头部布局
//     */
//    private fun setHeadData(holder: ViewHolder, myShop: MyShop) {
//        Log.e("tag", "myShop" + myShop.cash_all)
//    }
//
//    override fun getItemCount(): Int {
//        if (mData == null || mData.size == 0) {
//            return 2
//        } else {
//            return mData.size + 2
//        }
//    }
//
//    private val linearLayoutManager by lazy {
//        GridLayoutManager(context, 4)
//    }
//
//    private val mAdapter by lazy {
//        MainGridAdapter(context, this.mList!!)
//    }
//}