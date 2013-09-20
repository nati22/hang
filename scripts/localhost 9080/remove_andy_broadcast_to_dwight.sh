URL=http://localhost:9080

# Stop Broadcasting to Michael
#CURL -XDELETE -d target=1234 $URL/users/9101112/broadcasts -G
# Stop Broadcast to Andy
CURL -XDELETE -d target=5678 $URL/users/9101112/broadcasts -G
