using System;
using System.Collections.Generic;
using System.Net;
using System.IO;
//this import please resolve here: http://james.newtonking.com/pages/json-net.aspx
//note, this is one of many possible JSON helpers
using Newtonsoft.Json;


/**
 *  @author peterk
 *  
 *  You are allowed to use it and modify.
 * 
 *  Example C# client for DPOST.
 *  
 *  It basically sends JSON HTTP request to the DPOST server, located at localhost:9000/dpost
 *  
 *  More about DPOST clients:
 *  
 *  clients.pdf
 * 
 * 
 * */
namespace dpost.csharp.client
{
    

    public class DpostClient
    {

        private String uriString;
        
        public DpostClient()
        {
        }

        public DpostClient(String dpostServerUrl, int dpostServerPort)
        {
            this.uriString = "http://" + dpostServerUrl + ":" + dpostServerPort + "/dpost";
        }

        private class DpostMail
        {
            public Dictionary<String, String> subjMap{ get; set; }
            public Dictionary<String, String> bodyMap { get; set; }
            public String toMail { get; set; }
            public String template { get; set; }
            public String lang { get; set; }

            public DpostMail() { }
            public DpostMail(Dictionary<String, String> subjMap, Dictionary<String, String> bodyMap, String toMail, String template, String lang)
            {
                this.subjMap = subjMap;
                this.bodyMap = bodyMap;
                this.toMail = toMail;
                this.template = template;
                this.lang = lang;
            }

        }

        public String sendMail(Dictionary<String, String> subjMap, Dictionary<String, String> bodyMap, String toMail, String template, String lang)
        {

            DpostMail mail = new DpostMail(subjMap, bodyMap, toMail, template, lang);

            string json = JsonConvert.SerializeObject(mail);

            String request = json;

            return HttpPost(this.uriString, request);

        }

        
        public String HttpPost(String URI, String parameters)
        {
            WebRequest req = WebRequest.Create(URI);
            //req.ContentType = "application/x-www-form-urlencoded";
            req.ContentType = "application/json; charset=UTF-8";
            req.Method = "POST";
            byte[] bytes = System.Text.Encoding.UTF8.GetBytes(parameters);
            req.ContentLength = bytes.Length;
            Stream os = req.GetRequestStream();
            os.Write(bytes, 0, bytes.Length); 
            os.Close();
            WebResponse resp = req.GetResponse();
            if (resp == null) return null;
            StreamReader sr = new StreamReader(resp.GetResponseStream());
            return sr.ReadToEnd().Trim();
        }

        


    }
}
