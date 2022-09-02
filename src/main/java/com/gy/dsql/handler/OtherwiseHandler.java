package com.gy.dsql.handler;

import com.gy.dsql.node.MixedSqlNode;
import com.gy.dsql.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 * @author guoyang
 * @date 2022/9/2 8:47 下午
 */
public class OtherwiseHandler implements NodeHandler {

    @Override
    public void handle(Element element, List<SqlNode> contents) {
        MixedSqlNode mixedSqlNode = new MixedSqlNode(XmlParser.parseElement(element));
        contents.add(mixedSqlNode);
    }
}
