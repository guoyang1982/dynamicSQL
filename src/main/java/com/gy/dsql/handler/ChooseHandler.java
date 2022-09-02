package com.gy.dsql.handler;


import com.gy.dsql.exception.BuilderException;
import com.gy.dsql.node.ChooseSqlNode;
import com.gy.dsql.node.SqlNode;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guoyang
 * @date 2022/9/2 8:37 下午
 */
 public class ChooseHandler implements NodeHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> whenSqlNodes = new ArrayList<>();
        List<SqlNode> otherwiseSqlNodes = new ArrayList<>();
        handleWhenOtherwiseNodes(element, whenSqlNodes, otherwiseSqlNodes);
        SqlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
        ChooseSqlNode chooseSqlNode = new ChooseSqlNode(whenSqlNodes, defaultSqlNode);
        targetContents.add(chooseSqlNode);
    }

    private void handleWhenOtherwiseNodes(Element chooseSqlNode, List<SqlNode> ifSqlNodes, List<SqlNode> defaultSqlNodes) {
        List<Object> children = chooseSqlNode.content();
        for (Object child : children) {
            Element ch = (Element) child;
            String nodeName = ch.getName();
            NodeHandler handler = XmlParser.nodeHandlers.get(nodeName);
            if (handler instanceof IfHandler) {
                handler.handle(ch, ifSqlNodes);
            } else if (handler instanceof OtherwiseHandler) {
                handler.handle(ch, defaultSqlNodes);
            }
        }
    }

    private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlNodes) {
        SqlNode defaultSqlNode = null;
        if (defaultSqlNodes.size() == 1) {
            defaultSqlNode = defaultSqlNodes.get(0);
        } else if (defaultSqlNodes.size() > 1) {
            throw new BuilderException("Too many default (otherwise) elements in choose statement.");
        }
        return defaultSqlNode;
    }
}
