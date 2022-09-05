package com.gy.dsql;

import com.gy.dsql.engine.DynamicSqlEngine;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.scripting.xmltags.WhereSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @program: orange
 * @description:
 * @author: jiangqiang
 * @create: 2021-02-23 10:19
 **/
public class TestOrange {

    @Test
    public void test() {
        StringBuilder builder = new StringBuilder();
        String a = null;
        builder.append("abc").append(a).append("333");
        System.out.println(builder.toString());
    }

    @Test
    public void testIf() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<script>id &lt;= #{maxId}</script>";
        Map<String, Object> map = new HashMap<>();
        map.put("maxId", 10);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    @Test
    public void testTrim() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<script><trim prefix='(' suffix=')' suffixesToOverride=',' prefixesToOverride='and' ><foreach collection='list' index='idx' open='(' separator=',' close=')'>#{item.name}== #{idx}</foreach><if test='id!=null'>  and xyz.,</if></trim></script>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testWhere() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<script>select * from table <where> <if test='id!=null'>   id = ${id}</if><if test='id==2'>  and id = #{id}</if> or name =${name:tttt} </where></script>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);
        map.put("name", "asdfasdf");

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<script>select * from user where name in "
            + "<foreach collection='list' index='idx' open='(' separator=',' close=')'>"
            + "${item}"
            + "</foreach>"
            + " and desic = ${default:hehe}</script>");
        Map<String, Object> map = new HashMap<>();

        ArrayList arrayList = new ArrayList<>();
        arrayList.add("gy");
        arrayList.add("jerry");
        map.put("list", arrayList.toArray());
//        map.put("default","gg");

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testChoose() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<script>select * from user "
            + "<where>"
            + "<choose>"
            + "<when test='title != null'>"
            + "AND title like #{title}"
            + "</when>"
            + "<when test='user != null and user.name != null'>"
            + "    AND author_name like ${user.name}"
            + "</when>"
            + "<otherwise>"
            + " AND featured = 1"
            + "</otherwise>"
            + "</choose>"
            + "</where></script>");
        Map<String, Object> map = new HashMap<>();
        map.put("title", "ggg");
        User user = new User(10, "asdf");
        map.put("user",user);
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }


    @Test
    public void testForeachIF() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<script>select * from user where name in <foreach collection='list' index='idx' open='(' separator=',' close=')'>${item.name}== #{idx}<if test='id!=null'>  and id = #{id}</if></foreach></script>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList.toArray());
        map.put("id", 100);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

/*
    @Test
    public void testForeachMap() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='users' open='(' separator=',' close=')'>#{item}</foreach>");
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> users = new HashMap<String, Object>() {
            {
                put("aaa", "a1");
                put("bbb", "b1");
            }
        };

        map.put("users", users);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }
*/

    @Test
    public void testMultiForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<script>select * from a where name in <foreach collection='list' item='name' index='index' open='(' separator=',' close=')'>'${name}'</foreach>"
            + " and id in <foreach collection='list2' open='{' separator=',' close='}'>#{item}</foreach></script>");
        Map<String, Object> map = new HashMap<>();
        ArrayList<String> list = new ArrayList<String>() {{
            add("a");
            add("b");
            add("gy");
        }};
        map.put("list", list);
        ArrayList<String> list2 = new ArrayList<String>() {{
            add("c");
            add("d");
            add("c");
            add("d");
        }};
        map.put("list2", list2.toArray());
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testSet() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<script>update<set><if test='id !=null'> id = ${a} ,</if><if test='id !=null'> id = ${id} , </if></set></script>");
        Map<String, Object> map = new HashMap<>();
        map.put("id", 10);
        User user = new User(10, "asdf");
        map.put("user", user);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testParseParam() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='list' open='(' separator=',' close=')'>#{item.name} #{item} #{id} ${indexName} </foreach><where><if test='id!=null'>  and id = #{mid}</if> ${name}</where>");
        Set<String> set = engine.parseParameter(sql);
        set.stream().forEach(System.out::println);
    }

    @Test
    public void testSet2() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<script>select ${dimensionValues:20 sdf(sub)},${targetValues} "
            + "from"
            + "(select * "
            + "from boss.dwd_da_shuffle_hive_qiyue_order_widetable_order_analysis"
            + "        where"
            + "        dt = '${dt}'"
            + "<if test='payStartTime !=null'>and pay_time>='${payStartTime}'</if>"
            + "        and vip_order_status=1"
            + "        and vip_product_type = 0"
            + "        ${b}"
            + ") a "
            + "LEFT join boss.hive_dim_fv_channel b on split(a.vip_fv, '-')[0] = b.vip_fv "
            + "group by ${dimensionCodes}</script>");
        Map<String, Object> map = new HashMap<>();
        map.put("dimensionValues", 10);
        map.put("targetValues", 10);
        map.put("dt", 10);
        map.put("payStartTime", 102);
        map.put("b", 10);
        map.put("dimensionCodes", 10);

        User user = new User(10, "asdf");
        map.put("user", user);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    @Test
    public void testSet3() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("select ${dimensionValues:20},${targetValues} "
            + "from"
            + "(select * "
            + "from boss.dwd_da_shuffle_hive_qiyue_order_widetable_order_analysis"
            + "        where"
            + "        dt = '${dt}'"
            + "        and vip_order_status=1"
            + "        and vip_product_type = 0"
            + "        and id = ${b}"
            + "        and user_name = ${user.name}"
            + ") a "
            + "LEFT join boss.hive_dim_fv_channel b on split(a.vip_fv, '-')[0] = b.vip_fv "
            + "group by ${dimensionCodes}");
        Map<String, Object> map = new HashMap<>();
        map.put("dimensionValues", 10);
        map.put("targetValues", "10");
        map.put("dt", "10");
        map.put("payStartTime", "102");
        map.put("b", "10");
        map.put("dimensionCodes", "10");

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
    }

//    @Test
//    public void testParseParam1() {
//        final String expected = "SELECT * FROM BLOG WHERE  NAME = ?";
//        DynamicSqlSource source = createDynamicSqlSource(
//            new TextSqlNode("SELECT * FROM BLOG"),
//            new WhereSqlNode(new Configuration(),mixedContents(
//                new IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?  ")), "false"),
//                new IfSqlNode(mixedContents(new TextSqlNode("   or NAME = ?  ")), "true"))));
//        BoundSql boundSql = source.getBoundSql(null);
//        assertEquals(expected, boundSql.getSql());
//    }
//
//    private DynamicSqlSource createDynamicSqlSource(SqlNode... contents) throws IOException, SQLException {
////        createBlogDataSource();
//        final String resource = "org/apache/ibatis/builder/MapperConfig.xml";
//        final Reader reader = Resources.getResourceAsReader(resource);
//        SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
//        Configuration configuration = sqlMapper.getConfiguration();
//        MixedSqlNode sqlNode = mixedContents(contents);
////创建动态数据源
//        return new DynamicSqlSource(configuration, sqlNode);
//    }
}
