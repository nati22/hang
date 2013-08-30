import webapp2
from push import tickle_users
from google.appengine.ext import db

class ProposalRequestHandler(webapp2.RequestHandler):
    def put(self, jid):
        try:
        #    self.response.write("test\n")
            # Grab the PUT request parameters and put them into variables.
            param_description = self.request.get('des')
            param_location = self.request.get('loc')
            param_time = self.request.get('time')
            
            # Create the Key for the User query we're about to perform
            key_user = db.Key.from_path('User', jid)
            
            # Retrieve the User object using the Key
            user = db.get(key_user)
            
            # Modify the User's fields
            user.proposal_description = param_description
            user.proposal_location = param_location
            user.proposal_time = param_time
            
            # Save it back into the datastore.
            user.put()

            # Make list of Users to tickle
            users = []

            for broadcastee_key in user.outgoing_broadcasts:
                users.append(db.get(broadcastee_key))
                users.append(user)

            tickle_users(users, user)
            
            # Tell the user.
            self.response.write("Updated %s's proposal to %s.\n" % (user.first_name, user.proposal_description))
            
        except (TypeError, ValueError):
            # If we had an error, then show it
            self.response.write('Invalid inputs\n')
            return
        
    def delete(self, jid):
        
        key_user = db.Key.from_path('User', jid)
        
        user = db.get(key_user)
        
        user.proposal_description = None
        user.proposal_location = None
        user.proposal_time = None
        user.proposal_interested = []
        user.proposal_confirmed = []
        user.proposal_interested_jids = []
        user.proposal_confirmed_jids = []

        user.put()

        # Modify Broadcastees proposals_seen_jids fields if they were interested
        for key in user.outgoing_broadcasts:
            broadcastee = db.get(key)
            if jid in broadcastee.proposals_seen_jids:
                broadcastee.proposals_seen_jids.remove(jid)
                broadcastee.put()
            else:
                self.response.write(key.name() + " not in " + broadcastee.first_name + "'s seen proposals\n")


        # Make list of Users to tickle
        users = []
        for broadcastee_key in user.outgoing_broadcasts:
            users.append(db.get(broadcastee_key))
            users.append(user)

        tickle_users(users, user)
        
        self.response.write("Deleted %s's proposal.\n" % user.first_name)

class InterestedRequestHandler(webapp2.RequestHandler):
    def put(self, jid):
        # Grab the PUT request parameter (the Broadcasting User)
        param_target = self.request.get('target')

        # Create a Key for the User whose Broadcast is being affected
        key_user = db.Key.from_path('User', param_target)

        # Retrieve the User object for the Broadcasting User
        broadcasting_user = db.get(key_user)

        # Create a Key for the Interested User to be added
        key_interested = db.Key.from_path('User', jid)

        # Check if Interested User is already Interested
        if key_interested not in broadcasting_user.proposal_interested:
            # Add Key of Interested User to Broadcasting User
            broadcasting_user.proposal_interested.append(key_interested) 

            # Add jid of Interested User to Broadcasting User
            broadcasting_user.proposal_interested_jids.append(jid)

            # Save it back into the datastore.
            broadcasting_user.put()

            # Make list of Users to tickle
            users = []
            for broadcastee_key in broadcasting_user.outgoing_broadcasts:
                users.append(db.get(broadcastee_key))
                users.append(broadcasting_user)

            tickle_users(users, broadcasting_user)            

            self.response.write("User " + jid + " is now Interested in " + broadcasting_user.first_name + "'s Proposal.\n")
        else:
            self.response.write("User " + jid + " was ALREADY Interested in " + broadcasting_user.first_name + "'s Proposal, idiot!!\n")
        return

    def delete(self, jid):
        # Grab the DELETE request parameter (the Broadcasting User)
        param_target = self.request.get('target')

        # Create a Key for the User whose Broadcast is being affected
        key_user = db.Key.from_path('User', param_target)

        # Retrieve the User object for the Broadcasting User
        broadcasting_user = db.get(key_user)

        # Create a Key for the Interested User to be removed
        key_interested = db.Key.from_path('User', jid)

        # Check if Interested User to be removed is Interested to begin with
        if key_interested in broadcasting_user.proposal_interested:
            # Remove Key of Interested User to Broadcasting User
            broadcasting_user.proposal_interested.remove(key_interested) 
            
            # Remove jid of Confirmed User from Broadcasting User
            broadcasting_user.proposal_interested_jids.remove(jid)

            # Save it back into the datastore.
            broadcasting_user.put()

            # Make list of Users to tickle
            users = []
            for broadcastee_key in broadcasting_user.outgoing_broadcasts:
                users.append(db.get(broadcastee_key))
                users.append(broadcasting_user)

            tickle_users(users, broadcasting_user) 

            self.response.write("User " + jid + " is no longer Interested in " + broadcasting_user.first_name + "'s Proposal.\n")
        else:
            self.response.write("User " + jid + " wasn't even Interested in " + broadcasting_user.first_name + "'s Proposal, idiot!!\n")
        return 

class ConfirmedRequestHandler(webapp2.RequestHandler):
    def put(self, jid):
        # Grab the PUT request parameter (the Broadcasting User)
        param_target = self.request.get('target')

        # Create a Key for the User whose Broadcast is being affected
        key_user = db.Key.from_path('User', param_target)
        
        # Retrieve the User object for the Broadcasting User
        broadcasting_user = db.get(key_user)

        # Create a Key for the Confirmed User to be added
        key_confirmed = db.Key.from_path('User', jid)

        # Check if Confirmed User is already Confirmed
        if key_confirmed not in broadcasting_user.proposal_confirmed:
            # Add Key of Confirmed User to Broadcasting User
            broadcasting_user.proposal_confirmed.append(key_confirmed) 

            # Add jid of Confirmed User to Broadcasting User
            broadcasting_user.proposal_confirmed_jids.append(jid)

            #################### Because Confirmed and Interested are mutually exclusive
            #################### We're removing you from Interested here as well, to prevent 
            #################### having two requests with one client toggle switch
            if key_confirmed in broadcasting_user.proposal_interested:
                broadcasting_user.proposal_interested.remove(key_confirmed)
                self.response.write("key " + jid + " removed from interested")
            else:
                self.response.write("key " + jid + " was never interested")

            if jid in broadcasting_user.proposal_interested_jids:
                broadcasting_user.proposal_interested_jids.remove(jid)
                self.response.write(jid + " removed from interested jids")
            else:
                self.response.write(jid + " was never interested jids")

            # Save it back into the datastore.
            broadcasting_user.put()

            # Make list of Users to tickle
            users = []
            for broadcastee_key in broadcasting_user.outgoing_broadcasts:
                users.append(db.get(broadcastee_key))
                users.append(broadcasting_user)

            tickle_users(users, broadcasting_user) 

            self.response.write("User " + jid + " is now Confirmed for " + broadcasting_user.first_name + "'s Proposal.\n")
        else:
            self.response.write("User " + jid + " was ALREADY Confirmed for " + broadcasting_user.first_name + "'s Proposal, idiot!!!\n")
        return

    def delete(self, jid):
        # Grab the DELETE request parameter (the Broadcasting User)
        param_target = self.request.get('target')

        # Create a Key for the User whose Broadcast is being affected
        key_user = db.Key.from_path('User', param_target)

        # Retrieve the User object for the Broadcasting User
        broadcasting_user = db.get(key_user)

        # Create a Key for the Confirmed User to be removed
        key_confirmed = db.Key.from_path('User', jid)

        # Check if Confirmed User to be removed is Confirmed to begin with
        if key_confirmed in broadcasting_user.proposal_confirmed:
            # Remove Key of Confirmed User from Broadcasting User
            broadcasting_user.proposal_confirmed.remove(key_confirmed)

            # Add jid of Confirmed User from Broadcasting User
            broadcasting_user.proposal_confirmed_jids.remove(jid)

            # Save it back into the datastore.
            broadcasting_user.put()

            # Make list of Users to tickle
            users = []
            for broadcastee_key in broadcasting_user.outgoing_broadcasts:
                users.append(db.get(broadcastee_key))
                users.append(broadcasting_user)

            tickle_users(users, broadcasting_user) 

            self.response.write("User " + jid + " is no longer Confirmed for " + broadcasting_user.first_name + "'s Proposal.\n")
        else:
            self.response.write("User " + jid + " wasn't even Confirmed for " + broadcasting_user.first_name + "'s Proposal, idiot!!!\n")
        return

class ProposalSeenHandler(webapp2.RequestHandler):
    def get(self, jid):
        self.response.write("proposal seen GET handler")
        return

    def put(self, jid):
        # Grab the PUT request parameter (the Broadcasting User's jid)
        param_target = self.request.get('target')

        # Create a Key for the Broadcastee that has seen the proposal
        key_seeing_user = db.Key.from_path('User', jid)

        # Retrieve the User object for said Broadcastee
        seeing_user = db.get(key_seeing_user)

        # Check if the proposal has been seen
        if param_target not in seeing_user.proposals_seen_jids:
            # Add the jid of the Broadcaster to the Broadcastee's proposals_seen list
            seeing_user.proposals_seen_jids.append(param_target)

        # Save it back into the datastore.
        seeing_user.put()

        # If all goes well, send back a confirmation message
        self.response.write(seeing_user.first_name + " has seen the proposal of user #" + param_target)

        return

    def delete(self, jid):
        # Grab the DELETE request parameter (the Broadcasting User's jid)
        param_target = self.request.get('target')

        # Create a Key for the Broadcastee that had seen the proposal
        key_seeing_user = db.Key.from_path('User', jid)

        # Retrieve the User object for said Broadcastee
        seeing_user = db.get(key_seeing_user)

        # Ensure the proposal has been seen
        if param_target in seeing_user.proposals_seen_jids:
            # Remove the jid of the former Broadcaster from the Broadcastee's proposals_seen list
            seeing_user.proposals_seen_jids.remove(param_target)

        # Save it back into the datastore.
        seeing_user.put()

        # If all goes well, send back a confirmation message
        self.response.write(seeing_user.first_name + " has NO LONGER seen the proposal of user #" + param_target)

        return
