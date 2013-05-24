URL=http://localhost:9080

# Stop broadcasting to Dwight
CURL -XDELETE -d target=5678 $URL/users/1234/broadcast -G
# Stop Broadcast to Andy
CURL -XDELETE -d target=9101112 $URL/users/1234/broadcast -G
