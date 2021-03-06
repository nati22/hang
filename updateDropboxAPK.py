# This script will only work if it is located just inside the hang repository
# and if the relative path to the hang apk is described correctly in the 
# rel_src_location or alt_rel_src_location variables, and that the directory
# "The Latest Hang APK" is located inside of the Dropbox folder in the home directory. 


#!/usr/bin/python
import os, shutil, sys, smtplib
from os.path import expanduser

# The relative location (from hang repo) of the recently built APK
rel_src_location = 'android/hang/bin/hang.apk'
# The alternate relative location (from hang repo) of the recently built APK
alt_rel_src_location = 'android/hang/bin/HomeActivity.apk'
# The relative location (from home directory) of the old APK
rel_dest_location = '/Dropbox/The Latest Hang APK/hang.apk'
# The home directory
home_dir = expanduser("~")

# Verify location of source APK
try:
	with open(rel_src_location): pass
except IOError:
	print "Unable to locate APK at '" + rel_src_location + "'..."
	# Check if Eclipse decided to rename the APK to HomeActivity again
	try:
		with open(alt_rel_src_location): pass
		print "Found APK at '" + alt_rel_src_location + "'"

		# Change the APK source location accordingly
		rel_src_location = alt_rel_src_location

	except IOError:
		print "Unable to locate APK at '" + alt_rel_src_location + "'."
		sys.exit("Update of Dropbox APK FAILED.")

# Copy the APK to Dropbox
try:
	shutil.copyfile(rel_src_location, home_dir + rel_dest_location)
except IOError:
	sys.exit("Error writing to '" + rel_dest_location + "'")
print "Successfully updated Dropbox APK."


# Notify Sam
print "Notifying Sam..."

server = smtplib.SMTP( "smtp.gmail.com", 587)
server.starttls()
server.login( 'hangdevteam@gmail.com', 'thehardestpasswordever')

samora_address = '9165090227@tmomail.net'
nati_address = '9166622523@vtext.com'
#girum_address = '9162289577@messaging.sprintpcs.com'

#hang_dropbox_url = 'dropbox.com/sh/40w6ko5qt6ao2je/Hl6lk8qxZI' # this url can't contain http or https
hang_dropbox_direct_url = 'dropbox.com/sh/40w6ko5qt6ao2je/LWx9LXgvn9/hang.apk'


server.sendmail('Nati', samora_address, "Yo Sam, there's a new hang"
	+ " APK on Dropbox! Download it at " + hang_dropbox_direct_url)
server.sendmail('Nati', nati_address, "Yo Nati, there's a new hang"
	+ " APK on Dropbox! Download it at " + hang_dropbox_direct_url)
print "Done!"


# nati_address = '9166622523@vtext.com'
# girum_address = '9162289577@messaging.sprintpcs.com'


















