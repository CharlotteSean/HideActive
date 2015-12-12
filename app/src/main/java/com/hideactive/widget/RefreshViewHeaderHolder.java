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
public class RefreshViewHeaderHolder {

    private View headerView;
    public ProgressBar progressBar;
    public TextView textView;
    public ImageView imageView;

    public RefreshViewHeaderHolder(Context context) {
        headerView = LayoutInflater.from(context)
                .inflate(R.layout.layout_refresh_head, null);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        progressBar.setVisibility(View.GONE);
    }

    public View getHeaderView() {
        return headerView;
    }

}
