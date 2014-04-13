package com.abs192.mitfiles.misc;

import java.util.ArrayList;

public class ParseData {

	private String source;

	public ParseData(String source) {
		this.source = source;
	}

	public ArrayList<String> getUrl() {
		ArrayList<String> a = new ArrayList<String>();

		if (source.contains("<hr>")) {

			String p = source.split("<hr>")[1].trim();

			String A[] = p.split("<a href=\"");

			for (int i = 1; i < A.length; i++) {
				a.add((A[i].split("\">"))[0]);
			}

		} else
			return null;

		return a;
	}

	public ArrayList<String> getData() {
		ArrayList<String> a = new ArrayList<String>();

		if (source.contains("<hr>")) {

			String p = source.split("<hr>")[1].trim();

			String A[] = p.split("<a href=\"");

			for (int i = 1; i < A.length; i++) {
				String q = (A[i].split("\">"))[1];
				String pq = q.split("</a>")[0];
				if (pq.endsWith("..&gt;")) {
					a.add(getFull(pq));
				} else
					a.add(pq);
			}

		} else
			return null;
		return a;
	}

	private String getFull(String pq) {
		ArrayList<String> qwe = getUrl();
		pq = pq.replace("..&gt;", "").trim().replaceAll(" ", "%20").trim();
		for (int i = 0; i < qwe.size(); i++) {
			if (qwe.get(i).startsWith(pq)) {
				return qwe.get(i).replaceAll("&amp;", "&");
			}
		}
		return pq;
	}
}
