/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author thuyetlv
 */
public class Partition {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

    public static void main(String[] args) {
        //ALTER TABLE city PARTITION BY RANGE((to_days(date)) (
//PARTITION city_01012015 VALUES LESS THAN (to_days('2015-01-01')),
//PARTITION city_01022015 VALUES LESS THAN (to_days('2015-02-01')),
//PARTITION city_01032015 VALUES LESS THAN (to_days('2015-03-01')),
//PARTITION city_01042015 VALUES LESS THAN (to_days('2015-04_01')),
//PARTITION city_max VALUES LESS THAN (MAXVALUE));

        //transactions_171225
        //charge_log_170220
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 25);

        System.out.println("" + cal.getTime());
        System.out.println("" + cal.get(Calendar.YEAR));

        StringBuffer sb = new StringBuffer();

        while (cal.get(Calendar.YEAR) <= 2020) {
            System.out.println("----- " + sdf.format(cal.getTime()));
//            sb.append("CREATE TABLE transactions_").append(sdf.format(cal.getTime())).append(" LIKE transactions_171225;");
            sb.append("CREATE TABLE charge_log_").append(sdf.format(cal.getTime())).append(" LIKE charge_log_170220;");
            sb.append("\n");
            cal.add(Calendar.DATE, 7);
        }

        System.out.println("" + sb.toString());

    }
}
