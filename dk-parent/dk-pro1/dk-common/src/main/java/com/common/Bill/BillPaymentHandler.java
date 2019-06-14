package com.common.Bill;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BillPaymentHandler {
    private static final double AMOUNT_RATE_FROM = 0.9;
    private static final double AMOUNT_RATE_TO = 0.95;
    private static final String DATE_FORMAT_1 = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
    private LinkedList<Integer> threeChargeTimesQueue;
    private LinkedList<Integer> twoChargeTimesQueue;
    private List<LinkedList<Integer>> hoursQueues;
    private LinkedList<Integer> minuteQueue;
    private LinkedList<Integer> timesQueue;
    private LinkedList<Integer> amountQueue;
    private BigDecimal chargeRate;
    private BigDecimal paymentRate;


    private Date now = new Date();
    private int todayPaymentTimes;
    private BigDecimal paymentedAmount = BigDecimal.ZERO;
    private BigDecimal billAmount;
    private boolean fullPayment = false;
    private LinkedList<Integer> addMinuteQueue;

    public static void main(String[] args) throws ParseException {
        BigDecimal setAmount = new BigDecimal(200).setScale(2, BigDecimal.ROUND_CEILING);
//        Date startDate = DateUtils.parseDate("2019-06-01", DATE_FORMAT_1);
//        Date billDate = DateUtils.parseDate("2019-06-03", DATE_FORMAT_1);
        String startDate = "2019-06-01";
        String billDate = "2019-06-05";
        BigDecimal billAmount = new BigDecimal(1000);
        BigDecimal chargeRate = new BigDecimal(0.0065);
        BigDecimal paymentRate = BigDecimal.ONE;
        System.out.println(chargeRate);
        System.out.println(paymentRate);
        String dateListStr = "2019-06-01,2019-06-03,2019-06-04,2019-06-05,";
        BillPaymentHandler test = new BillPaymentHandler();
        BillPaymentParam billPaymentParam = new BillPaymentParam(dateListStr, setAmount, billAmount, startDate, billDate, chargeRate, paymentRate);
//        for (int i = 0; i < 10; i++) {
//            test.dealData(setAmount, startDate, billDate, dateListStr, billAmount, chargeRate, paymentRate);
        BillPaymentPlan billPaymentPlan = test.getBillPaymentPlan(billPaymentParam);

        System.out.println(billPaymentPlan);
//        }
        System.out.println("===================");
        System.out.println(test.paymentedAmount);
        System.out.println("====================");

        billPaymentPlan.getPaymentDetailList().forEach(paymentDetail -> {

        });

    }

    public BillPaymentPlan getBillPaymentPlan(BillPaymentParam paymentParam) {
        if (!this.validParams(paymentParam)) {
            System.out.println("参数校验失败");
            // TODO 抛出自定义异常
            return null;
        }
        Date startDate = null;
        Date billDate = null;
        try {
            startDate = DateUtils.parseDate(paymentParam.getStartDate(), DATE_FORMAT_1);
            billDate = DateUtils.parseDate(paymentParam.getBillDate(), DATE_FORMAT_1);
        } catch (ParseException e) {
            System.out.println("解析日期报错");
            // throw 自定义异常
        }
        if (startDate == null) {
            System.out.println("开始时间为空");
            return null;
        }
        List<List<PaymentDetail>> lists = this.dealData(paymentParam.getSetPerPaymentAmount(), startDate, billDate, paymentParam.getPaymentDateStr(),
                paymentParam.getBillAmount(), paymentParam.getChargeRate(), paymentParam.getPaymentRate());
        if (CollectionUtils.isEmpty(lists)) {
            System.out.println("lists is null");
            return null;
        }
        BillPaymentPlan billPaymentPlan = new BillPaymentPlan();
        billPaymentPlan.setBillAmount(paymentParam.getBillAmount());
        billPaymentPlan.setSetPerPaymentAmount(paymentParam.getSetPerPaymentAmount());
        List<String> dateStrList = new ArrayList<>();

        final Boolean[] isModifyScale = {false};
        lists.forEach(list -> {
            if (!CollectionUtils.isEmpty(list)) {
                PaymentDetail paymentDetail = list.get(0);
                dateStrList.add(DateFormatUtils.format(paymentDetail.getPaymentDate(), DATE_FORMAT_1));
                billPaymentPlan.getPaymentDetailList().addAll(list);
                // TODO
//                if (!isModifyScale[0]) {
//                    isModifyScale[0] = true;
//                    BigDecimal addSacle = this.modifySacle();
//                    if (addSacle.compareTo(BigDecimal.ZERO) != 0) {
//                        // 修改精度
//                        ChargeDetail chargeDetail = paymentDetail.getChargeDetailList().get(0);
//                        chargeDetail.setChargeAmountAfterRate(chargeDetail.getChargeAmountAfterRate().add(addSacle));
//                    }
//                }
            }
        });
        billPaymentPlan.getPaymentDetailList().forEach(paymentDetail -> {
            List<ChargeDetail> chargeDetailList = paymentDetail.getChargeDetailList();
            if (!CollectionUtils.isEmpty(chargeDetailList)) {
                chargeDetailList.forEach(chargeDetail -> {
//                    paymentDetail.setPaymentAmount(chargeDetail.getChargeAmount());
//                    paymentDetail.setPaymentAmountAfterRate(chargeDetail.getChargeAmountAfterRate());
                    billPaymentPlan.setChargeAmount(chargeDetail.getChargeAmount());
                    billPaymentPlan.setPaymentAmount(chargeDetail.getChargeAmountAfterRate());
                    System.out.println(chargeDetail);
                });
                billPaymentPlan.getChargeDetails().addAll(chargeDetailList);
            }

        });
        billPaymentPlan.setPaymentTimes(billPaymentPlan.getPaymentDetailList().size());
        billPaymentPlan.setPaymentDates(dateStrList);
//        this.modifySacle(billPaymentPlan, this.paymentRate, this.billAmount);


        return billPaymentPlan;
    }
    public BigDecimal modifySacle() {
        return this.billAmount.subtract(this.paymentedAmount.subtract(paymentRate));
    }
    private boolean validParams(BillPaymentParam paymentParam) {
        // TODO 校验入参
        System.out.println(paymentParam);
        return true;
    }

    /**
     * @param setAmount 设置的预留金额
     * @param startDate 设置的开始时间
     * @param billDate 账单还款日，暂未使用
     * @param dealDateListStr 还款日期数组字符串， 例如"2019-06-01,2019-06-03,2019-06-04,2019-06-05,"， 使用英文逗号分隔
     * @param billAmount 账单金额
     * @param chargeRate 刷卡费率
     * @param paymentRate 还款费率
     */
    private List<List<PaymentDetail>> dealData(BigDecimal setAmount, Date startDate, Date billDate, String dealDateListStr, BigDecimal billAmount, BigDecimal chargeRate, BigDecimal paymentRate) {

        // 设置当天是否还款
        this.setTodayPayment(startDate);
        List<Date> dealDateList = this.parseDealDateList(dealDateListStr);

        if (CollectionUtils.isEmpty(dealDateList)) {
            // TODO 打印校验日志，校验不通过，返回校验信息或报错
            System.out.println("dealDateList is empty");
            return null;
        }

        this.chargeRate = BigDecimal.ONE.subtract(chargeRate);
        int setPaymentDays = dealDateList.size();

        BigDecimal totalTimes = billAmount.divide(setAmount, 2, BigDecimal.ROUND_FLOOR);
        totalTimes = this.setTotalTimes(totalTimes);
        this.paymentRate = paymentRate.multiply(totalTimes);
        // 加上了还款手续费的账单
        this.billAmount = billAmount;

        if (!this.validTimesPaymentDays(totalTimes, setPaymentDays)) {
            // TODO 打印校验日志，校验不通过，返回校验信息或报错
            System.out.println("还款天数不够");
            // 还款天数不够
            return null;
        }

        // 初始化随机队列
        this.initQueues(setAmount);

        List<Integer> perDayTimesList = new ArrayList<>(setPaymentDays);
        List<List<PaymentDetail>> perDaysPayments = new ArrayList<>(setPaymentDays);
        // 获取每天次数
        this.getTimesList(perDayTimesList, setPaymentDays, totalTimes.intValue(), this.timesQueue);
        if (perDayTimesList.size() < setPaymentDays) {
            int timesListSize = perDayTimesList.size();
            for (int i = 0; i < (setPaymentDays - timesListSize); i++) {
                perDayTimesList.add(3);
            }
        }
        // 获取详情
        for (int i = 0; i < perDayTimesList.size(); i++) {
            if (fullPayment) {
                break;
            }
            Integer times = perDayTimesList.get(i);
            List<PaymentDetail> paymentDetails = new ArrayList<>(times);
            perDaysPayments.add(paymentDetails);
            this.getPerDayPaymentData(paymentDetails, times, dealDateList.get(i), billAmount);
        }
        return perDaysPayments;
    }

    private BigDecimal setTotalTimes(BigDecimal totalTimes) {
        int divResult = totalTimes.divide(BigDecimal.valueOf(0.9), 0, BigDecimal.ROUND_FLOOR).intValue();
        BigDecimal add1 = totalTimes.add(BigDecimal.ONE);
        int add2 = totalTimes.setScale(0, BigDecimal.ROUND_CEILING).intValue() + 1;
        int addResult = add1.intValue();
        if (divResult == addResult || addResult == add2) {
            return add1.setScale(0, BigDecimal.ROUND_CEILING);
        }
        return totalTimes.setScale(0, BigDecimal.ROUND_CEILING);
    }

    /**
     * 设置当天还款次数 12点前3次，15点前2次 ， 17点前1次 17点后  当天不还款
     * @param startDate
     */
    private void setTodayPayment(Date startDate) {

        long day1 = DateUtils.getFragmentInDays(startDate, Calendar.YEAR);
        long day2 = DateUtils.getFragmentInDays(this.now, Calendar.YEAR);
        if (day1 != day2) {
            return;
        }
        // 设置开始时间为当天
        Calendar calendar = DateUtils.toCalendar(this.now);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // TODO 需确定当天设置时间，对还款次数的影响，暂时为8点前为3次，12点前为2次， 17点前为1次
        if (hour < 8) {
            // 10点前设置的，当天还款三次
            this.todayPaymentTimes = 3;
        } else if (hour < 12) {
            this.todayPaymentTimes = 2;
        } else if (hour < 17) {
            this.todayPaymentTimes = 1;
        } else {
            this.todayPaymentTimes = 0;
        }
    }

    /**
     * 校验还款天数是否足够
     * @param setPaymentDays
     * @return
     */
    private boolean validTimesPaymentDays(BigDecimal totalTimes, int setPaymentDays) {
        BigDecimal needDays = totalTimes.divide(BigDecimal.valueOf(3), 0, BigDecimal.ROUND_CEILING);
        return needDays.intValue() <= setPaymentDays;
    }

    /**
     * 解析处理日期字符串
     * @param dealDateListStr
     * @return
     */
    private List<Date> parseDealDateList(String dealDateListStr) {
        if (StringUtils.isNotBlank(dealDateListStr)) {
            List<Date> dates = new ArrayList<>();
            String[] split = dealDateListStr.split(",");
            for (String s : split) {
                try {
                    Date date = DateUtils.parseDate(s, DATE_FORMAT_1);
                    if (DateUtils.toCalendar(date).get(Calendar.DAY_OF_YEAR) == DateUtils.toCalendar(this.now).get(Calendar.DAY_OF_YEAR) && this.todayPaymentTimes == 0) {
                        // 当天不处理还款
                        continue;
                    }
                    dates.add(date);
                } catch (ParseException e) {
                    // TODO 打印错误日志
                    e.printStackTrace();
                    System.out.println("parse date error, date str: [ " + s + "]");
                    System.out.println(e.getMessage());
                }
            }
            return dates;
        }

        return null;
    }

    /**
     * 每日还款次数
     * @param paymentDetailList
     * @param times
     * @param chargeDate
     * @param billAmount
     */
    public void getPerDayPaymentData(List<PaymentDetail> paymentDetailList, int times, Date chargeDate, BigDecimal billAmount) {
        List<Integer> indexList = new ArrayList<>(times);
        this.setIndexList(indexList, times);
        for (int i = 0; i < times; i++) {
            if (this.fullPayment) {
                break;
            }
            PaymentDetail paymentDetail = new PaymentDetail();
            paymentDetail.setPaymentDate(chargeDate);
            paymentDetailList.add(paymentDetail);


            Integer chargeTimes = this.getValueFromQueue(this.timesQueue);
//            paymentDetail.setPaymentAmount(payment);
//            paymentDetail.setPaymentAmountAfterRate(paymentAfterRate);

            List<ChargeDetail> chargeDetailList = new ArrayList<>(chargeTimes);

            this.getPerTimeChargeData(paymentDetail, chargeDetailList, chargeTimes, chargeDate, indexList.get(i));

            chargeDetailList.sort(Comparator.comparing(ChargeDetail::getChargeTime));
            Date chargeTime = chargeDetailList.get(chargeDetailList.size() - 1).getChargeTime();
            // 还款时间为最后一次刷卡时间后的30-40分钟
            this.setPaymentDate(paymentDetail, chargeTime);
            paymentDetail.setChargeDetailList(chargeDetailList);
        }
    }

    private void setPaymentDate(PaymentDetail paymentDetail, Date lastChargeTime) {
        Integer addMinute = this.getValueFromQueue(this.addMinuteQueue);
        Date date = DateUtils.addMinutes(lastChargeTime, addMinute);
        Calendar chargeTime = DateUtils.toCalendar(date);
        int hour = chargeTime.get(Calendar.HOUR_OF_DAY);
        if (hour > 19) {
            chargeTime.set(Calendar.HOUR_OF_DAY, 19);
            chargeTime.set(Calendar.MINUTE, 59);
        }
        paymentDetail.setPaymentDate(chargeTime.getTime());
    }

    private BigDecimal validPaymentAmount(BigDecimal paymentAmount) {
        BigDecimal paymentAmountAfterRate = paymentAmount.multiply(this.chargeRate).setScale(2, BigDecimal.ROUND_CEILING);
        // 账单剩余
        BigDecimal subtractAmount = this.billAmount.add(this.paymentRate).subtract(this.paymentedAmount).setScale(2, BigDecimal.ROUND_CEILING);
        System.out.println("_____________");
        System.out.println(subtractAmount);
        System.out.println(this.paymentedAmount);
        System.out.println("__________");
        // 每次还款金额
        if (subtractAmount.compareTo(paymentAmountAfterRate) < 1) {
            this.fullPayment = true;
            return subtractAmount;
        }
        return paymentAmountAfterRate;
    }

    /**
     * 选取刷卡时间点
     * @param indexList
     * @param times
     */
    private void setIndexList(List<Integer> indexList, int times) {
        // 3的时候 1,2,3 都要，  两次和一次的需要随机
        if (times == 3) {
            indexList.add(0);
            indexList.add(1);
            indexList.add(2);
        }
        if (times == 2) {
            Integer index = this.getValueFromQueue(this.timesQueue);
            indexList.add(index);
            int secondIndex = index - 1;
            if (secondIndex < 0) {
                secondIndex = 1;
            }
            indexList.add(secondIndex);
        }
        if (times == 1) {
            indexList.add(this.getValueFromQueue(this.timesQueue));
        }
    }

    /**
     * 每次还款，刷卡数据处理
     * @param chargeDetailList 刷卡列表
     * @param chargeTimes 刷卡次数
     * @param chargeDate 刷卡日期
     * @param hoursQueuesIndex 刷卡时间点选取
     */
    private void getPerTimeChargeData(PaymentDetail paymentDetail, List<ChargeDetail> chargeDetailList, Integer chargeTimes, Date chargeDate, int hoursQueuesIndex) {
        if (chargeTimes.equals(0)) {
            chargeTimes = 1;
        }
        Integer threePop = this.getValueFromQueue(this.threeChargeTimesQueue);
        Integer minutePop = this.getValueFromQueue(this.minuteQueue);
        Integer hourPop = this.getValueFromQueue(this.getHourQueue(hoursQueuesIndex));
        List<BigDecimal> paymentAmountArr = new ArrayList<>();
        List<BigDecimal> paymentAmountAfterRateArr = new ArrayList<>();
        List<Integer> popList = new ArrayList<>(3);
        if (chargeTimes <= 1) {
//            paymentAmountArr.add(paymentAmount);
            popList.add(100);
            this.calcChargeAmount(paymentDetail, paymentAmountArr, paymentAmountAfterRateArr, 1, popList);
        } else if (chargeTimes == 2) {
//            BigDecimal chargeAmount1 = paymentAmount.multiply(BigDecimal.valueOf(this.getValueFromQueue(this.twoChargeTimesQueue).doubleValue() / 100));
//            BigDecimal chargeAmount2 = paymentAmount.subtract(chargeAmount1);
//            paymentAmountArr.add(chargeAmount1);
//            paymentAmountArr.add(chargeAmount2);·
            popList.add(this.getValueFromQueue(this.twoChargeTimesQueue));
            this.calcChargeAmount(paymentDetail, paymentAmountArr, paymentAmountAfterRateArr, 2, popList);
        } else if (chargeTimes == 3) {
            popList.add(threePop);
            popList.add(this.getValueFromQueue(this.threeChargeTimesQueue));
            this.calcChargeAmount(paymentDetail, paymentAmountArr, paymentAmountAfterRateArr, 3, popList);
//            BigDecimal threeChargeAmount1 = paymentAmount.multiply(BigDecimal.valueOf(threePop.doubleValue() / 100));
//            BigDecimal threeChargeAmount2 = paymentAmount.multiply(BigDecimal.valueOf(threePop.doubleValue() / 100)).add(BigDecimal.TEN);
//            BigDecimal threeChargeAmount3 = paymentAmount.subtract(threeChargeAmount1).subtract(threeChargeAmount2);
//            paymentAmountArr.add(threeChargeAmount1);
//            paymentAmountArr.add(threeChargeAmount2);
//            paymentAmountArr.add(threeChargeAmount3);
        } else {
            return;
        }

        Integer addMinute = this.getValueFromQueue(this.addMinuteQueue);
        for (int i = 0; i < chargeTimes; i++) {
            ChargeDetail chargeDetail = this.getChargeDetail(minutePop, paymentAmountArr.get(i), paymentAmountAfterRateArr.get(i), chargeDate, hourPop);
            chargeDetailList.add(chargeDetail);
            minutePop = minutePop + addMinute + 2 * i * i;
            hourPop = hourPop + minutePop / 60;
            minutePop = minutePop % 60;
        }

    }

    /**
     * 通过还款金额设置刷卡金额
     * @param paymentDetail
     * @param paymentAmountArr
     * @param paymentAmountAfterRateArr
     * @param times
     * @param popList
     */
    private void calcChargeAmount(PaymentDetail paymentDetail, List<BigDecimal> paymentAmountArr, List<BigDecimal> paymentAmountAfterRateArr, int times, List<Integer> popList) {

        // 还款刷卡金额
        BigDecimal paymentAmount = new BigDecimal(this.getValueFromQueue(this.amountQueue));
        // 检验剩余待还额与paymentAmount大小，最后一次还款时 取两者中的小的
//         每次还款金额
        BigDecimal paymentAfterRate = this.validPaymentAmount(paymentAmount);

        // 每次还款金额
//        BigDecimal paymentAfterRate = paymentAmount.multiply(this.chargeRate).setScale(2, BigDecimal.ROUND_CEILING);
        if (this.fullPayment) {
            paymentAmount = paymentAfterRate.divide(this.chargeRate, 2, BigDecimal.ROUND_CEILING);
        }

        BigDecimal chargeTotal = BigDecimal.ZERO;
        BigDecimal ratedChargeTotal = BigDecimal.ZERO;
        for (int i = 0; i < times; i++) {
            BigDecimal charge = null;
            BigDecimal ratedCharge = null;
            if (i == times - 1) {
                // 最后一次的刷卡 等于预还款 减去 已刷卡
//                ratedCharge = paymentAfterRate.subtract(ratedChargeTotal);
                if (this.fullPayment) {
                    ratedCharge = paymentAfterRate.subtract(ratedChargeTotal);
                    charge = ratedCharge.divide(this.chargeRate, 2, BigDecimal.ROUND_CEILING);
                } else {
                    charge = paymentAmount.subtract(chargeTotal);
                    ratedCharge = charge.multiply(this.chargeRate).setScale(2, BigDecimal.ROUND_CEILING);
                }
            } else {
                // 刷卡金额
                charge = paymentAmount.multiply(BigDecimal.valueOf(popList.get(i)).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_CEILING)).setScale(0, BigDecimal.ROUND_CEILING);
//                charge = charge.setScale(0, BigDecimal.ROUND_CEILING);
                // 每次刷卡占还款额的比例
//                BigDecimal rate = charge.divide(paymentAmount, 5, BigDecimal.ROUND_CEILING);
                chargeTotal = chargeTotal.add(charge);
                // 每次刷卡费率后金额
//                ratedCharge = paymentAfterRate.multiply(rate).setScale(0, 2);
//                ratedCharge = charge.multiply(this.chargeRate).setScale(2, BigDecimal.ROUND_CEILING);
//                ratedChargeTotal = ratedChargeTotal.add(ratedCharge);
                ratedCharge = charge.multiply(this.chargeRate).setScale(2, BigDecimal.ROUND_CEILING);
            }

            ratedChargeTotal = ratedChargeTotal.add(ratedCharge);
            paymentAmountArr.add(charge);
            paymentAmountAfterRateArr.add(ratedCharge);

        }
        // 已还款额
        this.paymentedAmount = this.paymentedAmount.add(ratedChargeTotal);
        paymentDetail.setPaymentAmount(paymentAmount);
        paymentDetail.setPaymentAmountAfterRate(ratedChargeTotal);
    }



    /**
     * 刷卡详情
     * @param minute 刷卡时间的分钟
     * @param chargeAmount 随机的刷卡金额
     * @param chargeDate  刷卡日期
     * @param hPop 刷卡的小时
     * @return
     */
    private ChargeDetail getChargeDetail(Integer minute, BigDecimal chargeAmount, BigDecimal chargeAmountAfterRate, Date chargeDate, int hPop) {
//        chargeAmount = this.validPaymentAmount(chargeAmount);
//        chargeAmount = chargeAmount.setScale(0, BigDecimal.ROUND_CEILING);
        ChargeDetail chargeDetail = new ChargeDetail();
        chargeDetail.setChargeAmount(chargeAmount);
        chargeDetail.setChargeAmountAfterRate(chargeAmountAfterRate);
//         在payment中计算了
//        this.paymentedAmount = this.paymentedAmount.add(chargeDetail.getChargeAmountAfterRate());
//        paymentDetail.setPaymentAmount(chargeAmount);
//        paymentDetail.setPaymentAmountAfterRate(chargeDetail.getChargeAmountAfterRate());

        Integer secondPop = this.getValueFromQueue(minuteQueue);
        Calendar calendar = DateUtils.toCalendar(chargeDate);
        calendar.set(Calendar.HOUR_OF_DAY, hPop);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, secondPop);
        chargeDetail.setChargeTime(calendar.getTime());
        return chargeDetail;
    }

    private Integer getValueFromQueue(LinkedList<Integer> queue) {
        Integer pop = queue.pop();
        queue.offer(pop);
        return pop;
    }

    /**
     *
     * @param timesList 每天还款次数
     * @param days 剩余天数
     * @param times 剩余次数
     * @param queue 次数随机数
     */
    public void getTimesList(List<Integer> timesList, int days, int times, LinkedList<Integer> queue) {
        Integer pop = this.getValueFromQueue(queue);
        int remainingDays = days - 1;
        int remainingTimes = times - pop;
        if (remainingDays <= 0 || remainingTimes <= 0) {
            // 最后一次，退出递归
            timesList.add(times);
            return;
        }
        if (remainingDays * 3 > remainingTimes) {
            // 次数足够，添加
            timesList.add(pop);
        }
        if (remainingDays * 3 < remainingTimes) {
            // 次数不足， 重新下一次
            remainingDays = days;
            remainingTimes = times;
        }
        if (remainingDays * 3 == remainingTimes) {
            // 次数刚好，退出递归
            timesList.add(pop);
            return;
        }
        this.getTimesList(timesList, remainingDays, remainingTimes, queue);
    }

    private LinkedList<Integer> getHourQueue(int index) {
        if (index < 0 || index > 2) {
            index = 1;
        }
        return this.hoursQueues.get(index);
    }
    private void initQueues(BigDecimal setAmount) {
        // 每笔刷三次，拆分比例
        this.threeChargeTimesQueue = BillPaymentHandler.getQueue(30, 40, 100);
        // 每笔两次，拆分比例
        this.twoChargeTimesQueue = BillPaymentHandler.getQueue(40, 50, 100);
        LinkedList<Integer> hoursQueue8To11 = BillPaymentHandler.getQueue(8, 11, 100);
        LinkedList<Integer> hoursQueue12To16 = BillPaymentHandler.getQueue(12, 15, 100);
        // 取到18点截止， 三次还款可到19点多
        LinkedList<Integer> hoursQueue17To20 = BillPaymentHandler.getQueue(16, 19, 100);
        // 还款时间点，三个 上午 下午 晚上
        this.hoursQueues = new ArrayList<>(3);
        this.hoursQueues.add(hoursQueue8To11);
        this.hoursQueues.add(hoursQueue12To16);
        this.hoursQueues.add(hoursQueue17To20);
        this.minuteQueue = BillPaymentHandler.getQueue(0, 59, 100);

        this.addMinuteQueue = BillPaymentHandler.getQueue(20, 30, 100);

        // 次数随机队列 0-3
        this.timesQueue = BillPaymentHandler.getQueue(0, 4, 1000);
        BigDecimal fromAmount = setAmount.multiply(BigDecimal.valueOf(AMOUNT_RATE_FROM));
        BigDecimal toAmount = setAmount.multiply(BigDecimal.valueOf(AMOUNT_RATE_TO));
        // 还款金额随机，为设置金额的百分比
        this.amountQueue = BillPaymentHandler.getQueue(fromAmount.intValue(), toAmount.intValue(), 1000);
    }
    private static LinkedList<Integer> getQueue(int randomFrom, int randomTo, int limit) {
        LinkedList<Integer> queue = new LinkedList<>();
        Random random = new Random();
        IntStream intStream = random.ints(randomFrom, randomTo);
        List<Integer> randomList = intStream
                .limit(limit)
                .boxed()
                .collect(Collectors.toList());
        randomList.forEach(queue::offer);
        intStream.close();
        return queue;
    }
}
