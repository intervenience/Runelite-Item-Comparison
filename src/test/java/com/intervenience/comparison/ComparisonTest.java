package com.intervenience.comparison;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ComparisonTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ComparisonTest.class);
		RuneLite.main(args);
	}
}