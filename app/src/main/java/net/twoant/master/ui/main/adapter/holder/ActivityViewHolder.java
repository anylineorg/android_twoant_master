package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2016/11/24.
 * 首页活动的 viewHolder
 */

public class ActivityViewHolder extends BaseRecyclerViewHolder {

    //--------------红包 start -------------------------------------------
    private AppCompatTextView mTvPriceRedPacket;
    private AppCompatTextView mTvRedPacketNameRedPacket;
    private AppCompatTextView mTvRedPacketDescriptionRedPacket;
    private AppCompatTextView mTvValidDateRedPacket;
    private AppCompatTextView mTvConditionRedPacket;
    private AppCompatButton mTvDrawRedPacket;
    private AppCompatTextView mTvDetailsRedPacket;
    private AppCompatTextView mTvReadCountFooterRedPacket;
    private AppCompatTextView mTvDateActivityFooterRedPacket;
    
    //--------------红包 end ---------------------------------------------


    //--------------通用头部 start ---------------------------------------------
    private CircleImageView mIvHeadImgActivityHeader;
    private AppCompatTextView mTvNameActivityHeader;
    private AppCompatTextView mTvDistanceActivityHeader;
    private View mLinearLayoutCompat;
    //--------------通用头部 end ---------------------------------------------

    //--------------除红包 start ---------------------------------------------

    private AppCompatImageView mIvDisplayImageActivityCommon;
    private AppCompatTextView mTvClickCountFooterActivity;
    private AppCompatTextView mTvDetailsActivityFooter;
    private AppCompatTextView mTvDateActivityFooter;
    private AppCompatTextView mTvTypeActivityFooter;
    private AppCompatTextView mTvTypePriceActivityFooter;
    private AppCompatTextView mTvAddressActivityFooter;
    private AppCompatTextView mTvPriceActivityFooter;

    //--------------除红包 end -----------------------------------------------


    public ActivityViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    protected void initView(View itemView, int viewType) {
        switch (viewType) {
            case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
            case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
            case IRecyclerViewConstant.KIND_METER_ACTIVITY:
            case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                this.mTvClickCountFooterActivity = (AppCompatTextView) itemView.findViewById(R.id.tv_click_count_footer_activity);
                this.mLinearLayoutCompat = itemView.findViewById(R.id.ll_header_parent);
                this.mIvHeadImgActivityHeader = (CircleImageView) itemView.findViewById(R.id.iv_head_img_activity_header);
                this.mTvNameActivityHeader = (AppCompatTextView) itemView.findViewById(R.id.tv_name_activity_header);
                this.mTvDistanceActivityHeader = (AppCompatTextView) itemView.findViewById(R.id.tv_distance_activity_header);
                this.mIvDisplayImageActivityCommon = (AppCompatImageView) itemView.findViewById(R.id.iv_display_image_activity_common);
                this.mTvDetailsActivityFooter = (AppCompatTextView) itemView.findViewById(R.id.tv_details_activity_footer);
                this.mTvDateActivityFooter = (AppCompatTextView) itemView.findViewById(R.id.tv_date_activity_footer);
                this.mTvTypeActivityFooter = (AppCompatTextView) itemView.findViewById(R.id.tv_type_activity_footer);
                this.mTvTypePriceActivityFooter = (AppCompatTextView) itemView.findViewById(R.id.tv_type_price_activity_footer);
                this.mTvAddressActivityFooter = (AppCompatTextView) itemView.findViewById(R.id.tv_address_activity_footer);
                this.mTvPriceActivityFooter = (AppCompatTextView) itemView.findViewById(R.id.tv_price_activity_footer);

                break;
            case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                this.mLinearLayoutCompat = itemView.findViewById(R.id.ll_header_parent);
                this.mIvHeadImgActivityHeader = (CircleImageView) itemView.findViewById(R.id.iv_head_img_activity_header);
                this.mTvNameActivityHeader = (AppCompatTextView) itemView.findViewById(R.id.tv_name_activity_header);
                this.mTvDistanceActivityHeader = (AppCompatTextView) itemView.findViewById(R.id.tv_distance_activity_header);
                this.mTvPriceRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_price_red_packet);
                this.mTvRedPacketNameRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_red_packet_name_red_packet);
                this.mTvRedPacketDescriptionRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_red_packet_description_red_packet);
                this.mTvValidDateRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_valid_date_red_packet);
                this.mTvConditionRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_condition_red_packet);
                this.mTvDrawRedPacket = (AppCompatButton) itemView.findViewById(R.id.btn_draw_red_packet);
                this.mTvDetailsRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_details_footer_red_packet);
                this.mTvReadCountFooterRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_read_count_footer_red_packet);
                this.mTvDateActivityFooterRedPacket = (AppCompatTextView) itemView.findViewById(R.id.tv_date_footer_red_packet);
                break;
        }
    }


    public AppCompatTextView getTvClickCountFooterActivity() {
        return this.mTvClickCountFooterActivity;
    }

    public View getLinearLayoutCompat() {
        return this.mLinearLayoutCompat;
    }

    public AppCompatTextView getTvPriceRedPacket() {
        return mTvPriceRedPacket;
    }

    public AppCompatTextView getTvRedPacketNameRedPacket() {
        return mTvRedPacketNameRedPacket;
    }

    public AppCompatTextView getmTvRedPacketDescriptionRedPacket() {
        return mTvRedPacketDescriptionRedPacket;
    }

    public AppCompatTextView getTvValidDateRedPacket() {
        return mTvValidDateRedPacket;
    }

    public AppCompatTextView getTvConditionRedPacket() {
        return mTvConditionRedPacket;
    }

    public AppCompatButton getBtnDrawRedPacket() {
        return mTvDrawRedPacket;
    }

    public AppCompatTextView getTvDetailsRedPacket() {
        return mTvDetailsRedPacket;
    }

    public AppCompatTextView getTvReadCountFooterRedPacket() {
        return mTvReadCountFooterRedPacket;
    }

    public AppCompatTextView getTvDateActivityFooterRedPacket() {
        return mTvDateActivityFooterRedPacket;
    }

    public CircleImageView getIvHeadImgActivityHeader() {
        return mIvHeadImgActivityHeader;
    }

    public AppCompatTextView getTvNameActivityHeader() {
        return mTvNameActivityHeader;
    }

    public AppCompatTextView getTvDistanceActivityHeader() {
        return mTvDistanceActivityHeader;
    }

    public AppCompatImageView getIvDisplayImageActivityCommon() {
        return mIvDisplayImageActivityCommon;
    }

    public AppCompatTextView getTvDetailsActivityFooter() {
        return mTvDetailsActivityFooter;
    }

    public AppCompatTextView getTvDateActivityFooter() {
        return mTvDateActivityFooter;
    }

    public AppCompatTextView getTvTypeActivityFooter() {
        return mTvTypeActivityFooter;
    }

    public AppCompatTextView getTvTypePriceActivityFooter() {
        return mTvTypePriceActivityFooter;
    }

    public AppCompatTextView getTvAddressActivityFooter() {
        return mTvAddressActivityFooter;
    }

    public AppCompatTextView getTvPriceActivityFooter() {
        return mTvPriceActivityFooter;
    }
}
