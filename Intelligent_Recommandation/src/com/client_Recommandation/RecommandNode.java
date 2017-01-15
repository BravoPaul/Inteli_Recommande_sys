package com.client_Recommandation;

public class RecommandNode implements Comparable<RecommandNode>{
	
	String id;

	public RecommandNode(String id) {
		super();
		this.id = id;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public int compareTo(RecommandNode o) {
		// TODO Auto-generated method stub
		return id.compareTo(o.getName());
	}
}
