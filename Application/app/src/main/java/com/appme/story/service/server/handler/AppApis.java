package com.appme.story.service.server.handler;

import java.util.HashMap;
import org.json.JSONObject;
import com.appme.story.service.server.TinyWebServer;
import org.json.JSONException;

/**
 *
 * @author cis
 */
public class AppApis {

    public AppApis() {
    }

    public String helloworld(HashMap qparms) {
        //demo of simple html webpage from controller method 
        TinyWebServer.CONTENT_TYPE = "text/html";
        return "<html><head><title>Simple HTML and Javascript Demo</title>\n" +
            "  <script>\n" +
            "  \n" +
            "</script>\n" +
            "  \n" +
            "  </head><body style=\"text-align:center;margin-top: 5%;\" cz-shortcut-listen=\"true\" class=\"\">\n" +
            "    <h3>Say Hello !</h3>\n" +
            "<div style=\"text-align:center;margin-left: 29%;\">\n" +
            "<div id=\"c1\" style=\"width: 100px;height: 100px;color: gray;background: gray;border-radius: 50%;float: left;\"></div>\n" +
            "<div id=\"c2\" style=\"width: 100px;height: 100px;color: gray;background: yellow;border-radius: 50%;float: left;\"></div>\n" +
            "<div id=\"c3\" style=\"width: 100px;height: 100px;color: gray;background: skyblue;border-radius: 50%;float: left;\"></div>\n" +
            "<div id=\"c4\" style=\"width: 100px;height: 100px;color: gray;background: yellowgreen;border-radius: 50%;float: left;\"></div>\n" +
            "<div id=\"c5\" style=\"width: 100px;height: 100px;color: gray;background: red;border-radius: 50%;position: ;position: ;float: left;\" class=\"\"></div></div>\n" +
            "  </body></html>";
    }

    public String update(HashMap qparms) {
        try {
            JSONObject json = new JSONObject();
            json.put("versionCode", 2);
            json.put("versionName", 1.0);
            json.put("content", "1.Camera Server Yang Lebih Baik,\n 2.");
            json.put("minSupport", 1);
            json.put("url", "");    
            return json.toString();
        } catch (JSONException e) {

        }
        return null;
    }

    public String simplejson(HashMap qparms) {
        //simple json output demo from controller method
        String json = "{\"name\":\"sonu\",\"age\":29}";
        return json.toString();
    }

    public String simplegetparm(HashMap qparms) {
        /*
         qparms is hashmap of get and post parameter

         simply use qparms.get(key) to get parameter value
         user _POST as key for post data
         e.g to get post data use qparms.get("_POST"), return will be post method 
         data
         */

        System.out.println("output in simplehelloworld " + qparms);
        String p="";
        if (qparms != null) {
            p = qparms.get("age") + "";
        }
        String json = "{\"name\":\"sonu\",\"age\":" + p + ",\"isp\":yes}";
        return json.toString();
    }


    //implement web callback here and access them using method name
}

