package com.unimelb.distributedsystem.ymca.model;

public class ResultSize
{
	private int resultSize;

	public ResultSize(int resultSize)
	{
		this.resultSize = resultSize;
	}

	public void setResultSize(int resultSize)
	{
		this.resultSize = resultSize;
	}

	public int getResultSize()
	{
		return resultSize;
	}

	@Override
	public String toString()
	{
		return "resultSize: " + resultSize;
	}
}
