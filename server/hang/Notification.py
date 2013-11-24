
import webapp2
from push import push_new_chats_to_users
from google.appengine.ext import db

class ChatNotificationHandler(webapp2.RequestHandler):
	def put(self, jid):
	#	try:
			# Grab the PUT request parameter for the Proposal host
			param_host = self.request.get('host_jid')

			# Grab the PUT request parameter for the 
			param_targets = self.request.get_all('target')

			# Create list of Users to notify
			targets = []
			for target in param_targets:
				key_user = db.Key.from_path('User', target)
				targets.append(db.get(key_user))
		
			# Get sender's User
			key_sender = db.Key.from_path('User', jid)
			sender = db.get(key_sender)

			# Get host's User
			key_host = db.Key.from_path('User', param_host)
			host = db.get(key_host)


			for user in targets:
				self.response.write('sending to ' + user.first_name + '\n')

			push_new_chats_to_users(self, targets, host, sender)
			return

#		except (TypeError, ValueError):
#			self.response.write('Invalid inputs\n')
#			return

	def delete(self, jid):

		self.response.write("Deleting Chat notification")
		return
