package com.clubmycab;

import android.widget.BaseAdapter;

import com.clubmycab.model.RideDetailsModel;

import java.util.ArrayList;
import java.util.List;

public abstract class PagingBaseAdapter<T> extends BaseAdapter {

	protected List<RideDetailsModel> items;

	public PagingBaseAdapter() {
		this.items = new ArrayList<RideDetailsModel>();
	}

	public PagingBaseAdapter(List<RideDetailsModel> items) {
		this.items = items;
	}

	public void addMoreItems(List<RideDetailsModel> newItems) {
		this.items.addAll(newItems);
		notifyDataSetChanged();
	}

	public void addMoreItems(int location, List<RideDetailsModel> newItems) {
		this.items.addAll(location, newItems);
		notifyDataSetChanged();
	}

	public void removeAllItems() {
		this.items.clear();
		notifyDataSetChanged();
	}

}
