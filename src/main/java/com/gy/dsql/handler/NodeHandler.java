package com.gy.dsql.handler;

import com.gy.dsql.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

public interface NodeHandler {

    void handle(Element element, List<SqlNode> contents);
}
