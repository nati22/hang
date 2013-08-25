import webapp2

from push import tickle_users
from google.appengine.ext import db

class StatusRequestHandler(webapp2.RequestHandler):
    def put(self, jid):
        try:
            # Grab the PUT request parameters and put them into variables.
            param_color = self.request.get('color')
            param_exp = self.request.get('exp')
            param_status_text = self.request.get('text')
            
            # Create the Key for the User query we're about to perform
            key_user = db.Key.from_path('User', jid)
            
            # Retrieve the User object using the Key
            user = db.get(key_user)
            
            # Modify the User's fields
            # TODO: Sanity check statuses to ensure that they're a valid color. 
            user.status_color = param_color
            user.status_expiration_date = param_exp
            user.status_description = param_status_text
            
            # Save it back into the datastore.
            user.put()

            # Make list of Users to tickle
            users = []
            for broadcastee_key in user.outgoing_broadcasts:
                users.append(db.get(broadcastee_key))

            tickle_users(users, user)
            
            # Tell the user.
            self.response.write("Updated %s's status to %s.\n" % (user.first_name, user.status_color))
            
            
        except (TypeError, ValueError):
            # If we had an error, then show it
            self.response.write('Invalid inputs')
            return
        
    def delete(self, jid):
        
        key_user = db.Key.from_path('User', jid)
        
        user = db.get(key_user)
        
        user.status_color = None
        user.status_expiration_date = None

        user.put()

        # Make list of Users to tickle
        users = []
        for broadcastee_key in user.outgoing_broadcasts:
            users.append(db.get(broadcastee_key))

        tickle_users(users, user)        
        
        self.response.write("Deleted %s's status.\n" % user.first_name)    
        
            