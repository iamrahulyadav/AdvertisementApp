package com.projects.owner.camlocation;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebServiceHandler {

    private static final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        private static final String SOAP_ADDRESS = "http://cameraappcloud.cloudapp.net/CameraWCFService.svc";
//    private static final String SOAP_ADDRESS = "http://172.16.2.105:6543/Service.asmx";
    private String SOAP_ACTION;
    private IWebServiceHandler eventHandler;
    private SoapObject request;


    public WebServiceHandler() {
    }

    public WebServiceHandler(IWebServiceHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void executeProcedure() throws Exception {
        new AsyncTask<Void, Void, Object>() {
            @Override
            protected void onPreExecute() {
                eventHandler.StartedRequest();
            }

            @Override
            protected Object doInBackground(Void... params) {
                return work();
            }

            @Override
            protected void onPostExecute(Object result) {
                eventHandler.CodeEndedRequest();
                if (result != null) {
                    eventHandler.CodeFinished(SOAP_ACTION, result);
                }
            }
        }.execute();
    }


    ////////if different number of params of this function make new
    public void setValues(String SOAP_ACTION, String OPERATION_NAME, PropertyInfo propertyInfo1, PropertyInfo propertyInfo2, String params1, String params2) {
        this.SOAP_ACTION = SOAP_ACTION;
        request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        ///////add new params
        request.addProperty(propertyInfo1, params1);
        request.addProperty(propertyInfo2, params2);
    }

    public void setValues(String SOAP_ACTION, String OPERATION_NAME, PropertyInfo propertyInfo1, PropertyInfo propertyInfo2, PropertyInfo propertyInfo3, PropertyInfo propertyInfo4, PropertyInfo propertyInfo5, PropertyInfo propertyInfo6, PropertyInfo propertyInfo7, PropertyInfo propertyInfo8, PropertyInfo propertyInfo9,
                          String lat, String lng, int CAMPAIGN_ID, int CITY_ID, String location, byte[] byteArrayl, byte[] byteArraym, byte[] byteArrays, int pictureOrentation) {
        this.SOAP_ACTION = SOAP_ACTION;
        request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        request.addProperty(propertyInfo1, lat);
        request.addProperty(propertyInfo2, lng);
        request.addProperty(propertyInfo3, CAMPAIGN_ID);
        request.addProperty(propertyInfo4, CITY_ID);
        request.addProperty(propertyInfo5, location);
        request.addProperty(propertyInfo6, byteArrayl);
        request.addProperty(propertyInfo7, byteArraym);
        request.addProperty(propertyInfo8, byteArrays);
        request.addProperty(propertyInfo9, pictureOrentation);
    }

    public void setValues(String soap_action, String operation_name) {
        this.SOAP_ACTION = soap_action;
        request = new SoapObject(WSDL_TARGET_NAMESPACE, operation_name);
    }

    public void setValues(String SOAP_ACTION, String OPERATION_NAME, PropertyInfo propertyInfo1, PropertyInfo propertyInfo2, PropertyInfo propertyInfo3, PropertyInfo propertyInfo4, PropertyInfo propertyInfo5, PropertyInfo propertyInfo6, PropertyInfo propertyInfo7,
                          String cp_code, String year_code, String opn_from, String opn_to, String prj_code, String coa_code, String coa_code1) {
        this.SOAP_ACTION = SOAP_ACTION;
        request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        ///////add new params
        request.addProperty(propertyInfo1, cp_code);
        request.addProperty(propertyInfo2, year_code);
        request.addProperty(propertyInfo3, opn_from);
        request.addProperty(propertyInfo4, opn_to);
        request.addProperty(propertyInfo5, prj_code);
        request.addProperty(propertyInfo6, coa_code);
        request.addProperty(propertyInfo7, coa_code1);
    }


    @Nullable
    private Object work() {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.encodingStyle = SoapSerializationEnvelope.XSD;
        new MarshalBase64().register(envelope);
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object retObj = envelope.bodyIn;
            if (!(retObj instanceof SoapFault)) {
                return retObj;
            } else {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.CodeFinishedWithException(ex, "");
                return null;
            }
//            return envelope.getResponse();
        } catch (Exception e) {
            eventHandler.CodeFinishedWithException(e, "");
            Log.e("b", "exception is : " + e.toString());
            return null;
        }
    }


}