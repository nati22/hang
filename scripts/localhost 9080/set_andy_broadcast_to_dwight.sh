URL=http://localhost:9080

# Broadcast to Michael
#CURL -XPUT -d target=1234 $URL/users/9101112/broadcasts 
# Broadcast to Dwight
CURL -XPUT -d target=1234 target=5678 $URL/users/9101112/broadcasts 