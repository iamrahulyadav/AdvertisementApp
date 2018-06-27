package com.projects.owner.camlocation.Services;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projects.owner.camlocation.Activities.CamLocationApplication;
import com.projects.owner.camlocation.Utils.Constants;
import com.projects.owner.camlocation.Utils.Logger;
import com.projects.owner.camlocation.Utils.NetworkUtil;
import com.projects.owner.camlocation.Utils.NonUiWorkerThread;
import com.projects.owner.camlocation.Utils.ServiceProgressDialog;
import com.projects.owner.camlocation.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class BaseService {

    private static final GsonBuilder builder = new GsonBuilder();
    private static Gson gson;
    private final String SERVER_PROB_MSG = "There is some problem with server, We can't connect you right now, Please try again later.";
    private AQuery mAQuery;
    private static Activity mContext;
    private int DEFAULT_TIME_OUT = 60 * 1000;
    private static final String TZ_KEY = "&tz=", TZ_KEY_END = "tz=";
    private static final String PRACTICE_KEY_END = "&practice_id=";

    /**
     * @param act
     */
    public BaseService(Activity act) {
        mAQuery = new AQuery(act);
        mContext = act;
//        builder.registerTypeAdapter(InboxAttachmentModel.class,
//                new InboxAttachmentDeserializer());
        gson = builder.create();


    }

    public static void dismissProgressDialog() {
        ServiceProgressDialog.dismissDialog();
    }

    /**
     * @param sUrl
     * @param callBack
     * @param model
     * @param showProgress
     */
    public void get(String sUrl, final CallBack callBack, final Object model, final boolean showProgress) {


        if (!checkNetWorkAvailable(model,callBack)){
            return;
        }

        handleProgressBar(showProgress);

        final Handler postDataHandler = new Handler() {
            /**
             *
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg != null && msg.obj != null) {
                    callBack.invoke(msg.obj);
                    dismissProgressBar(showProgress);
                }

            }
        };
        // Tag used to cancel the request
        final String serviceTag = callBack.caller.getClass().getSimpleName();
        final String url = sUrl;
        Logger.log("Get url: " + url + " " + showProgress);
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    /**
                     *
                     * @param response
                     */
                    @Override
                    public void onResponse(final JSONObject response) {
                        if (response == null) {
                            dismissProgressBar(showProgress);
                            callBack.invoke(model);
                            return;
                        }

                        try {
                            if (validateToken(response)) {
                                dismissProgressBar(showProgress);
                                callBack.invoke(model);
                                return;
                            }
                             Logger.log("Get Response :: " + response.toString());
                            final NonUiWorkerThread nonUiWorkerThread = new NonUiWorkerThread(serviceTag);
                            Runnable dataTask = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Object dataObj = gson.fromJson(response.toString(), model.getClass());
                                        Message msg = new Message();
                                        msg.obj = dataObj;
                                        postDataHandler.sendMessage(msg);
                                        nonUiWorkerThread.quit();

//                                        mResultCallback.notifySuccess(response);


                                    } catch (Exception ignore) {
                                        Message msg = new Message();
                                        msg.obj = model;
                                        postDataHandler.sendMessage(msg);
                                        ignore.printStackTrace();
                                    }
                                }
                            };
                            nonUiWorkerThread.start();
                            nonUiWorkerThread.prepareHandler();
                            nonUiWorkerThread.postTask(dataTask);

                        } catch (Exception ignore) {
                            ignore.printStackTrace();
                            dismissProgressBar(showProgress);
                            callBack.invoke(model);
                        }
                        dismissProgressBar(showProgress);
                    }
                }, new Response.ErrorListener() {
                    /**
                     *
                     * @param error
                     */
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message= error.getMessage();
                     //   Logger.log("Error: " + message);

                        // hide the progress dialog
                        dismissProgressBar(showProgress);
                        try {
                            callBack.invoke(model);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };

        // Adding request to request queue
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CamLocationApplication.getInstance().addToRequestQueue(jsonObjReq, serviceTag);
            }
        };
        new Thread(runnable).start();

        return;

    }

    /**
     * @param showProgress
     */
    public static void handleProgressBar(boolean showProgress) {
        if (showProgress) {
            ServiceProgressDialog.showDialog(mContext);
        }
    }

    /**
     * @param showProgress
     */
    public void dismissProgressBar(boolean showProgress) {

        if (showProgress) {
            ServiceProgressDialog.dismissDialog();
    }
    }

    /**
     * @param json
     * @return
     */
    private boolean validateToken(JSONObject json) {
        try {
            int code = json.getJSONObject("response").getInt("code");
            if (code == 0) {
                return false;
            }
            if (code == -2) {
              //  NetworkUtil.showStatusDialog(mContext, mContext.getResources().getString(R.string.app_name), "" + json.getJSONObject("response").getString("msg").toString(), true);
                return true;
            } else if (code == 500) {
              //  Logger.log("there is something wrong with service");
                return true;
            } else if (code == 400 || code == 401) {
             //   Logger.log("there is something wrong with url not found response");
                return true;
            }
            else if (code == 200)
            {
                return true;
            }
            ServiceProgressDialog.dismissDialog();

        } catch (JSONException je) {

        }
        return false;
    }


    /**
     * @param sUrl
     * @param params
     * @param callBack
     * @param model
     * @param showProgress
     */
    public void post(String sUrl, final HashMap<String, String> params, final CallBack callBack, final Object model, final boolean showProgress) {

    /*    if (!checkNetWorkAvailable(model,callBack)){
            return;
        }*/
        handleProgressBar(showProgress);
        // Tag used to cancel the request
        //final String url = getTimeZoneOffSet(sUrl);
        final String url = sUrl;
        Logger.log("post url: " + url);
        Logger.log("post params: " + params);
        final String serviceTag = callBack.caller.getClass().getSimpleName();
        final JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    /**
                     *
                     * @param response
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.log("JSON Response:: " + response);
                        params.clear();
                        Object obj = model;
                        if (response != null) {
                            try {
                                if (validateToken(response)) {
                                    dismissProgressBar(showProgress);
                                    callBack.invoke(model);
                                   // setLogoutDirection(url);
                                    return;
                                }

                                obj = gson.fromJson(response.toString(), obj.getClass());
                                callBack.invoke(obj);

//                                mResultCallback.notifySuccess(response);

//                                Log.d("xxx", "" + obj.toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                                callBack.invoke(model);
                            }
                            dismissProgressBar(showProgress);
                        } else {
                            dismissProgressBar(showProgress);
                            callBack.invoke(model);
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            /**
             *
             * @param error
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                params.clear();
                String message="";
                if(error.getMessage() == null)
                {
                    message = "UnknownHostException";
                }
                else {
                    try {
                        callBack.invoke(model);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    message = error.getMessage().toString();
                    Log.d("xxxxx", "" + message);
                }
                if (!message.isEmpty() && message.contains("UnknownHostException")){
                    NetworkUtil.showStatusDialog(mContext, "Server Error", "Please try again later...", false);
                }
                else {
                    if (error.networkResponse != null && error.networkResponse.statusCode != Constants.STATUS_OK) {
                        Utils.showToast(mContext, SERVER_PROB_MSG);
                    }
                }
                dismissProgressBar(showProgress);
                callBack.invoke(model);
//                if (error != null) {
//                    error.printStackTrace();
//                }

                Log.d("xxxxx", ""+error.toString());

                String json = null;

                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){

                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if(json != null) displayMessage(json);

                    }
                    //Additional cases

                if(error.networkResponse != null && error.networkResponse.data != null){
                    VolleyError err = new VolleyError(new String(error.networkResponse.data));
                    Log.d("xx", err.toString());
                    Log.d("xx", error.networkResponse.data.toString());
                }

            }

            public String trimMessage(String json, String key){
                String trimmedString = null;

                try{
                    JSONObject obj = new JSONObject(json);
                    trimmedString = obj.getString(key);
                } catch(JSONException e){
                    e.printStackTrace();
                    return null;
                }

                return trimmedString;
            }

            //Somewhere that has access to a context
            public void displayMessage(String toastString){
                Toast.makeText(mContext, toastString, Toast.LENGTH_LONG).show();
            }

        }) {
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        // Adding request to request queue
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CamLocationApplication.getInstance().addToRequestQueue(postRequest, serviceTag);

        return;
    }




    /**
     *
     * @param model
     * @param callBack
     * @return
     */
    private boolean checkNetWorkAvailable(Object model, CallBack callBack) {
     //   resetGeneralModel(model);
        if (!NetworkUtil.isInternetConnected(mContext)) {
            NetworkUtil.internetFailedDialog(mContext);
            try {
                callBack.invoke(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

}
