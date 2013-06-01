URL=http://localhost:9080

# Interested in Broadcast for Michael
#CURL -XPUT -d target=1234 $URL/users/9101112/proposal/interested 
# Interested in Broadcast for Dwight
CURL -XPUT -d target=5678 $URL/users/9101112/proposal/interested