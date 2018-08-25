package com.huashi.sms.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class TemplateRegexTest {

    private static volatile Map<String, Pattern>   WILDCARDS_WORDS     = new HashMap<>();
    private String content;

    @Before
    public void init() {
        for(int i = 0;i< 1000;i++) {
            WILDCARDS_WORDS.put(".*办.*卡.*", Pattern.compile(".*办.*卡.*"));
        }
        
        content = "【车点点】您好，办理会员卡享受8折优惠，请尽快办理";
    }

    @Test
    public void test() {
        for (int j = 0; j < 100; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                pickupWildcardsWords(content);
            }
            System.out.println("共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }

        // Assert.assertTrue(matcher.matches());
    }
    
    /**
     * TODO 根据通配敏感词摘取短信内容符合通配敏感词的数据
     * 
     * @param content
     * @return
     */
    public Set<String> pickupWildcardsWords(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        try {
            Set<String> finalWords = new HashSet<>();
            for (Entry<String, Pattern> wordsPattern : WILDCARDS_WORDS.entrySet()) {
                if (isMatched(wordsPattern.getValue(), content)) {
                    finalWords.add(wordsPattern.getKey());
                }
            }

            return finalWords;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * TODO 正则表达式是否匹配
     * 
     * @param pattern
     * @param content
     * @return
     */
    private static boolean isMatched(Pattern pattern, String content) {
        try {
            return pattern.matcher(content).matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
