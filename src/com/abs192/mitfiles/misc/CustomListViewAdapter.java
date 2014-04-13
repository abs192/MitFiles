package com.abs192.mitfiles.misc;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abs192.mitfiles.R;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

	Context context;

	public CustomListViewAdapter(Context context, int resourceId,
			List<RowItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* private view holder class */
	public class ViewHolder {
		int index;
		public ImageView imageView;
		public TextView txtTitle;
		public String url;
		public boolean offlineStatus;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.list_content);
			holder.txtTitle.setTextColor(Color.BLACK);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageViewListItem);
			holder.offlineStatus = rowItem.isOfflineStatus();
			holder.index = rowItem.getIndex();
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		Drawable right = context.getResources().getDrawable(
				R.drawable.ic_offline);
		holder.txtTitle.setText(rowItem.getTitle());
		if (holder.index == 1) {
			if (holder.offlineStatus) {
				right.setBounds(0, 0, right.getMinimumWidth(),
						right.getMinimumHeight());
				holder.txtTitle.setCompoundDrawables(null, null, right, null);
			}
		} else if (holder.index == 2) {

			holder.txtTitle.setBackgroundColor(Color.parseColor("#2233b5e5"));
			Drawable blank = context.getResources().getDrawable(
					android.R.color.transparent);
			blank.setBounds(0, 0, right.getMinimumWidth(),
					right.getMinimumHeight());
			holder.txtTitle.setCompoundDrawables(null, null, blank, null);

		} else {
			Drawable blank = context.getResources().getDrawable(
					android.R.color.transparent);
			blank.setBounds(0, 0, right.getMinimumWidth(),
					right.getMinimumHeight());
			holder.txtTitle.setCompoundDrawables(null, null, blank, null);

		}
		holder.txtTitle.invalidate();
		holder.imageView.setImageResource(rowItem.getImageId());
		holder.url = rowItem.getUrl();

		return convertView;
	}
}