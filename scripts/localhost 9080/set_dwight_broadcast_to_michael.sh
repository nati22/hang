URL=http://localhost:9080

# Broadcast to Michael
CURL -XPUT -d target=1234 $URL/users/5678/broadcasts 
# Broadcast to Andy
#CURL -XPUT -d target=9101112 $URL/users/5678/broadcasts 