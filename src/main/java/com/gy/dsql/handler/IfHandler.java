package com.gy.dsql.handler;

import com.gy.dsql.node.IfSqlNode;
import com.gy.dsql.node.MixedSqlNode;
import com.gy.dsql.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

public class IfHandler implements NodeHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        String test = element.attributeValue("test");
        if (test == null) {
            throw new RuntimeException("<if> tag missing test attribute");
        }

        List<SqlNode> contents = XmlParser.parseElement(element);
        IfSqlNode ifSqlNode = new IfSqlNode(test, new MixedSqlNode(contents));
        targetContents.add(ifSqlNode);
    }
}
