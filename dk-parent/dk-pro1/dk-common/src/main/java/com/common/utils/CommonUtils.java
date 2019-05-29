package com.common.utils;

import com.common.bean.RestResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtils {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
    private static String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

    /**
     * 随机生成串数字字符串
     * @param count  位数
     * @return
     */
    public static String getRandom(int count) {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i <= 5; i++) {
            sb.append(String.valueOf(r.nextInt(10)));
        }
        return sb.toString();
    }

    /**
     * 随机生成订单号
     * @return
     */
    public static String getOrderNo(){
        String orderNo = "DH"+date+ getRandom(4);
        return orderNo;
    }

    /**
     * 实体null转换为空字符串
     * @param e
     * @throws Exception
     */
    public static void reflect(Object e) throws Exception{
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            if(StringUtils.isEmpty(f.get(e))){
                f.set(e,"");
            }
        }
    }

    /**
     * 随机取一条数字
     * @param list
     * @return
     */
    public static int randomOne(int list){
        /**
         * 随机取一条
         */
        Random random = new Random();
        int n = random.nextInt(list);//随机数
        return n;
    }

    /**
     * 计算两个日期的相差天数
     * @param begin
     * @param end
     * @return
     * @throws ParseException
     */
    public static int daysOfTwo(String begin,String end) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        //跨年的情况会出现问题哦
        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
        Date fDate=sdf.parse(begin);
        Date oDate=sdf.parse(end);
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        System.out.println(day1);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        System.out.println(day2);
        int days=day2-day1 + 1;

        return days;
    }

    /**
     * 随机计划笔数
     * @param penNum
     * @param dateNum
     * @return
     */
    public static List randomplan(int penNum,int dateNum){
        List<Integer> numList ;
        lab:while (true) {
            numList = new LinkedList<>();
            /*int penNum = 31;//笔数
            int dateNum = 12;//还款天数*/
            int totNum = 0;//已还款笔数
            int weiNum = penNum - totNum;//未还笔数
            for(int i=1; i <= dateNum ;i++){
                int dom = randomOne(3);
                dom = dom+1;

                if(weiNum < 3){
                    dom = weiNum;
                    if(dom <= 0){
                        return numList;
                    }
                }
                if(i == dateNum){//最后一天 未还笔数必须为0
                    if(weiNum >0 && weiNum <= 3){
                        dom = weiNum;
                    }
                }else{
                    if(weiNum<0 ){
                        return numList;
                    }

                }
                numList.add(dom);
               /* System.out.println("第"+i+"天,随机还款"+dom+"笔");*/
                totNum = totNum + dom;//累计笔数
                /*System.out.println("第"+i+"天,累计还款笔数"+totNum+"笔");*/
                weiNum = penNum - totNum ;
                /*System.out.println("第"+i+"天,未还笔数"+weiNum+"笔");
                System.out.println("---------------");*/
                if (weiNum == 0 ) {
                    break lab;
                }
            }
        }
        return numList;
    }

    public static void main(String[] args) throws Exception {
        //getRandom(6);
        /*Random random = new Random();
        int n = random.nextInt(6);//随机数*/
        //System.out.println(getRandom(6));

        //System.out.println(daysOfTwo("2019-03-21","2019-03-21"));

        /*String datearr = "2019/05/29,2019/05/30,2019/05/31";
        datearr = datearr.replace("/","-");
        String[] strArr = datearr.split(",");//注意分隔符是需要转译
        System.out.println(strArr);
        for (int i = 0; i < strArr.length; i++) {
            System.out.println(strArr[i]);
        }
        String realbeginTime = strArr[0];//实际开始时间
        String realendTime = strArr[strArr.length-1];//实际结束时间
        System.out.println(realbeginTime);
        System.out.println(realendTime);
        System.out.println(strArr.length);*/

        List list = randomplan(6,3);
        System.out.println(list);

        /*List<Integer> numList;
        lab:while (true) {
            numList = new ArrayList<>();
            int penNum = 31;//笔数
            int dateNum = 12;//还款天数
            int totNum = 0;//已还款笔数
            int weiNum = penNum - totNum;//未还笔数
            for(int i=1; i <= dateNum ;i++){
                int dom = randomOne(3);
                dom = dom+1;

                if(weiNum < 3){
                    dom = weiNum;
                    if(dom <= 0){
                        return;
                    }
                }
                if(i == dateNum){//最后一天 未还笔数必须为0
                    if(weiNum >0 && weiNum <= 3){
                        dom = weiNum;
                    }
                }else{
                    if(weiNum<0 ){
                        return;
                    }

                }
                numList.add(dom);
                System.out.println("第"+i+"天,随机还款"+dom+"笔");
                totNum = totNum + dom;//累计笔数
                System.out.println("第"+i+"天,累计还款笔数"+totNum+"笔");
                weiNum = penNum - totNum ;
                System.out.println("第"+i+"天,未还笔数"+weiNum+"笔");
                System.out.println("---------------");
                if (weiNum == 0 ) {
                    break lab;
                }

            }
        }
        for (Integer num:numList) {
            System.out.println(num);
        }*/
    }

}
