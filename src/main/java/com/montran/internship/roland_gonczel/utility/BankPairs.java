package com.montran.internship.roland_gonczel.utility;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BankPairs {

    public static List<Pair<String, String>> getBanks(){
        ArrayList<Pair<String,String>> list = new ArrayList<Pair<String,String>>();
        list.add(new Pair<String,String>("RBC Royal Bank", "RBTTTTPX"));
        list.add(new Pair<String,String>("Republic Bank", "RBNKTTPX"));
        list.add(new Pair<String,String>("Scotiabank", "NOSCTTPS"));



        return list;

    }
}
