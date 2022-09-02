package com.gy.dsql.handler;

import com.gy.dsql.node.MixedSqlNode;
import com.gy.dsql.node.SetSqlNode;
import com.gy.dsql.node.SqlNode;
import org.dom4j.Element;

import java.util.List;


public class SetHandler implements NodeHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        SetSqlNode node = new SetSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
