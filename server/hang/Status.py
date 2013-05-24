import webapp2

from google.appengine.ext import db

class StatusRequestHandler(webapp2.RequestHandler):
    def put(self, jid):
        try:
            # Grab the PUT request parameters and put them into variables.
            param_color = self.request.get('color')
            param_exp = self.request.get('exp')
            
            # Create the Key for the User query we're about to perform
            key_user = db.Key.from_path('User', jid)
            
            # Retrieve the User object using the Key
            user = db.get(key_user)
            
            # Modify the User's fields
            # TODO: Sanity check statuses to ensure that they're a valid color. 
            user.status_color = param_color
            user.status_expiration_date = param_exp
            
            # Save it back into the datastore.
            user.put()
            
            # Tell the user.
            self.response.write("Updated %s's status to %s.\n" % (user.first_name, user.status_color))
            
            
        except (TypeError, ValueError):
            # If we had an error, then show it
            self.response.write('Invalid inputs')
            return
            