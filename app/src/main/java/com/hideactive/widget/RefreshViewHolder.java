package com.hideactive.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hideactive.R;

/**
 * Created by Senierr on 2015/12/10.
 */
public class RefreshViewHolder {

    public static final int REFRESH_CAN_NOT = 0;
    public static final int REFRESH_CAN = 1;
    public static final int REFRESH_ING = 2;
    public static final int REFRESH_FINISHED = 3;

    private View headerView;
    public ProgressBar headerView_progressBar;
    public TextView headerView_textView;
    public ImageView headerView_imageView;

    private View footerView;
    public ProgressBar footerView_progressBar;
    public TextView footerView_textView;
    public ImageView footerView_imageView;

    public RefreshViewHolder(Context context) {
        headerView = LayoutInflater.from(context)
                .inflate(R.layout.layout_refresh_header, null);
        headerView_textView = (TextView) headerView.findViewById(R.id.text_view);
        headerView_imageView = (ImageView) headerView.findViewById(R.id.image_view);
        headerView_progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        refreshHeaderView(REFRESH_CAN_NOT);

        footerView = LayoutInflater.from(context)
                .inflate(R.layout.layout_refresh_footer, null);
        footerView_textView = (TextView) footerView.findViewById(R.id.text_view);
        footerView_imageView = (ImageView) footerView.findViewById(R.id.image_view);
        footerView_progressBar = (ProgressBar) footerView.findViewById(R.id.pb_view);
        refreshFooterView(REFRESH_CAN_NOT);
    }

    /**
     * 获取刷新头部视图
     * @return
     */
    public View getHeaderView() {
        return headerView;
    }

    /**
     * 获取刷新底部视图
     * @return
     */
    public View getFooterView() {
        return footerView;
    }

    /**
     * 刷新头部视图
     * @param mode
     */
    public void refreshHeaderView(int mode) {
        switch (mode) {
            case REFRESH_CAN_NOT :
                headerView_textView.setText("下拉刷新");
                headerView_imageView.setImageResource(R.mipmap.indicator_arrow);
                headerView_imageView.setVisibility(View.VISIBLE);
                headerView_imageView.setRotation(0);
                headerView_progressBar.setVisibility(View.GONE);
                break;
            case REFRESH_CAN :
                headerView_textView.setText("松开刷新");
                headerView_imageView.setVisibility(View.VISIBLE);
                headerView_imageView.setRotation(180);
                headerView_progressBar.setVisibility(View.GONE);
                break;
            case REFRESH_ING :
                headerView_textView.setText("正在刷新...");
                headerView_imageView.setVisibility(View.GONE);
                headerView_progressBar.setVisibility(View.VISIBLE);
                break;
            case REFRESH_FINISHED :
                headerView_textView.setText("刷新完成");
                headerView_imageView.setImageResource(R.mipmap.refresh_successed);
                headerView_imageView.setRotation(0);
                headerView_imageView.setVisibility(View.VISIBLE);
                headerView_progressBar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 刷新底部视图
     * @param mode
     */
    public void refreshFooterView(int mode) {
        switch (mode) {
            case REFRESH_CAN_NOT :
                footerView_textView.setText("上拉刷新");
                footerView_imageView.setImageResource(R.mipmap.indicator_arrow);
                footerView_imageView.setVisibility(View.VISIBLE);
                footerView_imageView.setRotation(0);
                footerView_progressBar.setVisibility(View.GONE);
                break;
            case REFRESH_CAN :
                footerView_textView.setText("松开刷新");
                footerView_imageView.setVisibility(View.VISIBLE);
                footerView_imageView.setRotation(180);
                footerView_progressBar.setVisibility(View.GONE);
                break;
            case REFRESH_ING :
                footerView_textView.setText("正在刷新...");
                footerView_imageView.setVisibility(View.GONE);
                footerView_progressBar.setVisibility(View.VISIBLE);
                break;
            case REFRESH_FINISHED :
                footerView_textView.setText("刷新完成");
                footerView_imageView.setImageResource(R.mipmap.refresh_successed);
                footerView_imageView.setRotation(0);
                footerView_imageView.setVisibility(View.VISIBLE);
                footerView_progressBar.setVisibility(View.GONE);
                break;
        }
    }

}
