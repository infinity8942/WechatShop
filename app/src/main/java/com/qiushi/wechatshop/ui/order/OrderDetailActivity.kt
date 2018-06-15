package com.qiushi.wechatshop.ui.order

import android.support.v7.widget.LinearLayoutManager
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_order_detail.*

/**
 * Created by Rylynn on 2018-06-12.
 *
 * 订单详情
 */
class OrderDetailActivity : BaseActivity() {

    private var orderID: Long = 0

    override fun layoutId(): Int {
        return R.layout.activity_order_detail
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        back.setOnClickListener(this)

        //TODO test
        ImageHelper.loadAvatar(this, logo, Constants.AVATAR, 24)
        shop.text = "咪蒙韩国代购"

        val list = ArrayList<Goods>()
        for (i in 1..2) {
            list.add(Goods("商品" + i))
        }

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = OrderGoodsAdapter(list)

        amount.text = "共计1件商品"
        price.text = "￥240.00"
        message.text = "买家留言：请给我一个会安装的快递小哥哥吧..."
        number.text = "订单编号：20171290078765"
        create_time.text = "创建时间：2017-12-90  07:07:65"
        pay_time.text = "付款时间：2017-12-90  07:07:65"
        payment.text = "支付方式：微信 "
        deliver_time.text = "发货时间：2017-12-90  07:07:65"
        achieve_time.text = "成交时间：2017-12-90  07:07:65"
    }

    override fun getData() {
    }
}