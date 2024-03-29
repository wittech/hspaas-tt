package com.huashi.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	private static final SimpleDateFormat DAY = getFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DAY_NUMBER = getFormat("yyyyMMdd");

	private static final SimpleDateFormat YEAR_DAY_NUMBER = getFormat("yyMMdd");

	private static final SimpleDateFormat DAY_ONLY = getFormat("MM:dd");

	private static final SimpleDateFormat SECOND = getFormat("yyyy-MM-dd HH:mm:ss");
	// private static final SimpleDateFormat SECOND_NUMBER =
	// getFormat("yyMMddHHmmss");

	private static final SimpleDateFormat SECOND_ONLY = getFormat("HH:mm:ss");

	private static final SimpleDateFormat MINUTE = getFormat("yyyy-MM-dd HH:mm");

	private static final SimpleDateFormat MINUTE_ONLY = getFormat("HH:mm");

	private static final SimpleDateFormat MINUTE_ANOTHER = getFormat("yyyyMMdd-HHmm");

	private static final SimpleDateFormat NOW_NUMBER = getFormat("MMddHHmmssSSS");

	public static String getMinuteDbStr(Date date) {
		synchronized (MINUTE_ANOTHER) {
			return getStr(date, MINUTE_ANOTHER);
		}
	}

	/**
	 * 转成时间戳
	 * 
	 * @param startDate
	 * @return
	 */
	public static Long getStringTurnDate(String parameter) {
		Date date = getDayDate(parameter);
		return date.getTime();
	}

	public static Date getMinuteDbDate(String str) {
		synchronized (MINUTE_ANOTHER) {
			return getDate(str, MINUTE_ANOTHER);
		}
	}
	
	/**
	 * 
	   * TODO 获取X 时0分0秒 毫秒时间
	   * @param xhour
	   * @return
	 */
	public static long getXHourWithMzSzMillis(int xhour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getSecondDate(getFormat("yyyy-MM-dd HH:00:00").format(new Date())));
		cal.add(Calendar.HOUR, xhour);
		
		return cal.getTime().getTime();
	}
	
	/**
     * 
       * TODO 获取X时分59秒59分 毫秒时间
       * @param xhour
       * @return
     */
    public static long getAfterXHourWithMzSzMillis(int xhour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getSecondDate(getFormat("yyyy-MM-dd HH:59:59").format(new Date())));
        cal.add(Calendar.HOUR, xhour);
        
        return cal.getTime().getTime();
    }

	public static String getSecondOnlyStr(Date date) {
		synchronized (SECOND_ONLY) {
			return getStr(date, SECOND_ONLY);
		}
	}

	public static String getOnlyDayStr(long date) {
		synchronized (DAY_ONLY) {
			return getStr(new Date(date), DAY_ONLY);
		}
	}

	public static String getSecondDateStr(long date) {
		return getSecondStr(new Date(date));
	}

	public static String getSecondStr(long date) {
		return getSecondOnlyStr(new Date(date));
	}

	public static String getMinuteStr(Date date) {
		synchronized (MINUTE) {
			return getStr(date, MINUTE);
		}
	}

	public static String getMinuteStr(long date) {
		return getMinuteStr(new Date(date));
	}

	public static String getMinuteOnlyStr(Date date) {
		synchronized (MINUTE_ONLY) {
			return getStr(date, MINUTE_ONLY);
		}
	}

	public static String getSecondStr(Date date) {
		synchronized (SECOND) {
			return getStr(date, SECOND);
		}
	}

	public static String getYYDayStr(Date date) {
		synchronized (YEAR_DAY_NUMBER) {
			return getStr(date, YEAR_DAY_NUMBER);
		}
	}

	public static String getDayStr(Date date) {
		synchronized (DAY) {
			return getStr(date, DAY);
		}
	}

	public static String getNow() {
		return getSecondStr(new Date());
	}
	
	public static String getCurrentDate() {
		return getDayStr(new Date());
	}

	public static String getDayGoXday(int x) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, x);
		synchronized(DAY) {
			return DAY.format(cal.getTime());
		}
	}

	public static String getMonthXDay(int x) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -3);
		return DAY.format(calendar.getTime());
	}

	public static String getDayNumberStr(Date date) {
		synchronized (DAY_NUMBER) {
			return getStr(date, DAY_NUMBER);
		}
	}

	public static String getNowNumberStr() {
		synchronized (NOW_NUMBER) {
			return getStr(new Date(), NOW_NUMBER);
		}
	}

	public static int getDayNumber(Date date) {
		if (date == null) {
			return 0;
		}
		synchronized (DAY_NUMBER) {
			return Integer.valueOf(getStr(date, DAY_NUMBER));
		}
	}

	public static int getYYDayNumber(Date date) {
		if (date == null) {
			return 0;
		}
		synchronized (YEAR_DAY_NUMBER) {
			return Integer.valueOf(getStr(date, YEAR_DAY_NUMBER));
		}
	}

	public static Date getDayDate(Date date) {
		return getDayDate(getDayStr(date));
	}

	public static String getDayStr(long date) {
		return getDayStr(new Date(date));
	}

	public static Date getSecondDate(String dateStr) {
		synchronized (SECOND) {
			return getDate(dateStr, SECOND);
		}
	}

	public static Date getDayDate(String dateStr) {
		synchronized (DAY) {
			return getDate(dateStr, DAY);
		}
	}

	public static Date getMinuteOnlyDate(String dateStr) {
		synchronized (MINUTE_ONLY) {
			return getDate(dateStr, MINUTE_ONLY);
		}
	}

	public static Date getMinuteDate(String dateStr) {
		synchronized (MINUTE) {
			return getDate(dateStr, MINUTE);
		}
	}

	public static Date getMinuteDate(long time) {
		return getMinuteDate(getMinuteStr(time));
	}

	public static Date daysAddOrSub(Date date, int offset) {
		return addOrSub(date, Calendar.DAY_OF_MONTH, offset);
	}

	public static Date hoursAddOrSub(Date date, int offset) {
		return addOrSub(date, Calendar.HOUR_OF_DAY, offset);
	}

	public static Date minutesAddOrSub(Date date, int offset) {
		return addOrSub(date, Calendar.MINUTE, offset);
	}

	public static Date secondsAddOrSub(Date date, int offset) {
		return addOrSub(date, Calendar.SECOND, offset);
	}

	private static Date addOrSub(Date date, int type, int offset) {
		if (date == null) {
            {
                return null;
            }
        }
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.get(type);
		cal.set(type, cal.get(type) + offset);
		return cal.getTime();
	}

	private static String getStr(Date date, SimpleDateFormat format) {
		if (date == null) {
			return "";
		}
		return format.format(date);
	}

	private static SimpleDateFormat getFormat(String format) {
		return new SimpleDateFormat(format);
	}

	private static Date getDate(String dateStr, SimpleDateFormat format) {
		if ("".equals(dateStr) || dateStr == null) {
            {
                return null;
            }
        }
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /////////////////////////////////time
	// tools///////////////////////////////
	/**
	 * 是否为TimeString 样式的String 00:00 - 23:59
	 * 
	 * @param toCheck
	 * @return
	 */
	public static boolean isTimeString(String toCheck) {
		if (!StringUtils.isNotBlank(toCheck)) {
			return false;
		}
		if (toCheck.trim().matches("([0-1][0-9]|2[0-3]):[0-5][0-9]|24:00")) {
            {
                return true;
            }
        } else {
            {
                return false;
            }
        }
	}

	/**
	 * 比较两个TimeString 的大小
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	public static int compareHHmmInString(String ts1, String ts2) {
		return ts1.compareTo(ts2);
	}

	/**
	 * 检测是否在开始与结束之间，前闭后开区间 -1： start 不小于end 0: 不在start 与end之间 1: 在start与end之间
	 * 
	 * @param ts
	 * @param start
	 * @param end
	 * @return
	 */
	public static int betweenHHmmInString(String ts, String start, String end) {
		if (compareHHmmInString(start, end) >= 0) {
            {
                return -1;
            }
        }
		if (compareHHmmInString(ts, start) < 0) {
            {
                return 0;
            }
        }
		if (compareHHmmInString(end, ts) <= 0) {
            {
                return 0;
            }
        }
		return 1;
	}

	/**
	 * 检测两个时间是否相等, 00:00 == 24:00
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	public static boolean equalsInTimeString(String ts1, String ts2) {
		if (ts1.equals(ts2)) {
            {
                return true;
            }
        }
		if (ts1.equals("00:00") || ts1.equals("24:00")) {
			if (ts2.equals("00:00") || ts2.equals("24:00")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测是否为同一个小时和分钟. 1: cal>c; 0:cal=c; -1:cal<c
	 */
	public static int compareHHmm(Calendar cal, Calendar c) {
		if (cal.get(Calendar.HOUR_OF_DAY) > c.get(Calendar.HOUR_OF_DAY)) {
			return 1;
		} else if (cal.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)) {
			if (cal.get(Calendar.MINUTE) > c.get(Calendar.MINUTE)) {
                {
                    return 1;
                }
            } else if (cal.get(Calendar.MINUTE) == c.get(Calendar.MINUTE)) {
                {
                    return 0;
                }
            } else {
                {
                    return -1;
                }
            }
		} else {
            {
                return -1;
            }
        }
	}

	/**
	 * 检测是否在开始与结束之间，闭区间 -1： start 不小于end 0: 不在start 与end之间 1: 在start与end之间
	 * 
	 * @param cal
	 * @param start
	 * @param end
	 * @return
	 */
	public static int betweenHHmm(Calendar cal, Calendar start, Calendar end) {
		if (compareHHmm(start, end) != -1) {
            {
                return -1;
            }
        }
		if (compareHHmm(cal, start) == -1) {
            {
                return 0;
            }
        }
		if (compareHHmm(end, cal) == -1) {
            {
                return 0;
            }
        }
		return 1;
	}

	/**
	 * 检测是否为同在一天. 1: cal>c; 0:cal=c; -1:cal<c
	 */
	public static boolean compareDay(Calendar cal, Calendar c) {
		if (cal.get(Calendar.MONTH) == c.get(Calendar.MONTH)
				&& cal.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH)) {
            {
                return true;
            }
        } else {
            {
                return false;
            }
        }
	}

	/**
	 * 将00:00格式的字符串转为Calender
	 * 
	 * @param timeString
	 * @return Calender
	 * @throws Exception
	 */
	public static Calendar string2calendar(String timeString) throws Exception {
		if (!DateUtil.isTimeString(timeString)) {
            {
                throw new Exception("Wrong argument : timeString format error " + timeString);
            }
        }
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, getHour(timeString));
		cal.set(Calendar.MINUTE, getMinute(timeString));
		return cal;
	}

	/**
	 * 将calendar转为HH:MM格式的字符串
	 * 
	 * @param cal
	 * @return
	 */
	public static String calendar2TimeString(Calendar cal) {
		return (cal.get(Calendar.HOUR_OF_DAY) > 9 ? cal.get(Calendar.HOUR_OF_DAY) : "0" + cal.get(Calendar.HOUR_OF_DAY))
				+ ":" + (cal.get(Calendar.MINUTE) > 9 ? cal.get(Calendar.MINUTE) : "0" + cal.get(Calendar.MINUTE));
	}

	/**
	 * TimeString 中得到小时信息
	 * 
	 * @param timeString
	 * @return
	 */
	public static int getHour(String timeString) {
		return Integer.parseInt(timeString.substring(0, 2));
	}

	/**
	 * TimeString 中得到分钟信息
	 * 
	 * @param timeString
	 * @return
	 */
	public static int getMinute(String timeString) {
		return Integer.parseInt(timeString.substring(3, 5));
	}

	public static String getDateStringFromMinute(String minute) {
		return minute.substring(5, 10);
	}

	public static String getMiniteOnlyFromMinute(String minute) {
		return minute.substring(11, 16);
	}

	public static boolean isMiniteDate(String str) {
		if (str == null) {
			return false;
		}
		try {
			synchronized (MINUTE_ONLY) {
				MINUTE_ONLY.parse(str);
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static long getMiniteDate(Date date, String str) {
		if (str == null) {
            {
                return 0;
            }
        }
		return getMinuteDate(getDayStr(date) + " " + str).getTime();
	}

	/**
	 * 从Date的字符串中只得到月日信息
	 * 
	 * @param dateString
	 * @return
	 */
	public static String getDateOnlyFromDate(String dateString) {
		return dateString.substring(5, 10);
	}

	// /////////////////////////////date tools////////////////////////////
	/**
	 * 将calendar转为MM:DD格式的字符串
	 * 
	 * @param cal
	 * @return
	 */
	public static String calendar2DateString(Calendar cal) {
		return (cal.get(Calendar.MONTH) + 1 > 9 ? cal.get(Calendar.MONTH) + 1 : "0" + (cal.get(Calendar.MONTH) + 1))
				+ ":" + (cal.get(Calendar.DAY_OF_MONTH) > 9 ? cal.get(Calendar.DAY_OF_MONTH)
						: "0" + cal.get(Calendar.DAY_OF_MONTH));
	}

	public static boolean isDateString(String toCheck) {
		if (toCheck == null) {
            {
                return false;
            }
        }
		if (toCheck.trim().matches("(0[0-9]|1[0-2]):([0-2][0-9]|3[0-1])")) {
            {
                return true;
            }
        } else {
            {
                return false;
            }
        }
	}

	public static Date[] getIntervalDate(Date start, Date end) {
		List<Date> ret = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDayDate(start));
		Date tmpDate = calendar.getTime();
		long endTime = getDayDate(end).getTime();
		while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
			ret.add(calendar.getTime());
			calendar.add(GregorianCalendar.DATE, 1);
			tmpDate = calendar.getTime();
		}
		return (Date[]) ret.toArray(new Date[ret.size()]);
	}

	/**
	 * 
	 * TODO 判断是否超过24小时
	 * 
	 * @param start
	 *            开始日期
	 * @param end
	 *            截止日期
	 * @return
	 */
	public static boolean isBeyond24Hour(Date start, Date end) {
		long cha = end.getTime() - start.getTime();
		if (cha < 0) {
            {
                return false;
            }
        }
		double result = cha * 1.0 / (1000 * 60 * 60);
		if (result <= 24) {
            {
                return true;
            }
        }
		return false;
	}

}
