#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2
from User import UserRequestHandler, NudgeRequestHandler
from Status import StatusRequestHandler
from Proposal import ProposalRequestHandler, InterestedRequestHandler, ConfirmedRequestHandler, ProposalSeenHandler
from Broadcast import MultipleBroadcastRequestHandler
#from push import NotificationReceivedRequestHandler
            
class HelloWorldRequestHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello world!')

# This defines which requests go to which classes. 
app = webapp2.WSGIApplication([
    ('/', HelloWorldRequestHandler),
    (r'/users/(\d+)', UserRequestHandler),  # Maps /users/{jid} to the Users request handler.
    (r'/users/(\d+)/broadcasts', MultipleBroadcastRequestHandler), 
    (r'/users/(\d+)/status', StatusRequestHandler),
    (r'/users/(\d+)/nudge', NudgeRequestHandler),
    (r'/users/(\d+)/proposal', ProposalRequestHandler),
    (r'/users/(\d+)/proposal/interested', InterestedRequestHandler),
    (r'/users/(\d+)/proposal/confirmed', ConfirmedRequestHandler),
    (r'/users/(\d+)/proposal/seen', ProposalSeenHandler)
], debug=True)