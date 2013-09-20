from google.appengine.ext import db
from push import push_to_user

import webapp2
import json
import urllib2

class BroadcastRequestHandler(webapp2.RequestHandler):
    def put(self, jid):
        # Create the Keys for the Broadcaster               
        key_broadcaster_jid = db.Key.from_path('User', jid)

        # Retrieve the User object for the Broadcaster
        broadcaster = db.get(key_broadcaster_jid)

        self.response.write("body = " + self.request.body + "\n")

        if broadcaster is None:
            self.response.write(json.dumps({"error_message": "User %s doesn't exist on hang server" % jid}))
            return;

        # Grab the POST request parameters and put them into variables.
        param_target = self.request.get_all('target')        

        for broadcastee_jid in param_target:
            # Create the Key for this Broadcastee
            key_broadcastee_jid = db.Key.from_path('User', broadcastee_jid)

            # Retrieve the User object for this Broadcastee
            broadcastee = db.get(key_broadcastee_jid)

            if broadcastee is None:
                self.response.write(json.dumps({'error_message': "User %s doesn't exist on hang server" % param_target}));
                return

            # Add broadcaster jid to broadcastee's incoming_broadcasts
            if key_broadcaster_jid not in broadcastee.incoming_broadcasts:
                broadcastee.incoming_broadcasts.append(key_broadcaster_jid)
            else: 
                self.response.write("%s is already receiving Broadcasts from %s.\n" % (broadcastee.first_name, broadcaster.first_name))
                continue    

            # Add broadcastee jid to broadcaster's outgoing_broadcasts
            if key_broadcastee_jid not in broadcaster.outgoing_broadcasts:
                broadcaster.outgoing_broadcasts.append(key_broadcastee_jid)
            else: 
                self.response.write(json.dumps({"error message": "%s is already broadcasting to %s.\n" % (broadcaster.first_name, broadcastee.first_name)}))
                continue

            self.response.write(broadcaster.first_name + " is now broadcasting to " + broadcastee.first_name + "\n")

            broadcastee.put()
            broadcaster.put()

            push_to_user(broadcastee, broadcaster, 'new_broadcast')

        return

    def delete(self, jid):
        try:
            # Grab the DELETE request parameters and put them into variables.
            param_target = self.request.get_all('target')

            # Create the Keys for the Broadcaster               
            key_broadcaster_jid = db.Key.from_path('User', jid)
            
            # Retrieve the User object for the User
            broadcaster = db.get(key_broadcaster_jid)

            if broadcaster is None:
                self.response.write(json.dumps({'error_message': "User %s doesn't exist on hang server" % param_target}));
                return

            for target in param_target:
            	# Create the Key for this Broadcastee
            	key_broadcastee_jid = db.Key.from_path('User', target)
            	
            	# Retrieve the User object for this Broadcastee
                broadcastee = db.get(key_broadcastee_jid)

            	broadcastee.incoming_broadcasts.remove(key_broadcaster_jid)
            	broadcaster.outgoing_broadcasts.remove(key_broadcastee_jid)

				# Remove broadcaster jid from broadcastee's seen proposals (if present)
                if jid in broadcastee.proposals_seen_jids:
	                broadcastee.proposals_seen_jids.remove(jid)
                else:
	            	self.response.write(jid + " is not in " + broadcastee.first_name + "'s seen proposals\n")
                broadcastee.put()
                broadcaster.put()

                if key_broadcaster_jid not in broadcastee.incoming_broadcasts and key_broadcastee_jid not in broadcaster.outgoing_broadcasts:
                	self.response.write("%s is no longer receiving Broadcasts from %s.\n" % (broadcastee.first_name, broadcaster.first_name))
                else:
                	self.response.write("There is an inconsistency in %s and %s's Broadcast data!\n" % (broadcastee.first_name, broadcaster.first_name))
                	return

	            # Tell the user.                
                push_to_user(broadcastee, broadcaster, 'tickle')

            # Girum said not to but "ehh"
            push_to_user(broadcaster, broadcastee, 'tickle')
            
        except (TypeError, ValueError):
            # If we couldn't grab the DELETE request parameters, then show an error.
            self.response.write('Invalid inputs: Couldn\'t grab the DELETE request parameters.\n')
            return
        return

# class BroadcastRequestHandler(webapp2.RequestHandler):
#     def put(self, jid):
#         try:
#             # Grab the PUT request parameters and put them into variables.
#             param_target = self.request.get('target')
            
#             # Create the Keys for the Entities we want using the JIDs that were passed in.               
#             key_broadcaster_jid = db.Key.from_path('User', jid)
#             key_broadcastee_jid = db.Key.from_path('User', param_target)

#             # Retrieve the User objects for each
#             broadcaster = db.get(key_broadcaster_jid)
#             if broadcaster is None:
#                 self.response.write(json.dumps({"error_message": "User %s doesn't exist on hang server" % jid}))
#                 return;
            
#             broadcastee = db.get(key_broadcastee_jid)
#             if broadcastee is None:
#                 self.response.write(json.dumps({'error_message': "User %s doesn't exist on hang server" % param_target}));
#                 return

#             # Add broadcaster jid to broadcastee's incoming_broadcasts
#             if key_broadcaster_jid not in broadcastee.incoming_broadcasts:
#                 broadcastee.incoming_broadcasts.append(key_broadcaster_jid)
#             else: 
#                 self.response.write("%s is already receiving Broadcasts from %s.\n" % (broadcastee.first_name, broadcaster.first_name))
#                 return
                
#             # Add broadcastee jid to broadcaster's outgoing_broadcasts
#             if key_broadcastee_jid not in broadcaster.outgoing_broadcasts:
#                 broadcaster.outgoing_broadcasts.append(key_broadcastee_jid)
#             else: 
#                 self.response.write(json.dumps({"error message": "%s is already broadcasting to %s.\n" % (broadcaster.first_name, broadcastee.first_name)}))
#                 return

#             broadcastee.put()
#             broadcaster.put()

#             push_to_user(broadcastee, broadcaster, 'new_broadcast')
            
#             self.response.write(json.dumps(broadcastee.get_partial_json()))
            
            
#         except (TypeError, ValueError):
#             # If we couldn't grab the PUT request parameters, then show an error.
#             self.response.write('Invalid inputs: Couldn\'t grab the PUT request parameters.\n')
#             return

#     def delete(self, jid):
#         try:
#             # Grab the DELETE request parameters and put them into variables.
#             param_target = self.request.get('target')

#             # Create the Keys for the Entities we want using the JIDs that were passed in.               
#             key_broadcaster_jid = db.Key.from_path('User', jid)
#             key_broadcastee_jid = db.Key.from_path('User', param_target)

#             # Retrieve the User objects for each
#             broadcaster = db.get(key_broadcaster_jid)
#             broadcastee = db.get(key_broadcastee_jid)

#             # Remove broadcaster jid from broadcastee's incoming_broadcasts
#             broadcastee.incoming_broadcasts.remove(key_broadcaster_jid)
#             broadcaster.outgoing_broadcasts.remove(key_broadcastee_jid)

#             # Remove broadcaster jid from broadcastee's seen proposals (if present)
#             if jid in broadcastee.proposals_seen_jids:
#                 broadcastee.proposals_seen_jids.remove(jid)
#             else:
#                 self.response.write(jid + " is not in " + broadcastee.first_name + "'s seen proposals\n")

#             broadcastee.put()
#             broadcaster.put()

#             # Tell the user.                
#             push_to_user(broadcastee, broadcaster, 'tickle')
#             # Girum said not to but "ehh"
#             push_to_user(broadcaster, broadcastee, 'tickle')

#             if key_broadcaster_jid not in broadcastee.incoming_broadcasts and key_broadcastee_jid not in broadcaster.outgoing_broadcasts:
#                 self.response.write("%s is no longer receiving Broadcasts from %s.\n" % (broadcastee.first_name, broadcaster.first_name))
#             else:
#                 self.response.write("There is an inconsistency in %s and %s's Broadcast data!\n" % (broadcastee.first_name, broadcaster.first_name))
#                 return
            
#         except (TypeError, ValueError):
#             # If we couldn't grab the DELETE request parameters, then show an error.
#             self.response.write('Invalid inputs: Couldn\'t grab the DELETE request parameters.\n')
#             return