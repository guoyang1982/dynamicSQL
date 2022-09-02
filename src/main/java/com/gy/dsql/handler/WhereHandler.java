package com.gy.dsql.handler;

import com.gy.dsql.node.MixedSqlNode;
import com.gy.dsql.node.SqlNode;
import com.gy.dsql.node.WhereSqlNode;
import org.dom4j.Element;
import java.util.List;


public class WhereHandler implements NodeHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        WhereSqlNode node = new WhereSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
