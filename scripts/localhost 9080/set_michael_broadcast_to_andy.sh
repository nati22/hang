URL=http://localhost:9080

# Broadcast to Dwight
#CURL -XPUT -d target=5678 $URL/users/1234/broadcasts 
# Broadcast to Andy
CURL -XPUT -d target=9101112 $URL/users/1234/broadcasts 