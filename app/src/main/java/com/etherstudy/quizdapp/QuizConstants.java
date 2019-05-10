package com.etherstudy.quizdapp;

import java.math.BigInteger;

public class QuizConstants {

//    public static final String SERVER_IP = "http://101.101.161.251:8001";
    public static final String SERVER_IP = "http://52.43.10.165:8001";
    public static final String ETH_NODE_URL = "https://ropsten.infura.io/v3/8f79bace6d6440e3a40300868915d9ec";

    public static final String QUIZ_CONTRACT_ADDRESS = "0xf9da1791376e03da8cf6636aa65cfb72dc77169b";
    public static final String TOKEN_HOLDER_ADDRESS = "0x2a02cd212dbc13c2457f1937b5c3ffe190970bd7";
    public static final String TOKEN_HOLDER_PK = "570649F60ACCF43702A258562E1677DDB6D701123427A6827DE1A53DF91EB6F8";
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(2100000);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(2000000000);
    public static final int BIG_DEFAULT_APPROVE = 100000000;

    public static final int decimal = 18;
    public static final BigInteger CONTRACT_DECIMAL = BigInteger.TEN.pow(decimal);
    public static final BigInteger CONTRACT_DEFAULT_APPROVE = CONTRACT_DECIMAL.multiply(new BigInteger(String.valueOf(BIG_DEFAULT_APPROVE)));

}
