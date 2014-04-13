package com.abs192.mitfiles.misc;

public class RowItem {
	private int imageId;
	private String title;
	private String url;
	private boolean offlineStatus;
	private int index;

	public RowItem(int index, int imageId, String title, String url,
			boolean offlineStatus) {
		this.imageId = imageId;
		this.title = title;
		this.offlineStatus = offlineStatus;
		this.url = url;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isOfflineStatus() {
		return offlineStatus;
	}

	public void setOfflineStatus(boolean offlineStatus) {
		this.offlineStatus = offlineStatus;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
}