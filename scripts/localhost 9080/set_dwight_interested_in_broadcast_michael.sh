URL=http://localhost:9080

# Interested in Broadcast for Dwight
CURL -XPUT -d target=1234 $URL/users/5678/proposal/interested 
# Interested in Broadcast for Andy
#CURL -XPUT -d target=9101112 $URL/users/5678/proposal/interested