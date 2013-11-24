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

# Eventually tickle users should be switched to the more generic push_to_users
def push_to_users(users, sender, type):
    if isinstance(users, list):
        for user in users:
            push_to_user(user, sender, type)
            # do work
    # this should return some type of Error, I'll figure it out later
    # return 

def push_new_chats_to_users(self, users, host, sender):
    if isinstance(users, list):
        for user in users:
            push_new_chats_to_user(user, sender, host, 'new_chat')

        self.response.write('successful\n')

def push_new_chats_to_user(user, sender, host, type):
    data = {'type': type,'target_jid' : user.key().name(), 'sender_jid': sender.key().name(), 'host_jid' : host.key().name()}

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



def push_to_user(user, sender, type):
    data = {'type': type, 'target_jid' : user.key().name(), 'nudger': sender.first_name, 'toFn' : user.first_name, 'toLn' : user.last_name}

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