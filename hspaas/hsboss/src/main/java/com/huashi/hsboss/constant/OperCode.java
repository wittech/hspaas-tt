package com.huashi.hsboss.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * Author youngmeng
 * Created 2018-09-04 14:48
 */
public class OperCode {

    /*通用权限*/
    public static final String OPER_CODE_COMMON = "0000000000";
    /**所有关联*/
    public Map<String, String[]> OPER_CODE_MAP = new HashMap<String, String[]>();
    /**客户基础信息_新增客户*/
    public static final String OPER_CODE_1001001001 = "1001001001";
    /**客户基础信息_编辑*/
    public static final String OPER_CODE_1001001002 = "1001001002";
    /**余额_冲扣值*/
    public static final String OPER_CODE_1001001003001 = "1001001003001";
    /**余额_告警设置*/
    public static final String OPER_CODE_1001001003002 = "1001001003002";
    /**余额_禁止告警*/
    public static final String OPER_CODE_1001001003003 = "1001001003003";
    /**余额_冲扣值日志*/
    public static final String OPER_CODE_1001001003004 = "1001001003004";
    /**客户基础信息_模板*/
    public static final String OPER_CODE_1001001004 = "1001001004";
    /**客户基础信息_禁用*/
    public static final String OPER_CODE_1001001005 = "1001001005";
    /**参数配置管理_编辑*/
    public static final String OPER_CODE_1001002001 = "1001002001";
    /**公告管理_发布公告*/
    public static final String OPER_CODE_1002001001 = "1002001001";
    /**公告管理_编辑*/
    public static final String OPER_CODE_1002001002 = "1002001002";
    /**公告管理_禁用*/
    public static final String OPER_CODE_1002001003 = "1002001003";
    /**站内消息_发送站内消息*/
    public static final String OPER_CODE_1002002001 = "1002002001";
    /**站内消息_编辑*/
    public static final String OPER_CODE_1002002002 = "1002002002";
    /**套餐管理_套餐管理操作*/
    public static final String OPER_CODE_1003001001 = "1003001001";
    /**套餐购买充值记录_套餐购买充值记录操作*/
    public static final String OPER_CODE_1003002001 = "1003002001";
    /**产品管理_产品管理*/
    public static final String OPER_CODE_1003003001 = "1003003001";
    /**待处理短信任务_审核通过*/
    public static final String OPER_CODE_2001001001 = "2001001001";
    /**待处理短信任务_同内容批放*/
    public static final String OPER_CODE_2001001002 = "2001001002";
    /**待处理短信任务_驳回*/
    public static final String OPER_CODE_2001001003 = "2001001003";
    /**待处理短信任务_同内容驳回*/
    public static final String OPER_CODE_2001001004 = "2001001004";
    /**待处理短信任务_切换通道*/
    public static final String OPER_CODE_2001001005 = "2001001005";
    /**待处理短信任务_重新分包*/
    public static final String OPER_CODE_2001001006 = "2001001006";
    /**待处理短信任务_修改内容*/
    public static final String OPER_CODE_2001001007 = "2001001007";
    /**待处理短信任务_子任务*/
    public static final String OPER_CODE_2001001008 = "2001001008";
    /**待处理短信任务_模板报备*/
    public static final String OPER_CODE_2001001009 = "2001001009";
    /**短信发送记录_短信发送记录*/
    public static final String OPER_CODE_2001002001 = "2001002001";
    /**已完成短信任务_子任务*/
    public static final String OPER_CODE_2001003001 = "2001003001";
    /**已完成短信任务_发送记录*/
    public static final String OPER_CODE_2001003002 = "2001003002";
    /**已完成短信任务_模板*/
    public static final String OPER_CODE_2001003003 = "2001003003";
    /**短信上行接收记录_短信发送记录*/
    public static final String OPER_CODE_2001004001 = "2001004001";
    /**短信调用失败记录_短信调用失败记录*/
    public static final String OPER_CODE_2001005001 = "2001005001";
    /**短信处理失败记录_短信调用失败记录*/
    public static final String OPER_CODE_2001006001 = "2001006001";
    /**模板管理_重载redis*/
    public static final String OPER_CODE_2002001 = "2002001";
    /**模板管理_添加模板*/
    public static final String OPER_CODE_2002002 = "2002002";
    /**模板管理_编辑*/
    public static final String OPER_CODE_2002003 = "2002003";
    /**模板管理_删除*/
    public static final String OPER_CODE_2002004 = "2002004";
    /**模板管理_测试*/
    public static final String OPER_CODE_2002005 = "2002005";
    /**通道管理_添加模板*/
    public static final String OPER_CODE_2003001001 = "2003001001";
    /**通道管理_编辑*/
    public static final String OPER_CODE_2003001002 = "2003001002";
    /**通道管理_删除*/
    public static final String OPER_CODE_2003001003 = "2003001003";
    /**通道管理_禁用*/
    public static final String OPER_CODE_2003001004 = "2003001004";
    /**通道管理_断连接*/
    public static final String OPER_CODE_2003001005 = "2003001005";
    /**通道管理_测试通道*/
    public static final String OPER_CODE_2003001006 = "2003001006";
    /**通道组管理_添加通道组*/
    public static final String OPER_CODE_2003002001 = "2003002001";
    /**通道组管理_编辑*/
    public static final String OPER_CODE_2003002002 = "2003002002";
    /**通道模板管理_添加通道模板*/
    public static final String OPER_CODE_2003003001 = "2003003001";
    /**通道模板管理_添加通道模板*/
    public static final String OPER_CODE_2003003002 = "2003003002";
    /**通道模板管理_添加通道模板*/
    public static final String OPER_CODE_2003003003 = "2003003003";
    /**用户运行中通道管理_重载redis*/
    public static final String OPER_CODE_2003004001 = "2003004001";
    /**用户运行中通道管理_编辑*/
    public static final String OPER_CODE_2003004002 = "2003004002";
    /**通道轮训控制管理_添加轮询控制*/
    public static final String OPER_CODE_2003005001 = "2003005001";
    /**通道轮训控制管理_编辑*/
    public static final String OPER_CODE_2003005002 = "2003005002";
    /**通道轮训控制管理_启用|停用*/
    public static final String OPER_CODE_2003005003 = "2003005003";
    /**通道轮训控制管理_删除*/
    public static final String OPER_CODE_2003005004 = "2003005004";
    /**通道短信模板管理_添加模板*/
    public static final String OPER_CODE_2003006001 = "2003006001";
    /**通道短信模板管理_编辑*/
    public static final String OPER_CODE_2003006002 = "2003006002";
    /**通道短信模板管理_删除*/
    public static final String OPER_CODE_2003006003 = "2003006003";
    /**通道短信模板管理_测试*/
    public static final String OPER_CODE_2003006004 = "2003006004";
    /**黑名单管理_重载redis*/
    public static final String OPER_CODE_2004001001 = "2004001001";
    /**黑名单管理_新增黑名单*/
    public static final String OPER_CODE_2004001002 = "2004001002";
    /**黑名单管理_删除*/
    public static final String OPER_CODE_2004001003 = "2004001003";
    /**白名单管理_新增白名单*/
    public static final String OPER_CODE_2004002001 = "2004002001";
    /**白名单管理_删除*/
    public static final String OPER_CODE_2004002002 = "2004002002";
    /**敏感词管理_重载redis*/
    public static final String OPER_CODE_2004003001 = "2004003001";
    /**敏感词管理_新增敏感词*/
    public static final String OPER_CODE_2004003002 = "2004003002";
    /**敏感词管理_编辑*/
    public static final String OPER_CODE_2004003003 = "2004003003";
    /**敏感词管理_删除*/
    public static final String OPER_CODE_2004003004 = "2004003004";
    /**服务器IP白名单_重载redis*/
    public static final String OPER_CODE_2004004001 = "2004004001";
    /**服务器IP白名单_新增白名单*/
    public static final String OPER_CODE_2004004002 = "2004004002";
    /**服务器IP白名单_删除*/
    public static final String OPER_CODE_2004004003 = "2004004003";
    /**优先级词库_新增优先级词库*/
    public static final String OPER_CODE_2004005001 = "2004005001";
    /**优先级词库_编辑*/
    public static final String OPER_CODE_2004005002 = "2004005002";
    /**优先级词库_关闭*/
    public static final String OPER_CODE_2004005003 = "2004005003";
    /**优先级词库_删除*/
    public static final String OPER_CODE_2004005004 = "2004005004";
    /**签名扩展管理_重载redis*/
    public static final String OPER_CODE_2005001 = "2005001";
    /**签名扩展管理_添加签名*/
    public static final String OPER_CODE_2005002 = "2005002";
    /**签名扩展管理_编辑*/
    public static final String OPER_CODE_2005003 = "2005003";
    /**签名扩展管理_删除*/
    public static final String OPER_CODE_2005004 = "2005004";
    /**接入商管理_接入商管理操作*/
    public static final String OPER_CODE_3001001 = "3001001";
    /**通道管理_通道管理操作*/
    public static final String OPER_CODE_3002001001 = "3002001001";
    /**通道组管理_通道组管理操作*/
    public static final String OPER_CODE_3002002001 = "3002002001";
    /**处理中的记录_处理中的记录操作*/
    public static final String OPER_CODE_3003001001 = "3003001001";
    /**已完成的记录_已完成的记录操作*/
    public static final String OPER_CODE_3003002001 = "3003002001";
    /**流量包管理_流量包管理操作*/
    public static final String OPER_CODE_3004001 = "3004001";
    /**接入商管理_接入商管理操作*/
    public static final String OPER_CODE_4001001 = "4001001";
    /**通道管理_通道管理操作*/
    public static final String OPER_CODE_4002001001 = "4002001001";
    /**通道组管理_通道组管理操作*/
    public static final String OPER_CODE_4002002001 = "4002002001";
    /**处理中的记录_处理中的记录操作*/
    public static final String OPER_CODE_4003001001 = "4003001001";
    /**已完成的记录_已完成的记录操作*/
    public static final String OPER_CODE_4003002001 = "4003002001";
    /**流量包管理_流量包管理操作*/
    public static final String OPER_CODE_4004001 = "4004001";
    /**客户通道发送统计_图表*/
    public static final String OPER_CODE_5001001001 = "5001001001";
    /**客户发送统计_图表*/
    public static final String OPER_CODE_5001002001 = "5001002001";
    /**通道发送统计_图表*/
    public static final String OPER_CODE_5001003001 = "5001003001";
    /**通道发送统计_图表*/
    public static final String OPER_CODE_5001004001 = "5001004001";
    /**客户消费统计_客户消费统计操作*/
    public static final String OPER_CODE_5002001001 = "5002001001";
    /**上家统计_上家统计操作*/
    public static final String OPER_CODE_5002002001 = "5002002001";
    /**账单统计_账单统计操作*/
    public static final String OPER_CODE_5002003001 = "5002003001";
    /**套餐统计_套餐统计操作*/
    public static final String OPER_CODE_5003001 = "5003001";
    /**用户管理_新增用户*/
    public static final String OPER_CODE_6001001 = "6001001";
    /**用户管理_编辑*/
    public static final String OPER_CODE_6001002 = "6001002";
    /**用户管理_删除*/
    public static final String OPER_CODE_6001003 = "6001003";
    /**角色管理_新增角色*/
    public static final String OPER_CODE_6002001 = "6002001";
    /**角色管理_编辑*/
    public static final String OPER_CODE_6002002 = "6002002";
    /**角色管理_删除*/
    public static final String OPER_CODE_6002003 = "6002003";
    /**角色管理_设置权限*/
    public static final String OPER_CODE_6002004 = "6002004";
    /**日志管理_日志管理操作*/
    public static final String OPER_CODE_6003001 = "6003001";
    /**密码管理_密码管理操作*/
    public static final String OPER_CODE_6004001 = "6004001";
    /**发票管理_新增发票*/
    public static final String OPER_CODE_7001001 = "7001001";
    /**发票管理_处理*/
    public static final String OPER_CODE_7001002 = "7001002";
    /**发票管理_查看*/
    public static final String OPER_CODE_7001003 = "7001003";
    /**用户账户余额管理_冲扣值*/
    public static final String OPER_CODE_7002001 = "7002001";
    /**用户账户余额管理_告警设置*/
    public static final String OPER_CODE_7002002 = "7002002";
    /**用户账户余额管理_禁止告警*/
    public static final String OPER_CODE_7002003 = "7002003";
    /**用户账户余额管理_冲扣值日志*/
    public static final String OPER_CODE_7002004 = "7002004";
    /**通道自取报告_通道自取报告*/
    public static final String OPER_CODE_8001001001 = "8001001001";
    /**通道状态监控_通道状态监控*/
    public static final String OPER_CODE_8001002001 = "8001002001";
    /**回执率失标查询_回执率失标查询*/
    public static final String OPER_CODE_8002001001 = "8002001001";
    /**短信通道监控设置_添加通道监控设置*/
    public static final String OPER_CODE_8003001 = "8003001";
    /**短信通道监控设置_编辑*/
    public static final String OPER_CODE_8003002 = "8003002";
    /**短信通道监控设置_启用|停用*/
    public static final String OPER_CODE_8003003 = "8003003";

}
