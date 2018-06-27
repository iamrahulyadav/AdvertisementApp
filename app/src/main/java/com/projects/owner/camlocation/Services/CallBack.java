package com.projects.owner.camlocation.Services;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class CallBack {
    Object caller;
    String method;

    public CallBack(Object callie, String name){
        this.caller = callie;
        this.method = name;
    }

    public void invoke(Object obj){

        Method method;
        ArrayList<Class> partTypes = new ArrayList<>();
        partTypes.add(Object.class);
        partTypes.add(Object.class);

        ArrayList<Object> args = new ArrayList<>();
        args.add(caller);
        args.add(obj);

        try {

            method = caller.getClass().getMethod(this.method, partTypes.toArray(new Class[partTypes.size()]));
            method.invoke(caller, args.toArray(new Object[args.size()]));
            // clear the data array which used before
            partTypes.clear();
            args.clear();
            partTypes = null;
            args = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        destroyData();
    }

    private void destroyData(){
        caller = null;
        method = null;
    }
}
