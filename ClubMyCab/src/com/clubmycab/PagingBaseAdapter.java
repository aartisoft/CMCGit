package com.clubmycab;

import java.util.ArrayList;
import java.util.List;

import android.widget.BaseAdapter;

public abstract class PagingBaseAdapter<T> extends BaseAdapter {

	protected List<MyRidesObject> items;

	public PagingBaseAdapter() {
		this.items = new ArrayList<MyRidesObject>();
	}

	public PagingBaseAdapter(List<MyRidesObject> items) {
		this.items = items;
	}

	public void addMoreItems(List<MyRidesObject> newItems) {
		this.items.addAll(newItems);
		notifyDataSetChanged();
	}

	public void addMoreItems(int location, List<MyRidesObject> newItems) {
		this.items.addAll(location, newItems);
		notifyDataSetChanged();
	}

	public void removeAllItems() {
		this.items.clear();
		notifyDataSetChanged();
	}

}
