import urllib2
import webapp2
import json

def make_request(self):
    self.response.out.write("made it into make_request")
    
    json_data = {"collapse_key" : "msg", 
                 "data" : {
                           "data": "xyz",
               }, 
            "registration_ids": ['APA91bGi13Rg2l_*******beNOGxxP25o0hmtpg'],
    }


    url = 'https://android.googleapis.com/gcm/send'
    # this is our key hangapp
    myKey = "AIzaSyBa4tOm2_Pb8S0xOgB8Hswk-y9gQrAAhis" 
    data = json.dumps(json_data)
    headers = {'Content-Type': 'application/json', 'Authorization': myKey} # prefix to mykey: 'key=%s' % 
    req = urllib2.Request(url, data, headers)
    f = urllib2.urlopen(req)
    response = json.loads(f.read())


    self.response.out.write(json.dumps(response,sort_keys=True, indent=2) )    