package com.plantrice.forum.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤的工具类
 */
@Component
public class SensitiveFilter {

    //定义前缀树 根据敏感词，初始化前缀树 编写敏感词的方法
    //方便打印日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 替换符
    private static final String REPLACEMENT = "***";
    // 根节点
    private TireNode rootNode = new TireNode();

    //初始化  PostConstruct，表示当容器实例化当前这个bean以后，调用这个bean的构造器后，会被自动的调用
    @PostConstruct
    public void init() {
        try (
                //从类路径下加载敏感词文件
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }
        // 将一个敏感词添加到前缀树中
        private void addKeyword(String keyword) {
            TireNode tempNode = rootNode;
            for (int i = 0; i < keyword.length(); i++) {
                char c = keyword.charAt(i);
                TireNode subNode = tempNode.getSubNode(c);

                if (subNode == null) {
                    // 初始化子节点
                    subNode = new TireNode();
                    tempNode.addSubNode(c, subNode);
                }
                // 指向子节点,进入下一轮循环
                tempNode = subNode;
                // 设置结束标识
                if (i == keyword.length() - 1) {
                    tempNode.setKeywordEnd(true);
                }
            }
        }

    /**
     * 过滤敏感词的方法
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TireNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树节点的内部类
    private class TireNode{

        //关键词结束标识,树的末节点，默认为false。
        private boolean isKeywordEnd = false;

        //当前节点的子节点,子节点可能会有点多，定义为map,key:Character子节点对应的是那个字符(下级字符），
        // value:TireNode表示前缀树节点的类型（下级节点）
        private Map<Character,TireNode> subNode = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点的方法
        public void addSubNode(Character c,TireNode node){
            subNode.put(c,node);
        }
        //获取节点的方法
        public TireNode getSubNode(Character c){
            return subNode.get(c);
        }
    }
}
