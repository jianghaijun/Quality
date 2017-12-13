package com.sx.quality.listener;

import com.sx.quality.tree.Node;

import java.util.List;

public interface ContractorListener {
	void returnData(List<Node> allsCache, List<Node> alls, int point, String userId);
}
