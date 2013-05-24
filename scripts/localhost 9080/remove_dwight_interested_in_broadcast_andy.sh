URL=http://localhost:9080

# Interested in Broadcast for Dwight
#CURL -XDELETE -d target=1234 $URL/users/5678/proposal/interested -G
# Interested in Broadcast for Andy
CURL -XDELETE -d target=9101112 $URL/users/5678/proposal/interested -G