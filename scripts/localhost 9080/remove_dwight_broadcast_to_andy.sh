URL=http://localhost:9080

# Stop Broadcasting to Michael
#CURL -XDELETE -d target=1234 $URL/users/5678/broadcast -G
# Stop Broadcast to Andy
CURL -XDELETE -d target=9101112 $URL/users/5678/broadcasts -G
