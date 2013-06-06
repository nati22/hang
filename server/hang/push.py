from google.appengine.ext import db
from gcm import GCM

import urllib2
import User
import webapp2
import json

API_KEY = "AIzaSyAJtklyMjzyHNfRC2Ratkoh3ziFodaZWZU"

def tickle_users(users, sender):
    if isinstance(users, list):
        for user in users:
            push_to_user(user, sender, 'tickle')

            # do work
    # this should return some type of Error, I'll figure it out later
    #return 

def push_to_user(user, sender, type):
    data = {'type': type, 'nudger': sender.first_name}

    gcm = GCM(API_KEY)

    for reg_id in user.gcm_registration_ids:
        gcm.plaintext_request(registration_id=reg_id, data=data)
    # As of now this RequestHandler will clear Notifications 
    # from all devices where the User is signed in
    #class NotificationReceivedRequestHandler(webapp2.RequestHandler):
    #    def put(self, jid):