URL=http://localhost:9080

# Not interested in Broadcast for Michael
#CURL -XDELETE -d target=1234 $URL/users/9101112/proposal/interested -G
# Interested in Broadcast for Andy
CURL -XDELETE -d target=5678 $URL/users/9101112/proposal/interested -G