URL=http://localhost:9080

# Interested in Broadcast for Dwight
#CURL -XPUT -d target=5678 $URL/users/1234/proposal/interested 
# Interested in Broadcast for Andy
CURL -XPUT -d target=9101112 $URL/users/1234/proposal/interested