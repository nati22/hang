from google.appengine.ext import db
from gcm import GCM, GCMNotRegisteredException, GCMUnavailableException

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
    # return 

def push_to_user(user, sender, type):
    data = {'type': type, 'nudger': sender.first_name, 'toFn' : user.first_name, 'toLn' : user.last_name}

    gcm = GCM(API_KEY)

    response = gcm.json_request(registration_ids=user.gcm_registration_ids, data=data)

    # Handling errors
    if 'errors' in response:
        for error, reg_ids in response['errors'].items():
            # Check for errors and act accordingly
            if error is 'NotRegistered':
                # Remove reg_ids from database
                for reg_id in reg_ids:
                    user.gcm_registration_ids.remove(reg_id)
                    user.put()
    if 'canonical' in response:
        for reg_id, canonical_id in response['canonical'].items():
            # Repace reg_id with canonical_id in your database
            user.gcm_registration_ids.remove(reg_id)
            user.gcm_registration_ids.append(canonical_id)
            user.put()