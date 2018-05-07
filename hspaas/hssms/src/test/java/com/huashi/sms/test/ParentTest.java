package com.huashi.sms.test;


public class ParentTest {

    static abstract class Parent {
        
        protected void t() {
            System.out.println("I'm father!");
        }
    }
    
    static class Son extends Parent{

        @Override
        protected void t() {
            System.out.println("I'm son!");
        }
        
    }
    
    public static void main(String[] args) {
        Parent son = new Son();
        son.t();
    }
}
