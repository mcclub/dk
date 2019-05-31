package com.common.utils;

import com.common.bean.RepaySplit;
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
     * @param penNum  笔数
     * @param dateNum 还款天数
     * @return
     */
    public static List randomplan(int penNum,int dateNum){
        List<Integer> numList ;
        lab:while (true) {
            numList = new LinkedList<>();
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

    /**
     * 拆分
     * @param list  笔数集合
     * @param repaymentAmount  还款总额
     * @param reservedAmount    预留金额
     * @param startTime   开始日期时间
     * @param endTime     结束日期时间
     * @return
     */
    public List<Object> splitMoney (List<Integer> list,double repaymentAmount,double reservedAmount,String startTime,String endTime) {
        List<Object> result = new ArrayList<>();
        //int hkDay = list.size();//还款天数
        int hkTimes = 0;//还款总次数
        int i = 0;
        List<String> allDate = this.getBetweenDate(startTime,endTime);
        for (Integer num:list) {
            hkTimes = hkTimes + num;
        }
        for (Integer num:list) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("每天还款次数",num);
            map.put("每天还款金额",repaymentAmount*((double)num/hkTimes));
            map.put("每天当中每次还款的时间",this.onceRepaymentTime(num,allDate.get(i)+" 08:00:00",allDate.get(i)+" 20:00:00"));
            map.put("每天当中每次还款的金额",this.singleSplit(repaymentAmount*((double)num/hkTimes),(int) reservedAmount,num));
            result.add(map);*/
            RepaySplit repaySplit = new RepaySplit();
            repaySplit.setRepnum(num);
            repaySplit.setRepamt(repaymentAmount*((double)num/hkTimes));
            repaySplit.setTimeList(this.onceRepaymentTime(num,allDate.get(i)+" 08:00:00",allDate.get(i)+" 20:00:00"));
            repaySplit.setAmtList(this.singleSplit(repaymentAmount*((double)num/hkTimes),(int) reservedAmount,num));
            result.add(repaySplit);
            i ++ ;
        }
        return result;
    }

    /**
     * 计算每天还款时间
     * @param num       每天还款笔数
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return list 具体每天还款时间
     */
    public List<String> onceRepaymentTime (int num,String startTime,String endTime) {
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < num ; i ++ ) {
            list.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.randomDate(startTime,endTime)));
        }
        return list;
    }

    /**
     * 具体每笔还款时间
     * @param beginDate 开始时间
     * @param endDate  结束时间
     * @return
     */
    public Date randomDate(String beginDate,String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if(start.getTime() >= end.getTime()){
                return null;
            }
            long date = random(start.getTime(),end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 随机时间
     * @param begin
     * @param end
     * @return
     */
    public long random(long begin,long end){
        long rtn = begin + (long)(Math.random() * (end - begin));
        if(rtn == begin || rtn == end){
            return random(begin,end);
        }
        return rtn;
    }


    /**
     * 获取两个日期字符串之间的日期集合
     */
    public static List<String> getBetweenDate(String startTime, String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 声明保存日期集合
        List<String> list = new ArrayList<String>();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                // 把日期添加到集合
                list.add(sdf.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一天
                calendar.add(Calendar.DATE, 1);
                // 获取增加后的日期
                startDate=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 单笔拆分
     * @param totalCount  总数
     * @param bound     范围控制
     * @param num   拆分笔数
     * @return
     */
    public List singleSplit (double totalCount,double bound,Integer num) {
        List<Double> list = new ArrayList<>();
        Random random = new Random();
        double tempTotal = 0;
        double temp = 0;

        for(int i=0;i<num;i++){
            if(tempTotal>totalCount){
                list.set(list.size()-1, list.get(list.size()-1)-(tempTotal-totalCount));
                tempTotal = totalCount;
            }else if(tempTotal<totalCount){
                while (true) {
                    temp =  random.nextDouble() * bound ;

                    if (temp < bound) {
                        break;
                    }
                }
                tempTotal = tempTotal+temp;
                list.add(temp);
            }else if(tempTotal==totalCount){
                list.add(0.0);
            }
        }

        double checkTotalCount = 0;
        for (double temp1 : list) {
            checkTotalCount = checkTotalCount + temp1;
            System.out.println(temp1);
        }
        System.out.println("-----------");

        if(checkTotalCount<totalCount){
            double surplus = totalCount -checkTotalCount;
            double floor = (int) Math.floor(surplus/num);
            for(int i=0;i<list.size();i++){
                list.set(i, list.get(i)+floor);
            }
            list.set(list.size()-1, list.get(list.size()-1)+surplus-floor*num);
        }
        checkTotalCount= 0;
        for (double temp1 : list) {
            checkTotalCount = checkTotalCount + temp1;
            System.out.println(temp1);
        }
        System.out.println("总数为"+checkTotalCount);
        return list;
    }

    /**
     * 拆分每次还款金额-----消费金额
     * @param patlist
     */
    public void splitmon(List<RepaySplit> patlist){
        for(int i = 0;i<patlist.size();i++){
            //每次还款金额
            List s = patlist.get(i).getAmtList();
            for(int j=0;j<s.size();j++){
                Double amt = (Double) s.get(j);

                int sad = randomOne(3)+1;
                List patamt = singleSplit(Double.valueOf(amt),Double.valueOf(amt)/sad,sad);
                System.out.println();
            }
        }

    }


    public static void main(String[] args) throws Exception {
        CommonUtils com = new CommonUtils();
        //随机计划笔数
        List penList = randomplan(10,4);
        //随机每笔计划金额
        List listMoney = com.splitMoney(penList,1000,200,"2019-05-26","2019-05-30");
        com.splitmon(listMoney);

        System.out.println();


    }

}
