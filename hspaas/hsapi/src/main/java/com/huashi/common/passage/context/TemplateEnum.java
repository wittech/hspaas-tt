// *********************************************************************
// 系统名称：DBRDR
// Copyright(C)2000-2016 NARI Information and Communication Technology
// Branch. All rights reserved.
// 版本信息：DBRDR-V1.000
// #作者：杨猛 $权重：100%#
// 版本 日期 作者 变更记录
// DBRDR-V1.000 2016年8月25日 杨猛　 新建
// *********************************************************************
package com.huashi.common.passage.context;

/**
 * @author ym
 * @created_at 2016年8月25日下午7:39:51
 */
public class TemplateEnum {

    public enum PassageTemplateType {
        SMS(1, "短信模板"), FS(2, "流量模板"), VS(3, "语音模板"), MMS(4, "彩信模板");

        private int    value;

        private String name;

        private PassageTemplateType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum PassageTemplateDetailType {
        DETAIL_1(1, "发送模板"), DETAIL_2(2, "状态回执推送"), DETAIL_3(3, "状态回执自取"), DETAIL_4(4, "上行推送"), DETAIL_5(5, "上行自取"),
        DETAIL_6(6, "模板报备"), DETAIL_7(7, "模板报备结果推送"), DETAIL_8(8, "模板报备结果自取");

        private int    value;
        private String name;

        private PassageTemplateDetailType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static PassageTemplateDetailType getByValue(int value) {
            for (PassageTemplateDetailType t : PassageTemplateDetailType.values()) {
                if (t.getValue() == value) {
                    return t;
                }
            }
            return null;
        }

    }
}
